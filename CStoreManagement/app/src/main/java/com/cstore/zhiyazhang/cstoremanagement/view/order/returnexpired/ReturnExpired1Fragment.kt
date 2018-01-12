package com.cstore.zhiyazhang.cstoremanagement.view.order.returnexpired

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ReturnExpiredBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.returnexpired.ReturnExpiredAdapter
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.fragment_return_expired1.*

/**
 * Created by zhiya.zhang
 * on 2018/1/4 15:58.
 */
class ReturnExpired1Fragment : Fragment() {
    private val data = ArrayList<ReturnExpiredBean>()
    private lateinit var mActivity: ReturnExpiredActivity
    private val adapter = ReturnExpiredAdapter(data, 1, object : ItemClickListener {
        override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {
            //添加按钮
            mActivity.goAdd()
        }

        override fun <T> onItemEdit(data: T, position: Int) {
            if (save_expired.visibility == View.GONE) save_expired.visibility = View.VISIBLE
        }
    })

    companion object {
        private val PAGE_POSITION = "page_position"
        private val PAGE_DATA = "page_data"

        fun newInstance(position: Int, data: ArrayList<ReturnExpiredBean>): ReturnExpired1Fragment {
            val result = ReturnExpired1Fragment()
            val bundle = Bundle()
            bundle.putInt(PAGE_POSITION, position)
            bundle.putSerializable(PAGE_DATA, data)
            result.arguments = bundle
            return result
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_return_expired1, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        data.addAll(arguments.getSerializable(PAGE_DATA) as ArrayList<ReturnExpiredBean>)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //开搞adapter
        return_expired_list.layoutManager = MyLinearlayoutManager(context, LinearLayoutManager.VERTICAL, false)
        return_expired_list.adapter = adapter
        //adapter.addItems(data)
        adapter.notifyDataSetChanged()
        save_expired.setOnClickListener {
            val editData = data.filter { it.editCount != 0 }
            if (editData.isNotEmpty()) {
                //有数据
                val isChangeData = data.filter { it.editCount != 0 }
                if (isChangeData.isNotEmpty()) {
                    mActivity.presenter.saveData(isChangeData as ArrayList<ReturnExpiredBean>)
                } else {
                    mActivity.showPrompt(getString(R.string.noSaveMessage))
                }
            } else {
                save_expired.visibility = View.GONE
                mActivity.showPrompt(context.getString(R.string.noSaveMessage))
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mActivity = context as ReturnExpiredActivity
    }

    override fun onDestroy() {
        data.clear()
        super.onDestroy()
    }

    /**
     * 新添加的数据
     */
    fun addData(newData: ReturnExpiredBean) {
        if (data.none { it.itemNumber == newData.itemNumber }) {
            adapter.addItem(newData)
        } else {
            data.filter { it.itemNumber == newData.itemNumber }
                    .forEach {
                        it.plnRtnQTY++
                        it.editCount++
                    }
            if (save_expired.visibility == View.GONE) save_expired.visibility = View.VISIBLE
            adapter.notifyDataSetChanged()
        }
    }

    fun updateDone() {
        data.filter { it.editCount != 0 }.forEach { it.editCount = 0 }
    }
}