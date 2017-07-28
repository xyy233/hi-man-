package com.cstore.zhiyazhang.cstoremanagement.presenter.ordercategory

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.OrderCategoryBean
import kotlinx.android.synthetic.main.item_contract_type.view.*

/**
 * Created by zhiya.zhang
 * on 2017/7/25 16:10.
 */
class OrderCategoryAdapter(val sqliteData: ArrayList<OrderCategoryBean>, val data: ArrayList<OrderCategoryBean>, val listener: (OrderCategoryBean) -> Unit) :
        RecyclerView.Adapter<OrderCategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            OrderCategoryAdapter.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_contract_type, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
            holder.bind(data[position], listener)

    override fun getItemCount(): Int = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(ocb: OrderCategoryBean, listener: (OrderCategoryBean) -> Unit) = with(itemView) {
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
    }

}
