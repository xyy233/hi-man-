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
        if (date==CStoreCalendar.getCurrentDate(3)&&CStoreCalendar.getNowStatus(3)==0){
            return data.size+1
        }else{
            return data.size
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position==data.size){
            //没有数据了，现在是+,隐藏数据页和状态，显示+
            holder.acceptanceData.visibility=View.GONE
            holder.acceptanceStatus.visibility=View.GONE
            holder.add.visibility=View.VISIBLE
            holder.acceptanceItem.setOnClickListener {
                //用长按替代加
                onClick.onItemLongClick(holder.acceptanceItem,position)
            }
            //全被隐藏了就不需要再管别的了直接结束
            return
        }
        if (type==1){
            holder.dlvText.text=MyApplication.instance().applicationContext.getString(R.string.dlv_count)
            holder.ordText.text=MyApplication.instance().applicationContext.getString(R.string.order_quantity)
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
                holder.dlvCount.text=data[position].dlvItemQTY.toString()
                holder.orderQuantity.text=data[position].ordQuantity.toString()
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
                    onClick.onItemClick(holder.acceptanceItem,position)
                }

        }else{
            holder.dlvText.text=MyApplication.instance().applicationContext.getString(R.string.return_item_qty)
            holder.ordText.text=MyApplication.instance().applicationContext.getString(R.string.return_qty)
            holder.dlvText2.text=MyApplication.instance().applicationContext.getString(R.string.ord_return_qty)
            holder.costTotalText.visibility=View.VISIBLE
            holder.costTotal.visibility=View.VISIBLE
            data as ArrayList<ReturnAcceptanceBean>
                holder.acceptanceData.visibility=View.VISIBLE
                holder.acceptanceStatus.visibility=View.VISIBLE
                holder.add.visibility=View.GONE
                holder.acceptanceId.text=data[position].distributionId
                holder.vendorName.text=data[position].vendorName
                holder.orderCount.text=data[position].plnRtnItemQTY.toString()
                holder.dlvCount.text=data[position].actRtnItemQTY.toString()
                holder.orderQuantity.text=data[position].ordQuantity.toString()
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
                    onClick.onItemClick(holder.acceptanceItem,position)
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
        val dlvCount=itemView.findViewById<TextView>(R.id.dlv_count)!!
        val orderQuantity=itemView.findViewById<TextView>(R.id.order_quantity)!!
        val dlvQuantity=itemView.findViewById<TextView>(R.id.dlv_quantity)!!
        val costTotal=itemView.findViewById<TextView>(R.id.cost_total)!!
        val retailTotal=itemView.findViewById<TextView>(R.id.retail_total)!!
        val add=itemView.findViewById<ImageView>(R.id.add)!!
        val acceptanceStatus=itemView.findViewById<ImageView>(R.id.acceptance_status)!!

        val dlvText=itemView.findViewById<TextView>(R.id.dlv_text)!!
        val dlvText2=itemView.findViewById<TextView>(R.id.dlv_text2)!!
        val ordText=itemView.findViewById<TextView>(R.id.order_text)!!
        val costTotalText =itemView.findViewById<TextView>(R.id.cost_total_text)!!
    }
}