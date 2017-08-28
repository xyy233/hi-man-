package com.cstore.zhiyazhang.cstoremanagement.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.util.Log
import android.view.*


/**
 * Created by zhiya.zhang
 * on 2017/6/21 12:16.
 */
class MyZoomImageView : AppCompatImageView, ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener {
    private val TAG = MyZoomImageView::class.java.simpleName

    val SCALE_MAX = 4.0f
    private val SCALE_MID = 2.0f
    /**
     * 初始化时的缩放比例，如果图片宽或高大于屏幕，此值将小于0
     */
    private var initScale = 1.0f

    /**
     * 用于存放矩阵的9个值
     */
    private val matrixValues = FloatArray(9)

    private var once = true

    /**
     * 缩放的手势检测
     */
    private var mScaleGestureDetector: ScaleGestureDetector? = null

    private val mScaleMatrix = Matrix()

    private var lastPointerCount = 0
    private var mLastX = 0f
    private var mLastY = 0f
    private var mGestureDetector: GestureDetector? = null
    private var isCheckTopAndBottom = true
    private var isCheckLeftAndRight = true
    private var isAutoScale = false
    private var mTouchSlop = 0
    private var isCanDrag = false

    constructor(context: Context) : this(context, null)

    @SuppressLint("ClickableViewAccessibility")
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        super.setScaleType(ScaleType.MATRIX)
        mGestureDetector = GestureDetector(context,
                object : GestureDetector.SimpleOnGestureListener() {
                    override fun onDoubleTap(e: MotionEvent): Boolean {
                        if (isAutoScale == true)
                            return true

                        val x = e.x
                        val y = e.y
                        Log.e("DoubleTap", getScale().toString() + " , " + initScale)
                        if (getScale() < SCALE_MID) {
                            this@MyZoomImageView.postDelayed(
                                    AutoScaleRunnable(SCALE_MID, x, y), 16)
                            isAutoScale = true
                        } else if (getScale() >= SCALE_MID && getScale() < SCALE_MAX) {
                            this@MyZoomImageView.postDelayed(
                                    AutoScaleRunnable(SCALE_MAX, x, y), 16)
                            isAutoScale = true
                        } else {
                            this@MyZoomImageView.postDelayed(
                                    AutoScaleRunnable(initScale, x, y), 16)
                            isAutoScale = true
                        }

                        return true
                    }
                })
        this.mScaleGestureDetector = ScaleGestureDetector(context, this)
        this.setOnTouchListener(this)
    }

    /**
     * 自动缩放的任务

     * @author zhy
     */
    private inner class AutoScaleRunnable
    /**
     * 传入目标缩放值，根据目标值与当前值，判断应该放大还是缩小

     * @param targetScale
     */
    (private val mTargetScale: Float,
     //缩放的中心
     private val x: Float, private val y: Float) : Runnable {
        private var tmpScale: Float = 0.toFloat()
        internal val BIGGER = 1.07f
        internal val SMALLER = 0.93f


        init {
            if (getScale() < mTargetScale) {
                tmpScale = BIGGER
            } else {
                tmpScale = SMALLER
            }

        }

        override fun run() {
            // 进行缩放
            mScaleMatrix.postScale(tmpScale, tmpScale, x, y)
            checkBorderAndCenterWhenScale()
            imageMatrix = mScaleMatrix

            val currentScale = getScale()
            // 如果值在合法范围内，继续缩放
            if (tmpScale > 1f && currentScale < mTargetScale || tmpScale < 1f && mTargetScale < currentScale) {
                this@MyZoomImageView.postDelayed(this, 16)
            } else
            // 设置为目标的缩放比例
            {
                val deltaScale = mTargetScale / currentScale
                mScaleMatrix.postScale(deltaScale, deltaScale, x, y)
                checkBorderAndCenterWhenScale()
                imageMatrix = mScaleMatrix
                isAutoScale = false
            }

        }
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean = true

    override fun onScaleEnd(detector: ScaleGestureDetector) {
    }

    @SuppressLint("NewApi")
    override fun onScale(detector: ScaleGestureDetector): Boolean {
        val scale = getScale()
        var scaleFactor = detector.scaleFactor

        if (drawable == null)
            return true

        //缩放的范围控制
        if (scale < SCALE_MAX && scaleFactor > 1.0f || scale > initScale && scaleFactor < 1.0f) {
            //最大值最小值判断
            if (scaleFactor * scale < initScale) {
                scaleFactor = initScale / scale
            }
            if (scaleFactor * scale > SCALE_MAX) {
                scaleFactor = SCALE_MAX / scale
            }
            //设置缩放比例
            mScaleMatrix.postScale(scaleFactor, scaleFactor,
                    detector.focusX, detector.focusY)
            checkBorderAndCenterWhenScale()
            imageMatrix = mScaleMatrix
        }
        return true

    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (mGestureDetector!!.onTouchEvent(event))
            return true

        mScaleGestureDetector!!.onTouchEvent(event)
        var x = 0f
        var y = 0f
        // 拿到触摸点的个数
        val pointerCount = event.pointerCount
        // 得到多个触摸点的x与y均值
        for (i in 0..pointerCount - 1) {
            x += event.getX(i)
            y += event.getY(i)
        }
        x /= pointerCount
        y /= pointerCount

        /**
         * 每当触摸点发生变化时，重置mLasX , mLastY
         */
        if (pointerCount != lastPointerCount) {
            isCanDrag = false
            mLastX = x
            mLastY = y
        }


        lastPointerCount = pointerCount
        val rectF=getMatrixRectF()
        when (event.action) {
            MotionEvent.ACTION_DOWN->{
                if (rectF.width() > width || rectF.height() > height) {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (rectF.width() > width || rectF.height() > height) {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                Log.e(TAG, "ACTION_MOVE")
                var dx = x - mLastX
                var dy = y - mLastY

                if (!isCanDrag) {
                    isCanDrag = isCanDrag(dx, dy)
                }
                if (isCanDrag) {
                    if (drawable != null) {
                        isCheckTopAndBottom = true
                        isCheckLeftAndRight = isCheckTopAndBottom
                        // 如果宽度小于屏幕宽度，则禁止左右移动
                        if (rectF.width() < width) {
                            dx = 0f
                            isCheckLeftAndRight = false
                        }
                        // 如果高度小雨屏幕高度，则禁止上下移动
                        if (rectF.height() < height) {
                            dy = 0f
                            isCheckTopAndBottom = false
                        }


                        mScaleMatrix.postTranslate(dx, dy)
                        checkMatrixBounds()
                        imageMatrix = mScaleMatrix
                    }
                }
                mLastX = x
                mLastY = y
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                Log.e(TAG, "ACTION_UP")
                lastPointerCount = 0
            }
        }

        return true
    }

    /**
     * 获得当前的缩放比例
     * @return
     */
    fun getScale(): Float {
        mScaleMatrix.getValues(matrixValues)
        return matrixValues[Matrix.MSCALE_X]
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    override fun onGlobalLayout() {
        if (once) {
            val d = drawable ?: return
            Log.e(TAG, d.intrinsicWidth.toString() + " , " + d.intrinsicHeight)
            val width = width
            val height = height
            // 拿到图片的宽和高
            val dw = d.intrinsicWidth
            val dh = d.intrinsicHeight
            var scale = 1.0f
            // 如果图片的宽或者高大于屏幕，则缩放至屏幕的宽或者高
            if (dw > width && dh <= height) {
                scale = width * 1.0f / dw
            }
            if (dh > height && dw <= width) {
                scale = height * 1.0f / dh
            }
            // 如果宽和高都大于屏幕，则让其按按比例适应屏幕大小
            if (dw > width && dh > height) {
                scale = Math.min(dw * 1.0f / width, dh * 1.0f / height)
            }
            initScale = scale
            // 图片移动至屏幕中心
            mScaleMatrix.postTranslate(((width - dw) / 2).toFloat(), ((height - dh) / 2).toFloat())
            mScaleMatrix
                    .postScale(scale, scale, (getWidth() / 2).toFloat(), (getHeight() / 2).toFloat())
            imageMatrix = mScaleMatrix
            once = false
        }

    }

    /**
     * 在缩放时，进行图片显示范围的控制
     */
    private fun checkBorderAndCenterWhenScale() {

        val rect = getMatrixRectF()
        var deltaX = 0f
        var deltaY = 0f

        val width = width
        val height = height

        // 如果宽或高大于屏幕，则控制范围
        if (rect.width() >= width) {
            if (rect.left > 0) {
                deltaX = -rect.left
            }
            if (rect.right < width) {
                deltaX = width - rect.right
            }
        }
        if (rect.height() >= height) {
            if (rect.top > 0) {
                deltaY = -rect.top
            }
            if (rect.bottom < height) {
                deltaY = height - rect.bottom
            }
        }
        // 如果宽或高小于屏幕，则让其居中
        if (rect.width() < width) {
            deltaX = width * 0.5f - rect.right + 0.5f * rect.width()
        }
        if (rect.height() < height) {
            deltaY = height * 0.5f - rect.bottom + 0.5f * rect.height()
        }
        Log.e(TAG, "deltaX = $deltaX , deltaY = $deltaY")

        mScaleMatrix.postTranslate(deltaX, deltaY)

    }

    /**
     * 根据当前图片的Matrix获得图片的范围

     * @return
     */
    private fun getMatrixRectF(): RectF {
        val matrix = mScaleMatrix
        val rect = RectF()
        val d = drawable
        if (null != d) {
            rect.set(0f, 0f, d.intrinsicWidth.toFloat(), d.intrinsicHeight.toFloat())
            matrix.mapRect(rect)
        }
        return rect
    }

    /**
     * 移动时，进行边界判断，主要判断宽或高大于屏幕的
     */
    private fun checkMatrixBounds() {
        val rect = getMatrixRectF()

        var deltaX = 0f
        var deltaY = 0f
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        // 判断移动或缩放后，图片显示是否超出屏幕边界
        if (rect.top > 0 && isCheckTopAndBottom) {
            deltaY = -rect.top
        }
        if (rect.bottom < viewHeight && isCheckTopAndBottom) {
            deltaY = viewHeight - rect.bottom
        }
        if (rect.left > 0 && isCheckLeftAndRight) {
            deltaX = -rect.left
        }
        if (rect.right < viewWidth && isCheckLeftAndRight) {
            deltaX = viewWidth - rect.right
        }
        mScaleMatrix.postTranslate(deltaX, deltaY)
    }

    /**
     * 是否是推动行为

     * @param dx
     * *
     * @param dy
     * *
     * @return
     */
    private fun isCanDrag(dx: Float, dy: Float): Boolean {
        return Math.sqrt((dx * dx + dy * dy).toDouble()) >= mTouchSlop
    }
}