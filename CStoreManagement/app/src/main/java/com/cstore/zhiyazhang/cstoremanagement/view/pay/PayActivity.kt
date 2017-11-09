package com.cstore.zhiyazhang.cstoremanagement.view.pay

import android.graphics.Bitmap
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.presenter.pay.PayAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.pay.PayPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyDividerItemDecoration
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.uuzuche.lib_zxing.activity.CaptureFragment
import com.uuzuche.lib_zxing.activity.CodeUtils
import kotlinx.android.synthetic.main.activity_pay.*
import kotlinx.android.synthetic.main.loading_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2017/11/8 16:11.
 */
class PayActivity(override val layoutId: Int = R.layout.activity_pay) : MyActivity(), GenericView {

    val presenter = PayPresenter(this)

    val adapter = PayAdapter(ArrayList())

    private val analyzeCallback= object : CodeUtils.AnalyzeCallback {
        override fun onAnalyzeSuccess(mBitmap: Bitmap?, result: String) {
            showPrompt(result)
        }

        override fun onAnalyzeFailed() {
            showPrompt(getString(com.cstore.zhiyazhang.cstoremanagement.R.string.noMessage))
        }

    }

    override fun initView() {
        my_toolbar.title = "收款"
        toolbar_btn.text = "清空"
        toolbar_btn.visibility = View.VISIBLE
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        pay_recycler.layoutManager = LinearLayoutManager(this@PayActivity, LinearLayoutManager.VERTICAL, false)
        val dividerItemDecoration = MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        dividerItemDecoration.setDivider(R.drawable.divider_bg)
        pay_recycler.addItemDecoration(dividerItemDecoration)
        pay_recycler.itemAnimator = DefaultItemAnimator()
        pay_recycler.adapter = adapter

        val captureFragment=CaptureFragment()
        CodeUtils.setFragmentArgs(captureFragment,R.layout.pay_camera)
        captureFragment.analyzeCallback=analyzeCallback
        supportFragmentManager.beginTransaction().replace(R.id.fl_my_container,captureFragment).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun initClick() {
        toolbar_btn.setOnClickListener {
            adapter.removeItem()
        }
    }

    override fun initData() {

    }

    override fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loading.visibility = View.GONE
    }

}