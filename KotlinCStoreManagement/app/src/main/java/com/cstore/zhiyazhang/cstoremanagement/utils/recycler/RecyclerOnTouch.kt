package com.cstore.zhiyazhang.cstoremanagement.utils.recycler

import android.view.MotionEvent

/**
 * Created by zhiya.zhang
 * on 2017/6/14 16:41.
 */
interface RecyclerOnTouch {
    fun <T> onClickImage(objects: T, position: Int)
    fun <T> onTouchAddListener(objects: T, event: MotionEvent, position:Int)
    fun <T> onTouchLessListener(objects: T, event: MotionEvent, position:Int)
}