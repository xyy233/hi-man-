package com.zhiyazhang.mykotlinapplication.utils

import android.content.Context
import android.support.v4.view.ScrollingView
import android.support.v4.widget.SwipeRefreshLayout
import android.view.MotionEvent
import android.view.View
import java.lang.Exception

/**
 * Created by zhiya.zhang
 * on 2017/6/9 17:20.
 */

class MySwipeRefresh(context: Context) : SwipeRefreshLayout(context) {
    var mHasScrollingChild = false
    var mScrollingChild: ScrollingView? = null
    override fun onFinishInflate() {
        super.onFinishInflate()
        // 此时SwipeRefreshLayout会有一个CircleImageView的子View
        if (childCount > 1 && getChildAt(1) is ScrollingView) {
            mHasScrollingChild = true
            mScrollingChild = getChildAt(1) as ScrollingView
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (mHasScrollingChild) {
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    isEnabled = true  //每次按下时先开启SwipeRefreshLayout，保证正常工作
                    if (mScrollingChild!!.computeVerticalScrollOffset() != 0) {
                        isEnabled = false  //如果子View不处于顶部则禁用SwipeRefreshLayout
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    fun autoRefresh() {
        try {
            //这里是拿到下拉那个进度条动画的控件
            val mCircleView = SwipeRefreshLayout::class.java.getDeclaredField("mCircleView")
            mCircleView.isAccessible = true
            val progress = (mCircleView.get(this) as View)
            progress.visibility = View.VISIBLE

            //这里是为了获取刷新函数，这里设置为true，就可以
            val setRefreshing = SwipeRefreshLayout::class.java.getDeclaredMethod("setRefreshing", Boolean::class.javaPrimitiveType, Boolean::class.javaPrimitiveType)
            setRefreshing.isAccessible = true
            setRefreshing.invoke(this, true, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
