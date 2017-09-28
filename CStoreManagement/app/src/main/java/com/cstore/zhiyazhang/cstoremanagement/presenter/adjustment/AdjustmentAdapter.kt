package com.cstore.zhiyazhang.cstoremanagement.presenter.adjustment

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.AdjustmentBean
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener

/**
 * Created by zhiya.zhang
 * on 2017/9/26 15:20.
 * type==1 查看; type==2 新增
 */
class AdjustmentAdapter(private val type:Int, val date:String, val data:ArrayList<AdjustmentBean>, private val onClick:ItemClickListener):RecyclerView.Adapter<AdjustmentAdapter.ViewHolder>(){
    private var nowDate:String = CStoreCalendar.getCurrentDate(0)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_adjustment, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.adjustmentItemId.text=data[position].itemId
        holder.adjustmentItemName.text=data[position].itemName
        holder.adjustmentActQty.text=data[position].actStockQTY.toString()
        holder.adjustmentCurrQty.text=data[position].currStockQTY.toString()
        if (data[position].isClickClear)holder.adjustmentClear.visibility=View.VISIBLE else holder.adjustmentClear.visibility=View.GONE
        if (type==2&&date==nowDate){
            //只有是操作且时间是营业换日时间才允许监听点击
            holder.adjustmentItemBody.setOnClickListener {
                holder.adjustmentClear.visibility=View.GONE
                onClick.onItemClick(holder.adjustmentItemBody,position)
            }
            holder.adjustmentItemBody.setOnLongClickListener {
                data[position].isClickClear=true
                holder.adjustmentClear.visibility=View.VISIBLE
                true
            }
        }
        holder.adjustmentClear.setOnClickListener {
            data[position].isClickClear=false
            holder.adjustmentClear.visibility=View.GONE
            removeData(position)
        }
    }

    fun addData(newData:ArrayList<AdjustmentBean>){
        data.addAll(newData)
        notifyDataSetChanged()
    }

    private fun removeData(position: Int){
        data.remove(data[position])
        notifyItemRemoved(position)
        if (position != data.size) {
            notifyItemRangeChanged(position, data.size - position)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val adjustmentItemBody=itemView.findViewById<CardView>(R.id.adjustment_item_body)!!
        val adjustmentItemId=itemView.findViewById<TextView>(R.id.adjustment_item_id)!!
        val adjustmentItemName=itemView.findViewById<TextView>(R.id.adjustment_item_name)!!
        val adjustmentActQty=itemView.findViewById<TextView>(R.id.adjustment_act_qty)!!
        val adjustmentCurrQty=itemView.findViewById<TextView>(R.id.adjustment_curr_quantity)!!
        val adjustmentClear=itemView.findViewById<ImageView>(R.id.adjustment_item_clear)!!
    }
}