/*
package com.cstore.zhiyazhang.cstoremanagement.presenter.scrap

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapContractBean
import com.zhiyazhang.mykotlinapplication.utils.MyApplication



*/
/**
 * Created by zhiya.zhang
 * on 2017/8/8 9:07.
 *//*

class SelectScrapAdapter(val data: ArrayList<ScrapContractBean>) : RecyclerView.Adapter<SelectScrapAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val animation = AnimationUtils.loadAnimation(MyApplication.instance().applicationContext, R.anim.anim_recycler_item_show)
        holder.v.startAnimation(animation)

        val aa1 = AlphaAnimation(1.0f, 0.1f)
        aa1.duration = 400
        holder.v.startAnimation(aa1)

        holder.scrapName.text = data[position].scrapName
        holder.mrkCount.text = data[position].mrkCount.toString()
        when (data[position].isScrap) {
            0 -> holder.isScrapIcon.visibility = View.GONE
            1 -> holder.isScrapIcon.visibility = View.VISIBLE
        }

        val aa = AlphaAnimation(0.1f, 1.0f)
        aa.duration = 400
        holder.v.startAnimation(aa)
    }

    override fun getItemCount(): Int =
            data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_scrap_select, parent, false))


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val scrapName = itemView.findViewById<TextView>(R.id.scrap_name)
        val mrkCount = itemView.findViewById<TextView>(R.id.mrk_count)
        val isScrapIcon = itemView.findViewById<ImageView>(R.id.isScrapIcon)
        val v = itemView.findViewById<LinearLayout>(R.id.v)
    }
}
*/
