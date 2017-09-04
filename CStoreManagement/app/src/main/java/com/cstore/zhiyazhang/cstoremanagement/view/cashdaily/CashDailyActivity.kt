package com.cstore.zhiyazhang.cstoremanagement.view.cashdaily

import android.view.MenuItem
import android.view.View
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import kotlinx.android.synthetic.main.activity_cashdaily.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2017/9/4 11:29.
 */
class CashDailyActivity(override val layoutId: Int=R.layout.activity_cashdaily) : MyActivity(), GenericView {
    private val tabFragments = ArrayList<CashDailyFragment>()
    private val tabIndicators = ArrayList<String>()

    override fun initView() {
        my_toolbar.title=getString(R.string.cash_daily)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        toolbar_time.visibility=View.VISIBLE
        toolbar_time.text="2017-09-04"
        setSupportActionBar(my_toolbar)
        val tags= resources.getStringArray(R.array.cashDailyTags)
        //设置title
        tags.forEach { tabIndicators.add(it) }
        //设置fragment
        (0..tags.size-1).mapTo(tabFragments) { CashDailyFragment.newInstance(it) }
        //设置viewPager的adapter，把fragment放入viewpager
        cash_daily_viewpager.adapter=CashDailyPagerAdapter(supportFragmentManager,tabFragments,tabIndicators)
        //设置tab和viewPager联动
        cash_daily_tab.setupWithViewPager(cash_daily_viewpager)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->onBackPressed()
        }
        return true
    }

    override fun initClick() {
    }

    override fun initData() {

    }

    override fun <T> requestSuccess(rData: T) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <T> showView(adapter: T) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun errorDealWith() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}