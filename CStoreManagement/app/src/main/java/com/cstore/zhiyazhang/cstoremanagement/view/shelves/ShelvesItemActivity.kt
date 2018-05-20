package com.cstore.zhiyazhang.cstoremanagement.view.shelves

import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ShelvesBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ShelvesItemResult
import com.cstore.zhiyazhang.cstoremanagement.presenter.shelves.ShelvesItemAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.shelves.ShelvesPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2018/5/20 11:52.
 */
class ShelvesItemActivity(override val layoutId: Int = R.layout.activity_order) : MyActivity() {
    private lateinit var d: ShelvesBean
    private val presenter = ShelvesPresenter(this)
    private lateinit var adapter: ShelvesItemAdapter


    override fun initView() {
        val data = intent.getSerializableExtra("data")
        if (data == null || data !is ShelvesBean) {
            showPrompt("冰箱数据类型错误!")
            finish()
            return
        }
        d = data
        val title = getString(R.string.inventory) + " - " + d.shelvesName
        my_toolbar.title = title
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        orderRecycler.layoutManager = MyLinearlayoutManager(this@ShelvesItemActivity, LinearLayout.VERTICAL, false)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun initClick() {
        orderretry.setOnClickListener {
            initData()
        }
    }

    override fun initData() {
        showRetry(false)
        presenter.getShelvesItem()
    }

    override fun <T> showView(aData: T) {
        if (aData !is ShelvesItemResult) {
            showPrompt("获得数据类型错误")
            errorDealWith()
            return
        }
        adapter = ShelvesItemAdapter(aData, this)
        orderRecycler.adapter = adapter
    }

    override fun getData1(): Any? {
        return d.shelvesId
    }

    override fun errorDealWith() {
        showRetry(true)
    }

    override fun showLoading() {
        orderLoading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        orderLoading.visibility = View.GONE
    }

    /**
     * 显示重试按钮
     * @param type true显示 false隐藏
     */
    private fun showRetry(type: Boolean) {
        if (type) {
            orderretry.visibility = View.VISIBLE
            orderpro.visibility = View.GONE
            orderprotext.visibility = View.GONE
        } else {
            orderretry.visibility = View.GONE
            orderpro.visibility = View.VISIBLE
            orderprotext.visibility = View.VISIBLE
        }
    }

}