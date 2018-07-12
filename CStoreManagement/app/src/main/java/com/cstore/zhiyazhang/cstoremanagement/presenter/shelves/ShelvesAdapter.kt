package com.cstore.zhiyazhang.cstoremanagement.presenter.shelves

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ShelvesResult
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener

/**
 * Created by zhiya.zhang
 * on 2018/5/20 11:10.
 */
class ShelvesAdapter(private val sr: ShelvesResult, private val onClick: ItemClickListener) : RecyclerView.Adapter<ShelvesAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        val size = sr.rows.size
        //如果有要留全部
        return if (size < 2) size else size + 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_shelves, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val size = sr.rows.size
        if (position == size) {
            //查看全部
            holder.shelvesAddress.visibility = View.GONE
            holder.shelvesName.text = MyApplication.instance().applicationContext.getString(R.string.view_all)
            holder.shelvesItem.setOnClickListener {
                onClick.onItemEdit("%", position)
            }
        } else {
            //查看单个
            val d = sr.rows[position]
            holder.shelvesAddress.visibility = View.VISIBLE
            holder.shelvesName.text = d.shelvesName
            holder.shelvesAddress.text = d.shelvesAddress
            holder.shelvesItem.setOnClickListener {
                onClick.onItemEdit(d, position)
            }
        }
    }

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val shelvesItem = item.findViewById<LinearLayout>(R.id.shelves)!!
        val shelvesName = item.findViewById<TextView>(R.id.shelves_name)!!
        val shelvesAddress = item.findViewById<TextView>(R.id.shelves_address)!!
    }
}