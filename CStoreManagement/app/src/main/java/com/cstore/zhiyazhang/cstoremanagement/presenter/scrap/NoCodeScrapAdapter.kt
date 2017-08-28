/*
package com.cstore.zhiyazhang.cstoremanagement.presenter.scrap

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapCategoryBean
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.RecyclerOnClick

*/
/**
 * Created by zhiya.zhang
 * on 2017/8/11 17:22.
 *//*

class NoCodeScrapAdapter(val data: ArrayList<ScrapCategoryBean>, val onClick: RecyclerOnClick) : RecyclerView.Adapter<NoCodeScrapAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.btn.text=data[position].categoryName
        holder.btn.setOnClickListener {
            onClick.onItemClick(holder.itemView,holder.layoutPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder=
    ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_onlybtn,parent,false))

    override fun getItemCount(): Int =
            data.size

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val btn=itemView.findViewById<Button>(R.id.categoryBtn)
    }
}*/
