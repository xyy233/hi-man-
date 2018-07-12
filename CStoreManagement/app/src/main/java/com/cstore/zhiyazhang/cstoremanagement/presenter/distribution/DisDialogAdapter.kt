package com.cstore.zhiyazhang.cstoremanagement.presenter.distribution

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.AddLessClickListener
import com.zhiyazhang.mykotlinapplication.utils.recycler.onMoveAndSwipedListener
import java.util.*

/**
 * Created by zhiya.zhang
 * on 2018/5/29 16:40.
 */
class DisDialogAdapter(private val data: ArrayList<String>, private val onClick: AddLessClickListener) : RecyclerView.Adapter<DisDialogAdapter.ViewHolder>(), onMoveAndSwipedListener {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text.text = data[position]
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_title, parent, false))

    fun addItems(newData: ArrayList<String>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(data, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemDismiss(position: Int) {
        onClick.onItemRemove(data[position], position)
        data.remove(data[position])
        notifyItemRemoved(position)
        if (position != data.size) {
            notifyItemRangeChanged(position, data.size - position)
        }
    }

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val text = itemView.findViewById<TextView>(R.id.shelves_title_name)!!
    }
}