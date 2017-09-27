package com.cstore.zhiyazhang.cstoremanagement.view.inventory

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.AdjustmentBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.adjustment.AdjustmentAdapter
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.fragment_adjustment.*
import kotlinx.android.synthetic.main.layout_search_line.*

/**
 * Created by zhiya.zhang
 * on 2017/9/25 17:17.
 */
class InventoryAdjustmentFragment : Fragment(){
    private var mActivity:InventoryAdjustmentActivity?=null
    private var date=""
    private val data=ArrayList<AdjustmentBean>()
    private var type=0
    var adapter:AdjustmentAdapter?=null
    var saveBtn:Button?=null

    companion object {
        private val PAGE_TYPE="page_type"
        private val PAGE_DATA="page_data"
        private val PAGE_DATE="page_date"

        fun newInstance(type:Int, data:ArrayList<AdjustmentBean>, date:String): InventoryAdjustmentFragment {
            val result= InventoryAdjustmentFragment()
            val bundle=Bundle()
            bundle.putInt(PAGE_TYPE, type)
            bundle.putSerializable(PAGE_DATA,data)
            bundle.putString(PAGE_DATE,date)
            result.arguments=bundle
            return result
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_adjustment,container,false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        data.addAll(arguments.getSerializable(PAGE_DATA) as ArrayList<AdjustmentBean>)
        date=arguments.getString(PAGE_DATE)
        type=arguments.getInt(PAGE_TYPE)
        saveBtn=view!!.findViewById(R.id.adjustment_save)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adjustment_recycler.layoutManager=MyLinearlayoutManager(mActivity as Context,LinearLayoutManager.VERTICAL,false)
        //在创建的时候传入的data就会相应type==1传该有的值type==2传空list
        adapter=AdjustmentAdapter(type, date, data, object : ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                mActivity!!.updateDate(view, data[position])
            }
        })
        adjustment_recycler.adapter=adapter
        //如果是搜索还要显示搜索栏
        if (type==2){
            search_bar.visibility=View.VISIBLE
            search_btn.setOnClickListener {
                mActivity!!.searchAdjustment(search_edit.text.toString())
                hideEdit()
            }
            qrcode.setOnClickListener {
                mActivity!!.openQRCode()
            }
            adjustment_save.setOnClickListener {
                mActivity!!.saveData(adapter!!.data)
            }
        }
    }

    /**
     * 隐藏输入法
     */
    private fun hideEdit(){
        val imm = mActivity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(search_edit.windowToken, 0)
    }

    override fun onDestroy() {
        data.clear()
        super.onDestroy()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is InventoryAdjustmentActivity){
            mActivity=context as InventoryAdjustmentActivity
        }else{
            throw ClassCastException(context.toString()+" activity错误")
        }
    }
}