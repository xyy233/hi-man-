/*
package com.cstore.zhiyazhang.cstoremanagement.presenter.scrap

import android.annotation.SuppressLint
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapContractBean
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.RecyclerOnTouch
import com.zhiyazhang.mykotlinapplication.utils.MyApplication
import com.zhiyazhang.mykotlinapplication.utils.recycler.onMoveAndSwipedListener
import java.util.*

*/
/**
 * Created by zhiya.zhang
 * on 2017/8/8 10:31.
 *//*

class BarCodeScrapAdapter(val data: ArrayList<ScrapContractBean>, val onClick: RecyclerOnTouch, val type: Int) : RecyclerView.Adapter<BarCodeScrapAdapter.ViewHolder>(), onMoveAndSwipedListener {
    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (type == 0) {
            if (data[position].mrkCount == 0) data[position].mrkCount = 1
        }
        holder.scrapCount.text = data[position].mrkCount.toString()
        holder.contractName.text=data[position].scrapName
        when (data[position].isScrap) {
            0 -> {
                holder.add.visibility = View.VISIBLE
                holder.less.visibility = View.VISIBLE
                holder.add.isEnabled = true
                holder.less.isEnabled = true
                holder.layout.setCardBackgroundColor(ContextCompat.getColor(MyApplication.instance().applicationContext, R.color.cstore_white))
            }
            1 -> {
                holder.add.visibility = View.GONE
                holder.less.visibility = View.GONE
                holder.add.isEnabled = false
                holder.less.isEnabled = false
                holder.layout.setCardBackgroundColor(ContextCompat.getColor(MyApplication.instance().applicationContext, R.color.yingshu))
            }
            else->{
                holder.add.visibility = View.VISIBLE
                holder.less.visibility = View.VISIBLE
                holder.add.isEnabled = true
                holder.less.isEnabled = true
                holder.layout.setCardBackgroundColor(ContextCompat.getColor(MyApplication.instance().applicationContext, R.color.cstore_white))
            }
        }
        holder.add.setOnTouchListener { v, event ->
            onClick.onTouchAddListener(data[position], event, position)
            true
        }
        holder.less.setOnTouchListener { v, event ->
            onClick.onTouchLessListener(data[position], event, position)
            true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_scrap, parent, false))

    override fun getItemCount(): Int =
            data.size

    fun addItem(newData: ScrapContractBean) {
        data.add(newData)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(data, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemDismiss(position: Int) {
        onClick.onClickImage(data[position],position)
        data.remove(data[position])
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contractName = itemView.findViewById<TextView>(R.id.scrap_name)
        val less = itemView.findViewById<ImageButton>(R.id.less)
        val scrapCount = itemView.findViewById<TextView>(R.id.scrap_count)
        val add = itemView.findViewById<ImageButton>(R.id.add)
        val layout = itemView.findViewById<CardView>(R.id.scrap_item)
    }
}*/
