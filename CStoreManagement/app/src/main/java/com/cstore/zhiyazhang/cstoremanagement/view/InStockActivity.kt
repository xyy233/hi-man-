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
import com.cstore.zhiyazhang.cstoremanagement.view.inquiry.UnitInquiryActivity
import com.cstore.zhiyazhang.cstoremanagement.view.inverror.InvErrorActivity
import com.cstore.zhiyazhang.cstoremanagement.view.transfer.TransferActivity
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.activity_in_stock.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2017/8/25 16:59.
 */
class InStockActivity(override val layoutId: Int = R.layout.activity_in_stock) : MyActivity() {
    override fun initView() {
        my_toolbar.title = getString(R.string.in_stock)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        inv_recycler.addItemDecoration(DividerItemDecoration(this@InStockActivity, DividerItemDecoration.VERTICAL))
        inv_recycler.addItemDecoration(DividerItemDecoration(this@InStockActivity, DividerItemDecoration.HORIZONTAL))
    }

    override fun initClick() {
    }

    override fun initData() {
        val data = ArrayList<LogoBean>()
        setData(data)
        inv_recycler.layoutManager = GridLayoutManager(this@InStockActivity, 3, GridLayoutManager.VERTICAL, false)
        inv_recycler.adapter = LogoAdapter(this@InStockActivity, data, object : ItemClickListener {
            override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {
                when (data[position].position) {
                    0 -> {
                        startActivity(Intent(this@InStockActivity, UnitInquiryActivity::class.java))
                    }
                    1 -> {
                        startActivity(Intent(this@InStockActivity, InvErrorActivity::class.java))
                    }
                    /*2 -> {
                        MyToast.getShortToast(getString(R.string.in_development))
                    }*/
                    2 -> {
                        startActivity(Intent(this@InStockActivity, TransferActivity::class.java))
                    }
                }
            }
        })
    }

    private fun setData(data: ArrayList<LogoBean>) {
        data.add(LogoBean(R.mipmap.ic_unit_inquiry, getString(R.string.unitSearch), 0))
        data.add(LogoBean(R.mipmap.ic_inv_error, getString(R.string.inv_error), 1))
        //data.add(LogoBean(R.mipmap.ic_inventory, getString(R.string.point), 2))
        data.add(LogoBean(R.mipmap.ic_transfer, getString(R.string.transfer), 2))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }
}