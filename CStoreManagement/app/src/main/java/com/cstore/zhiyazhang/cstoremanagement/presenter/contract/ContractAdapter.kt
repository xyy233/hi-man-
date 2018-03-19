package com.cstore.zhiyazhang.cstoremanagement.presenter.contract

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ContractBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ContractResult
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.RecyclerOnTouch
import kotlinx.android.synthetic.main.item_loading.view.*

/**
 * Created by zhiya.zhang
 * on 2017/6/14 15:58.
 */
class ContractAdapter(val cr: ContractResult, val context: Context, val onTouch: RecyclerOnTouch, var isJustLook: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private val TYPE_ITEM = 0  //普通Item View
        private val TYPE_FOOTER = 1  //底部FootView
        private var load_more_status = 0//上拉加载更多状态-默认为0
        val PULLUP_LOAD_MORE = 0//上拉加载更多
        val LOADING_MORE = 1//正在加载中
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            when (viewType) {//是什么页面就添加对应的
                TYPE_ITEM -> {
                    ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_contract, parent, false))
                }
                TYPE_FOOTER -> {
                    FooterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false))
                }
                else ->
                    ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_contract, parent, false))
            }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                holder.cid.text = cr.detail[position].cId
                holder.cname.text = cr.detail[position].cName
                holder.cin.text = cr.detail[position].inventory.toString()
                holder.cnc.text = cr.detail[position].tonightCount.toString()
                holder.cprice.text = cr.detail[position].cPrice.toString()
                holder.editCdc.text = cr.detail[position].todayCount.toString()
                holder.arriveDate.text = when (cr.detail[position].arriveDate) {
                    1 -> {
                        holder.arriveDate.setTextColor(ContextCompat.getColor(context, R.color.cstore_red))
                        context.getString(R.string.todayCount)
                    }
                    2 -> {
                        holder.arriveDate.setTextColor(ContextCompat.getColor(context, R.color.cstore_red))
                        context.getString(R.string.tomorrow_count)
                    }
                    3 -> {
                        holder.arriveDate.setTextColor(ContextCompat.getColor(context, R.color.add_less))
                        context.getString(R.string.acquired_count)
                    }
                    else -> {
                        holder.arriveDate.setTextColor(ContextCompat.getColor(context, R.color.cstore_red))
                        context.getString(R.string.tomorrow_count)
                    }
                }
                //通过判断商店修改决定是否改色
                when {
                    cr.detail[position].todayStore > 0 -> holder.myCommodify.setBackgroundColor(ContextCompat.getColor(context, R.color.add_bg))
                    cr.detail[position].todayStore < 0 -> holder.myCommodify.setBackgroundColor(ContextCompat.getColor(context, R.color.less_bg))
                    cr.detail[position].todayStore == 0 -> holder.myCommodify.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                }
                Glide.with(context).load(cr.detail[position].img_url)
                        .placeholder(R.mipmap.loading)
                        .error(R.mipmap.load_error)
                        .crossFade()
                        .into(holder.commodifyImg)
                holder.commodifyImg.setOnClickListener {
                    onTouch.onClickImage(cr.detail[position], position)
                }
                if (isJustLook) {
                    holder.alLine.visibility = View.GONE
                    holder.csCount.text = cr.detail[position].cSug.toString()
                    holder.cType.text = cr.detail[position].cType
                    holder.add.visibility = View.GONE
                    holder.less.visibility = View.GONE
                } else {
                    //提示不用管，只是说touch覆盖了辅助提示
                    holder.add.setOnTouchListener { _, event ->
                        onTouch.onTouchAddListener(cr.detail[position], event, position)
                        true
                    }
                    holder.less.setOnTouchListener { _, event ->
                        onTouch.onTouchLessListener(cr.detail[position], event, position)
                        true
                    }
                }
            }
            is FooterViewHolder -> {
                holder.bind()
            }
            else -> {
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        //确认是添加正常view还是底部加载view
        return if (position + 1 == itemCount) {
            if (cr.total > 10 && cr.detail.size < cr.total) {
                TYPE_FOOTER
            } else {
                TYPE_ITEM
            }
        } else {
            TYPE_ITEM
        }
    }

    //当前页不足10且已有的数据少于总数据才会添加底部加载
    override fun getItemCount(): Int =
            if (cr.total > 10 && cr.detail.size < cr.total) cr.detail.size + 1
            else cr.detail.size


    fun addItem(newDates: List<ContractBean>) {
        (cr.detail as ArrayList<ContractBean>).addAll(newDates)
    }

    /**
     * 上拉加载更多 PULLUP_LOAD_MORE=0
     * 正在加载中 LOADING_MORE=1
     * 加载完成已经没有更多数据了 NO_MORE_DATA=2;
     */
    fun changeMoreStatus(statud: Int) {
        load_more_status = statud
        notifyDataSetChanged()
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cid = itemView.findViewById<TextView>(R.id.cid)!!
        val cname = itemView.findViewById<TextView>(R.id.cname)!!
        val cin = itemView.findViewById<TextView>(R.id.cin)!!
        val cnc = itemView.findViewById<TextView>(R.id.cnc)!!
        val cprice = itemView.findViewById<TextView>(R.id.cprice)!!
        val myCommodify = itemView.findViewById<LinearLayout>(R.id.my_commodify)!!
        val commodifyImg = itemView.findViewById<ImageView>(R.id.commodify_img)!!
        val less = itemView.findViewById<ImageButton>(R.id.less)!!
        val add = itemView.findViewById<ImageButton>(R.id.add)!!
        val editCdc = itemView.findViewById<TextView>(R.id.edit_cdc)!!
        val arriveDate = itemView.findViewById<TextView>(R.id.arriveDate)!!
        val cType = itemView.findViewById<TextView>(R.id.ctype)!!
        val csCount = itemView.findViewById<TextView>(R.id.cs_count)!!
        val alLine = itemView.findViewById<View>(R.id.al_line)!!
    }

    private inner class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() = with(itemView) {
            when (load_more_status) {//通过不同状态修改显示内容
                PULLUP_LOAD_MORE -> footLoading.text = context.getString(R.string.loadingUp)
                LOADING_MORE -> footLoading.text = context.getString(R.string.loading)
            }
        }
    }
}