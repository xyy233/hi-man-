package com.cstore.zhiyazhang.cstoremanagement.presenter.pay

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.PayBean
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener

/**
 * Created by zhiya.zhang
 * on 2017/11/8 14:33.
 */
class PayAdapter(val data: ArrayList<PayBean>, private val listener: ItemClickListener) : RecyclerView.Adapter<PayAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = data[position].itemName
        holder.quantity.text = data[position].quantity.toString()
        val price = "￥${data[position].storePrice}"
        holder.price.text = price

        val discount = "￥${data[position].discAmt}"
        holder.discount.text = discount

        holder.less.setOnClickListener {
            if (data[position].quantity > 0) {
                listener.onItemRemove(data[position].barCode, 0)
            } else {
                MyToast.getShortToast(MyApplication.instance().getString(R.string.minCNoLess))
            }
        }
        holder.add.setOnClickListener {
            listener.onItemRemove(data[position].barCode, 1)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_pay, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun addItem(newData: ArrayList<PayBean>) {
        data.addAll(newData)
        data.sortBy { it.itemName }
        notifyDataSetChanged()
    }

    fun removeItem(newData: ArrayList<PayBean>) {
        data.removeAll(newData)
    }

    fun removeItem() {
        data.clear()
        notifyDataSetChanged()
    }

    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val name = item.findViewById<TextView>(R.id.pay_item_name)!!
        val price = item.findViewById<TextView>(R.id.pay_item_price)!!
        val less = item.findViewById<ImageButton>(R.id.pay_less)!!
        val add = item.findViewById<ImageButton>(R.id.pay_add)!!
        val quantity = item.findViewById<TextView>(R.id.pay_item_quantity)!!
        val discount = item.findViewById<TextView>(R.id.pay_item_discount)!!
    }
}