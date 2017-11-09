package com.cstore.zhiyazhang.cstoremanagement.presenter.returnpurchase

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ReasonBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ReturnPurchaseItemBean
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.item_purchase_acceptance.view.*

/**
 * Created by zhiya.zhang
 * on 2017/11/8 11:16.
 */
class ReturnPurchaseItemAdapter(private val date: String, private val data: ArrayList<ReturnPurchaseItemBean>, private val reason: ArrayList<ReasonBean>, private val onClick: ItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private val TYPE_ITEM = 0
        private val TYPE_FOOTER = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ITEM -> {
                ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_commodity_return, parent, false))
            }
            TYPE_FOOTER -> {
                ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_purchase_acceptance, parent, false))
            }
            else -> {
                ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_commodity_return, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
            }
            is FooterViewHolder -> {
            }
        }
    }

    override fun getItemCount(): Int {
        return if (date == CStoreCalendar.getCurrentDate(2) && CStoreCalendar.getNowStatus(2) == 0) {
            data.size + 1
        } else {
            data.size
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
        //品号
        val commodityId = item.findViewById<TextView>(R.id.commodity_id)!!
        //商品图片
        val commodityImg = item.findViewById<ImageView>(R.id.commodity_img)!!
        //零售价
        val commodityRetail = item.findViewById<TextView>(R.id.commodity_retail)!!
        //品名
        val commodityName = item.findViewById<TextView>(R.id.commodity_name)!!
        //库存
        val commodityInv = item.findViewById<TextView>(R.id.commodity_inv)!!
        //零售小计
        val commodityRetailTotal = item.findViewById<TextView>(R.id.commodity_retail_total)!!
        //历史净进货累计
        val commodityLsjjhlj = item.findViewById<TextView>(R.id.commodity_lsjjhlj)!!
        //原因
        val acceptanceSpinner = item.findViewById<Spinner>(R.id.acceptance_spinner)!!
        //预退数
        val plnrtnQuantity = item.findViewById<TextView>(R.id.plnrtn_quantity)!!
        //减
        val returnLess = item.findViewById<ImageButton>(R.id.return_less)!!
        //加
        val returnAdd = item.findViewById<ImageButton>(R.id.return_add)!!
    }

    inner class FooterViewHolder(item: View) : RecyclerView.ViewHolder(item) {
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