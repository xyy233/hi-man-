package com.cstore.zhiyazhang.cstoremanagement.presenter.transfer

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.TrsBean
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil.deleteTime
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.item_return.view.*
import kotlinx.android.synthetic.main.layout_foot.view.*

/**
 * Created by zhiya.zhang
 * on 2018/1/19 16:03.
 */
class TransferAdapter(private var date: String, val data: ArrayList<TrsBean>, private val onClick: ItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private val TYPE_ITEM = 1
        private val TYPE_FOOTER = 2
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (holder) {
            is ItemViewHolder -> {
                holder.bind(data[position])
            }
            is FooterViewHolder -> {
                holder.bind()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ITEM -> {
                ItemViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.item_return, parent, false))
            }
            TYPE_FOOTER -> {
                FooterViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.layout_foot, parent, false))
            }
            else -> {
                ItemViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.item_return, parent, false))
            }
        }
    }

    override fun getItemCount(): Int {
        return if (date == CStoreCalendar.getCurrentDate(0) && CStoreCalendar.getNowStatus(0) == 0) {
            data.size + 1
        } else {
            data.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position + 1 == itemCount) {
            if (date == CStoreCalendar.getCurrentDate(0) && CStoreCalendar.getNowStatus(0) == 0) {
                TYPE_FOOTER
            } else {
                TYPE_ITEM
            }
        } else {
            TYPE_ITEM
        }
    }

    /**
     * 更新数据
     */
    fun updateItem(aData: TrsBean) {
        data.removeAll(data.filter { it.trsNumber == aData.trsNumber })
        data.add(aData)
        data.sortByDescending { it.busiDate }
        notifyDataSetChanged()
    }

    private inner class ItemViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        fun bind(tb: TrsBean) = with(itemView) {
            val context = MyApplication.instance().applicationContext
            one_text.text = context.getString(R.string.trs_number)
            two_text.text = context.getString(R.string.ostore_id)
            three_text.text = context.getString(R.string.trs_count)
            four_text.text = context.getString(R.string.trs_qty)
            five_text.text = context.getString(R.string.trs_type)
            six_text.text = context.getString(R.string.unit_cost_total)
            seven_text.text = context.getString(R.string.trs_date)
            seven_box.visibility = View.VISIBLE

            return_id.text = tb.trsNumber
            return_vendor.text = tb.trsStoreId
            return_count.text = tb.trsItem.toString()
            return_quantity.text = tb.trsQty.toString()
            return_date.text = if (tb.trsId == 0) "调出" else "调入"
            return_total.text = tb.storeUnitPrice.toString()
            trs_type.text = deleteTime(tb.busiDate)

            return_item.setOnClickListener {
                onClick.onItemEdit(tb, 0)
            }
        }
    }

    private inner class FooterViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        fun bind() = with(itemView) {
            add_text.text="新增调拨"
            foot_item.setOnClickListener {
                onClick.onItemClick(this@FooterViewHolder, 0)
            }
        }
    }
}