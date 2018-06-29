package com.cstore.zhiyazhang.cstoremanagement.view.order.returnexpired

import android.content.Intent
import android.support.design.widget.TabLayout.MODE_FIXED
import android.support.v4.app.Fragment
import android.view.MenuItem
import android.view.View
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ReturnExpiredBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.returnexpired.ReturnExpiredPagerAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.returnexpired.ReturnExpiredPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.ZoomOutPageTransformer
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.cstore.zhiyazhang.cstoremanagement.view.order.contract.ContractSearchActivity
import kotlinx.android.synthetic.main.activity_cashdaily.*
import kotlinx.android.synthetic.main.layout_date.*
import kotlinx.android.synthetic.main.loading_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2018/1/3 11:27.
 *
 * 老东西不写注释了，要看注释去 CashDailyActivity
 */
class ReturnExpiredActivity(override val layoutId: Int = R.layout.activity_cashdaily) : GenericView, MyActivity() {

    private val tabIndicators = ArrayList<String>()
    val presenter = ReturnExpiredPresenter(this)
    private lateinit var fragment1: ReturnExpired1Fragment
    private lateinit var fragment2: ReturnExpired2Fragment

    override fun initView() {
        my_toolbar.title = getString(R.string.return_expired)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        date_util.visibility = View.VISIBLE
        time_flag.visibility = View.GONE
        MyTimeUtil.setTextViewDate(date_util, CStoreCalendar.getCurrentDate(2))
        setSupportActionBar(my_toolbar)
        tabIndicators.add(getString(R.string.return_expired1))
        tabIndicators.add(getString(R.string.return_expired2))
        cash_daily_tab.tabMode = MODE_FIXED
        cash_daily_tab.setupWithViewPager(cash_daily_viewpager)
        cash_daily_viewpager.setPageTransformer(true, ZoomOutPageTransformer())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun initClick() {

        loading.setOnClickListener {
            showPrompt(getString(R.string.wait_loading))
        }

        //时间不允许修改
        loading_retry.setOnClickListener {
            loading_retry.visibility = View.GONE
            loading_text.visibility = View.VISIBLE
            loading_progress.visibility = View.VISIBLE
            if (cash_daily_tab.selectedTabPosition == 0) {
                presenter.getAll()
            } else {
                presenter.getDateAll(getDate1(), getDate2(), getCheckIndex())
            }
        }
    }

    private fun getDate1(): String {
        return fragment2.getDate1()
    }

    private fun getDate2(): String {
        return fragment2.getDate2()
    }

    private fun getCheckIndex(): Int {
        return fragment2.getChesk()
    }

    override fun initData() {
        when {
            cash_daily_tab.selectedTabPosition == -1 -> presenter.getAll()
            cash_daily_tab.selectedTabPosition == 0 -> presenter.getAll()
            else -> presenter.getDateAll(getDate1(), getDate2(), getCheckIndex())
        }
    }

    //获得今天的退货数据:预约退货（为确认）
    override fun <T> showView(aData: T) {
        fragment1 = ReturnExpired1Fragment.newInstance(1, aData as ArrayList<ReturnExpiredBean>)
        fragment2 = ReturnExpired2Fragment.newInstance(2)
        val fragmentList = ArrayList<Fragment>()
        fragmentList.add(fragment1)
        fragmentList.add(fragment2)
        if (cash_daily_viewpager.adapter == null) {
            val fragmentAdapter = ReturnExpiredPagerAdapter(supportFragmentManager, fragmentList, tabIndicators)
            cash_daily_viewpager.adapter = fragmentAdapter
        } else {
            (cash_daily_viewpager.adapter as ReturnExpiredPagerAdapter).setFragments(fragmentList)
            cash_daily_viewpager.currentItem = 0
        }
    }

    override fun <T> requestSuccess2(rData: T) {
        fragment2.addItems(rData as ArrayList<ReturnExpiredBean>)
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

    /**
     * 开始添加新商品
     */
    fun goAdd() {
        val intent = Intent(this@ReturnExpiredActivity, ContractSearchActivity::class.java)
        intent.putExtra(ContractSearchActivity.WHERE_IS_IT, ContractSearchActivity.RTN)
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            when (requestCode) {
                0 -> {
                    presenter.searchExpiredCommodity(data.getStringExtra("message"))
                }
            }
        }
    }

    override fun <T> requestSuccess(rData: T) {
        rData as ArrayList<ReturnExpiredBean>
        if (rData.isNotEmpty()) {
            val rb = rData[0]
            if (rb.vendorYN != "Y") {
                showPrompt("商品:${rb.pluName} 不可厂退！")
                return
            }
            if (rb.stopThCode != null) {
                showPrompt("商品:${rb.pluName} 停售商品不可退货，停售档期:${rb.stopThCode}")
                return
            }
            if (rb.outThCode != null) {
                showPrompt("商品:${rb.pluName} 退货档期:${rb.outThCode}，不可退货!")
                return
            }
            if (rb.vendorId != "00000000000099999100" && rb.vendorId != "00000000000099999701") {
                showPrompt("商品:${rb.pluName} 不属于常低温配送商品，请在一般预约退货作业里退货！")
                return
            }
            fragment1.addData(rb)
        }
    }

    override fun <T> updateDone(uData: T) {
        fragment1.updateDone()
        showPrompt(getString(R.string.saveDone))
    }

}