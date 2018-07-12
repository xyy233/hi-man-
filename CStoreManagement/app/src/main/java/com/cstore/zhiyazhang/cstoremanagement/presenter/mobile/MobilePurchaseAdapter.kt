package com.cstore.zhiyazhang.cstoremanagement.presenter.mobile

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.MobilePluBean
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener

/**
 * Created by zhiya.zhang
 * on 2018/6/28 16:14.
 */
class MobilePurchaseAdapter(val data: ArrayList<MobilePluBean>, private val click: ItemClickListener) : RecyclerView.Adapter<MobilePurchaseAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val d = data[position]
        Glide.with(MyApplication.instance().applicationContext).load("http://${MyApplication.getIP()}:8666/uploadIMG/${d.pluId}.png")
                .placeholder(R.mipmap.loading)
                .error(R.mipmap.load_error)
                .crossFade()
                .into(holder.img)
        holder.id.text = d.pluId
        holder.name.text = d.pluName
        if (d.goodsNum == null) d.goodsNum = 1
        holder.qty.text = d.goodsNum.toString()
        holder.storeUnitPrice.text = d.storeUnitPrice.toString()
        holder.less.setOnClickListener {
            if (d.goodsNum!! <= 0) {
                MyToast.getLongToast("不能继续减少")
            } else {
                d.goodsNum = d.goodsNum!! - 1
                holder.qty.text = d.goodsNum.toString()
            }
        }
        holder.add.setOnClickListener {
            click.onItemEdit(d, position)
            if (d.goodsNum!! >= 992) {
            MyToast.getLongToast("不能大于100")
        } else {
            d.goodsNum = d.goodsNum!! + 1
            holder.qty.text = d.goodsNum.toString()
        }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_mjb_item, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val id = item.findViewById<TextView>(R.id.item_no)!!
        val name = item.findViewById<TextView>(R.id.item_name)!!
        val img = item.findViewById<ImageView>(R.id.item_img)!!
        val storeUnitPrice = item.findViewById<TextView>(R.id.store_unit_price)!!
        val qty = item.findViewById<TextView>(R.id.mjb_qty)!!
        val less = item.findViewById<ImageButton>(R.id.mjb_qty_less)!!
        val add = item.findViewById<ImageButton>(R.id.mjb_qty_add)!!
    }
}