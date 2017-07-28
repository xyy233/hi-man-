package com.cstore.zhiyazhang.cstoremanagement.view.order.category

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.CategoryItemBean
import com.cstore.zhiyazhang.cstoremanagement.bean.OrderCategoryBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.ordercategory.CategoryItemAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.ordercategory.CategoryItemPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.CategoryItemView
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import kotlinx.android.synthetic.main.activity_contract.*

/**
 * Created by zhiya.zhang
 * on 2017/7/26 14:23.
 */
class CategoryItemActivity(override val layoutId: Int = R.layout.activity_contract) : MyActivity(), GenericView, CategoryItemView {
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

    private var isBack = false
    private val changeData = ArrayList<CategoryItemBean>()//修改的数据
    private val layoutManager = MyLinearlayoutManager(this@CategoryItemActivity, LinearLayoutManager.VERTICAL, false)
    private var adapter: CategoryItemAdapter? = null
    private val presenter = CategoryItemPresenter(this, this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initSort()
        my_swipe.setOnRefreshListener {
            if (my_swipe.isEnabled) getData()
        }
        my_swipe.setProgressViewEndTarget(true, 200)
        my_swipe.autoRefresh()
    }

    private fun initSort() {
        val sortAdapter = ArrayAdapter(this, R.layout.custom_spiner_text_item, resources.getStringArray(R.array.mySort))
        sortAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        mySpinner.adapter = sortAdapter
        mySpinner.setSelection(0, false)
        mySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                getData()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun getData() {
        when (whereIsIt) {
            "category" -> {
                adapter = null
                presenter.getAllItem(null)
            }
        }
    }

    private fun initView() {
        toolbar.setNavigationIcon(R.drawable.ic_action_back)
        when (whereIsIt) {
            "category" -> {
                done.setOnClickListener {
                    toolbar.title = category.categoryName
                    changeData.removeAll(changeData.filter { it.orderQTY == 0 })
                    if (changeData.size == 0) {
                        showPrompt("无修改数据")
                        return@setOnClickListener
                    }
                    presenter.updateAllCategory()
                }
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
        retry.setOnClickListener { my_swipe.autoRefresh() }
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
        changeData.removeAll(changeData.filter { it.orderQTY == 0 })
        if (changeData.size != 0) AlertDialog.Builder(this@CategoryItemActivity)
                .setTitle("提示")
                .setMessage("您修改的订量尚未确认，是否放弃修改？")
                .setPositiveButton("保存", { _, _ ->
                    isBack = true
                    presenter.updateAllCategory()
                })
                .setNegativeButton("放弃", { _, _ ->
                    finish()
                })
                .show()
        else
            finish()
    }

    override val category: OrderCategoryBean
        get() = intent.getSerializableExtra("category") as OrderCategoryBean

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

    override val whereIsIt: String
        get() = intent.getStringExtra("whereIsIt")

    override val categoryList: ArrayList<CategoryItemBean>
        get() = changeData

    override fun updateDone() {
        when (whereIsIt) {
            "category" -> {
                saveCategory()
                adapter!!.data.filter { it.changeCount != 0 }.forEach {
                    it.changeCount = 0
                }
                changeData.clear()
                if (isBack) finish() else {
                    my_swipe.autoRefresh()
                    showPrompt(getString(R.string.saveDone))
                }
            }
        }
    }

    private fun saveCategory() {
        val preferences = getSharedPreferences("cg", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("categoryId", category.categoryId)
        editor.putInt("ordSku", category.ordSku)
        editor.putInt("ordPrice", category.ordPrice)
        editor.apply()
    }

    override fun <T> requestSuccess(objects: T) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showLoading() {
        my_swipe.isRefreshing = true
        layoutManager.setScrollEnabled(false)
    }

    override fun hideLoading() {
        my_swipe.isRefreshing = false
        layoutManager.setScrollEnabled(true)
    }

    override fun <T> showView(adapter: T) {
        retry.visibility = View.GONE
        this.adapter = adapter as CategoryItemAdapter
        if (adapter.data.isEmpty()) errorDealWith()
        swipe_recycler.adapter = adapter
    }

    override fun errorDealWith() {
        noMessage.visibility = android.view.View.VISIBLE
    }

    override fun touchAdd(cb: CategoryItemBean, event: MotionEvent, position: Int) {
        val adapterView: CategoryItemAdapter.ViewHolder = swipe_recycler.findViewHolderForAdapterPosition(position) as CategoryItemAdapter.ViewHolder
        onTouchChange(1, event.action, adapterView, cb)
    }

    override fun touchLess(cb: CategoryItemBean, event: MotionEvent, position: Int) {
        val adapterView: CategoryItemAdapter.ViewHolder = swipe_recycler.findViewHolderForAdapterPosition(position) as CategoryItemAdapter.ViewHolder
        onTouchChange(0, event.action, adapterView, cb)
    }

    var mt: Thread? = null
    var pt: Thread? = null
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
                MotionEvent.ACTION_UP -> {
                    if (mt != null)
                        isOnLongClick = false
                    runOrStopEdit()
                    mt = null
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun lessCount(view: CategoryItemAdapter.ViewHolder, cb: CategoryItemBean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}