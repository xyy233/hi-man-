package com.cstore.zhiyazhang.cstoremanagement.presenter.transfer

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.TransResult
import com.cstore.zhiyazhang.cstoremanagement.bean.TransServiceBean
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.item_trsz.view.*

/**
 * Created by zhiya.zhang
 * on 2018/5/11 10:13.
 */
class TransferServiceAdapter(val tr: TransResult, private val onClick: ItemClickListener) : RecyclerView.Adapter<TransferServiceAdapter.ViewHolder>() {
    private val data = tr.rows

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_trsz, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position], onClick, position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        fun bind(tb: TransServiceBean, onClick: ItemClickListener, position: Int) = with(itemView) {
            if (tb.trsType > 0) {
                type.setImageResource(R.drawable.dr)
                status.visibility = View.GONE
            } else {
                type.setImageResource(R.drawable.dc)
                status.visibility = View.VISIBLE
                if (tb.requestNumber == null)
                    status.setImageResource(R.drawable.no_sh)
                else
                    status.setImageResource(R.drawable.is_sh)
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
            trsz_box.setOnClickListener { onClick.onItemClick(this@ViewHolder, position) }
        }
    }

}