package com.cstore.zhiyazhang.cstoremanagement.view.inverror

import android.content.Intent
import android.view.MenuItem
import android.view.View
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.presenter.inverror.InvErrorPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.ZoomOutPageTransformer
import com.cstore.zhiyazhang.cstoremanagement.view.inquiry.UnitInquiryActivity
import kotlinx.android.synthetic.main.activity_cashdaily.*
import kotlinx.android.synthetic.main.loading_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2018/2/2 17:27.
 */
class InvErrorActivity(override val layoutId: Int = R.layout.activity_cashdaily) : MyActivity() {
    private val tabIndicators = ArrayList<String>()
    private val presenter = InvErrorPresenter(this)
    private lateinit var adapter: InvErrorPagerAdapter

    override fun initView() {
        my_toolbar.title = getString(R.string.inv_error)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        resources.getStringArray(R.array.errorInv).forEach { tabIndicators.add(it) }
        cash_daily_tab.setupWithViewPager(cash_daily_viewpager)
        cash_daily_viewpager.setPageTransformer(true, ZoomOutPageTransformer())
        adapter = InvErrorPagerAdapter(supportFragmentManager, ArrayList(), tabIndicators)
        cash_daily_viewpager.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun initClick() {
        loading.setOnClickListener {
            showPrompt(getString(R.string.wait_loading))
        }
        loading_retry.setOnClickListener {
            loading_retry.visibility = View.GONE
            loading_text.visibility = View.VISIBLE
            loading_progress.visibility = View.VISIBLE
            presenter.getAllFragment()
        }
    }

    override fun initData() {
        presenter.getAllFragment()
    }

    override fun showLoading() {
        if (loading_progress.visibility == View.GONE) loading_progress.visibility = View.VISIBLE
        if (loading_text.visibility == View.GONE) loading_text.visibility = View.VISIBLE
        if (loading_retry.visibility == View.VISIBLE) loading_retry.visibility = View.GONE
        loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loading.visibility = View.GONE
    }

    override fun <T> showView(aData: T) {
        aData as ArrayList<InvErrorFragment>
        adapter.setFragments(aData)
        cash_daily_viewpager.currentItem = 0
    }

    override fun getData1(): Any? {
        return this@InvErrorActivity
    }

    override fun <T> requestSuccess(rData: T) {
        val i = Intent(this@InvErrorActivity, UnitInquiryActivity::class.java)
        i.putExtra("pluId", rData.toString())
        i.putExtra("flag", 1)
        startActivity(i)
        finish()
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data!=null){
            when (resultCode) {
                1 -> {
                    presenter.getAllFragment()
                }
            }
        }
    }*/

}