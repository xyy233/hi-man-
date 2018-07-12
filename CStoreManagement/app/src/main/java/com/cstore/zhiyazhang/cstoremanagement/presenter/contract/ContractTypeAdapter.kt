package com.cstore.zhiyazhang.cstoremanagement.presenter.contract

import android.annotation.SuppressLint
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ContractTypeBean
import kotlinx.android.synthetic.main.item_type_horizontal.view.*

/**
 * Created by zhiya.zhang
 * on 2017/6/12 15:10.
 */
class ContractTypeAdapter(private val sqliteData: ArrayList<ContractTypeBean>, val isJustLook: Boolean, val ctbs: List<ContractTypeBean>, private val listener: (ContractTypeBean) -> Unit) :
        RecyclerView.Adapter<ContractTypeAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_type_horizontal, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(ctbs[position], listener, sqliteData.filter { it.typeId == ctbs[position].typeId }.count() != 0, isJustLook)

    override fun getItemCount(): Int = ctbs.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(ctb: ContractTypeBean, listener: (ContractTypeBean) -> Unit, isRead: Boolean, isJustLook: Boolean) = with(itemView) {
            type_h.text = ctb.typeName
            if (isJustLook) {
                //一览表
                inventory_h.text = ctb.modSku.toString() + "/" + ctb.sku.toString()
                tonightCount_h.text = ctb.todayGh.toString()
                todayCount_h.text = ctb.todayStore.toString()
                if (ctb.modSku != 0) {
                    type_tag_h.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.is_edit))
                    type_tag_h.visibility = View.VISIBLE
                } else {
                    type_tag_h.visibility = View.GONE
                }
            } else {
                inventory_h.text = ctb.inventory.toString()
                tonightCount_h.text = ctb.tonightCount.toString()
                todayCount_h.text = ctb.todayCount.toString()
                if (ctb.todayStore != 0) {
                    type_tag_h.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.is_edit))
                    type_tag_h.visibility = View.VISIBLE
                } else {
                    if (isRead || ctb.isChangeColor) {
                        type_tag_h.visibility = View.VISIBLE
                        type_tag_h.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.is_read))
                    } else {
                        type_tag_h.visibility = View.GONE
                    }
                }
            }
            setOnClickListener {
                listener(ctb)
            }
        }
    }
}