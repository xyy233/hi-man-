package com.cstore.zhiyazhang.cstoremanagement.presenter.ordercategory

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.NOPBean
import com.cstore.zhiyazhang.cstoremanagement.bean.OrderCategoryBean
import com.cstore.zhiyazhang.cstoremanagement.bean.SelfBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ShelfBean
import kotlinx.android.synthetic.main.item_contract_type.view.*

/**
 * Created by zhiya.zhang
 * on 2017/7/25 16:10.
 */
class OrderCategoryAdapter(val sqliteData: Any, val type: String, val data: Any, val listener: (Any) -> Unit) :
        RecyclerView.Adapter<OrderCategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return OrderCategoryAdapter.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_contract_type, parent, false))
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
            else -> return 0
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindCategory(ocb: OrderCategoryBean, listener: (OrderCategoryBean) -> Unit) = with(itemView) {
            type.text = ocb.categoryName
            inventory.text = ocb.allSku.toString()
            tonightCount.text = ocb.ordSku.toString()
            todayCount.text = ocb.ordPrice.toString()
            //todayCount.setTextColor(ContextCompat.getColor(MyApplication.instance().applicationContext, R.color.black))
            if (ocb.ordSku != 0) {
                type_tag.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.is_edit))
                type_tag.visibility = View.VISIBLE
            } else {
                type_tag.visibility = View.GONE
            }
            setOnClickListener { listener(ocb) }
        }

        fun bindShelf(sb: ShelfBean, listener: (ShelfBean) -> Unit) = with(itemView) {
            type.text = sb.shelfName
            inventory.visibility = View.GONE
            tonightCount.visibility = View.GONE
            todayCount.visibility = View.GONE
            type_tag.visibility = View.GONE
            setOnClickListener { listener(sb) }
        }

        fun bindSelf(sb: SelfBean, listener: (SelfBean) -> Unit) = with(itemView) {
            type.text = sb.selfName
            inventory.visibility = View.GONE
            tonightCount.visibility = View.GONE
            todayCount.visibility = View.GONE
            type_tag.visibility = View.GONE
            setOnClickListener { listener(sb) }
        }

        fun bindNOP(nb:NOPBean,listener: (NOPBean) -> Unit)= with(itemView){
            type.text=nb.nopName
            inventory.visibility = View.GONE
            tonightCount.visibility = View.GONE
            todayCount.visibility = View.GONE
            type_tag.visibility = View.GONE
            setOnClickListener { listener(nb) }
        }
    }

}
