package com.cstore.zhiyazhang.cstoremanagement.view.inverror

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import com.cstore.zhiyazhang.cstoremanagement.bean.InvErrorBean

/**
 * Created by zhiya.zhang
 * on 2018/2/3 12:01.
 */
class InvErrorPagerAdapter(private val fm: FragmentManager, private val tabTitle: ArrayList<String>) : FragmentPagerAdapter(fm) {

    private var time: Long = 0
    val result = ArrayList<InvErrorFragment>()

    override fun getItem(position: Int): Fragment {
        return result[position]
    }

    override fun getItemPosition(`object`: Any): Int {
        super.getItemPosition(`object`)
        return PagerAdapter.POSITION_NONE
    }

    override fun getCount(): Int {
        return result.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position) + time
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabTitle[position]
    }

    fun setData(data: ArrayList<InvErrorBean>) {
        if (data.size != 0) {
            result.add(InvErrorFragment.newInstance(data.filter { it.flag == 1 } as ArrayList<InvErrorBean>, 0))
            result.add(InvErrorFragment.newInstance(data.filter { it.flag == 2 } as ArrayList<InvErrorBean>, 1))
            result.add(InvErrorFragment.newInstance(data.filter { it.flag == 3 } as ArrayList<InvErrorBean>, 2))
            result.add(InvErrorFragment.newInstance(data.filter { it.flag == 4 } as ArrayList<InvErrorBean>, 3))
            result.add(InvErrorFragment.newInstance(data.filter { it.flag == 5 } as ArrayList<InvErrorBean>, 4))
            result.add(InvErrorFragment.newInstance(data.filter { it.flag == 6 } as ArrayList<InvErrorBean>, 5))
            notifyDataSetChanged()
        }
    }

    /**
     * 滚动到指定地址，position是第几位，tab是第几个fragment
     */
    fun setScroll(position: Int, tab: Int) {
        if (result.size != 0) {
            result[tab].scrollReccycler(position)
        }
    }

    fun clearData() {
        result.forEach { fm.beginTransaction().remove(it).commit() }
        result.clear()
        notifyDataSetChanged()
    }
}