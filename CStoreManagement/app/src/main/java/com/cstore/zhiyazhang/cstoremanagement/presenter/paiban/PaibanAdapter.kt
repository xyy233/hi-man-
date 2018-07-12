package com.cstore.zhiyazhang.cstoremanagement.presenter.paiban

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.SortPaiban
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil.deleteDate
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil.getDiscrepantHours
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil.getNowWeek
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.item_paiban.view.*

/**
 * Created by zhiya.zhang
 * on 2018/2/9 10:36.
 */
class PaibanAdapter(val data: ArrayList<SortPaiban>, private val onClick: ItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PaibanViewHolder) {
            holder.bind(data[position], position)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PaibanViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.item_paiban, parent, false))
    }

    inner class PaibanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(pb: SortPaiban, position: Int) = with(itemView) {
            name.text = pb.data[0].employeeName
            sunday.text = ""
            monday.text = ""
            tuesday.text = ""
            wednesday.text = ""
            thursday.text = ""
            friday.text = ""
            saturday.text = ""
            var hours = 0
            //循环数据
            pb.data.forEach {
                //只有有数据的才进去判断星期添加数据
                if (it.systemDate != "" && it.systemDate != null && it.beginDateTime != null && it.endDateTime != null) {
                    val nowDay = getNowWeek(it.systemDate)
                    when (nowDay) {
                        0 -> {
                            val sundayDate = deleteDate(it.beginDateTime!!) + "-" + deleteDate(it.endDateTime!!)
                            sunday.text = sundayDate
                            hours += getDiscrepantHours(it.beginDateTime!!, it.endDateTime!!)
                        }
                        1 -> {
                            val mondayDate = deleteDate(it.beginDateTime!!) + "-" + deleteDate(it.endDateTime!!)
                            monday.text = mondayDate
                            hours += getDiscrepantHours(it.beginDateTime!!, it.endDateTime!!)
                        }
                        2 -> {
                            val tuesdayDate = deleteDate(it.beginDateTime!!) + "-" + deleteDate(it.endDateTime!!)
                            tuesday.text = tuesdayDate
                            hours += getDiscrepantHours(it.beginDateTime!!, it.endDateTime!!)
                        }
                        3 -> {
                            val wednesdayDate = deleteDate(it.beginDateTime!!) + "-" + deleteDate(it.endDateTime!!)
                            wednesday.text = wednesdayDate
                            hours += getDiscrepantHours(it.beginDateTime!!, it.endDateTime!!)
                        }
                        4 -> {
                            val thursdayDate = deleteDate(it.beginDateTime!!) + "-" + deleteDate(it.endDateTime!!)
                            thursday.text = thursdayDate
                            hours += getDiscrepantHours(it.beginDateTime!!, it.endDateTime!!)
                        }
                        5 -> {
                            val fridayDate = deleteDate(it.beginDateTime!!) + "-" + deleteDate(it.endDateTime!!)
                            friday.text = fridayDate
                            hours += getDiscrepantHours(it.beginDateTime!!, it.endDateTime!!)
                        }
                        6 -> {
                            val saturdayDate = deleteDate(it.beginDateTime!!) + "-" + deleteDate(it.endDateTime!!)
                            saturday.text = saturdayDate
                            hours += getDiscrepantHours(it.beginDateTime!!, it.endDateTime!!)
                        }
                    }
                }
            }
            val statisticsData = hours.toString() + "h"
            statistics.text = statisticsData
            monday_box.setOnClickListener {
                onClick.onItemEdit(pb, 0)
            }
            tuesday_box.setOnClickListener {
                onClick.onItemEdit(pb, 1)
            }
            wednesday_box.setOnClickListener {
                onClick.onItemEdit(pb, 2)
            }
            thursday_box.setOnClickListener {
                onClick.onItemEdit(pb, 3)
            }
            friday_box.setOnClickListener {
                onClick.onItemEdit(pb, 4)
            }
            saturday_box.setOnClickListener {
                onClick.onItemEdit(pb, 5)
            }
            sunday_box.setOnClickListener {
                onClick.onItemEdit(pb, 6)
            }
            statistics_box.setOnClickListener { }
        }
    }
}