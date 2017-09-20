package com.zhang.givejoe

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.webkit.WebView

/**
 * Created by zhiya.zhang
 * on 2017/9/20 17:07.
 */

class cbWebView : WebView {

    private var gestureDetector: GestureDetector? = null
    private var cb: Context? = null
    private var mIWeb:IWebViewScroll?=null

    constructor(context: Context) : super(context) {
        cb = context
        gestureDetector = GestureDetector(this.context,
                onGestureListener)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        cb = context
        gestureDetector = GestureDetector(this.context,
                onGestureListener)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        cb = context
        gestureDetector = GestureDetector(this.context,
                onGestureListener)
    }

    // 重载滑动事件
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(evt: MotionEvent): Boolean {
        gestureDetector!!.onTouchEvent(evt)
        return super.onTouchEvent(evt)
    }

    private val onGestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float,
                             velocityY: Float): Boolean {

            val x = e2.x - e1.x
            val y = e2.y - e1.y

            if (x > 500) {
                // 右滑 事件
                this@cbWebView.goBack()
            }

            return true
        }
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (mIWeb!=null&&t==0){
            mIWeb!!.onTop()
        }else if (mIWeb!=null&&t!=0){
            mIWeb!!.onNotTop()
        }
    }

    fun setIWebViewScroll(IWeb:IWebViewScroll){
        this.mIWeb=IWeb
    }

    interface IWebViewScroll {
        fun onTop()
        fun onNotTop()
    }
}
