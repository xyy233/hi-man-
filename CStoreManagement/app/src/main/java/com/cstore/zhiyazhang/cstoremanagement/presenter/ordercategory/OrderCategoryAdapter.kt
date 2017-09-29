package com.cstore.zhiyazhang.cstoremanagement.presenter.ordercategory

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.*
import kotlinx.android.synthetic.main.item_type_horizontal.view.*
import kotlinx.android.synthetic.main.item_type_vertical.view.*

/**
 * Created by zhiya.zhang
 * on 2017/7/25 16:10.
 */
class OrderCategoryAdapter(val type: String, val data: Any, val listener: (Any) -> Unit) :
        RecyclerView.Adapter<OrderCategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (type == "shelf") return OrderCategoryAdapter.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_type_vertical, parent, false))
        return OrderCategoryAdapter.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_type_horizontal, parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (type) {
            "category" -> {
                holder.bindCategory((data as ArrayList<OrderCategoryBean>)[position], listener)
            }
            "shelf" -> {
                holder.bindShelf((data as ArrayList<ShelfBean>)[position], listener)
            }
            "self" -> {
                holder.bindSelf((data as ArrayList<SelfBean>)[position], listener)
            }
            "nop" -> {
                holder.bindNOP((data as ArrayList<NOPBean>)[position], type, listener)
            }
            "fresh" -> {
                holder.bindFresh((data as ArrayList<FreshGroup>)[position], listener)
            }
        }
    }

    override fun getItemCount(): Int {
        when (type) {
            "category" -> {
                return (data as ArrayList<OrderCategoryBean>).size
            }
            "shelf" -> {
                return (data as ArrayList<ShelfBean>).size
            }
            "self" -> {
                return (data as ArrayList<SelfBean>).size
            }
            "nop" -> {
                return (data as ArrayList<NOPBean>).size
            }
            "fresh" -> {
                return (data as ArrayList<FreshGroup>).size
            }
            else -> return 0
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindCategory(ocb: OrderCategoryBean, listener: (OrderCategoryBean) -> Unit) = with(itemView) {
            type_h.text = ocb.categoryName
            inventory_h.text = ocb.allSku.toString()
            tonightCount_h.text = ocb.ordSku.toString()
            todayCount_h.text = ocb.ordPrice.toString()
            when {
                ocb.categoryId=="-1" -> type_tag_h.visibility = View.GONE
                ocb.ordSku != 0 -> {
                    type_tag_h.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.is_edit))
                    type_tag_h.visibility = View.VISIBLE
                }
                else -> type_tag_h.visibility = View.GONE
            }
            setOnClickListener { listener(ocb) }
        }

        fun bindShelf(sb: ShelfBean, listener: (ShelfBean) -> Unit) = with(itemView) {
            type_v.text = sb.shelfName
            inventory_v.text = sb.allSku.toString()
            tonightCount_v.text = sb.ordSku.toString()
            todayCount_v.text = sb.ordPrice.toString()
            if (sb.ordSku != 0) {
                type_tag_v.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.is_edit))
                type_tag_v.visibility = View.VISIBLE
            } else {
                type_tag_v.visibility = View.GONE
            }
            setOnClickListener { listener(sb) }
        }

        fun bindSelf(sb: SelfBean, listener: (SelfBean) -> Unit) = with(itemView) {
            type_h.text = sb.selfName
            inventory_h.text = sb.allSku.toString()
            tonightCount_h.text = sb.ordSku.toString()
            todayCount_h.text = sb.ordPrice.toString()
            if (sb.ordSku != 0) {
                type_tag_h.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.is_edit))
                type_tag_h.visibility = View.VISIBLE
            } else {
                type_tag_h.visibility = View.GONE
            }
            setOnClickListener { listener(sb) }
        }

        fun bindNOP(nb: NOPBean, enterType: String, listener: (NOPBean) -> Unit) = with(itemView) {
            type_h.text = nb.nopName
            inventory_h.text = nb.allSku.toString()
            tonightCount_h.text = nb.ordSku.toString()
            todayCount_h.text = nb.ordPrice.toString()
            if (nb.ordSku != 0) {
                type_tag_h.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.is_edit))
                type_tag_h.visibility = View.VISIBLE
            } else {
                type_tag_h.visibility = View.GONE
            }

            setOnClickListener { listener(nb) }
        }

        fun bindFresh(fg: FreshGroup, listener: (FreshGroup) -> Unit) = with(itemView) {
            type_h.text = fg.name
            inventory_h.text = fg.allSku.toString()
            tonightCount_h.text = fg.ordSku.toString()
            todayCount_h.text = fg.ordPrice.toString()
            if (fg.ordSku != 0) {
                type_tag_h.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.is_edit))
                type_tag_h.visibility = View.VISIBLE
            } else {
                type_tag_h.visibility = View.GONE
            }
            setOnClickListener { listener(fg) }
        }
    }

}
