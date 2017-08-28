package com.cstore.zhiyazhang.cstoremanagement.presenter.scrap

import android.annotation.SuppressLint
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapContractBean
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.RecyclerOnTouch
import com.zhiyazhang.mykotlinapplication.utils.recycler.onMoveAndSwipedListener
import java.util.*

/**
 * Created by zhiya.zhang
 * on 2017/8/21 17:50.
 */
class ScrapAdapter(val data: ArrayList<ScrapContractBean>, val onClick: RecyclerOnTouch) : RecyclerView.Adapter<ScrapAdapter.ViewHolder>(), onMoveAndSwipedListener {
    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.scrapCount.text = data[position].mrkCount.toString()
        holder.scrapName.text = data[position].scrapName
        holder.scrapId.text = data[position].scrapId
        when (data[position].busiDate) {
            null -> {
                holder.scrapAdd.visibility = View.VISIBLE
                holder.scrapLess.visibility = View.VISIBLE
            }
            MyTimeUtil.nowDate -> {
                holder.scrapAdd.visibility = View.VISIBLE
                holder.scrapLess.visibility = View.VISIBLE
            }
            else -> {
                holder.scrapAdd.visibility = View.GONE
                holder.scrapLess.visibility = View.GONE
            }
        }
        try {
            //删除的时候会点到 +or- 因此开个try
            holder.scrapAdd.setOnTouchListener { _, event ->
                onClick.onTouchAddListener(data[position], event, position)
                true
            }
            holder.scrapLess.setOnTouchListener { _, event ->
                onClick.onTouchLessListener(data[position], event, position)
                true
            }
        } catch (e: Exception) {
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_scrap, parent, false))

    fun addItems(newDates: ArrayList<ScrapContractBean>) {
        data.addAll(newDates)
        notifyDataSetChanged()
    }

    fun addItem(newData: ScrapContractBean) {
        data.add(newData)
        notifyDataSetChanged()
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(data, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemDismiss(position: Int) {
        onClick.onClickImage(data[position], position)
        data.remove(data[position])
        notifyItemRemoved(position)
        if (position != data.size) {
            notifyItemRangeChanged(position, data.size - position)
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val scrapId = itemView.findViewById<TextView>(R.id.scrap_id)!!
        val scrapName = itemView.findViewById<TextView>(R.id.scrap_name)!!
        val scrapLess = itemView.findViewById<ImageButton>(R.id.scrap_less)!!
        val scrapCount = itemView.findViewById<TextView>(R.id.scrap_count)!!
        val scrapAdd = itemView.findViewById<ImageButton>(R.id.scrap_add)!!
        val scrapItem = itemView.findViewById<CardView>(R.id.scrap_item)!!
    }
}