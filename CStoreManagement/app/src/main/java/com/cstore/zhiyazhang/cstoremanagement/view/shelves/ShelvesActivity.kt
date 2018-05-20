package com.cstore.zhiyazhang.cstoremanagement.view.shelves

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ShelvesBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ShelvesResult
import com.cstore.zhiyazhang.cstoremanagement.presenter.shelves.ShelvesAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.shelves.ShelvesPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2018/5/20 11:44.
 */
class ShelvesActivity(override val layoutId: Int = R.layout.activity_order) : MyActivity() {
    private val presenter = ShelvesPresenter(this)
    private lateinit var adapter: ShelvesAdapter

    override fun initView() {
        my_toolbar.title = getString(R.string.shelves)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        orderRecycler.layoutManager = MyLinearlayoutManager(this@ShelvesActivity, LinearLayout.VERTICAL, false)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun initClick() {
        orderretry.setOnClickListener { initData() }
    }

    override fun initData() {
        showRetry(false)
        presenter.getShelves()
    }

    override fun <T> showView(aData: T) {
        if (aData !is ShelvesResult) {
            showPrompt("获得数据类型错误")
            errorDealWith()
            return
        }
        adapter = ShelvesAdapter(aData as ShelvesResult, object : ItemClickListener {
            override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {
            }

            override fun <T> onItemEdit(data: T, position: Int) {

                val i = Intent(this@ShelvesActivity, ShelvesItemActivity::class.java)
                if (data is ShelvesBean) {
                    i.putExtra("data", data)
                } else {
                    val sb = ShelvesBean("%", getString(R.string.all), "")
                    i.putExtra("data", sb)
                }
                startActivity(i)
            }
        })
        orderRecycler.adapter=adapter
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