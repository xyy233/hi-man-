package com.cstore.zhiyazhang.cstoremanagement.presenter.ordercategory

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.CategoryItemBean
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.RecyclerOnTouch

/**
 * Created by zhiya.zhang
 * on 2017/7/27 11:43.
 */
class CategoryItemAdapter(val data: ArrayList<CategoryItemBean>, val context: Context, val onTouch: RecyclerOnTouch) : RecyclerView.Adapter<CategoryItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryItemAdapter.ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_order_category, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.orderItemId.text = data[position].itemId
        holder.orderItemName.text = data[position].itemName
        holder.orderPrice.text = data[position].itemPrice.toString()
        holder.orderInv.text = data[position].itemInv.toString()
        holder.orderExpected.text = data[position].itemExpected.toString()
        holder.dlvQTY.text = data[position].dlvQTY.toString()
        holder.orderTomorrow.text = data[position].itemTomorrow.toString()
        holder.editOrderQTY.text = data[position].orderQTY.toString()
        Glide.with(context).load("http://watchstore.rt-store.com:8086/app/order/getImage${data[position].itemId}.do")
                .placeholder(R.mipmap.loading)
                .error(R.mipmap.load_error)
                .dontAnimate()
                .into(holder.orderImg)
        holder.add.setOnTouchListener { v, event ->
            onTouch.onTouchAddListener(data[position], event, position)
            true
        }
        holder.less.setOnTouchListener { v, event ->
            onTouch.onTouchLessListener(data[position], event, position)
            true
        }
        holder.arriveDate.text = context.getString(R.string.tomorrow_count)
        holder.arriveDate.setTextColor(ContextCompat.getColor(context, R.color.cstore_red))
        when {
            data[position].orderQTY > 0 -> holder.myCommodify.setBackgroundColor(ContextCompat.getColor(context, R.color.add_bg))
            data[position].orderQTY == 0 -> holder.myCommodify.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
    }

    override fun getItemCount(): Int =
            data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderItemId = itemView.findViewById(R.id.orderItemId) as TextView
        val orderItemName = itemView.findViewById(R.id.orderItemName) as TextView
        val orderPrice = itemView.findViewById(R.id.orderPrice) as TextView
        val orderInv = itemView.findViewById(R.id.orderInv) as TextView
        val orderExpected = itemView.findViewById(R.id.orderExpected) as TextView
        val dlvQTY = itemView.findViewById(R.id.dlvQTY) as TextView
        val orderTomorrow = itemView.findViewById(R.id.orderTomorrow) as TextView
        val arriveDate = itemView.findViewById(R.id.arriveDate) as TextView
        val editOrderQTY = itemView.findViewById(R.id.editOrderQTY) as TextView
        val orderImg = itemView.findViewById(R.id.orderImg) as ImageView
        val less = itemView.findViewById(R.id.less) as ImageButton
        val add = itemView.findViewById(R.id.add) as ImageButton
        val myCommodify = itemView.findViewById(R.id.my_commodify) as LinearLayout
    }
}