package com.cstore.zhiyazhang.cstoremanagement.view.cashdaily

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cstore.zhiyazhang.cstoremanagement.R

/**
 * Created by zhiya.zhang
 * on 2017/9/4 14:00.
 */
class CashDailyFragment : Fragment() {

    companion object{
        private val PAGE_POSITION="page_position"

        fun newInstance(position:Int):CashDailyFragment{
           val result=CashDailyFragment()
            val bundle=Bundle()
            bundle.putInt(PAGE_POSITION, position)
            result.arguments=bundle
            return result
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_cash_daily,container,false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //获得类型
        val type= resources.getStringArray(R.array.cashDailyTags)[arguments.getInt(PAGE_POSITION)]
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    /**
     * Activity 被销毁的时候，FragmentManager 里的 Fragment 也不会得到保留。
     */
    override fun onSaveInstanceState(outState: Bundle?) {
    }
}