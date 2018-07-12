package com.cstore.zhiyazhang.cstoremanagement.presenter.distribution

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.DistributionResult
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener

/**
 * Created by zhiya.zhang
 * on 2018/5/22 14:33.
 */
class DistributionAdapter(private val dr: DistributionResult, private val onClick: ItemClickListener) : RecyclerView.Adapter<DistributionAdapter.ViewHolder>() {
    private val qtyHead = "调入总数："
    private val qtyAmount = "调入金额："
    override fun getItemCount(): Int {
        return dr.rows!!.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistributionAdapter.ViewHolder {
        return DistributionAdapter.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_shelves, parent, false))
    }

    override fun onBindViewHolder(holder: DistributionAdapter.ViewHolder, position: Int) {
        val d = dr.rows!![position]
        holder.shelvesName.text = d.shelvesName
        val msg = "$qtyHead${d.trsInQty}          $qtyAmount${d.trsInAmount}"
        holder.shelvesAddress.text = msg
        holder.shelvesItem.setOnClickListener {
            onClick.onItemEdit(d, position)
        }
    }

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val shelvesItem = item.findViewById<LinearLayout>(R.id.shelves)!!
        val shelvesName = item.findViewById<TextView>(R.id.shelves_name)!!
        val shelvesAddress = item.findViewById<TextView>(R.id.shelves_address)!!
    }
}