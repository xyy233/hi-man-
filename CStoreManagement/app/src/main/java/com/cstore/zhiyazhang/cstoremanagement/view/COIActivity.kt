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
import com.cstore.zhiyazhang.cstoremanagement.view.acceptance.PurchaseAcceptanceActivity
import com.cstore.zhiyazhang.cstoremanagement.view.cashdaily.CashDailyActivity
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.activity_coi.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2017/9/15 9:27.
 */
class COIActivity(override val layoutId: Int = R.layout.activity_coi) : MyActivity() {
    override fun initView() {
        my_toolbar.title = getString(R.string.coi)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        coi_recycler.addItemDecoration(DividerItemDecoration(this@COIActivity, DividerItemDecoration.VERTICAL))
        coi_recycler.addItemDecoration(DividerItemDecoration(this@COIActivity, DividerItemDecoration.HORIZONTAL))
    }

    override fun initClick() {

    }

    override fun initData() {
        val data=ArrayList<LogoBean>()
        setData(data)
        coi_recycler.layoutManager = GridLayoutManager(this@COIActivity, 3, GridLayoutManager.VERTICAL, false)
        coi_recycler.adapter=LogoAdapter(this@COIActivity, data, object : ItemClickListener {
            override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {
                when(data[position].position){
                    0->{startActivity(Intent(this@COIActivity, CashDailyActivity::class.java))}
                    1->{val intent=Intent(this@COIActivity, PurchaseAcceptanceActivity::class.java)
                        intent.putExtra("type",1)
                        startActivity(intent)}
                    2->{val intent=Intent(this@COIActivity, PurchaseAcceptanceActivity::class.java)
                        intent.putExtra("type",2)
                        startActivity(intent)}
                }
            }
        })
    }

    private fun setData(data: ArrayList<LogoBean>) {
        data.add(LogoBean(R.mipmap.ic_cashdaily,getString(R.string.cash_daily),0))
        data.add(LogoBean(R.mipmap.ic_acceptance,getString(R.string.purchase_acceptance),1))
        data.add(LogoBean(R.mipmap.ic_return_acceptance,getString(R.string.return_purchase_acceptance),2))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }
}