package com.cstore.zhiyazhang.cstoremanagement.presenter.scrap

import android.annotation.SuppressLint
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapContractBean
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.AddLessClickListener
import com.zhiyazhang.mykotlinapplication.utils.recycler.onMoveAndSwipedListener
import java.util.*

/**
 * Created by zhiya.zhang
 * on 2017/8/21 17:50.
 */
class ScrapAdapter(val data: ArrayList<ScrapContractBean>, private val onClick: AddLessClickListener) : RecyclerView.Adapter<ScrapAdapter.ViewHolder>(), onMoveAndSwipedListener {

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.addLessBody.visibility=View.VISIBLE
        holder.addImg.visibility=View.VISIBLE
        holder.lessImg.visibility=View.VISIBLE
        holder.mrkCount.text = data[position].mrkCount.toString()
        holder.mrkCount.setTextColor(ContextCompat.getColor(MyApplication.instance(),R.color.count_text))
        holder.mrkCount.textSize = 20f
        holder.mrkCount.paint.isFakeBoldText=true
        val idName=data[position].scrapId+"/"+data[position].scrapName
        holder.idName.text=idName
        holder.unitPrice.text=data[position].unitPrice.toString()
        holder.allPrice.text=
                if(data[position].mrkCount==0) data[position].unitPrice.toString()
                else (data[position].unitPrice*data[position].mrkCount).toFloat().toString()
        val isClick: Boolean = when (data[position].busiDate) {
            null -> {
                true
            }
            ""->{
                true
            }
            MyTimeUtil.nowDate -> {
                true
            }
            else -> {
                false
            }
        }

        if (data[position].mrkCount>0)
            holder.itemBg.setBackgroundColor(ContextCompat.getColor(MyApplication.instance(),R.color.add_bg))
        else
            holder.itemBg.setBackgroundColor(ContextCompat.getColor(MyApplication.instance(),R.color.white))

        if (isClick){
            holder.add.setOnClickListener {
                onClick.onItemClick(holder,data[position],position,1)
            }
            holder.less.setOnClickListener {
                onClick.onItemClick(holder,data[position],position,0)
            }
        }else{
            holder.add.setOnClickListener { }
            holder.add.setOnLongClickListener { true }
            holder.less.setOnClickListener {}
            holder.less.setOnLongClickListener {true }
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_type_vertical, parent, false))

    fun addItems(newDates: ArrayList<ScrapContractBean>) {
        data.addAll(newDates)
        data.sortByDescending { it.action }
        notifyDataSetChanged()
    }

    fun addItem(newData: ScrapContractBean) {
        data.add(newData)
        notifyDataSetChanged()
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(data, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemDismiss(position: Int) {
        onClick.onItemRemove(data[position],position)
        data.remove(data[position])
        notifyItemRemoved(position)
        if (position != data.size) {
            notifyItemRangeChanged(position, data.size - position)
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemBg=itemView.findViewById<LinearLayout>(R.id.typebg_v)!!
        val idName=itemView.findViewById<TextView>(R.id.item_text1)!!
        val unitPrice=itemView.findViewById<TextView>(R.id.item_text2)!!
        val mrkCount=itemView.findViewById<TextView>(R.id.item_text3)!!
        val allPrice=itemView.findViewById<TextView>(R.id.item_text4)!!
        val addLessBody=itemView.findViewById<LinearLayout>(R.id.add_less_body)!!
        val add=itemView.findViewById<Button>(R.id.add)!!
        val less=itemView.findViewById<Button>(R.id.less)!!
        val addImg=itemView.findViewById<ImageView>(R.id.add_img)!!
        val lessImg=itemView.findViewById<ImageView>(R.id.less_img)!!

        /*val scrapId = itemView.findViewById<TextView>(R.id.scrap_id)!!
        val scrapPrice=itemView.findViewById<TextView>(R.id.scrap_unit_price)!!
        val scrapName = itemView.findViewById<TextView>(R.id.scrap_name)!!
        val scrapLess = itemView.findViewById<ImageButton>(R.id.scrap_less)!!
        val scrapCount = itemView.findViewById<TextView>(R.id.scrap_count)!!
        val scrapAdd = itemView.findViewById<ImageButton>(R.id.scrap_add)!!
        val scrapItem = itemView.findViewById<CardView>(R.id.scrap_item)!!*/
    }
}