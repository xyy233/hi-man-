package com.cstore.zhiyazhang.cstoremanagement.presenter.transfer

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.TrsItemBean
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.item_plu_card.view.*
import kotlinx.android.synthetic.main.layout_foot.view.*

/**
 * Created by zhiya.zhang
 * on 2018/1/22 9:52.
 */
class TransferItemAdapter(private val date: String, val data: ArrayList<TrsItemBean>, private val onClick: ItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    /**
     * 控制是否显示添加
     */
    private var showAdd = true

    companion object {
        private val TYPE_ITEM = 1
        private val TYPE_FOOTER = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ITEM -> {
                ItemViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.item_plu_card, parent, false))
            }
            TYPE_FOOTER -> {
                FooterViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.layout_foot, parent, false))
            }
            else -> {
                ItemViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.item_plu_card, parent, false))
            }
        }
    }

    override fun getItemCount(): Int {
        return if (date == CStoreCalendar.getCurrentDate(0) && CStoreCalendar.getNowStatus(0) == 0 && showAdd) {
            data.size + 1
        } else {
            data.size
        }
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

    override fun getItemViewType(position: Int): Int {
        return if (position + 1 == itemCount) {
            if (date == CStoreCalendar.getCurrentDate(0) && CStoreCalendar.getNowStatus(0) == 0 && showAdd) {
                TYPE_FOOTER
            } else {
                TYPE_ITEM
            }
        } else {
            TYPE_ITEM
        }
    }

    fun updateShowAdd() {
        showAdd = !showAdd
        notifyDataSetChanged()
    }

    fun addItem(aData: TrsItemBean) {
        data.add(aData)
        notifyDataSetChanged()
    }

    fun updateItem(aData: TrsItemBean) {
        data.removeAll(data.filter { it.pluId == aData.pluId })
        data.add(aData)
        data.sortBy { it.pluId }
        notifyDataSetChanged()
    }

    private inner class ItemViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        fun bind(tib: TrsItemBean) = with(itemView) {
            val context = MyApplication.instance().applicationContext
            edit_count_text.text = context.getString(R.string.transfer_quantity)
            one_text.text = context.getString(R.string.inventory2)
            two_text.text = context.getString(R.string.retail_total)
            plu_id.text = tib.pluId
            plu_name.text = tib.pluName
            Glide.with(context).load("http://watchstore.rt-store.com:8086/app/order/getImage${tib.pluId}.do")
                    .placeholder(R.mipmap.loading)
                    .error(R.mipmap.load_error)
                    .crossFade()
                    .into(plu_img)
            plu_retail.text = tib.storeUnitPrice.toString()
            two_text_detail.text = tib.total.toString()
            one_text_detail.text = tib.invQty.toString()
            pln_qty.text = tib.trsQty.toString()

            if (date == CStoreCalendar.getCurrentDate(0) && CStoreCalendar.getNowStatus(0) == 0) {
                btn_less.visibility = View.VISIBLE
                btn_add.visibility = View.VISIBLE
                add_less_line.visibility = View.VISIBLE
                btn_less.setOnClickListener {
                    if (tib.trsQty > 0) {
                        tib.trsQty--
                        tib.editCount--
                        pln_qty.text = tib.trsQty.toString()
                        onClick.onItemEdit(tib, 0)
                    } else {
                        MyToast.getShortToast(context.getString(R.string.maxOrMinError))
                    }
                }
                btn_add.setOnClickListener {
                    if (tib.trsQty < tib.invQty!!) {
                        tib.trsQty++
                        tib.editCount++
                        pln_qty.text = tib.trsQty.toString()
                        onClick.onItemEdit(tib, 0)
                    } else {
                        MyToast.getShortToast(context.getString(R.string.cant_bgt_inv))
                    }
                }
            } else {
                btn_less.visibility = View.GONE
                btn_add.visibility = View.GONE
                add_less_line.visibility = View.GONE
            }
        }
    }

    private inner class FooterViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        fun bind() = with(itemView) {
            add_text.text = "新增"
            foot_item.setOnClickListener {
                onClick.onItemClick(this@FooterViewHolder, 0)
            }
        }
    }

}