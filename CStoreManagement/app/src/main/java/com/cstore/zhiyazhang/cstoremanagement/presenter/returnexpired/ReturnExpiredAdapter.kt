package com.cstore.zhiyazhang.cstoremanagement.presenter.returnexpired

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ReturnExpiredBean
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil.deleteTime
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.item_commodity_return.view.*
import kotlinx.android.synthetic.main.item_purchase_acceptance.view.*
import kotlinx.android.synthetic.main.item_return_expired2.view.*

/**
 * Created by zhiya.zhang
 * on 2018/1/5 11:25.
 */
class ReturnExpiredAdapter(val data: ArrayList<ReturnExpiredBean>, private val type: Int, private val onClick: ItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private val TYPE_ITEM = 1
        private val TYPE_FOOTER = 2
    }
    private val ip = MyApplication.getIP()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                if (type == 1) {
                    holder.bindOne(data[position])
                } else {
                    holder.bindTwo(data[position])
                }
            }
            is FooterViewHolder -> {
                holder.bind()
            }
        }
    }

    override fun getItemCount(): Int {
        return if (type == 1) {
            data.size + 1
        } else {
            data.size
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ITEM -> {
                return if (type == 1) {
                    ViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.item_commodity_return, parent, false))
                } else {
                    ViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.item_return_expired2, parent, false))
                }
            }
            TYPE_FOOTER -> {
                FooterViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.item_purchase_acceptance, parent, false))
            }
            else -> {
                FooterViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.item_purchase_acceptance, parent, false))
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position + 1 == itemCount) {
            if (type == 1) {
                TYPE_FOOTER
            } else {
                TYPE_ITEM
            }
        } else {
            TYPE_ITEM
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindOne(reb: ReturnExpiredBean) = with(itemView) {
            retail_total_text.text = MyApplication.instance().getString(R.string.jhl)
            lsjjhlj_text.text = MyApplication.instance().getString(R.string.xsl)
            return_qty_box.visibility = View.VISIBLE
            reason_text.text = MyApplication.instance().getString(R.string.vendor)
            commodity_id.text = reb.itemNumber


            Glide.with(context).load("http://$ip:8666/uploadIMG/${reb.itemNumber}.png")
                    .placeholder(R.mipmap.loading)
                    .error(R.mipmap.load_error)
                    .crossFade()
                    .into(commodity_img)//商品图片


            commodity_retail.text = reb.storeUnitPrice.toString()
            commodity_name.text = reb.pluName
            commodity_inv.text = reb.stockQTY.toString()
            commodity_retail_total.text = if (reb.dlvQTY == null) "0" else reb.dlvQTY.toString()
            commodity_lsjjhlj.text = if (reb.saleQTY == null) "0" else reb.saleQTY.toString()
            return_qty.text = if (reb.rtnQTY == null) "0" else reb.rtnQTY.toString()
            return_reason.text = reb.vendorName
            plnrtn_quantity.text = reb.plnRtnQTY.toString()
            return_less.setOnClickListener {
                if (reb.plnRtnQTY > 0) {
                    reb.plnRtnQTY--
                    reb.editCount--
                    plnrtn_quantity.text = reb.plnRtnQTY.toString()
                    onClick.onItemEdit(reb, 0)
                } else {
                    MyToast.getShortToast(context.getString(R.string.maxOrMinError))
                }
            }
            return_add.setOnClickListener {
                if (reb.plnRtnQTY < reb.stockQTY) {
                    reb.plnRtnQTY++
                    reb.editCount++
                    plnrtn_quantity.text = reb.plnRtnQTY.toString()
                    onClick.onItemEdit(reb, 0)
                } else {
                    MyToast.getShortToast(context.getString(R.string.rtn_qty_inv))
                }
            }
        }

        fun bindTwo(reb: ReturnExpiredBean) = with(itemView) {
            rtn_id.text = reb.itemNumber
            Glide.with(context).load("http://$ip:8666/uploadIMG/${reb.itemNumber}.png")
                    .placeholder(R.mipmap.loading)
                    .error(R.mipmap.load_error)
                    .crossFade()
                    .into(rtn_img)//商品图片
            rtn_retail.text = reb.storeUnitPrice.toString()
            rtn_name.text = reb.pluName
            rtn_qty.text = reb.plnRtnQTY.toString()
            rtn_retail_total.text = reb.priceAmt.toString()
            rtn_vendor.text = reb.vendorName
            rtn_date.text = deleteTime(reb.plnRtnDate)
            rtn_status.text = reb.rtnStatus
        }
    }

    inner class FooterViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        fun bind() = with(itemView) {
            acceptance_data.visibility = View.GONE
            acceptance_status.visibility = View.GONE
            add.visibility = View.VISIBLE
            acceptance_item.setOnClickListener {
                onClick.onItemClick(this@FooterViewHolder, 0)
            }
        }
    }

    fun addItem(reb: ReturnExpiredBean) {
        if (type == 1) {
            data.add(reb)
            notifyDataSetChanged()
        }
    }

    fun addItems(rebs: ArrayList<ReturnExpiredBean>) {
        data.clear()
        data.addAll(rebs)
        notifyDataSetChanged()
    }
}
