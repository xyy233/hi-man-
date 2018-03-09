package com.cstore.zhiyazhang.cstoremanagement.presenter.returnpurchase

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ReturnPurchaseItemBean
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import kotlinx.android.synthetic.main.item_commodity_return.view.*

/**
 * Created by zhiya.zhang
 * on 2017/12/26 14:24.
 */
class ReturnPurchaseCreateAdapter(val data: ArrayList<ReturnPurchaseItemBean>) : RecyclerView.Adapter<ReturnPurchaseCreateAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_commodity_return, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    fun addItem(rb: ArrayList<ReturnPurchaseItemBean>) {
        data.addAll(rb)
        notifyDataSetChanged()
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) = with(itemView) {
            val rb = data[position]
            commodity_id.text = rb.itemNumber//品号
            Glide.with(context).load("http://watchstore.rt-store.com:8086/app/order/getImage${rb.itemNumber}.do")
                    .placeholder(R.mipmap.loading)
                    .error(R.mipmap.load_error)
                    .crossFade()
                    .into(commodity_img)//商品图片
            commodity_retail.text = rb.storeUnitPrice.toString()//零售价
            commodity_name.text = rb.pluName//品名
            commodity_inv.text = rb.invQuantity.toString()//库存
            val total = rb.storeUnitPrice * rb.plnRtnQuantity
            commodity_retail_total.text = total.toString()//零售小计
            //历史净进货累计
            val lsjjh = if (rb.lsjjh == -1) {
                "∞"
            } else {
                rb.lsjjh.toString()
            }
            commodity_lsjjhlj.text = lsjjh
            plnrtn_quantity.text = rb.plnRtnQuantity.toInt().toString()//预退数
            //原因
            return_reason.text = rb.reasonName
            //减
            return_less.setOnClickListener {
                if (rb.plnRtnQuantity > 0) {
                    rb.plnRtnQuantity--
                    rb.editCount--
                    plnrtn_quantity.text = rb.plnRtnQuantity.toInt().toString()
                } else {
                    MyToast.getShortToast(context.getString(R.string.maxOrMinError))
                }
            }
            //加
            return_add.setOnClickListener {

                //测试
                rb.plnRtnQuantity++
                rb.editCount++
                plnrtn_quantity.text = rb.plnRtnQuantity.toInt().toString()

                //lsjjh为空的话写死为-1，判断不为空且加量后lsjjh小与退货量就报错
                /*if (rb.lsjjh != -1 && rb.lsjjh < rb.plnRtnQuantity + 1) {
                    MyToast.getShortToast("退货量不能大于最大退货量，最大退货量为：${rb.lsjjh}")
                    return@setOnClickListener
                }
                if (rb.plnRtnQuantity < rb.invQuantity) {
                    rb.plnRtnQuantity++
                    rb.editCount++
                    plnrtn_quantity.text = rb.plnRtnQuantity.toInt().toString()
                } else {
                    MyToast.getShortToast(context.getString(R.string.rtn_qty_inv))
                }*/
            }
        }
    }
}