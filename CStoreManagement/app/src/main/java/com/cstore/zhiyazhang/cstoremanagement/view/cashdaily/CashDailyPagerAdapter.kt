package com.cstore.zhiyazhang.cstoremanagement.view.cashdaily

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import java.util.*

/**
 * Created by zhiya.zhang
 * on 2017/9/4 14:38.
 */

class CashDailyPagerAdapter(fm: FragmentManager, private val tabFragments: ArrayList<CashDailyFragment>, private val tabTitle: ArrayList<String>) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return tabFragments[position]
    }

    override fun getCount(): Int {
        return tabFragments.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabTitle[position]
    }
}
