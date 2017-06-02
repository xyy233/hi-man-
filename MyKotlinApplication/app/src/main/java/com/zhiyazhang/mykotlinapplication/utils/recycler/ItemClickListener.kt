package com.zhiyazhang.mykotlinapplication.utils.recycler

import android.view.View

/**
 * Created by zhiya.zhang
 * on 2017/6/2 17:09.
 */
interface ItemClickListener {
    /**
     * 点击一下
     */
    fun onItemClick(view: View, position: Int)

    /**
     * 长按
     */
    fun onItemLongClick(view: View, position: Int)
}