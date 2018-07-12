package com.cstore.zhiyazhang.cstoremanagement.presenter.shelves

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ShelvesItemResult
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication

/**
 * Created by zhiya.zhang
 * on 2018/5/20 12:21.
 */
class ShelvesItemAdapter(val data: ShelvesItemResult, private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val typeItem = 0//普通item
    private val typeTitle = 1//标题item

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            typeItem -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_trsz_item, parent, false))
            typeTitle -> TitleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_title, parent, false))
            else -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_trsz_item, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val d = data.rows[position]
        when (holder) {
            is ViewHolder -> {
                Glide.with(context).load("http://${MyApplication.getIP()}:8666/uploadIMG/${d.itemNo}.png")
                        .placeholder(R.mipmap.loading)
                        .error(R.mipmap.load_error)
                        .crossFade()
                        .into(holder.trsImg)
                holder.id.text = d.itemNo
                holder.name.text = d.itemName
                holder.trsQtyText.text = MyApplication.instance().getString(R.string.xsl)
                holder.trsQty.text = d.selQty.toString()
                holder.inv.text = d.endQty.toString()
                holder.sellCostBox.visibility = View.GONE
                holder.trsQty2Box.visibility = View.GONE
                holder.itemTrsz.setCardBackgroundColor(ContextCompat.getColor(context, R.color.contract_bg))
            }
            is TitleViewHolder -> {
                holder.shelvesTitleName.text = d.big_name
            }
        }

    }

    override fun getItemCount(): Int {
        return data.rows.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (data.rows[position].isTitle!!) {
            typeTitle
        } else {
            typeItem
        }
    }

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val id = item.findViewById<TextView>(R.id.commodity_id)!!
        val trsImg = item.findViewById<ImageView>(R.id.trs_img)!!
        val name = item.findViewById<TextView>(R.id.commodity_name)!!
        val trsQtyText = item.findViewById<TextView>(R.id.trs_qty_text)!!
        val trsQty = item.findViewById<TextView>(R.id.trs_qty)!!
        val trsQty2Box = item.findViewById<LinearLayout>(R.id.trs_qty2_box)!!
        val inv = item.findViewById<TextView>(R.id.inv)!!
        val sellCostBox = item.findViewById<LinearLayout>(R.id.sell_cost_body)!!
        val itemTrsz = item.findViewById<CardView>(R.id.item_trsz)!!
    }

    class TitleViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val shelvesTitleName = item.findViewById<TextView>(R.id.shelves_title_name)!!
    }


}