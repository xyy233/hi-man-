package com.cstore.zhiyazhang.cstoremanagement.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.SystemClock
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import com.cstore.zhiyazhang.cstoremanagement.R




/**
 * Created by zhiya.zhang
 * on 2018/3/28 15:26.
 *
 * 写来玩
 */

class MyButton(context: Context, attrs: AttributeSet) : android.support.v7.widget.AppCompatButton(context, attrs) {

    private var viewWidth: Float = 0f
    private var viewHeight: Float = 0f                   //控件宽高
    private var pointX: Float = 0f
    private var pointY: Float = 0f                          //控件原点坐标（左上角）
    private var maxRadio: Float = 0f                             //扩散的最大半径
    private var shaderRadio: Float = 0f                        //扩散的半径

    private lateinit var colorPaint: Paint          //画笔:背景和水波纹
    private var isPushButton: Boolean = false                 //记录是否按钮被按下

    private var eventX: Float = 0f
    private var eventY: Float = 0f                  //触摸位置的X,Y坐标
    private var downTime: Long = 0                 //按下的时间

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.MyButton)
        val colorValue = array.getColor(R.styleable.MyButton_ripple_color, ContextCompat.getColor(context, R.color.gray2))
        initPaint(colorValue)
        TAP_TIMEOUT = ViewConfiguration.getLongPressTimeout()
        array.recycle()
    }

    companion object {
        private val INVALIDATE_DURATION: Long = 15     //每次刷新的时间间隔
        private var DIFFUSE_GAP = 10                  //扩散半径增量
        private var TAP_TIMEOUT: Int = 0                         //判断点击和长按的时间
    }

    private fun initPaint(rippleColor: Int) {
        colorPaint = Paint()
        colorPaint.color = rippleColor
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (downTime == 0.toLong()) downTime = SystemClock.elapsedRealtime()
                eventX = event.x
                eventY = event.y
                //计算最大半径：
                countMaxRadio()
                isPushButton = true
                postInvalidateDelayed(INVALIDATE_DURATION)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> if (SystemClock.elapsedRealtime() - downTime < TAP_TIMEOUT) {
                DIFFUSE_GAP = 30
                postInvalidate()
            } else {
                clearData()
            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * 计算最大半径的方法
     */
    private fun countMaxRadio() {
        maxRadio = if (viewWidth > viewHeight) {
            if (eventX < viewWidth / 2) {
                viewWidth - eventX
            } else {
                viewWidth / 2 + eventX
            }
        } else {
            if (eventY < viewHeight / 2) {
                viewHeight - eventY
            } else {
                viewHeight / 2 + eventY
            }
        }
    }

    /**
     * 重置数据的方法
     */
    private fun clearData() {
        downTime = 0
        DIFFUSE_GAP = 10
        isPushButton = false
        shaderRadio = 0f
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!isPushButton) return  //如果按钮没有被按下则返回
        //绘制扩散圆形背景
        canvas.save()
        canvas.clipRect(pointX, pointY, pointX + viewWidth, pointY + viewHeight)
        canvas.drawCircle(eventX, eventY, shaderRadio, colorPaint)
        canvas.restore()
        //直到半径等于最大半径
        if (shaderRadio < maxRadio) {
            postInvalidateDelayed(INVALIDATE_DURATION,
                    pointX.toInt(), pointY.toInt(), (pointX + viewWidth).toInt(), (pointY + viewHeight).toInt())
            shaderRadio += DIFFUSE_GAP
        } else {
            clearData()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        this.viewWidth = w.toFloat()
        this.viewHeight = h.toFloat()
    }
}
