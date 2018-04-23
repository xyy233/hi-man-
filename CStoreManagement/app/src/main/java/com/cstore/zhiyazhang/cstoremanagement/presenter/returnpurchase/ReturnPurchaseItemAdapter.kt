package com.cstore.zhiyazhang.cstoremanagement.presenter.returnpurchase

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ReasonBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ReturnPurchaseItemBean
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.item_commodity_return.view.*
import kotlinx.android.synthetic.main.item_purchase_acceptance.view.*

/**
 * Created by zhiya.zhang
 * on 2017/11/8 11:16.
 */
class ReturnPurchaseItemAdapter(private val date: String, private val data: ArrayList<ReturnPurchaseItemBean>, private val reason: ArrayList<ReasonBean>, private val onClick: ItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private val TYPE_ITEM = 0
        private val TYPE_FOOTER = 1
    }
    private val ip = MyApplication.getIP()
    private val adapterResource = ArrayList<String>()

    init {
        reason.forEach {
            adapterResource.add(it.reasonName)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ITEM -> {
                ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_commodity_return, parent, false))
            }
            TYPE_FOOTER -> {
                FooterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_purchase_acceptance, parent, false))
            }
            else -> {
                ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_commodity_return, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                holder.bind(position)
            }
            is FooterViewHolder -> {
                holder.bind()
            }
        }
    }

    override fun getItemCount(): Int {
        return if (date == CStoreCalendar.getCurrentDate(2) && CStoreCalendar.getNowStatus(2) == 0) {
            data.size + 1
        } else {
            data.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position + 1 == itemCount) {
            if (date == CStoreCalendar.getCurrentDate(2) && CStoreCalendar.getNowStatus(2) == 0) {
                TYPE_FOOTER
            } else {
                TYPE_ITEM
            }
        } else {
            TYPE_ITEM
        }
    }

    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {

        fun bind(position: Int) = with(itemView) {
            val rb = data[position]
            commodity_id.text = rb.itemNumber//品号
            Glide.with(context).load("http://$ip:8666/uploadIMG/${rb.itemNumber}.png")
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
            //下拉框原因注释掉，需要再解放
            /*val adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, adapterResource)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            acceptance_spinner.adapter = adapter
            acceptance_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val newId = reason[acceptance_spinner.selectedItemPosition].reasonId
                    if (rb.reasonNumber != newId) {
                        rb.reasonNumber = newId
                        rb.reasonName = reason[acceptance_spinner.selectedItemPosition].reasonName
                        rb.editCount++
                    }
                }
            }*/
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
               /* rb.plnRtnQuantity++
                rb.editCount++
                plnrtn_quantity.text = rb.plnRtnQuantity.toInt().toString()*/

                //lsjjh为空的话写死为-1，判断不为空且加量后lsjjh小与退货量就报错
                if (rb.lsjjh != -1 && rb.lsjjh < rb.plnRtnQuantity + 1) {
                    MyToast.getShortToast("退货量不能大于最大退货量，最大退货量为：${rb.lsjjh}")
                    return@setOnClickListener
                }
                if (rb.plnRtnQuantity < rb.invQuantity) {
                    rb.plnRtnQuantity++
                    rb.editCount++
                    plnrtn_quantity.text = rb.plnRtnQuantity.toInt().toString()
                } else {
                    MyToast.getShortToast(context.getString(R.string.rtn_qty_inv))
                }
            }

            if (CStoreCalendar.getCurrentDate(2) != date) {
                //不在可操作时间
                //acceptance_spinner.isEnabled = false
                return_less.isEnabled = false
                return_add.isEnabled = false
                return_add.visibility = View.GONE
                return_less.visibility = View.GONE
            } else {
                //acceptance_spinner.isEnabled = true
                return_less.isEnabled = true
                return_add.isEnabled = true
                return_add.visibility = View.VISIBLE
                return_less.visibility = View.VISIBLE
            }
        }
    }

    inner class FooterViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        fun bind() = with(itemView) {
            acceptance_data.visibility = View.GONE
            acceptance_status.visibility = View.GONE
            add.visibility = View.VISIBLE
            acceptance_item.setOnClickListener {
                onClick.onItemClick(this@FooterViewHolder, 0)
            }
        }
    }

    fun addItem(rb: ArrayList<ReturnPurchaseItemBean>) {
        data.addAll(rb)
        notifyDataSetChanged()
    }
}