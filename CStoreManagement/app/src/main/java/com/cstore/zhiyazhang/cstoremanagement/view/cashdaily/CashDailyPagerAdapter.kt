package com.cstore.zhiyazhang.cstoremanagement.view.cashdaily

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter

/**
 * Created by zhiya.zhang
 * on 2017/9/4 14:38.
 */

class CashDailyPagerAdapter(fm: FragmentManager, private val tabFragments: ArrayList<CashDailyFragment>, private val tabTitle: ArrayList<String>) : FragmentPagerAdapter(fm) {

    private var time:Long=0


    override fun getItem(position: Int): Fragment {
        return tabFragments[position]
    }

    override fun getItemPosition(`object`: Any?): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun getCount(): Int {
        return tabFragments.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)+time
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabTitle[position]
    }

    fun setFragments(data:ArrayList<CashDailyFragment>){
        time = System.currentTimeMillis()
        tabFragments.clear()
        tabFragments.addAll(data)
        notifyDataSetChanged()
    }
}
