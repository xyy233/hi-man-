package com.cstore.zhiyazhang.cstoremanagement.presenter.returnpurchase

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ReturnedPurchaseBean
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.item_purchase_acceptance.view.*

/**
 * Created by zhiya.zhang
 * on 2017/11/8 9:21.
 */
class ReturnPurchaseAdapter(private val date: String, private val data: ArrayList<ReturnedPurchaseBean>, private val onClick: ItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private val TYPE_ITEM = 0
        private val TYPE_FOOTER = 1
    }

    override fun getItemCount(): Int {
        return if (date == CStoreCalendar.getCurrentDate(2) && CStoreCalendar.getNowStatus(2) == 0) {
            data.size + 1
        } else {
            data.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                holder.returnId.text = data[position].requestNumber
                holder.returnVendor.text = data[position].vendorName
                holder.returnCount.text = data[position].itemCount.toString()
                holder.returnQuantity.text = data[position].rtnQuantity.toString()
                holder.returnDate.text = data[position].rtnDate
                holder.returnTotal.text = data[position].total.toString()
                holder.returnItem.setOnClickListener {
                    onClick.onItemClick(holder, position)
                }
            }
            is FooterViewHolder -> {
                holder.bind()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ITEM -> {
                ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_return, parent, false))
            }
            TYPE_FOOTER -> {
                FooterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_purchase_acceptance, parent, false))
            }
            else -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_return, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position + 1 == itemCount) {
            if (date == CStoreCalendar.getCurrentDate(2) && CStoreCalendar.getNowStatus(2) == 0) {
                TYPE_FOOTER
            } else {
                TYPE_ITEM
            }
        } else {
            TYPE_ITEM
        }
    }

    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val returnItem = item.findViewById<LinearLayout>(R.id.return_item)!!
        //退货单号
        val returnId = item.findViewById<TextView>(R.id.return_id)!!
        //配送商
        val returnVendor = item.findViewById<TextView>(R.id.return_vendor)!!
        //品项数
        val returnCount = item.findViewById<TextView>(R.id.return_count)!!
        //预退数
        val returnQuantity = item.findViewById<TextView>(R.id.return_quantity)!!
        //退货验收日
        val returnDate = item.findViewById<TextView>(R.id.return_date)!!
        //零售小计
        val returnTotal = item.findViewById<TextView>(R.id.return_total)!!
    }

    private inner class FooterViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        fun bind() = with(itemView) {
            acceptance_data.visibility = View.GONE
            acceptance_status.visibility = View.GONE
            add.visibility = View.VISIBLE
            acceptance_item.setOnClickListener {
                onClick.onItemLongClick(this@FooterViewHolder, 0)
            }
        }
    }
}