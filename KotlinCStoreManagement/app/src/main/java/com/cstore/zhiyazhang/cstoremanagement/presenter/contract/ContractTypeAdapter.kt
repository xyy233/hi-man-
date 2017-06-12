package com.cstore.zhiyazhang.cstoremanagement.presenter.contract

import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ContractTypeBean
import com.zhiyazhang.mykotlinapplication.utils.MyApplication
import kotlinx.android.synthetic.main.item_contract_type.view.*

/**
 * Created by zhiya.zhang
 * on 2017/6/12 15:10.
 */
class ContractTypeAdapter(val ctbs: List<ContractTypeBean>, val listener: (ContractTypeBean) -> Unit) :
        RecyclerView.Adapter<ContractTypeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_contract_type, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(ctbs[position], listener)

    override fun getItemCount(): Int = ctbs.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(ctb: ContractTypeBean, listener: (ContractTypeBean) -> Unit) = with(itemView) {
            type.text = ctb.typeName
            inventory.text = ctb.inventory.toString()
            tonightCount.text = ctb.tonightCount.toString()
            todayCount.text = ctb.todayCount.toString()
            if (ctb.isChangeColor) contract_item.setBackgroundColor(Color.YELLOW) else contract_item.setBackgroundColor(ContextCompat.getColor(MyApplication.instance().applicationContext, R.color.windowBackground))
            setOnClickListener {
                listener(ctb)
            }
        }
    }
}