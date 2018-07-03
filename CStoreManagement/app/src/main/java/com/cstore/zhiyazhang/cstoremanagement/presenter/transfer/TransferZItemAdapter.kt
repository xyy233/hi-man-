package com.cstore.zhiyazhang.cstoremanagement.presenter.transfer

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.TransItem
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import java.text.DecimalFormat

/**
 * Created by zhiya.zhang
 * on 2018/5/11 12:24.
 */
class TransferZItemAdapter(val data: ArrayList<TransItem>, private var isShowEdit: Boolean, val context: Context) : RecyclerView.Adapter<TransferZItemAdapter.ViewHolder>() {
    private val df = DecimalFormat("#####.##")

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //取消复用，edittext有复用的话就混乱了
        holder.setIsRecyclable(false)
        val d = data[position]
        Glide.with(context).load("http://${MyApplication.getIP()}:8666/uploadIMG/${d.itemNo}.png")
                .placeholder(R.mipmap.loading)
                .error(R.mipmap.load_error)
                .crossFade()
                .into(holder.trsItem)
        holder.id.text = d.itemNo
        holder.name.text = d.itemName
        holder.trsQty.text = d.trsQty.toString()
        if (d.inv != null) {
            holder.invBox.visibility = View.VISIBLE
            holder.inv.text = d.inv.toString()
            //只有在可编辑的情况下才处理修改量
            if (isShowEdit) {
                //调拨量大于库存就要修改量为库存
                if (d.trsQty > d.inv!!) {
                    if (d.storeTrsQty==null){
                        if (d.inv!! < 0) {
                            d.storeTrsQty = 0
                        } else {
                            d.storeTrsQty = d.inv
                        }
                    }
                }
            }
        } else {
            holder.invBox.visibility = View.GONE
        }
        if (d.storeUnitPrice != null) {
            holder.sellCostBox.visibility = View.VISIBLE
            try {
                holder.sellCost.text = df.format(d.storeUnitPrice.toString())
            } catch (e: Exception) {
                holder.sellCost.text = d.storeUnitPrice.toString()
            }
        } else {
            holder.sellCostBox.visibility = View.GONE
        }
        if (d.storeTrsQty != null) holder.trsQty2.setText(d.storeTrsQty.toString())

        if (isShowEdit) {
            holder.trsQty2.isEnabled = true
            holder.trsQty2.inputType = InputType.TYPE_CLASS_NUMBER
            holder.trsQty2.keyListener = DigitsKeyListener.getInstance("1234567890")
            holder.trsQty2.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    try {
                        //输入结束
                        if (holder.trsQty2.text.toString() != "" && holder.trsQty2.text != null) {
                            val tq2 = holder.trsQty2.text.toString().toInt()
                            if (tq2 == 0) {
                                d.storeTrsQty = 0
                                return
                            }
                            if (d.inv != null) {
                                if (tq2 > d.inv!!) {
                                    if (d.inv!! < 0) {
                                        d.storeTrsQty = 0
                                    } else {
                                        d.storeTrsQty = d.inv
                                        MyToast.getShortToast("修改量不能大于库存")
                                    }
                                    holder.trsQty2.setText(d.storeTrsQty.toString())
                                    return
                                }
                            }
                            d.storeTrsQty = holder.trsQty2.text.toString().toInt()
                        }
                    } catch (e: Exception) {
                        holder.trsQty2.setText("")
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
        } else {
            holder.trsQty2.isEnabled = false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_trsz_item, parent, false))

    fun changeShowEdit(isShowEdit: Boolean) {
        this.isShowEdit = isShowEdit
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val id = itemView.findViewById<TextView>(R.id.commodity_id)!!
        val trsItem = itemView.findViewById<ImageView>(R.id.trs_img)!!
        val name = itemView.findViewById<TextView>(R.id.commodity_name)!!
        val trsQty = itemView.findViewById<TextView>(R.id.trs_qty)!!
        val trsQty2 = itemView.findViewById<EditText>(R.id.trs_qty2)!!
        val invBox = itemView.findViewById<LinearLayout>(R.id.inv_box)!!
        val inv = itemView.findViewById<TextView>(R.id.inv)!!
        val sellCostBox = itemView.findViewById<LinearLayout>(R.id.sell_cost_body)!!
        val sellCost = itemView.findViewById<TextView>(R.id.sell_cost)!!
    }
}