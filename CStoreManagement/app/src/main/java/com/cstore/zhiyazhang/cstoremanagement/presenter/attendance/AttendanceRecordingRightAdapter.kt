package com.cstore.zhiyazhang.cstoremanagement.presenter.attendance

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.AttendanceRecordingBean
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import kotlinx.android.synthetic.main.item_attendance_recording_right.view.*

/**
 * Created by zhiya.zhang
 * on 2018/3/10 15:47.
 */
class AttendanceRecordingRightAdapter(private val data: ArrayList<AttendanceRecordingBean>) : RecyclerView.Adapter<AttendanceRecordingRightAdapter.ViewHolder>() {

    fun setData(data: ArrayList<AttendanceRecordingBean>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val color = position % 2
        holder.bind(data[position], color)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_attendance_recording_right, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(arb: AttendanceRecordingBean, color: Int) = with(itemView) {
            table_user_id.text = arb.uId
            table_tot_day.text = arb.totDay
            table_tot_night.text = arb.totNight
            table_tot_danren.text = arb.totDanren
            table_tot_hr.text = arb.totHr
            table_voc_ahr.text = arb.vocAHr
            table_voc_bhr.text = arb.vocBHr
            table_voc_xhr.text = arb.voccXHr
            table_voc_jhr.text = arb.vocJHr
            table_voc_otherhr.text = arb.vocOtherHr
            table_voc_tot.text = arb.vocTot
            table_voc_ctimes.text = arb.vocCTimes
            table_voc_chr.text = arb.vocCHr
            table_tot_feria.text = arb.totFeria
            
            val totCommonAdded = arb.totCommonAdded
            table_tot_common_added.text = if (totCommonAdded.toDouble() < 0) "0" else totCommonAdded
            val totAdded = arb.totAdded
            table_tot_added.text = if (totAdded.toDouble() < 0) "0" else arb.totAdded

            right_detail_box.background = if (color == 1) {
                ContextCompat.getDrawable(MyApplication.instance().applicationContext, R.color.gray3)
            } else {
                ContextCompat.getDrawable(MyApplication.instance().applicationContext, R.color.white)
            }
        }
    }
}