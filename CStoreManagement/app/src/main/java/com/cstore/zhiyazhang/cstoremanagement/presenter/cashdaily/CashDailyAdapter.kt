package com.cstore.zhiyazhang.cstoremanagement.presenter.cashdaily

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.CashDailyBean
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener

/**
 * Created by zhiya.zhang
 * on 2017/9/5 11:29.
 */
class CashDailyAdapter(val data:ArrayList<CashDailyBean>, private val onClick:ItemClickListener, val date:String): RecyclerView.Adapter<CashDailyAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //设置显示
        holder.itemName.text=data[position].cdName
        holder.itemNum.text=data[position].cdValue
        //设置能否修改
        if (data[position].isEdit!="N"&&date==CStoreCalendar.getCurrentDate(1)){
            when(data[position].isEdit){
                "Y"->{
                    //谁都可以修改
                    holder.item.isEnabled=true
                    if (data[position].cdId=="1003"){
                        holder.itemNum.setTextColor(ContextCompat.getColor(MyApplication.instance().applicationContext,R.color.delete_red))
                        holder.itemNum.setBackgroundResource(R.drawable.bg_orange_line)
                    }else{
                        holder.itemNum.setTextColor(ContextCompat.getColor(MyApplication.instance().applicationContext,R.color.sure))
                        holder.itemNum.setBackgroundResource(R.drawable.bg_orange_line)
                    }
                }
                "S"->{
                    //只有管理员能修改
                    if (User.getUser().uId=="99999990"){
                        holder.item.isEnabled=true
                        if (data[position].cdId=="1003"){
                            holder.itemNum.setTextColor(ContextCompat.getColor(MyApplication.instance().applicationContext,R.color.delete_red))
                            holder.itemNum.setBackgroundResource(R.drawable.bg_orange_line)
                        }else{
                            holder.itemNum.setTextColor(ContextCompat.getColor(MyApplication.instance().applicationContext,R.color.sure))
                            holder.itemNum.setBackgroundResource(R.drawable.bg_orange_line)
                        }
                    }else {
                        holder.item.isEnabled=false
                        holder.itemNum.setTextColor(ContextCompat.getColor(MyApplication.instance().applicationContext,R.color.gray))
                        holder.itemNum.setBackgroundResource(R.color.white)
                    }
                }
            }
        }else {
            holder.item.isEnabled = data[position].cdId=="1100"
            holder.itemNum.setTextColor(ContextCompat.getColor(MyApplication.instance().applicationContext,R.color.gray))
            holder.itemNum.setBackgroundResource(R.color.white)
        }
        //设置点击事件
        holder.item.setOnClickListener {
            onClick.onItemClick(holder,position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_cash_daily,parent,false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val item= itemView.findViewById<LinearLayout>(R.id.item_cash_daily)!!
        val itemName=itemView.findViewById<TextView>(R.id.item_cash_daily_name)!!
        val itemNum=itemView.findViewById<TextView>(R.id.item_cash_daily_number)!!
    }
}