package com.cstore.zhiyazhang.cstoremanagement.view.personnel

import android.content.Intent
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.LogoBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.LogoAdapter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import com.cstore.zhiyazhang.cstoremanagement.view.personnel.checkin.CheckInActivity
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.activity_in_stock.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2017/10/11 14:36.
 */
class PersonnelActivity(override val layoutId: Int = R.layout.activity_in_stock) :MyActivity(){
    override fun initView() {
        my_toolbar.title = getString(R.string.in_stock)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        inv_recycler.addItemDecoration(DividerItemDecoration(this@PersonnelActivity, DividerItemDecoration.VERTICAL))
        inv_recycler.addItemDecoration(DividerItemDecoration(this@PersonnelActivity, DividerItemDecoration.HORIZONTAL))
    }

    override fun initClick() {
    }

    override fun initData() {
        val data=ArrayList<LogoBean>()
        setData(data)
        inv_recycler.layoutManager = GridLayoutManager(this@PersonnelActivity, 3, GridLayoutManager.VERTICAL, false)
        inv_recycler.adapter= LogoAdapter(this@PersonnelActivity, data, object : ItemClickListener {
            override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {
                when(data[position].position){
                    0->{startActivity(Intent(this@PersonnelActivity, CheckInActivity::class.java))}
                    1->{MyToast.getShortToast(getString(R.string.in_development))}
                    2->{MyToast.getShortToast(getString(R.string.in_development))}
                    3->{MyToast.getShortToast(getString(R.string.in_development))}
                }
            }
        })
    }

    private fun setData(data: ArrayList<LogoBean>) {
        data.add(LogoBean(R.mipmap.ic_check_in,getString(R.string.check_in),0))
        data.add(LogoBean(R.mipmap.ic_attendance,getString(R.string.attendance),1))
        data.add(LogoBean(R.mipmap.ic_attendance_record,getString(R.string.attendance_record),2))
        data.add(LogoBean(R.mipmap.ic_scheduling,getString(R.string.scheduling),3))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

}