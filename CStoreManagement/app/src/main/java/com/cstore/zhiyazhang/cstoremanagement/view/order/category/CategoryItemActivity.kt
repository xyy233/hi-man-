package com.cstore.zhiyazhang.cstoremanagement.view.order.category

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.Camera
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.*
import com.cstore.zhiyazhang.cstoremanagement.presenter.ordercategory.CategoryItemAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.ordercategory.CategoryItemPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.*
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.CategoryItemView
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.cstore.zhiyazhang.cstoremanagement.view.order.contract.ContractSearchActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_contract.*
import kotlinx.android.synthetic.main.layout_search_title.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2017/7/26 14:23.
 */
class CategoryItemActivity(override val layoutId: Int = R.layout.activity_contract) : MyActivity(), GenericView, CategoryItemView, EasyPermissions.PermissionCallbacks {

    override var nowMidId: String = ""

    override var nowId: String = ""

    companion object {
        //订量倒序
        private val TODAY_SORT_DESC = "order by x.ordActualQuantity desc"
        //品号倒序
        private val COMMODIFY_ID_SORT_DESC = "order by x.itemNumber desc"
        //品名倒序
        private val COMMODIFY_NAME_SORT_DESC = "order by x.pluName desc"
        //价格倒序
        private val MONEY_SORT_DESC = "order by x.storeUnitPrice desc"
        //订量正序
        private val TODAY_SORT = "order by x.ordActualQuantity"
        //品号正序
        private val COMMODIFY_ID_SORT = "order by x.itemNumber"
        //品名正序
        private val COMMODIFY_NAME_SORT = "order by x.pluName"
        //价格正序
        private val MONEY_SORT = "order by x.storeUnitPrice"
        //DMS销量正序
        private val DMS_SORT = " order by x.dms "
        //DMS销量倒序
        private val DMS_SORT_DESC = " order by x.dms desc"
    }

    private var isBack = false
    private val changeData = ArrayList<CategoryItemBean>()//修改的数据
    private val layoutManager = MyLinearlayoutManager(this@CategoryItemActivity, LinearLayoutManager.VERTICAL, false)
    private var adapter: CategoryItemAdapter? = null
    private val presenter = CategoryItemPresenter(this, this, this, this)
    private var changeCategory: OrderCategoryBean? = null
    private var isNext = false
    private var isLast = false

    override fun initView() {
        my_swipe.setProgressViewEndTarget(true, 300)
        toolbar.setNavigationIcon(R.drawable.ic_action_back)
        when (whereIsIt) {
            "category" -> {
                nowId = category.categoryId
                toolbar.title = category.categoryName
            }
            "shelf" -> {
                nowId = shelf.shelfId
                toolbar.title = shelf.shelfName
            }
            "search" -> {
                search_bar.visibility = View.VISIBLE
                toolbar.title = getString(R.string.unit_order)
            }
            "unitord" -> {
                search_bar.visibility = View.VISIBLE
                toolbar.title = getString(R.string.unit_order)
            }
            "self" -> {
                nowId = self.selfId
                toolbar.title = self.selfName
            }
            "nop" -> {
                nowId = nop.nopId
                toolbar.title = nop.nopName
            }
            "fresh" -> {
                nowId = fg.categoryId
                nowMidId = fg.midId
                toolbar.title = fg.name
            }
        }

        setSupportActionBar(toolbar)
        //设置loading样式
        my_swipe.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this@CategoryItemActivity, R.color.cstore_white))
        my_swipe.setProgressViewOffset(false, 0, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, resources.displayMetrics).toInt())
        my_swipe.setColorSchemeColors(
                ContextCompat.getColor(this@CategoryItemActivity, R.color.cstore_red),
                ContextCompat.getColor(this@CategoryItemActivity, R.color.yellow),
                ContextCompat.getColor(this@CategoryItemActivity, R.color.blue),
                ContextCompat.getColor(this@CategoryItemActivity, R.color.cstore_green))
        //设置下拉刷新是否能用
        appbar.addOnOffsetChangedListener { _, verticalOffset ->
            my_swipe.isEnabled = verticalOffset >= 0
        }
        swipe_recycler.layoutManager = layoutManager
        initSort()
        initSearch()
    }

    override fun initClick() {
        my_swipe.setOnRefreshListener {
            if (my_swipe.isEnabled) getData()
        }
        done.setOnClickListener {
            if (MyTimeUtil.nowHour<18){
                changeData.removeAll(changeData.filter { it.changeCount == 0 })
                if (changeData.size == 0) {
                    showPrompt(getString(R.string.no_edit_msg))
                    return@setOnClickListener
                }
                presenter.updateAllCategory()
            }else{
                showPrompt("超出订货时间，不能保存")
            }
        }
        order_item_next.setOnClickListener {
            my_swipe.isRefreshing=true
            if (MyTimeUtil.nowHour>18){
                goNext()
                return@setOnClickListener
            }
            changeData.removeAll(changeData.filter { it.changeCount == 0 })
            if (changeData.size != 0) AlertDialog.Builder(this@CategoryItemActivity)
                    .setTitle("提示")
                    .setMessage("您修改的订量尚未确认，是否放弃修改？")
                    .setPositiveButton("保存", { _, _ ->
                        isNext = true
                        presenter.updateAllCategory()
                    })
                    .setNegativeButton("放弃", { _, _ ->
                        changeData.clear()
                        goNext()
                    })
                    .show()
            else goNext()
        }
        order_item_last.setOnClickListener {
            if (MyTimeUtil.nowHour>18){
                goLast()
                return@setOnClickListener
            }
            changeData.removeAll(changeData.filter { it.changeCount == 0 })
            if (changeData.size != 0) AlertDialog.Builder(this@CategoryItemActivity)
                    .setTitle("提示")
                    .setMessage("您修改的订量尚未确认，是否放弃修改？")
                    .setPositiveButton("保存", { _, _ ->
                        isLast = true
                        presenter.updateAllCategory()
                    })
                    .setNegativeButton("放弃", { _, _ ->
                        changeData.clear()
                        goLast()
                    })
                    .show() else goLast()
        }
        retry.setOnClickListener { if (my_swipe.isEnabled) getData() }
    }

    override fun initData() {
        when (whereIsIt) {
            "search" -> {
            }
            "unitord" -> presenter.getAllSearch(intent.getStringExtra("search_message"))
            else -> my_swipe.autoRefresh()
        }
    }

    private fun initSearch() {
        when (whereIsIt) {
            "search" -> {
                search_bar.visibility = View.VISIBLE
                order_item_next.visibility = View.GONE
                order_item_last.visibility = View.GONE
            }
            "unitord" -> {
                search_bar.visibility = View.VISIBLE
                order_item_next.visibility = View.GONE
                order_item_last.visibility = View.GONE
            }
            else -> {
                search_bar.visibility = View.GONE
                order_item_next.visibility = View.VISIBLE
                order_item_last.visibility = View.VISIBLE
            }
        }
        search_btn.setOnClickListener {
            presenter.getAllSearch(search_edit.text.toString())
            val imm = getSystemService(
                    Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(search_edit.windowToken, 0)
        }
        search_edit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                presenter.getAllSearch(search_edit.text.toString())
                val imm = getSystemService(
                        Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(search_edit.windowToken, 0)
                true
            } else {
                false
            }
        }
        qrcode.setOnClickListener {
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                if (judgmentCarmer()) {
                    val i = Intent(this@CategoryItemActivity, ContractSearchActivity::class.java)
                    i.putExtra("whereIsIt", "unitord")
                    startActivity(i)
                    finish()
                }
            } else {
                if (cameraIsCanUse()) {
                    val i = Intent(this@CategoryItemActivity, ContractSearchActivity::class.java)
                    i.putExtra("whereIsIt", "unitord")
                    startActivity(i)
                    finish()
                } else {
                    MyToast.getLongToast("您未开启相机权限，请开启相机权限！")
                    val intent = Intent()
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                    intent.data = Uri.fromParts("package", this@CategoryItemActivity.packageName, null)
                    this@CategoryItemActivity.startActivity(intent)
                }
            }
        }
    }

    private fun initSort() {
        when (whereIsIt) {
            "search" -> mySpinner.visibility = View.GONE
            "unitord" -> mySpinner.visibility = View.GONE
            else -> {
                val sortAdapter = if (whereIsIt == "self") {
                    ArrayAdapter(this, R.layout.custom_spinner_text_item, resources.getStringArray(R.array.mySort))
                } else {
                    ArrayAdapter(this, R.layout.custom_spinner_text_item, resources.getStringArray(R.array.mySortDMS))
                }
                sortAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
                mySpinner.adapter = sortAdapter
                if (whereIsIt == "self") mySpinner.setSelection(3, false) else mySpinner.setSelection(9, false)
                mySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                        getData()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
            }
        }
    }

    private fun getData() {
        adapter = null
        when (whereIsIt) {
            "category" -> {
                presenter.getAllItem()
            }
            "shelf" -> {
                presenter.getAllShelf()
            }
            "search" -> {
                presenter.getAllSearch(search_edit.text.toString())
            }
            "self" -> {
                presenter.getAllSelf()
            }
            "nop" -> {
                presenter.getAllNOP()
            }
            "fresh" -> {
                presenter.getAllFresh()
            }
        }
    }

    private fun goNext() {
        my_swipe.isRefreshing=true
        isNext = false
        when (whereIsIt) {
            "category" -> {
                val itemIdData = (getAllItemId() as ArrayList<OrderCategoryBean>)
                for (i in itemIdData.indices) {
                    if (itemIdData[i].categoryId == nowId) {
                        if (i == itemIdData.size - 1) {
                            nowId = itemIdData[0].categoryId
                            toolbar.title = itemIdData[0].categoryName
                        } else {
                            nowId = itemIdData[i + 1].categoryId
                            toolbar.title = itemIdData[i + 1].categoryName
                        }
                        break
                    }
                }
            }
            "shelf" -> {
                val itemIdData = (getAllItemId() as ArrayList<ShelfBean>)
                for (i in itemIdData.indices) {
                    if (itemIdData[i].shelfId == nowId) {
                        if (i == itemIdData.size - 1) {
                            nowId = itemIdData[0].shelfId
                            toolbar.title = itemIdData[0].shelfName
                        } else {
                            nowId = itemIdData[i + 1].shelfId
                            toolbar.title = itemIdData[i + 1].shelfName
                        }
                        break
                    }
                }
            }
            "self" -> {
                val itemIdData = (getAllItemId() as ArrayList<SelfBean>)
                for (i in itemIdData.indices) {
                    if (itemIdData[i].selfId == nowId) {
                        if (i == itemIdData.size - 1) {
                            nowId = itemIdData[0].selfId
                            toolbar.title = itemIdData[0].selfName
                        } else {
                            nowId = itemIdData[i + 1].selfId
                            toolbar.title = itemIdData[i + 1].selfName
                        }
                        break
                    }
                }
            }
            "nop" -> {
                val itemIdData = (getAllItemId() as ArrayList<NOPBean>)
                for (i in itemIdData.indices) {
                    if (itemIdData[i].nopId == nowId) {
                        if (i == itemIdData.size - 1) {
                            nowId = itemIdData[0].nopId
                            toolbar.title = itemIdData[0].nopName
                        } else {
                            nowId = itemIdData[i + 1].nopId
                            toolbar.title = itemIdData[i + 1].nopName
                        }
                        break
                    }
                }
            }
            "fresh" -> {
                val itemIdData = (getAllItemId() as ArrayList<FreshGroup>)
                for (i in itemIdData.indices) {
                    if (itemIdData[i].categoryId == nowId && itemIdData[i].midId == nowMidId) {
                        if (i == itemIdData.size - 1) {
                            nowId = itemIdData[0].categoryId
                            nowMidId=itemIdData[0].midId
                            toolbar.title = itemIdData[0].name
                        } else {
                            nowId = itemIdData[i + 1].categoryId
                            nowMidId = itemIdData[i + 1].midId
                            toolbar.title = itemIdData[i + 1].name
                        }
                        break
                    }
                }
            }
        }
        if (my_swipe.isEnabled) getData()
    }

    private fun goLast() {
        my_swipe.isRefreshing=true
        isLast = false
        when (whereIsIt) {
            "category" -> {
                val itemIdData = (getAllItemId() as ArrayList<OrderCategoryBean>)
                for (i in itemIdData.indices) {
                    if (itemIdData[i].categoryId == nowId) {
                        if (i == 0) {
                            nowId = itemIdData[itemIdData.size - 1].categoryId
                            toolbar.title = itemIdData[itemIdData.size - 1].categoryName
                        } else {
                            nowId = itemIdData[i - 1].categoryId
                            toolbar.title = itemIdData[i - 1].categoryName
                        }
                        break
                    }
                }
            }
            "shelf" -> {
                val itemIdData = (getAllItemId() as ArrayList<ShelfBean>)
                for (i in itemIdData.indices) {
                    if (itemIdData[i].shelfId == nowId) {
                        if (i == 0) {
                            nowId = itemIdData[itemIdData.size - 1].shelfId
                            toolbar.title = itemIdData[itemIdData.size - 1].shelfName
                        } else {
                            nowId = itemIdData[i - 1].shelfId
                            toolbar.title = itemIdData[i - 1].shelfName
                        }
                        break
                    }
                }
            }
            "self" -> {
                val itemIdData = (getAllItemId() as ArrayList<SelfBean>)
                for (i in itemIdData.indices) {
                    if (itemIdData[i].selfId == nowId) {
                        if (i == 0) {
                            nowId = itemIdData[itemIdData.size - 1].selfId
                            toolbar.title = itemIdData[itemIdData.size - 1].selfName
                        } else {
                            nowId = itemIdData[i - 1].selfId
                            toolbar.title = itemIdData[i - 1].selfName
                        }
                        break
                    }
                }
            }
            "nop" -> {
                val itemIdData = (getAllItemId() as ArrayList<NOPBean>)
                for (i in itemIdData.indices) {
                    if (itemIdData[i].nopId == nowId) {
                        if (i == 0) {
                            nowId = itemIdData[itemIdData.size - 1].nopId
                            toolbar.title = itemIdData[itemIdData.size - 1].nopName
                        } else {
                            nowId = itemIdData[i - 1].nopId
                            toolbar.title = itemIdData[i - 1].nopName
                        }
                        break
                    }
                }
            }
            "fresh" -> {
                val itemIdData = (getAllItemId() as ArrayList<FreshGroup>)
                for (i in itemIdData.indices) {
                    if (itemIdData[i].categoryId == nowId && itemIdData[i].midId == nowMidId) {
                        if (i == 0) {
                            nowId = itemIdData[itemIdData.size - 1].categoryId
                            nowMidId = itemIdData[itemIdData.size - 1].midId
                            toolbar.title = itemIdData[itemIdData.size - 1].name
                        } else {
                            nowId = itemIdData[i - 1].categoryId
                            nowMidId = itemIdData[i - 1].midId
                            toolbar.title = itemIdData[i - 1].name
                        }
                        break
                    }
                }
            }
        }
        if (my_swipe.isEnabled) getData()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> judgmentUpdate()
        }
        return true
    }

    override fun onBackPressed() {
        judgmentUpdate()
    }

    private fun judgmentUpdate() {
        if (MyTimeUtil.nowHour>18){
            super.onBackPressed()
            return
        }
        changeData.removeAll(changeData.filter { it.changeCount == 0 })
        if (changeData.size != 0) AlertDialog.Builder(this@CategoryItemActivity)
                .setTitle("提示")
                .setMessage("您修改的订量尚未确认，是否放弃修改？")
                .setPositiveButton("保存", { _, _ ->
                    isBack = true
                    presenter.updateAllCategory()
                })
                .setNegativeButton("放弃", { _, _ ->
                    super.onBackPressed()
                })
                .show()
        else
            super.onBackPressed()
    }

    override val category: OrderCategoryBean
        get() = intent.getSerializableExtra("category") as OrderCategoryBean

    override val shelf: ShelfBean
        get() = intent.getSerializableExtra("shelf") as ShelfBean

    override val self: SelfBean
        get() = intent.getSerializableExtra("self") as SelfBean

    override val nop: NOPBean
        get() = intent.getSerializableExtra("nop") as NOPBean

    override val fg: FreshGroup
        get() = intent.getSerializableExtra("fg") as FreshGroup

    override fun getAllItemId(): Serializable {
        return intent.getSerializableExtra("itemIds")
    }

    override val sort: String
        get() = when (mySpinner.selectedItemPosition) {
            0 -> TODAY_SORT_DESC
            1 -> TODAY_SORT
            2 -> COMMODIFY_ID_SORT_DESC
            3 -> COMMODIFY_ID_SORT
            4 -> COMMODIFY_NAME_SORT_DESC
            5 -> COMMODIFY_NAME_SORT
            6 -> MONEY_SORT_DESC
            7 -> MONEY_SORT
            8 -> DMS_SORT
            9 -> DMS_SORT_DESC
            else -> COMMODIFY_ID_SORT_DESC
        }

    override val whereIsIt: String
        get() = intent.getStringExtra("whereIsIt")

    override val categoryList: ArrayList<CategoryItemBean>
        get() = changeData

    override fun updateDone() {
        adapter!!.data.filter { it.changeCount != 0 }.forEach {
            it.changeCount = 0
        }
        changeData.clear()
        when {
            isBack -> {
                hideLoading()
                super.onBackPressed()
            }
            isNext -> {
                showPrompt(getString(R.string.saveDone))
                goNext()
            }
            isLast -> {
                showPrompt(getString(R.string.saveDone))
                goLast()
            }
            else -> when (whereIsIt) {
                "search" -> {
                    showPrompt(getString(R.string.saveDone))
                    hideLoading()
                }
                "unitord" -> {
                    showPrompt(getString(R.string.saveDone))
                    hideLoading()
                }
                else -> {
                    showPrompt(getString(R.string.saveDone))
                    if (my_swipe.isEnabled) getData()
                }
            }
        }
    }

    private fun saveCategory() {
        val preferences = getSharedPreferences("cib", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("categoryId", changeCategory!!.categoryId)
        editor.putInt("ordSku", changeCategory!!.ordSku)
        //editor.putInt("ordPrice", changeCategory!!.ordPrice)
        editor.apply()
    }

    override fun <T> requestSuccess(rData: T) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showLoading() {
        my_swipe.isRefreshing = true
        order_item_next.isEnabled = false
        order_item_last.isEnabled = false
        layoutManager.setScrollEnabled(false)
        MyHandler.removeCallbacksAndMessages(null)
    }

    override fun hideLoading() {
        my_swipe.isRefreshing = false
        order_item_next.isEnabled = true
        order_item_last.isEnabled = true
        layoutManager.setScrollEnabled(true)
        MyHandler.removeCallbacksAndMessages(null)
    }

    override fun <T> showView(aData: T) {
        retry.visibility = View.GONE
        noMessage.visibility = View.GONE
        this.adapter = aData as CategoryItemAdapter
        if (aData.data.isEmpty()) errorDealWith()
        swipe_recycler.adapter = aData
    }

    override fun errorDealWith() {
        adapter = null
        swipe_recycler.adapter = adapter
        noMessage.visibility = View.VISIBLE
    }

    override fun touchAdd(cb: CategoryItemBean, event: MotionEvent, position: Int) {
        val adapterView: CategoryItemAdapter.ViewHolder = swipe_recycler.findViewHolderForAdapterPosition(position) as CategoryItemAdapter.ViewHolder
        onTouchChange(1, event.action, adapterView, cb)
    }

    override fun touchLess(cb: CategoryItemBean, event: MotionEvent, position: Int) {
        val adapterView: CategoryItemAdapter.ViewHolder = swipe_recycler.findViewHolderForAdapterPosition(position) as CategoryItemAdapter.ViewHolder
        onTouchChange(0, event.action, adapterView, cb)
    }

    private var mt: Thread? = null
    private var pt: Thread? = null
    var hd: Handler? = null
    var isOnLongClick = false
    private fun onTouchChange(addLess: Int, action: Int, view: CategoryItemAdapter.ViewHolder, cb: CategoryItemBean) {
        hd = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> if (view.less.isEnabled) lessCount(view, cb)
                    1 -> if (view.add.isEnabled) addCount(view, cb)
                }
            }
        }
        if (addLess == 0) {//less
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    if(!isOnLongClick){
                        mt = object : Thread(Runnable {
                            while (isOnLongClick) {
                                Thread.sleep(200)
                                hd!!.sendEmptyMessage(0)
                            }
                        }) {}
                        isOnLongClick = true
                        runOrStopEdit()
                        mt!!.start()
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (mt != null){
                        isOnLongClick = false
                        runOrStopEdit()
                        mt = null
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (mt != null && Integer.parseInt(view.editOrderQTY.text.toString()) != 0)
                        isOnLongClick = true
                    runOrStopEdit()
                }
            }
            runOrStopEdit()
        } else {//add
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    pt = object : Thread(Runnable {
                        while (isOnLongClick) {
                            Thread.sleep(200)
                            hd!!.sendEmptyMessage(1)
                        }
                    }) {}
                    isOnLongClick = true
                    runOrStopEdit()
                    pt!!.start()
                }
                MotionEvent.ACTION_UP -> {
                    if (pt != null) isOnLongClick = false
                    runOrStopEdit()
                    pt = null
                }

                MotionEvent.ACTION_MOVE -> {
                    if (pt != null) isOnLongClick = true
                    runOrStopEdit()
                }
            }
            runOrStopEdit()
        }
    }

    /**
     * 根据是否是在运行中来静止滑动或开启滑动,因为分发机制，不禁止会被截取
     */
    private fun runOrStopEdit() {
        //长按中禁止所有滑动
        if (isOnLongClick) {
            layoutManager.setScrollEnabled(false)
            my_swipe.isEnabled = false
        } else {//停止长按开启所有滑动
            layoutManager.setScrollEnabled(true)
            my_swipe.isEnabled = true
        }
    }

    private fun addCount(view: CategoryItemAdapter.ViewHolder, cb: CategoryItemBean) {
        val nowItemCount = cb.orderQTY + cb.stepQty
        if (nowItemCount > cb.maxQty) {
            showPrompt(getString(R.string.maxCNoAdd))
            return
        }
        cb.orderQTY = nowItemCount
        cb.changeCount++
        view.editOrderQTY.text = cb.orderQTY.toString()
        val searchData = changeData.filter { it.itemId == cb.itemId }
        if (searchData.isNotEmpty()) {
            if (searchData.size > 1) {
                //错误数据发送到服务器
                ReportListener.report(User.getUser().storeId, MyApplication.getVersion()!!, getString(R.string.duplicateData), Gson().toJson(adapter!!.data))
                changeData.removeAll(searchData)
                changeData.add(cb.copy())
            } else {
                changeData.filter { it.itemId == cb.itemId }.forEach {
                    it.orderQTY = nowItemCount
                    it.changeCount++
                }
            }
        } else {
            changeData.add(cb.copy())
        }

        when {
            cb.orderQTY > 0 -> view.myCommodify.setBackgroundColor(ContextCompat.getColor(this@CategoryItemActivity, R.color.add_bg))
            cb.orderQTY < 0 -> view.myCommodify.setBackgroundColor(ContextCompat.getColor(this@CategoryItemActivity, R.color.less_bg))
            cb.orderQTY == 0 -> view.myCommodify.setBackgroundColor(ContextCompat.getColor(this@CategoryItemActivity, R.color.white))
        }
    }

    private fun lessCount(view: CategoryItemAdapter.ViewHolder, cb: CategoryItemBean) {
        val nowItemCount = cb.orderQTY - cb.stepQty
        if (nowItemCount < 0) {
            showPrompt(getString(R.string.minCNoLess))
            return
        }
        cb.orderQTY = nowItemCount
        cb.changeCount--
        view.editOrderQTY.text = nowItemCount.toString()
        val searchData = changeData.filter { it.itemId == cb.itemId }
        if (searchData.isNotEmpty()) {
            if (searchData.size > 1) {
                //错误数据发送到服务器
                ReportListener.report(User.getUser().storeId, MyApplication.getVersion()!!, getString(R.string.duplicateData), Gson().toJson(adapter!!.data))
                changeData.removeAll(searchData)
                changeData.add(cb.copy())
            } else {
                changeData.filter { it.itemId == cb.itemId }.forEach {
                    it.orderQTY = nowItemCount
                    it.changeCount--
                }
            }
        } else {
            changeData.add(cb.copy())
        }
        when {
            cb.orderQTY > 0 -> view.myCommodify.setBackgroundColor(ContextCompat.getColor(this@CategoryItemActivity, R.color.add_bg))
            cb.orderQTY < 0 -> view.myCommodify.setBackgroundColor(ContextCompat.getColor(this@CategoryItemActivity, R.color.less_bg))
            cb.orderQTY == 0 -> view.myCommodify.setBackgroundColor(ContextCompat.getColor(this@CategoryItemActivity, R.color.white))
        }
    }

    //获得相机权限
    @pub.devrel.easypermissions.AfterPermissionGranted(1)
    private fun judgmentCarmer(): Boolean {
        val perms = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (!EasyPermissions.hasPermissions(this, *perms)) {
            EasyPermissions.requestPermissions(this, "请打开权限以操作扫描更新", 1, *perms)
            return false
        }
        return true
    }


    /**
     * 返回true 表示可以使用  返回false表示不可以使用
     */
    private fun cameraIsCanUse(): Boolean {
        var isCanUse = true
        var mCamera: Camera? = null
        try {
            mCamera = Camera.open()
            val mParameters = mCamera!!.parameters //针对魅族手机
            mCamera.parameters = mParameters
        } catch (e: Exception) {
            isCanUse = false
        }

        if (mCamera != null) {
            try {
                mCamera.release()
            } catch (e: Exception) {
                e.printStackTrace()
                return isCanUse
            }

        }
        return isCanUse
    }

    //请求权限结果
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    //获取权限失败
    override fun onPermissionsDenied(requestCode: Int, list: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    //获取权限成功
    override fun onPermissionsGranted(requestCode: Int, list: List<String>) {
        val i = Intent(this@CategoryItemActivity, ContractSearchActivity::class.java)
        i.putExtra("whereIsIt", "unitord")
        startActivity(i)
    }

}