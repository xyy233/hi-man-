package com.cstore.zhiyazhang.cstoremanagement.view.scrap

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import kotlinx.android.synthetic.main.activity_scrap_select.*
import kotlinx.android.synthetic.main.loading_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2017/7/10 10:17.
 */
class ScrapSelectActivity(override val layoutId: Int = R.layout.activity_scrap_select) : MyActivity(), GenericView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        my_toolbar.title = getString(R.string.scrap)
        setSupportActionBar(my_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        select_recycler.layoutManager = MyLinearlayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    override fun <T> requestSuccess(objects: T) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loading.visibility = View.GONE
    }

    override fun <T> showView(adapter: T) {
    }

    override fun errorDealWith() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}