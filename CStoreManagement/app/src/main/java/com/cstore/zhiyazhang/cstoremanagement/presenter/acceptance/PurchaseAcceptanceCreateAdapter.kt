package com.cstore.zhiyazhang.cstoremanagement.presenter.acceptance

import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.AcceptanceItemBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ReturnAcceptanceItemBean
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import java.text.DecimalFormat

/**
 * Created by zhiya.zhang
 * on 2017/9/19 15:38.
 */
class PurchaseAcceptanceCreateAdapter(private val type:Int, val data:ArrayList<*>):RecyclerView.Adapter<PurchaseAcceptanceCreateAdapter.ViewHolder>(){
    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val df= DecimalFormat("######0.00")
        if (type==1){
            data as ArrayList<AcceptanceItemBean>
            holder.dlvText.text=MyApplication.instance().applicationContext.getString(R.string.dlv_quantity)
            holder.commodityId.text=data[position].itemId
            holder.commodityName.text=data[position].itemName
            holder.taxSellCost.text=df.format(data[position].taxSellCost)
            holder.retail.text=df.format(data[position].storeUnitPrice)
            holder.dlvQuantity.text=""
            holder.dlvQuantity.keyListener= DigitsKeyListener.getInstance("1234567890")
            holder.dlvQuantity.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    //输入结束
                    if (holder.dlvQuantity.text.toString()!=""){
                        data[position].dlvQuantity=holder.dlvQuantity.text.toString().toInt()
                        data[position].isChange = data[position].dlvQuantity != 0
                    }else{
                        data[position].dlvQuantity=0
                        data[position].isChange=false
                    }
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    //开始输入
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    //文字变化
                }
            })
        }else{
            data as ArrayList<ReturnAcceptanceItemBean>
            holder.dlvText.text=MyApplication.instance().applicationContext.getString(R.string.return_quantity)
            holder.commodityId.text=data[position].itemId
            holder.commodityName.text=data[position].itemName
            holder.taxSellCost.text=df.format(data[position].taxSellCost)
            holder.retail.text=df.format(data[position].storeUnitPrice)
            holder.dlvQuantity.text=""
            holder.dlvQuantity.keyListener= DigitsKeyListener.getInstance("1234567890")
            holder.dlvQuantity.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    if (holder.dlvQuantity.text.toString()!=""){
                        //输入结束
                        data[position].rtnQuantity=holder.dlvQuantity.text.toString().toInt()
                        data[position].isChange = data[position].rtnQuantity != 0
                    }else{
                        data[position].rtnQuantity=0
                        data[position].isChange=false
                    }
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    //开始输入
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    //文字变化
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_purchase_acceptance_create, parent, false))
    }


    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val body=itemView.findViewById<LinearLayout>(R.id.acceptance_create_body)!!
        val commodityId=itemView.findViewById<TextView>(R.id.commodity_id)!!
        val commodityName=itemView.findViewById<TextView>(R.id.commodity_name)!!
        val taxSellCost=itemView.findViewById<TextView>(R.id.tax_sell_cost)!!
        val retail=itemView.findViewById<TextView>(R.id.retail)!!
        val dlvQuantity=itemView.findViewById<TextView>(R.id.dlv_quantity)!!
        val dlvText=itemView.findViewById<TextView>(R.id.dlv_text)!!
    }
}