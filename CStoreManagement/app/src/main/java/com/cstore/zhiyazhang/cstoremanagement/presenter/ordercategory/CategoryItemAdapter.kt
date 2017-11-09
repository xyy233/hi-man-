package com.cstore.zhiyazhang.cstoremanagement.presenter.ordercategory

import android.annotation.SuppressLint
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
import java.text.DecimalFormat

/**
 * Created by zhiya.zhang
 * on 2017/7/27 11:43.
 */
class CategoryItemAdapter(val data: ArrayList<CategoryItemBean>, val context: Context, private val onTouch: RecyclerOnTouch, private val isType: String) : RecyclerView.Adapter<CategoryItemAdapter.ViewHolder>() {

    private val df = DecimalFormat("#####.####")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryItemAdapter.ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_order_category, parent, false))

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (isType) {
            "self" -> {
                holder.price.visibility = View.GONE
                holder.sell.visibility = View.VISIBLE
            }
        }
        //对是否能订的做处理
        when (data[position].orderType) {
            "N" -> {
                holder.add.isEnabled = false
                holder.less.isEnabled = false
                holder.add.setBackgroundColor(ContextCompat.getColor(context, R.color.sort_gray))
                holder.less.setBackgroundColor(ContextCompat.getColor(context, R.color.sort_gray))
                holder.isNoBuy.visibility = View.VISIBLE
            }
            else -> {
                holder.add.isEnabled = true
                holder.less.isEnabled = true
                holder.add.setBackgroundColor(ContextCompat.getColor(context, R.color.add_less))
                holder.less.setBackgroundColor(ContextCompat.getColor(context, R.color.add_less))
                holder.isNoBuy.visibility = View.GONE
            }
        }
        //对是否是促销品做处理
        if (isType != "self") {
            when (data[position].proYN) {
                "Y" -> {
                    holder.isPro.visibility = View.VISIBLE
                }
                "N" -> {
                    holder.isPro.visibility = View.GONE
                }
            }
        }
        holder.orderItemId.text = data[position].itemId
        holder.orderItemName.text = data[position].itemName
        holder.orderInv.text =
                if (data[position].dlvQTY != 0) {
                    data[position].itemInv.toString() + "+${data[position].dlvQTY}"
                } else {
                    data[position].itemInv.toString()
                }
        if (data[position].dlvQTY2 == 0) {
            holder.dlvQty2.visibility = View.GONE
        } else {
            holder.dlvQty2.visibility = View.VISIBLE
            holder.dlvQty2.text = data[position].dlvQTY2.toString()
        }
        holder.orderExpected.text = data[position].itemExpected.toString()
        holder.dlvQTY.text = data[position].dlvQTY1.toString()
        holder.orderTomorrow.text = data[position].itemTomorrow.toString()
        holder.orderPrice.text = data[position].itemPrice.toString()
        holder.editOrderQTY.text = data[position].orderQTY.toString()
        //这里double会变成1.0E-4。需要转换成string
        holder.dms.text = df.format(data[position].dms)
        holder.dma.text = df.format(data[position].dma)
        Glide.with(context).load("http://watchstore.rt-store.com:8086/app/order/getImage${data[position].itemId}.do")
                .placeholder(R.mipmap.loading)
                .error(R.mipmap.load_error)
                .crossFade()
                .into(holder.orderImg)
        holder.add.setOnTouchListener { _, event ->
            onTouch.onTouchAddListener(data[position], event, position)
            true
        }
        holder.less.setOnTouchListener { _, event ->
            onTouch.onTouchLessListener(data[position], event, position)
            true
        }
        if (data[position].dlvQTY2 == 0) {
            //D+1
        } else {
            //D+2
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
        val orderItemId = itemView.findViewById<TextView>(R.id.orderItemId)!!
        val orderItemName = itemView.findViewById<TextView>(R.id.orderItemName)!!
        val orderPrice = itemView.findViewById<TextView>(R.id.orderPrice)!!
        val price = itemView.findViewById<TextView>(R.id.price)!!
        val sell = itemView.findViewById<TextView>(R.id.sell)!!
        val orderInv = itemView.findViewById<TextView>(R.id.orderInv)!!
        val orderExpected = itemView.findViewById<TextView>(R.id.orderExpected)!!
        val dlvQTY = itemView.findViewById<TextView>(R.id.dlvQTY)!!
        val orderTomorrow = itemView.findViewById<TextView>(R.id.orderTomorrow)!!
        val arriveDate = itemView.findViewById<TextView>(R.id.arriveDate)!!
        val editOrderQTY = itemView.findViewById<TextView>(R.id.editOrderQTY)!!
        val orderImg = itemView.findViewById<ImageView>(R.id.orderImg)!!
        val less = itemView.findViewById<ImageButton>(R.id.less)!!
        val add = itemView.findViewById<ImageButton>(R.id.add)!!
        val myCommodify = itemView.findViewById<LinearLayout>(R.id.my_commodify)!!
        val isNoBuy = itemView.findViewById<ImageView>(R.id.is_nobuy)!!
        val isPro = itemView.findViewById<ImageView>(R.id.is_pro)!!
        val dms = itemView.findViewById<TextView>(R.id.order_dms)!!
        val dma = itemView.findViewById<TextView>(R.id.order_dma)!!
        val dlvQty2 = itemView.findViewById<TextView>(R.id.tomorrow_arrival)!!
    }
}