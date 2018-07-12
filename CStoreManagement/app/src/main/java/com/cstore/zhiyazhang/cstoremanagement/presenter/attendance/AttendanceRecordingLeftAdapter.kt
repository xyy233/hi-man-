package com.cstore.zhiyazhang.cstoremanagement.presenter.attendance

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.AttendanceRecordingBean
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication

/**
 * Created by zhiya.zhang
 * on 2018/3/10 15:47.
 */
class AttendanceRecordingLeftAdapter(private val data: ArrayList<AttendanceRecordingBean>) : RecyclerView.Adapter<AttendanceRecordingLeftAdapter.ViewHolder>() {

    fun setData(data: ArrayList<AttendanceRecordingBean>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = data[position].uName
        holder.leftDetailBox.background = if (position % 2 == 1) {
            ContextCompat.getDrawable(MyApplication.instance().applicationContext, R.color.gray3)
        } else {
            ContextCompat.getDrawable(MyApplication.instance().applicationContext, R.color.white)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_attendance_recording_left, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.table_user_name)!!
        val leftDetailBox = itemView.findViewById<LinearLayout>(R.id.left_detail_box)!!
    }
}