package com.zhiyazhang.mykotlinapplication.utils.recycler

import android.support.v7.widget.RecyclerView

/**
 * Created by zhiya.zhang
 * on 2017/6/2 17:09.
 */
interface ItemClickListener {
    /**
     * 点击一下
     */
    fun onItemClick(view: RecyclerView.ViewHolder, position: Int)

    /**
     * 长按
     */
    fun onItemLongClick(view: RecyclerView.ViewHolder, position: Int) {}

    fun <T> onItemRemove(data: T, position: Int) {}

    fun <T> onItemEdit(data: T, position: Int) {}
}