package com.cstore.zhiyazhang.cstoremanagement.presenter.inverror

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.InvErrorBean
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.item_inv_error.view.*
import java.text.DecimalFormat

/**
 * Created by zhiya.zhang
 * on 2018/2/3 15:59.
 */
class InvErrorAdapter(val data: ArrayList<InvErrorBean>, private val onClick: ItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            when (data[position].flag) {
                7 -> {
                    holder.bindNeedRemove(data[position], position)
                }
                else -> {
                    holder.bind(data[position], position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_inv_error, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }
    private val ip = MyApplication.getIP()
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(ieb: InvErrorBean, position: Int) = with(itemView) {
            val df = DecimalFormat("#.00")
            plu_id.text = ieb.pluId
            Glide.with(context).load("http://$ip:8666/uploadIMG/${ieb.pluId}.png")
                    .placeholder(R.mipmap.loading)
                    .error(R.mipmap.load_error)
                    .crossFade()
                    .into(plu_img)//商品图片
            plu_retail.text = df.format(ieb.storeUnitPrice)
            now_inv_detail.text = ieb.inv.toString()
            init_inv_detail.text = ieb.befInvQuantity.toString()
            plu_name.text = ieb.pluName
            is_order.isChecked = ieb.orderMode == "Y"
            is_return.isChecked = ieb.returnType == "Y"
            is_sales_detail.text = ieb.days
            status_detail.text = ieb.status.toString()
            chenlie_detail.text = ieb.layClass
            on_the_way_detail.text = ieb.dlv.toString()
            dms_detail.text = ieb.dms.toString()
            m_package_detail.text = ieb.unitClass.toString()
            init_order_detail.text = ieb.minImaOrderQuantity.toString()
            end_sales_detail.text = ieb.saleDate
            end_acc_detail.text = ieb.dlvDate
            min_qty_detail.text = ieb.sfQty.toString()
            card_item.setOnClickListener {
                onClick.onItemEdit(ieb, position)
            }
        }

        fun bindNeedRemove(ieb: InvErrorBean, position: Int) = with(itemView) {
            head_box.visibility = View.GONE
            first_box.visibility = View.GONE
            on_the_way_box.visibility = View.GONE
            m_package_box.visibility = View.GONE
            init_order_box.visibility = View.GONE
            four_box.visibility = View.GONE
            five_box.visibility = View.GONE
            val df = DecimalFormat("#.00")
            plu_id.text = ieb.pluId
            Glide.with(context).load("http://$ip:8666/uploadIMG/${ieb.pluId}.png")
                    .placeholder(R.mipmap.loading)
                    .error(R.mipmap.load_error)
                    .crossFade()
                    .into(plu_img)//商品图片
            plu_retail.text = df.format(ieb.storeUnitPrice)
            status_detail.text = ieb.status.toString()
            chenlie_detail.text = ieb.layClass
            dms_detail.text = ieb.dms.toString()
            card_item.setOnClickListener {
                onClick.onItemEdit(ieb, position)
            }
        }
    }

}