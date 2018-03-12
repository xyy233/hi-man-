package com.cstore.zhiyazhang.cstoremanagement.presenter.attendance

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.cstore.zhiyazhang.cstoremanagement.R

/**
 * Created by zhiya.zhang
 * on 2018/3/10 15:47.
 */
class AttendanceRecordingAdapter(val data: ArrayList<String>) : RecyclerView.Adapter<AttendanceRecordingAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder!!.name.text = data[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.item_paiban, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.name)!!
    }
}