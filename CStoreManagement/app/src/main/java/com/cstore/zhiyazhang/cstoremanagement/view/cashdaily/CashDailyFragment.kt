package com.cstore.zhiyazhang.cstoremanagement.view.cashdaily

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.CashDailyBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.cashdaily.CashDailyAdapter
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.fragment_cash_daily.*

/**
 * Created by zhiya.zhang
 * on 2017/9/4 14:00.
 */
class CashDailyFragment : Fragment() {

    private val data=ArrayList<CashDailyBean>()
    private var date=""
    private var mActivity: CashDailyActivity?=null

    companion object{
        private val PAGE_POSITION="page_position"
        private val PAGE_DATA="page_data"
        private val PAGE_DATE="page_date"

        fun newInstance(position:Int, data:ArrayList<CashDailyBean>, date:String):CashDailyFragment{
           val result=CashDailyFragment()
            val bundle=Bundle()
            bundle.putInt(PAGE_POSITION, position)
            bundle.putSerializable(PAGE_DATA,data)
            bundle.putString(PAGE_DATE,date)
            result.arguments=bundle
            return result
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_cash_daily,container,false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //获得类型和数据
        data.addAll(arguments!!.getSerializable(PAGE_DATA) as ArrayList<CashDailyBean>)
        date= arguments!!.getString(PAGE_DATE)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        cash_recycler.layoutManager=MyLinearlayoutManager(activity!!,LinearLayoutManager.VERTICAL,false)
        cash_recycler.adapter=CashDailyAdapter(data, object : ItemClickListener {
            override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {
                view as CashDailyAdapter.ViewHolder
                val itemNumView=view.itemNum
                mActivity!!.updateData(itemNumView,data[position])
            }
        },date)
    }

    override fun onDestroy() {
        data.clear()
        super.onDestroy()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mActivity=context as CashDailyActivity
    }
}