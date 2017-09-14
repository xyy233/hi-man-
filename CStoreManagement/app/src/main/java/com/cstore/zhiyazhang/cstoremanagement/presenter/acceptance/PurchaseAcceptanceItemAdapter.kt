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
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener

/**
 * Created by zhiya.zhang
 * on 2017/9/11 17:31.
 */
class PurchaseAcceptanceItemAdapter(private val data: AcceptanceBean, private val date:String, private val onClick: ItemClickListener) : RecyclerView.Adapter<PurchaseAcceptanceItemAdapter.ViewHolder>() {

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
            holder.acceptance_data.visibility=View.GONE
            holder.item.setOnClickListener {
                onClick.onItemLongClick(holder.item,position)
            }
        }else{
            //还是内部数据
            holder.add.visibility=View.GONE
            holder.acceptance_data.visibility=View.VISIBLE
            holder.commodity_id.text=data.allItems[position].itemId
            holder.commodity_name.text=data.allItems[position].itemName
            holder.order_quantity.text=data.allItems[position].ordQutity.toString()
            holder.hq_quantity.text=data.allItems[position].hqQuantity.toString()
            holder.dc_quantity.text=data.allItems[position].dctrsQuantity.toString()
            holder.trs_quantity.text=data.allItems[position].trsQuantity.toString()
            holder.dlv_quantity.text=data.allItems[position].dlvQuantity.toString()
            holder.tax_sell_cost.text=data.allItems[position].taxSellCost.toString()
            holder.retail.text=data.allItems[position].storeUnitPrice.toString()
            holder.item.setOnClickListener {
                onClick.onItemClick(holder.item, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_purchase_acceptance_item, parent, false))
    }

    fun addItem(aib:AcceptanceItemBean){
        data.allItems.add(aib)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item=itemView.findViewById<CardView>(R.id.acceptance_item)!!
        val add=itemView.findViewById<ImageView>(R.id.add)!!
        val acceptance_data=itemView.findViewById<LinearLayout>(R.id.acceptance_data)!!
        val commodity_id = itemView.findViewById<TextView>(R.id.commodity_id)!!
        val commodity_name = itemView.findViewById<TextView>(R.id.commodity_name)!!
        val order_quantity = itemView.findViewById<TextView>(R.id.order_quantity)!!
        val hq_quantity = itemView.findViewById<TextView>(R.id.hq_quantity)!!
        val dc_quantity = itemView.findViewById<TextView>(R.id.dc_quantity)!!
        val trs_quantity = itemView.findViewById<TextView>(R.id.trs_quantity)!!
        val dlv_quantity = itemView.findViewById<TextView>(R.id.dlv_quantity)!!
        val tax_sell_cost = itemView.findViewById<TextView>(R.id.tax_sell_cost)!!
        val retail = itemView.findViewById<TextView>(R.id.retail)!!
    }
}