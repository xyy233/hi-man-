package com.cstore.zhiyazhang.cstoremanagement.presenter.Acceptance

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.AcceptanceBean
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener

/**
 * Created by zhiya.zhang
 * on 2017/9/11 17:31.
 */
class PurchaseAcceptanceAdapter(private val data: ArrayList<AcceptanceBean>, private val onClick: ItemClickListener) : RecyclerView.Adapter<PurchaseAcceptanceAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.t.text = data[position].toString()
        holder.t.setOnClickListener {
            onClick.onItemClick(holder.t, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_purchase_acceptance, parent, false))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val t = itemView.findViewById<TextView>(R.id.testP)!!
    }
}