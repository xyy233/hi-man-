package com.cstore.zhiyazhang.cstoremanagement.view.order.contract

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ContractBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ContractTypeBean
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.presenter.contract.ContractAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.contract.ContractPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.ReportListener
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.cstore.zhiyazhang.cstoremanagement.view.ImageActivity
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.ContractView
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.google.gson.Gson
import com.zhiyazhang.mykotlinapplication.utils.MyApplication
import kotlinx.android.synthetic.main.activity_contract.*

/**
 * Created by zhiya.zhang
 * on 2017/6/15 12:00.
 */
class ContractActivity(override val layoutId: Int = R.layout.activity_contract) : MyActivity(), ContractView, GenericView {

    override val contractType: ContractTypeBean
        get() = ctb!!

    private var previousIntent: Intent? = null//上个页面的intent
    private var adapter: ContractAdapter? = null
    private val presenter = ContractPresenter(this, this, this)
    private val layoutManager = MyLinearlayoutManager(this@ContractActivity, LinearLayoutManager.VERTICAL, false)
    private var isBack = false//是否是通过返回按钮保存的
    private var page = 0
    private var ctb: ContractTypeBean? = null
    //订量倒序
    private val TODAY_SORT_DESC = "act_ord_qty__desc"
    //品号倒序
    private val COMMODIFY_ID_SORT_DESC = "item_id__desc"
    //品名倒序
    private val COMMODIFY_NAME_SORT_DESC = "item_name__desc"
    //价格倒序
    private val MONEY_SORT_DESC = "price__desc"
    //订量正序
    private val TODAY_SORT = "act_ord_qty"
    //品号正序
    private val COMMODIFY_ID_SORT = "item_id"
    //品名正序
    private val COMMODIFY_NAME_SORT = "item_name"
    //价格正序
    private val MONEY_SORT = "price"

    private val changeData = ArrayList<ContractBean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        previousIntent = intent
        initView()
        //先去查询是否是通过搜索进入的，然后根据结果给页面赋值数据或隐藏
        initIsSearch()
        //设置下拉刷新和Recycler
        initSwipeAndRecycler()
        //设置排序
        initSort()
        //启动加载
        my_swipe.setOnRefreshListener {
            setPage(0)
            if (my_swipe.isEnabled) isSearchOrGetAll()
        }
        my_swipe.setProgressViewEndTarget(true, 100)
        my_swipe.autoRefresh()
    }

    private fun initView() {
        if (!isJustLook) {
            done.setOnClickListener {
                if (MyTimeUtil.nowHour<18){
                    changeData.removeAll(changeData.filter { it.todayStore == 0 })
                    if (changeData.size == 0) {
                        showPrompt(getString(R.string.noEditMsg))
                        return@setOnClickListener
                    }
                    ReportListener.report(User.getUser().storeId, MyApplication.getVersion()!!, "保存订量", Gson().toJson(changeData))
                    presenter.updateAllContract()
                }else showPrompt("超出订货时间，不能保存")
            }
        } else {
            done.visibility = View.GONE
        }
        retry.setOnClickListener { my_swipe.autoRefresh() }
    }

    private fun initIsSearch() {
        if (!isSearch) {
            ctb = previousIntent!!.getSerializableExtra("ctb") as ContractTypeBean
            toolbar.title = contractType.typeName
        }
        toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(toolbar)
    }

    private fun initSwipeAndRecycler() {
        //设置loading样式
        my_swipe.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this@ContractActivity, R.color.cstore_white))
        my_swipe.setProgressViewOffset(false, 0, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, resources.displayMetrics).toInt())
        my_swipe.setColorSchemeColors(
                ContextCompat.getColor(this@ContractActivity, R.color.cstore_red),
                ContextCompat.getColor(this@ContractActivity, R.color.yellow),
                ContextCompat.getColor(this@ContractActivity, R.color.blue),
                ContextCompat.getColor(this@ContractActivity, R.color.cstore_green))
        //设置下拉刷新是否能用
        appbar.addOnOffsetChangedListener { _, verticalOffset ->
            my_swipe.isEnabled = verticalOffset >= 0
        }
        swipe_recycler.layoutManager = layoutManager
    }

    private fun initSort() {
        if (isJustLook) {
            mySpinner.visibility = View.GONE
        } else {
            val sortAdapter = ArrayAdapter(this, R.layout.custom_spinner_text_item, resources.getStringArray(R.array.mySort))
            sortAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
            mySpinner.adapter = sortAdapter
            mySpinner.setSelection(3, false)
            mySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                    setPage(0)
                    isSearchOrGetAll()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
        }
    }

    //toolbar图标监听
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> judgmentUpdate()
        }
        return true
    }

    override fun onBackPressed() {
        judgmentUpdate()
    }

    /**
     * 判断用户是否有对订量修改，如果修改过要提示
     */
    private fun judgmentUpdate() {
        if (MyTimeUtil.nowHour>18){
            finish()
            return
        }
        if (changeData.size != 0)
            AlertDialog.Builder(this@ContractActivity)
                    .setTitle("提示")
                    .setMessage("您修改的订量尚未确认，是否放弃修改？")
                    .setPositiveButton("保存", { _, _ ->
                        isBack = true
                        presenter.updateAllContract()
                    })
                    .setNegativeButton("放弃", { _, _ ->
                        finish()
                    })
                    .show()
        else
            finish()
    }

    override fun <T> requestSuccess(objects: T) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val isSearch: Boolean
        get() = previousIntent!!.getBooleanExtra("is_search", false)
    override val isJustLook: Boolean
        get() = previousIntent!!.getBooleanExtra("is_just_look", false)
    override val searchMsg: String
        get() = previousIntent!!.getStringExtra("search_message")


    override fun showLoading() {
        my_swipe.isRefreshing = true
        layoutManager.setScrollEnabled(false)
        //my_swipe.isEnabled = false
    }

    override fun hideLoading() {
        my_swipe.isRefreshing = false
        layoutManager.setScrollEnabled(true)
        //my_swipe.isEnabled = true
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
            else -> COMMODIFY_ID_SORT_DESC
        }

    override fun getPage(): Int {
        return page
    }

    override fun setPage(value: Int) {
        page = value
    }

    override fun clickImage(cb: ContractBean, position: Int) {
        val adapterView: ContractAdapter.ViewHolder = swipe_recycler.findViewHolderForAdapterPosition(position) as ContractAdapter.ViewHolder
        val intent = android.content.Intent(this@ContractActivity, ImageActivity::class.java)
        val transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this@ContractActivity, adapterView.commodifyImg, "image")
        intent.putExtra("cb", cb)
        ActivityCompat.startActivity(this@ContractActivity, intent, transitionActivityOptions.toBundle())
        /*startActivity(intent,
                ActivityOptions.makeSceneTransitionAnimation(this@ContractActivity, adapterView.commodifyImg, "image").toBundle())*/
    }

    override fun touchAdd(cb: ContractBean, event: MotionEvent, position: Int) {
        val adapterView: ContractAdapter.ViewHolder = swipe_recycler.findViewHolderForAdapterPosition(position) as ContractAdapter.ViewHolder
        onTouchChange("add", event.action, adapterView, cb)
    }

    override fun touchLess(cb: ContractBean, event: MotionEvent, position: Int) {
        val adapterView: ContractAdapter.ViewHolder = swipe_recycler.findViewHolderForAdapterPosition(position) as ContractAdapter.ViewHolder
        onTouchChange("less", event.action, adapterView, cb)
    }

    var mt: Thread? = null
    var pt: Thread? = null
    var hd: Handler? = null
    var isOnLongClick: Boolean = false
    private fun onTouchChange(methodName: String, action: Int, view: ContractAdapter.ViewHolder, cb: ContractBean) {
        hd = @android.annotation.SuppressLint("HandlerLeak")
        object : android.os.Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    1 -> if (view.less.isEnabled) {
                        lessCount(view, cb)
                    }
                    2 -> if (view.add.isEnabled) {
                        addCount(view, cb)
                    }
                }
            }
        }
        if (methodName == "less") {
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    mt = object : Thread(Runnable {
                        while (isOnLongClick) {
                            Thread.sleep(200)
                            hd!!.sendEmptyMessage(1)
                        }
                    }) {}
                    isOnLongClick = true
                    runOrStopEdit()
                    mt!!.start()
                }
                MotionEvent.ACTION_UP -> {
                    if (mt != null)
                        isOnLongClick = false
                    runOrStopEdit()
                    mt = null
                }

                MotionEvent.ACTION_MOVE -> {
                    if (mt != null && Integer.parseInt(view.editCdc.text.toString()) != 0)
                        isOnLongClick = true
                    runOrStopEdit()
                }
            }
            runOrStopEdit()
        } else {
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    pt = object : Thread(Runnable {
                        while (isOnLongClick) {
                            Thread.sleep(200)
                            hd!!.sendEmptyMessage(2)
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
     * 增加量
     */
    private fun addCount(view: ContractAdapter.ViewHolder, cb: ContractBean) {
        //当前量增加后的量
        val nowContractCount = cb.todayCount + cb.stepQty
        //检测是否大于商品最大量
        if (nowContractCount > cb.maxQty) {
            showPrompt(getString(R.string.maxCNoAdd))
            return
        }
        if (!isSearch) {
            //当前类的量增加后的量
            val nowTypeAllCount = ctb!!.todayCount + cb.stepQty
            //检测是否大于类的最大量
            if (nowTypeAllCount > ctb!!.maxQty) {
                showPrompt(getString(R.string.maxTNoAdd))
                return
            }
            //更改类的属性
            ctb!!.todayCount = nowTypeAllCount
            ctb!!.todayStore += cb.stepQty
        }
        val nowTodayStore = cb.todayStore + cb.stepQty
        cb.todayStore = nowTodayStore
        cb.todayCount = nowContractCount
        cb.changeCount++
        view.editCdc.text = nowContractCount.toString()

        //检测数据
        val searchData = changeData.filter { it.cId == cb.cId }
        if (searchData.isNotEmpty()) {//不为空
            //如果有重复数据
            if (searchData.size > 1) {
                //错误数据发送到服务器
                ReportListener.report(User.getUser().storeId, MyApplication.getVersion()!!, getString(R.string.duplicateData), Gson().toJson(adapter!!.cr.detail))
                changeData.removeAll(searchData)
                changeData.add(cb.copy(cType = ""))
            } else {
                //修改商品数据
                changeData.filter { it.cId == cb.cId }.forEach {
                    it.todayStore = nowTodayStore
                    it.todayCount = nowContractCount
                    it.changeCount++
                }
            }
        } else {
            changeData.add(cb.copy(cType = ""))
        }

        //通过判断商店修改决定是否改色
        when {
            cb.todayStore > 0 -> view.myCommodify.setBackgroundColor(ContextCompat.getColor(this@ContractActivity, R.color.add_bg))
            cb.todayStore < 0 -> view.myCommodify.setBackgroundColor(ContextCompat.getColor(this@ContractActivity, R.color.less_bg))
            cb.todayStore == 0 -> view.myCommodify.setBackgroundColor(ContextCompat.getColor(this@ContractActivity, R.color.white))
        }
    }

    /**
     * 减少量
     */
    private fun lessCount(view: ContractAdapter.ViewHolder, cb: ContractBean) {
        //当前量减少后的量
        val nowContractCount = cb.todayCount - cb.stepQty
        //检测是否小于商品最小量
        if (nowContractCount < cb.minQty) {
            showPrompt(getString(R.string.minCNoLess))
            return
        }
        if (!isSearch) {
            //当前类的量减少后的量
            val nowTypeAllCount = ctb!!.todayCount - cb.stepQty
            //检测是否小于类的最小量
            if (nowTypeAllCount < ctb!!.minQty) {
                showPrompt(getString(R.string.minTNoLess))
                return
            }
            //更改类的属性
            ctb!!.todayCount = nowTypeAllCount
            ctb!!.todayStore -= cb.stepQty
        }

        cb.todayStore -= cb.stepQty
        cb.todayCount = nowContractCount
        cb.changeCount--
        view.editCdc.text = nowContractCount.toString()

        //修改数据不为空
        if (changeData.filter { it.cId == cb.cId }.isNotEmpty()) {
            //修改商品数据
            changeData.filter { it.cId == cb.cId }.forEach {
                it.todayStore -= cb.stepQty
                it.todayCount = nowContractCount
                it.changeCount--
            }
        } else {
            changeData.add(cb.copy(cType = ""))
        }

        //通过判断商店修改决定是否改色
        when {
            cb.todayStore > 0 -> view.myCommodify.setBackgroundColor(ContextCompat.getColor(this@ContractActivity, R.color.add_bg))
            cb.todayStore < 0 -> view.myCommodify.setBackgroundColor(ContextCompat.getColor(this@ContractActivity, R.color.less_bg))
            cb.todayStore == 0 -> view.myCommodify.setBackgroundColor(ContextCompat.getColor(this@ContractActivity, R.color.white))
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

    private var lastVisibleItem = 0
    override fun <T> showView(adapter: T) {
        retry.visibility = View.GONE
        this.adapter = adapter as ContractAdapter
        swipe_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.itemCount) {
                    adapter.changeMoreStatus(adapter.LOADING_MORE)
                    pullLoading(adapter)
                }
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                lastVisibleItem = layoutManager.findLastVisibleItemPosition()
            }
        })
        if (adapter.cr.detail.isEmpty()) {
            showNoMessage()
        }
        swipe_recycler.adapter = adapter
    }

    override fun errorDealWith() {
    }

    override fun pullLoading(adapter: ContractAdapter) {
        this.adapter = adapter
        if (adapter.cr.total > adapter.cr.detail.size) {
            if (isSearch) presenter.searchAllContract(adapter) else presenter.getAllContract(adapter)

        }
    }

    //根据是搜索或是得到全部去选择执行
    private fun isSearchOrGetAll() {
        if (isSearch) presenter.searchAllContract(null) else presenter.getAllContract(null)
    }

    override fun showNoMessage() {
        noMessage.visibility = android.view.View.VISIBLE
    }

    override val contractList: List<ContractBean>
        get() = changeData

    override fun updateDone() {
        if (!isSearch) saveType()

        //修改action为upd并且修改changeCount
        adapter!!.cr.detail.filter { it.changeCount != 0 }.forEach {
            it.action = "upd"
            it.changeCount = 0
        }
        removeChangeDate()
        hideLoading()
        if (isBack) finish() else {
            showPrompt(getString(R.string.saveDone))
        }
    }

    override fun removeChangeDate() {
        changeData.clear()
    }

    /**
     * 保存已被修改过的类的数据
     */
    private fun saveType() {
        val preferences = getSharedPreferences("ct", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("typeId", ctb!!.typeId)
        editor.putInt("todayGh", ctb!!.todayGh)
        editor.putInt("todayStore", ctb!!.todayStore)
        editor.putInt("todayCount", ctb!!.todayCount)
        editor.apply()
    }
}
