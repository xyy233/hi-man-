package com.cstore.zhiyazhang.cstoremanagement.view.inventory

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.AdjustmentBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.adjustment.AdjustmentAdapter
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyDividerItemDecoration
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
    private var rootView:View?=null
    var saveBtn: Button?=null

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
        if (rootView==null){
            rootView=inflater.inflate(R.layout.fragment_adjustment,container,false)
            saveBtn=rootView!!.findViewById(R.id.adjustment_save)
        }
        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        data.addAll(arguments!!.getSerializable(PAGE_DATA) as ArrayList<AdjustmentBean>)
        date=arguments!!.getString(PAGE_DATE)
        type=arguments!!.getInt(PAGE_TYPE)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adjustment_recycler.layoutManager=MyLinearlayoutManager(mActivity as Context,LinearLayoutManager.VERTICAL,false)
        val dividerItemDecoration= MyDividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        dividerItemDecoration.setDivider(R.drawable.divider_bg2)
        adjustment_recycler.addItemDecoration(dividerItemDecoration)
        adjustment_recycler.itemAnimator= DefaultItemAnimator()
        //在创建的时候传入的data就会相应type==1传该有的值type==2传空list
        adapter=AdjustmentAdapter(type, date, data, object : ItemClickListener {
            override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {
                mActivity!!.updateDate(data[position])
            }
        })
        adjustment_recycler.adapter=adapter
        //如果是搜索还要显示搜索栏
        if (type==2){
            search_line_bar.visibility=View.VISIBLE
            search_btn.setOnClickListener {
                mActivity!!.searchAdjustment(search_edit.text.toString())
                hideEdit()
            }
            search_edit.setOnEditorActionListener { _, actionId, _ ->
                if (actionId==EditorInfo.IME_ACTION_SEARCH){
                    mActivity!!.searchAdjustment(search_edit.text.toString())
                    hideEdit()
                    true
                }else false
            }
            qrcode.setOnClickListener {
                mActivity!!.openQRCode()
            }
            adjustment_save.setOnClickListener {
                mActivity!!.saveData(adapter!!.data)
            }
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser){
            //fragment可见的时候
            if (type==2){
                mActivity!!.showTutorial(type)
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
            mActivity= context
        }else{
            throw ClassCastException(context.toString()+" activity错误")
        }
    }

    fun clearEdit() {
        search_edit.setText("")
    }
}