package com.cstore.zhiyazhang.cstoremanagement.presenter.scrap

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapHotBean
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener

/**
 * Created by zhiya.zhang
 * on 2017/9/1 14:43.
 */
class ScrapHotAdapter(val data: ArrayList<ScrapHotBean>, val onClick: ItemClickListener) : RecyclerView.Adapter<ScrapHotAdapter.ViewHolder>() {
    override fun getItemCount(): Int =
            data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.scrapHotName.text = data[position].sName
        holder.scrapHotCount.text=data[position].sCount.toString()
        holder.scrapHotPrice.text=data[position].sPrice.toString()
        holder.hotItem.setOnClickListener {
            onClick.onItemClick(holder, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_scrap_hot_mid,parent,false))

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val scrapHotName = itemView.findViewById<TextView>(R.id.scrap_hot_name)!!
        val scrapHotCount = itemView.findViewById<TextView>(R.id.scrap_hot_count)!!
        val scrapHotPrice = itemView.findViewById<TextView>(R.id.scrap_hot_price)!!
        val hotItem = itemView.findViewById<LinearLayout>(R.id.hot_item)!!
    }
}