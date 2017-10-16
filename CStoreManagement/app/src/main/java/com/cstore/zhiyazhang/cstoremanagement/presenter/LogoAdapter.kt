package com.cstore.zhiyazhang.cstoremanagement.presenter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.LogoBean
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener

/**
 * Created by zhiya.zhang
 * on 2017/9/29 11:05.
 */
class LogoAdapter(private val context: Context, val data: ArrayList<LogoBean>, private val itemClick: ItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false))


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                Glide.with(context).load(data[position].img).crossFade().into(holder.orderImg)
                holder.orderMsg.text = data[position].msg
                holder.orderItem.setOnClickListener { itemClick.onItemClick(holder, position) }
            }
        }

    }

    override fun getItemCount(): Int = data.size

    class ViewHolder(itemVIew: View) : RecyclerView.ViewHolder(itemVIew) {
        val orderImg = itemView.findViewById<ImageView>(R.id.orderImg)!!
        val orderMsg = itemView.findViewById<TextView>(R.id.orderMsg)!!
        val orderItem = itemView.findViewById<LinearLayout>(R.id.orderItem)!!
    }
}