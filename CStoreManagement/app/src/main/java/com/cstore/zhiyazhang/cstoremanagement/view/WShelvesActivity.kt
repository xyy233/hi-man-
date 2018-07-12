package com.cstore.zhiyazhang.cstoremanagement.view

import android.content.Intent
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.LogoBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.LogoAdapter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.view.distribution.DistributionActivity
import com.cstore.zhiyazhang.cstoremanagement.view.shelves.ShelvesActivity
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.activity_coi.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2018/5/20 11:54.
 */
class WShelvesActivity(override val layoutId: Int = R.layout.activity_coi) : MyActivity() {
    override fun initView() {
        my_toolbar.title = getString(R.string.shelves)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        coi_recycler.addItemDecoration(DividerItemDecoration(this@WShelvesActivity, DividerItemDecoration.VERTICAL))
        coi_recycler.addItemDecoration(DividerItemDecoration(this@WShelvesActivity, DividerItemDecoration.HORIZONTAL))
    }

    override fun initClick() {
    }

    override fun initData() {
        val data = ArrayList<LogoBean>()
        setData(data)
        coi_recycler.layoutManager = GridLayoutManager(this@WShelvesActivity, 3, GridLayoutManager.VERTICAL, false)
        coi_recycler.adapter = LogoAdapter(this@WShelvesActivity, data, object : ItemClickListener {
            override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {
                when (data[position].position) {
                    0 -> {
                        startActivity(Intent(this@WShelvesActivity, ShelvesActivity::class.java))
                    }
                    1 -> {
                        startActivity(Intent(this@WShelvesActivity, DistributionActivity::class.java))
//                        showPrompt("未开放")
                    }
                }
            }
        })
    }

    private fun setData(data: ArrayList<LogoBean>) {
        data.add(LogoBean(R.mipmap.w_inventory, getString(R.string.inventory), 0))
        data.add(LogoBean(R.mipmap.w_distribution, getString(R.string.distribution), 1))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

}