package com.cstore.zhiyazhang.cstoremanagement.view.inventory

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter

/**
 * Created by zhiya.zhang
 * on 2017/9/27 14:17.
 */
class InventoryAdjustmentPagerAdapter(private val fm:FragmentManager, private val tabFragments:ArrayList<InventoryAdjustmentFragment>, private val tabTitle:ArrayList<String>): FragmentPagerAdapter(fm){

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

    /**
     * clear引用的list自然就被情况，往里添加也会添加，只需要刷新就好
     */
    fun refresh(){
        time=System.currentTimeMillis()
        notifyDataSetChanged()
    }
}