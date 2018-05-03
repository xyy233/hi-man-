package com.cstore.zhiyazhang.cstoremanagement.presenter.ordercategory

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.*
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.DataClickListener
import kotlinx.android.synthetic.main.item_type_horizontal.view.*
import kotlinx.android.synthetic.main.item_type_vertical.view.*

/**
 * Created by zhiya.zhang
 * on 2017/7/25 16:10.
 */
class OrderCategoryAdapter(val type: String, var data: Any, private val listener: DataClickListener) :
        RecyclerView.Adapter<OrderCategoryAdapter.ViewHolder>() {

    init {
        val d = PresenterUtil.judgmentClass(data)
        if (d == "null" || d == "item") {
            (data as ArrayList<*>).clear()
        }
    }

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
                holder.bindNOP((data as ArrayList<NOPBean>)[position], listener)
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
        fun bindCategory(ocb: OrderCategoryBean, listener: DataClickListener) = with(itemView) {
            type_h.text = ocb.categoryName
            inventory_h.text = ocb.allSku.toString()
            tonightCount_h.text = ocb.ordSku.toString()
            todayCount_h.text = ocb.ordPrice.toString()
            ordCount_h.visibility = View.VISIBLE
            ordCount_h.text = ocb.ordCount.toString()
            when {
                ocb.categoryId == "-1" -> type_tag_h.visibility = View.GONE
                ocb.ordSku != 0 -> {
                    type_tag_h.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.is_edit))
                    type_tag_h.visibility = View.VISIBLE
                }
                else -> type_tag_h.visibility = View.GONE
            }
            setOnClickListener { listener.click(ocb) }
        }

        fun bindShelf(sb: ShelfBean, listener: DataClickListener) = with(itemView) {
            item_text1.text = sb.shelfName
            item_text2.text = sb.allSku.toString()
            item_text3.text = sb.ordSku.toString()
            item_text3_5.visibility = View.VISIBLE
            item_text3_5.text = sb.ordCount.toString()
            item_text4.text = sb.ordPrice.toString()
            typebg_v.setBackgroundColor(ContextCompat.getColor(MyApplication.instance(), R.color.white))
            if (sb.ordSku != 0) {
                type_tag_v.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.is_edit))
                type_tag_v.visibility = View.VISIBLE
            } else {
                type_tag_v.visibility = View.GONE
            }
            typebg_v.setOnClickListener { listener.click(sb) }
        }

        fun bindSelf(sb: SelfBean, listener: DataClickListener) = with(itemView) {
            type_h.text = sb.selfName
            inventory_h.text = sb.allSku.toString()
            tonightCount_h.text = sb.ordSku.toString()
            todayCount_h.text = sb.ordPrice.toString()
            ordCount_h.visibility = View.VISIBLE
            ordCount_h.text = sb.ordCount.toString()
            if (sb.ordSku != 0) {
                type_tag_h.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.is_edit))
                type_tag_h.visibility = View.VISIBLE
            } else {
                type_tag_h.visibility = View.GONE
            }
            setOnClickListener { listener.click(sb) }
        }

        fun bindNOP(nb: NOPBean, listener: DataClickListener) = with(itemView) {
            type_h.text = nb.nopName
            inventory_h.text = nb.allSku.toString()
            tonightCount_h.text = nb.ordSku.toString()
            todayCount_h.text = nb.ordPrice.toString()
            ordCount_h.visibility = View.VISIBLE
            ordCount_h.text = nb.ordCount.toString()
            if (nb.ordSku != 0) {
                type_tag_h.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.is_edit))
                type_tag_h.visibility = View.VISIBLE
            } else {
                type_tag_h.visibility = View.GONE
            }

            setOnClickListener { listener.click(nb) }
        }

        fun bindFresh(fg: FreshGroup, listener: DataClickListener) = with(itemView) {
            type_h.text = fg.name
            inventory_h.text = fg.allSku.toString()
            tonightCount_h.text = fg.ordSku.toString()
            todayCount_h.text = fg.ordPrice.toString()
            ordCount_h.visibility = View.VISIBLE
            ordCount_h.text = fg.ordCount.toString()
            if (fg.ordSku != 0) {
                type_tag_h.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.is_edit))
                type_tag_h.visibility = View.VISIBLE
            } else {
                type_tag_h.visibility = View.GONE
            }
            setOnClickListener { listener.click(fg) }
        }
    }

}
