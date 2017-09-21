package com.cstore.zhiyazhang.cstoremanagement.presenter.acceptance

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.AcceptanceItemBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ReturnAcceptanceItemBean
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener

/**
 * Created by zhiya.zhang
 * on 2017/9/11 17:31.
 * type 1=进货验收 2=退货验收
 */
class PurchaseAcceptanceItemAdapter(private val type:Int, private val data: ArrayList<*>, private val date:String, private val onClick: ItemClickListener) : RecyclerView.Adapter<PurchaseAcceptanceItemAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return if (date==CStoreCalendar.getCurrentDate(3)&&CStoreCalendar.getNowStatus(3)==0){
            data.size+1
        }else{
            data.size
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (data.size==position){
            //是加
            holder.add.visibility=View.VISIBLE
            holder.acceptanceData.visibility=View.GONE
            holder.item.setOnClickListener {
                onClick.onItemLongClick(holder.item,position)
            }
            return
        }
        if (type==1){
            data as ArrayList<AcceptanceItemBean>
            if (User.getUser().storeAttr==1||User.getUser().storeAttr==5)holder.sellCostBody.visibility=View.GONE
            //还是内部数据
            holder.add.visibility=View.GONE
            holder.hqText.text=MyApplication.instance().applicationContext.getString(R.string.hq_quantity)
            holder.ordText.text=MyApplication.instance().applicationContext.getString(R.string.order_quantity)
            holder.dlvText.text=MyApplication.instance().applicationContext.getString(R.string.dlv_quantity)
            holder.trsText.text=MyApplication.instance().applicationContext.getString(R.string.trs_quantity)
            holder.acceptanceData.visibility=View.VISIBLE
            holder.commodityId.text=data[position].itemId
            holder.commodityName.text=data[position].itemName
            holder.orderQuantity.text=data[position].ordQuantity.toString()
            holder.hqQuantity.text=data[position].hqQuantity.toString()
            holder.dcQuantity.text=data[position].dctrsQuantity.toString()
            holder.trsQuantity.text=data[position].trsQuantity.toString()
            holder.dlvQuantity.text=data[position].dlvQuantity.toString()
            holder.taxSellCost.text=data[position].taxSellCost.toString()
            holder.retail.text=data[position].storeUnitPrice.toString()
            if (date==CStoreCalendar.getCurrentDate(3)&&CStoreCalendar.getNowStatus(3)==0){
                holder.item.setOnClickListener {
                    onClick.onItemClick(holder.item, position)
                }
            }
        }else{
            data as ArrayList<ReturnAcceptanceItemBean>
            if (User.getUser().storeAttr==1||User.getUser().storeAttr==5)holder.sellCostBody.visibility=View.GONE
            holder.dcBody.visibility=View.GONE
            holder.hqText.text=MyApplication.instance().applicationContext.getString(R.string.return_quantity)
            holder.ordText.text=MyApplication.instance().applicationContext.getString(R.string.pre_quantity)
            holder.dlvText.text=MyApplication.instance().applicationContext.getString(R.string.tax_sell_cost)
            holder.trsText.text=MyApplication.instance().applicationContext.getString(R.string.retail)
            holder.retailBody.visibility=View.GONE
            holder.add.visibility=View.GONE
            holder.acceptanceData.visibility=View.VISIBLE
            holder.commodityId.text=data[position].itemId
            holder.commodityName.text=data[position].itemName
            holder.orderQuantity.text=data[position].ordQuantity.toString()
            holder.hqQuantity.text=data[position].rtnQuantity.toString()
            holder.trsQuantity.text=data[position].taxSellCost.toString()
            holder.dlvQuantity.text=data[position].storeUnitPrice.toString()
            if (date==CStoreCalendar.getCurrentDate(3)&&CStoreCalendar.getNowStatus(3)==0){
                holder.item.setOnClickListener {
                    onClick.onItemClick(holder.item, position)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_purchase_acceptance_item, parent, false))
    }

    fun addItem(aib:ArrayList<*>){
        if (type==1){
            data as ArrayList<AcceptanceItemBean>
            aib as ArrayList<AcceptanceItemBean>
            data.addAll(aib)
        }else{
            data as ArrayList<ReturnAcceptanceItemBean>
            aib as ArrayList<ReturnAcceptanceItemBean>
            data.addAll(aib)
        }
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item=itemView.findViewById<CardView>(R.id.acceptance_item)!!
        val add=itemView.findViewById<ImageView>(R.id.add)!!
        val acceptanceData=itemView.findViewById<LinearLayout>(R.id.acceptance_data)!!
        val commodityId = itemView.findViewById<TextView>(R.id.commodity_id)!!
        val commodityName = itemView.findViewById<TextView>(R.id.commodity_name)!!
        val orderQuantity = itemView.findViewById<TextView>(R.id.order_quantity)!!
        val hqQuantity = itemView.findViewById<TextView>(R.id.hq_quantity)!!
        val dcQuantity = itemView.findViewById<TextView>(R.id.dc_quantity)!!
        val trsQuantity = itemView.findViewById<TextView>(R.id.trs_quantity)!!
        val dlvQuantity = itemView.findViewById<TextView>(R.id.dlv_quantity)!!
        val taxSellCost = itemView.findViewById<TextView>(R.id.tax_sell_cost)!!
        val retail = itemView.findViewById<TextView>(R.id.retail)!!
        val sellCostBody =itemView.findViewById<LinearLayout>(R.id.sell_cost_body)!!
        val retailBody=itemView.findViewById<LinearLayout>(R.id.retail_body)!!
        val dcBody=itemView.findViewById<LinearLayout>(R.id.dc_body)!!
        val ordText=itemView.findViewById<TextView>(R.id.order_text)!!
        val dlvText=itemView.findViewById<TextView>(R.id.dlv_text)!!
        val hqText=itemView.findViewById<TextView>(R.id.hq_text)!!
        val trsText=itemView.findViewById<TextView>(R.id.trs_text)!!
    }
}