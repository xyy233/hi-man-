package com.cstore.zhiyazhang.cstoremanagement.utils.recycler

import android.support.v7.widget.RecyclerView

/**
 * Created by zhiya.zhang
 * on 2017/10/25 16:33.
 */
interface AddLessClickListener{
    fun <T> onItemClick(view: RecyclerView.ViewHolder, beanData:T, position:Int, type:Int)
    fun <T> onItemLongClick(view: RecyclerView.ViewHolder, beanData:T, position:Int, type:Int){}
    fun <T> onItemRemove(data:T, position: Int){}
}