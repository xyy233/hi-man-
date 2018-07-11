package com.cstore.zhiyazhang.cstoremanagement.presenter.transfer

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.TransResult
import com.cstore.zhiyazhang.cstoremanagement.bean.TransServiceBean
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.item_trsz.view.*

/**
 * Created by zhiya.zhang
 * on 2018/5/11 10:13.
 */
class TransferServiceAdapter(val tr: TransResult, private val onClick: ItemClickListener) : RecyclerView.Adapter<TransferServiceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_trsz, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tr.rows[position], onClick, position)
    }

    override fun getItemCount(): Int {
        return tr.rows.size
    }

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        fun bind(tb: TransServiceBean, onClick: ItemClickListener, position: Int) = with(itemView) {
            if (tb.trsType > 0) {
                type.setImageResource(R.drawable.dr)
                status.visibility = View.GONE
                trs_fee.text = ""
                if (tb.isDone == 0) {
                    trs_fee_box.visibility = View.VISIBLE
                    trs_fee_box.setBackgroundColor(ContextCompat.getColor(MyApplication.instance().applicationContext, R.color.delete_red))
                    trs_fee_text.text = MyApplication.instance().applicationContext.getString(R.string.trs_done)
                } else {
                    trs_fee_box.visibility = View.GONE
                }
            } else {
                type.setImageResource(R.drawable.dc)
                trs_fee_box.visibility = View.VISIBLE
                status.visibility = View.VISIBLE
                if (tb.requestNumber == null)
                    status.setImageResource(R.drawable.no_sh)
                else
                    status.setImageResource(R.drawable.is_sh)
                if (tb.requestNumber != null) {
                    trs_fee_box.visibility = View.VISIBLE
                    trs_fee.text = if (tb.trsFee == null) {
                        trs_fee_box.setBackgroundColor(ContextCompat.getColor(MyApplication.instance().applicationContext, R.color.delete_red))
                        MyApplication.instance().applicationContext.getString(R.string.click_edit_trs_fee)
                    } else {
                        trs_fee_box.setBackgroundColor(ContextCompat.getColor(MyApplication.instance().applicationContext, R.color.item_green1))
                        tb.trsFee.toString()
                    }
                } else {
                    trs_fee_box.visibility = View.GONE
                }
            }
            if (tb.requestNumber != null) {
                trs_number_box.visibility = View.VISIBLE
                trs_number.text = tb.requestNumber
            } else {
                trs_number_box.visibility = View.GONE
            }
            dis_time.text = tb.disTime
            ostore_id.text = tb.trsStoreId
            ostore_name.text = tb.trsStoreName
            trs_count.text = tb.items.size.toString()
            trs_qty.text = tb.trsQuantities.toString()

            if (tb.storeUnitPrice != null) {
                sell_cost_body.visibility = View.VISIBLE
                sell_cost.text = tb.storeUnitPrice.toString()
            } else {
                sell_cost_body.visibility = View.GONE
            }
            trsz_box.setOnClickListener { onClick.onItemClick(this@ViewHolder, position) }
            trs_fee_box.setOnClickListener {
                //调入未确认，调出未提交配送费的才允许点击修改
                if ((tb.trsType > 0 && tb.isDone == 0) || (tb.trsType < 0 && tb.trsFee == null)) {
                    onClick.onItemEdit(tb, position)
                }
            }
        }
    }

}