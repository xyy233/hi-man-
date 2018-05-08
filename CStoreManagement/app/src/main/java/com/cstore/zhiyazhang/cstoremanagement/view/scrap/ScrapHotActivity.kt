package com.cstore.zhiyazhang.cstoremanagement.view.scrap

import android.content.Intent
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapContractBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapHotBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.scrap.ScrapHotAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.scrap.ScrapHotPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.activity_scrap_hot.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2017/9/1 11:48.
 */
class ScrapHotActivity(override val layoutId: Int = R.layout.activity_scrap_hot) : MyActivity() {

    val presenter = ScrapHotPresenter(this, this)

    override fun initView() {
        my_toolbar.title = getString(R.string.scrap2)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        val layoutManager = MyLinearlayoutManager(this, LinearLayoutManager.VERTICAL, false)
        scrap_hot_recycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        scrap_hot_recycler.layoutManager = layoutManager
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun onBackPressed() {
        setResult(1, Intent())
        finish()
    }

    override fun initClick() {
        scrap_hot_refresh.setOnRefreshListener {
            if (scrap_hot_refresh.isEnabled) {
                presenter.getScrapHotMid()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        scrap_hot_refresh.autoRefresh()
    }

    override fun initData() {
        scrap_hot_refresh.autoRefresh()
    }

    override fun <T> requestSuccess(rData: T) {
        rData as ArrayList<*>
        if (rData[0] is ScrapContractBean) {
            return
        }
        rData as ArrayList<ScrapHotBean>
        val adapter = ScrapHotAdapter(rData, object : ItemClickListener {
            override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {
                val i = Intent(this@ScrapHotActivity, ScrapHotItemActivity::class.java)
                i.putExtra("hotMid", rData)
                i.putExtra("position", position)
                startActivity(i)
            }
        })
        scrap_hot_recycler.adapter = adapter
        scrap_statistics.visibility = View.VISIBLE
        var allCount = 0
        var allPrice = 0.0
        rData.forEach {
            allCount += it.sCount
            allPrice += it.sPrice
        }
        val a = getString(R.string.all_scrap_hot_count) + allCount.toString()
        val b = getString(R.string.all_scrap_hot_price) + allPrice.toFloat().toString()
        scrap_count_statistics.text = a
        scrap_price_statistics.text = b
    }

    override fun showLoading() {
        scrap_hot_refresh.isRefreshing = true
        scrap_hot_refresh.isEnabled = false
    }

    override fun hideLoading() {
        scrap_hot_refresh.isRefreshing = false
        scrap_hot_refresh.isEnabled = true
    }

    override fun errorDealWith() {
        scrap_statistics.visibility = View.GONE
    }

}