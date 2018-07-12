package com.cstore.zhiyazhang.cstoremanagement.view.inverror

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.InvErrorBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.inverror.InvErrorAdapter
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.fragment_cash_daily.*

/**
 * Created by zhiya.zhang
 * on 2018/2/2 17:21.
 */
class InvErrorFragment : Fragment() {
    private val data = ArrayList<InvErrorBean>()
    private lateinit var view: GenericView
    private var fragmentPosition = 0

    companion object {
        private val PAGE_DATA = "page_data"
        private val PAGE_POSITION = "page_position"

        fun newInstance(data: ArrayList<InvErrorBean>, position: Int): InvErrorFragment {
            val result = InvErrorFragment()
            val bundler = Bundle()
            bundler.putSerializable(PAGE_DATA, data)
            bundler.putInt(PAGE_POSITION, position)
            result.arguments = bundler
            return result
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cash_daily, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        data.addAll(arguments!!.getSerializable(PAGE_DATA) as ArrayList<InvErrorBean>)
        fragmentPosition = arguments!!.getInt(PAGE_POSITION)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        cash_recycler.layoutManager = MyLinearlayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        cash_recycler.adapter = InvErrorAdapter(data, object : ItemClickListener {
            override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {}
            override fun <T> onItemEdit(data: T, position: Int) {
                data as InvErrorBean
                view.requestSuccess(data.pluId)
                view.requestSuccess2(position)
                view.updateDone(fragmentPosition)
            }
        })
    }

    fun scrollReccycler(position: Int) {
        cash_recycler.scrollToPosition(position)
    }

    override fun onDestroy() {
        data.clear()
        super.onDestroy()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        view = context as InvErrorActivity
    }
}