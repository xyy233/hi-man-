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
import com.cstore.zhiyazhang.cstoremanagement.bean.AcceptanceBean
import com.cstore.zhiyazhang.cstoremanagement.bean.AcceptanceItemBean
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener

/**
 * Created by zhiya.zhang
 * on 2017/9/11 17:31.
 * type 1=进货验收 2=退货验收
 */
class PurchaseAcceptanceItemAdapter(private val type:Int, private val data: AcceptanceBean, private val date:String, private val onClick: ItemClickListener) : RecyclerView.Adapter<PurchaseAcceptanceItemAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        if (date==CStoreCalendar.getCurrentDate(3)&&CStoreCalendar.getNowStatus(3)==0){
            return data.allItems.size+1
        }else{
            return data.allItems.size
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (data.allItems.size==position){
            //是加
            holder.add.visibility=View.VISIBLE
            holder.acceptanceData.visibility=View.GONE
            holder.item.setOnClickListener {
                onClick.onItemLongClick(holder.item,position)
            }
            return
        }
        if (type==1){
            if (User.getUser().storeAttr==1||User.getUser().storeAttr==5)holder.sellCostBody.visibility=View.GONE
            //还是内部数据
            holder.add.visibility=View.GONE
            holder.acceptanceData.visibility=View.VISIBLE
            holder.commodityId.text=data.allItems[position].itemId
            holder.commodityName.text=data.allItems[position].itemName
            holder.orderQuantity.text=data.allItems[position].ordQutity.toString()
            holder.hqQuantity.text=data.allItems[position].hqQuantity.toString()
            holder.dcQuantity.text=data.allItems[position].dctrsQuantity.toString()
            holder.trsQuantity.text=data.allItems[position].trsQuantity.toString()
            holder.dlvQuantity.text=data.allItems[position].dlvQuantity.toString()
            holder.taxSellCost.text=data.allItems[position].taxSellCost.toString()
            holder.retail.text=data.allItems[position].storeUnitPrice.toString()
            holder.item.setOnClickListener {
                onClick.onItemClick(holder.item, position)
            }
        }else{

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_purchase_acceptance_item, parent, false))
    }

    fun addItem(aib:ArrayList<AcceptanceItemBean>){
        data.allItems.addAll(aib)
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
    }
}