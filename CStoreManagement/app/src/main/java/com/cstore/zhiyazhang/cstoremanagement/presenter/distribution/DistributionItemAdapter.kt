package com.cstore.zhiyazhang.cstoremanagement.presenter.distribution

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.DistributionItemBean
import com.cstore.zhiyazhang.cstoremanagement.bean.DistributionItemResult
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener

/**
 * Created by zhiya.zhang
 * on 2018/5/22 14:46.
 */
class DistributionItemAdapter(val data: DistributionItemResult, val context: Context, val onClick: ItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val typeItem = 0//普通item
    private val typeTitle = 1//标题item

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val d = data.rows[position]
        when (holder) {
            is ViewHolder -> {
                Glide.with(context).load("http://${MyApplication.getIP()}:8666/uploadIMG/${d.itemNo}.png")
                        .placeholder(R.mipmap.loading)
                        .error(R.mipmap.load_error)
                        .crossFade()
                        .into(holder.img)
                val disName = "${d.itemName}(${d.itemNo})"
                holder.disName.text = disName
                holder.trsQty.text = d.trsQty.toString()
                holder.scanQty.text = d.scanQty!!.toString()
                holder.less.setOnClickListener {
                    if (d.scanQty != 0) {
                        onClick.onItemRemove(d, position)
                    } else {
                        MyToast.getShortToast(context.getString(R.string.minCNoLess))
                    }
                }
                holder.add.setOnClickListener {
                    if (d.dateType == "Y") {
                        onClick.onItemClick(holder, position)
                    } else {
                        onClick.onItemEdit(d, position)
                    }
                }
                holder.disBox.setOnClickListener {
                    onClick.onItemLongClick(holder, position)
                }
                if (d.newScan == null) {
                    d.newScan = false
                }
                if (d.newScan!!) {
                    holder.disItem.setBackgroundColor(ContextCompat.getColor(context, R.color.contract_bg))
                } else {
                    holder.disItem.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                }
            }
            is TitleViewHolder -> {
                holder.shelvesTitleName.text = d.bigName
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            typeItem -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_distribution, parent, false))
            typeTitle -> TitleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_title, parent, false))
            else -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_distribution, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return data.rows.size
    }

    override fun getItemViewType(position: Int): Int {
        if (data.rows[position].isTitle == null) return typeItem
        return if (data.rows[position].isTitle!!) {
            typeTitle
        } else {
            typeItem
        }
    }

    fun addData(d: DistributionItemBean) {
        data.rows.add(d)
        data.rows.sortBy {
            it.category
        }
        notifyDataSetChanged()
    }

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val disItem = item.findViewById<LinearLayout>(R.id.dis_item)!!
        val img = item.findViewById<ImageView>(R.id.dis_img)!!
        val disName = item.findViewById<TextView>(R.id.dis_name)!!
        val trsQty = item.findViewById<TextView>(R.id.trs_qty)!!
        val less = item.findViewById<ImageButton>(R.id.scan_qty_less)!!
        val scanQty = item.findViewById<TextView>(R.id.scan_qty)!!
        val add = item.findViewById<ImageButton>(R.id.scan_qty_add)!!
        val disBox = item.findViewById<LinearLayout>(R.id.dis_box)!!
    }

    class TitleViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val shelvesTitleName = item.findViewById<TextView>(R.id.shelves_title_name)!!
    }
}