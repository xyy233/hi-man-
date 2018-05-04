package com.cstore.zhiyazhang.cstoremanagement.presenter.personnel

import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.CheckInBean
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.item_check.view.*
import java.io.ByteArrayOutputStream

/**
 * Created by zhiya.zhang
 * on 2018/5/4 15:47.
 */
class CheckInDYAdapter(val datas: List<*>, private val itemClick: ItemClickListener) : RecyclerView.Adapter<CheckInDYAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (datas[position] is CheckInBean) {
            holder.bind(datas[position] as CheckInBean, itemClick, position)
        }
    }

    override fun getItemCount(): Int = datas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_check, parent, false))


    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val checkImg = itemView.findViewById<ImageView>(R.id.check_img)!!
        fun bind(data: CheckInBean, itemClick: ItemClickListener, position: Int) = with(itemView) {
            val date = MyTimeUtil.getStringByDate(MyTimeUtil.getDateByString3(data.fileName.substring(14, 28)))
            if (data.fileImg != null) {
                val baos = ByteArrayOutputStream()
                data.fileImg!!.compress(Bitmap.CompressFormat.PNG, 100, baos)
                Glide.with(context).load(baos.toByteArray()).crossFade().into(checkImg)
                checkImg.setOnClickListener { itemClick.onItemEdit(data, position) }
            }
            check_msg.text = date
        }
    }
}