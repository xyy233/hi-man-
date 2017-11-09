package com.cstore.zhiyazhang.cstoremanagement.presenter.acceptance

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.AcceptanceBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ReturnAcceptanceBean
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener

/**
 * Created by zhiya.zhang
 * on 2017/9/11 17:31.
 * type 1=进货验收 2=退货验收
 */
class PurchaseAcceptanceAdapter(private val type:Int, private val date:String, private val data: ArrayList<*>, private val onClick: ItemClickListener) : RecyclerView.Adapter<PurchaseAcceptanceAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return if (date==CStoreCalendar.getCurrentDate(3)&&CStoreCalendar.getNowStatus(3)==0){
            data.size+1
        }else{
            data.size
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //position 0=1   size 0=0,
        if (position==data.size){
            //没有数据了，现在是+,隐藏数据页和状态，显示+
            holder.acceptanceData.visibility=View.GONE
            holder.acceptanceStatus.visibility=View.GONE
            holder.add.visibility=View.VISIBLE
            holder.acceptanceItem.setOnClickListener {
                //用长按替代加
                onClick.onItemLongClick(holder,position)
            }
            //全被隐藏了就不需要再管别的了直接结束
            return
        }
        if (type==1){
            holder.dlvText2.text=MyApplication.instance().applicationContext.getString(R.string.dlv_quantity)
            if (User.getUser().storeAttr==1||User.getUser().storeAttr==5){
                holder.costTotalText.visibility=View.GONE
                holder.costTotal.visibility=View.GONE
            }
            data as ArrayList<AcceptanceBean>
                holder.acceptanceData.visibility=View.VISIBLE
                holder.acceptanceStatus.visibility=View.VISIBLE
                holder.add.visibility=View.GONE
                holder.acceptanceId.text=data[position].distributionId
                holder.vendorName.text=data[position].vendorName
                holder.orderCount.text=data[position].ordItemQTY.toString()
                holder.dlvQuantity.text=data[position].dlvQuantity.toString()
                holder.costTotal.text=data[position].sellCostTot.toString()
                holder.retailTotal.text=data[position].retailTotal.toString()
                when(data[position].dlvStatus){
                    0->{holder.acceptanceStatus.setImageResource(R.drawable.no_acceptance)}
                    1->{holder.acceptanceStatus.setImageResource(R.drawable.is_acceptance)}
                    2->{holder.acceptanceStatus.setImageResource(R.drawable.is_acceptance)}
                    3->{holder.acceptanceStatus.setImageResource(R.drawable.tomorrow)}
                }
                holder.acceptanceItem.setOnClickListener {
                    onClick.onItemClick(holder,position)
                }

        }else{
            holder.dlvText2.text=MyApplication.instance().applicationContext.getString(R.string.return_qty)
            if (User.getUser().storeAttr==1||User.getUser().storeAttr==5){
                holder.costTotalText.visibility=View.GONE
                holder.costTotal.visibility=View.GONE
            }
            data as ArrayList<ReturnAcceptanceBean>
                holder.acceptanceData.visibility=View.VISIBLE
                holder.acceptanceStatus.visibility=View.VISIBLE
                holder.add.visibility=View.GONE
                holder.acceptanceId.text=data[position].distributionId
                holder.vendorName.text=data[position].vendorName
                holder.orderCount.text=data[position].plnRtnItemQTY.toString()
                holder.dlvQuantity.text=data[position].rtnQuantity.toString()
                holder.costTotal.text=data[position].sellCostTot.toString()
                holder.retailTotal.text=data[position].retailTotal.toString()
                when(data[position].rtnStatus){
                    0->{holder.acceptanceStatus.setImageResource(R.drawable.no_acceptance)}
                    1->{holder.acceptanceStatus.setImageResource(R.drawable.is_acceptance)}
                    2->{holder.acceptanceStatus.setImageResource(R.drawable.is_acceptance)}
                    3->{holder.acceptanceStatus.setImageResource(R.drawable.tomorrow)}
                }
                holder.acceptanceItem.setOnClickListener {
                    onClick.onItemClick(holder,position)
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_purchase_acceptance, parent, false))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val acceptanceItem=itemView.findViewById<FrameLayout>(R.id.acceptance_item)!!
        val acceptanceData=itemView.findViewById<LinearLayout>(R.id.acceptance_data)!!
        val acceptanceId=itemView.findViewById<TextView>(R.id.acceptance_id)!!
        val vendorName=itemView.findViewById<TextView>(R.id.vendor_name)!!
        val orderCount=itemView.findViewById<TextView>(R.id.order_count)!!
        val dlvQuantity=itemView.findViewById<TextView>(R.id.dlv_quantity)!!
        val costTotal=itemView.findViewById<TextView>(R.id.cost_total)!!
        val retailTotal=itemView.findViewById<TextView>(R.id.retail_total)!!
        val add=itemView.findViewById<ImageView>(R.id.add)!!
        val acceptanceStatus=itemView.findViewById<ImageView>(R.id.acceptance_status)!!

        val dlvText2=itemView.findViewById<TextView>(R.id.dlv_text2)!!
        val costTotalText =itemView.findViewById<TextView>(R.id.cost_total_text)!!
    }
}