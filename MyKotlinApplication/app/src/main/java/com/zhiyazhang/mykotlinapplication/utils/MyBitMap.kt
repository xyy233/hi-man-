package com.zhiyazhang.mykotlinapplication.utils

import android.graphics.*

/**
 * Created by zhiya.zhang
 * on 2017/6/5 11:35.
 */

class MyBitMap{
    /**
     *用BitmapShader绘制圆角图片
     * @param bitmap 待处理图片
     * @param edgeWidth 正方形控件大小
     * @param radius 圆角半径大小
     * @return 结果图片
     */
    fun circleBitmap(bitmap: Bitmap, edgeWidth: Int, radius: Int): Bitmap {
        val btWidth: Float = bitmap.width.toFloat()
        val btHeight: Float = bitmap.height.toFloat()
        //水平方向开始剪裁位置
        var btWidthCutSite: Float = 0f
        //竖直方向开始剪裁未知
        var btHeightCutSite: Float = 0f
        //剪裁成正方形图片的边长，未拉伸缩放
        var squareWidth: Float = 0f
        //如果矩形宽度大于高度
        if (btWidth > btHeight) {
            btWidthCutSite = (btWidth - btHeight) / 2f
            squareWidth = btHeight
        } else {
            btHeightCutSite = (btHeight - btWidth) / 2f
            squareWidth = btWidth
        }
        //设置拉伸缩放比
        val scale = edgeWidth * 1.0f / squareWidth
        val matrix: Matrix = Matrix()
        matrix.setScale(scale, scale)
        //把矩形图片剪裁成正方形并拉伸缩放到控件大小
        val squareBT: Bitmap = Bitmap.createBitmap(bitmap, btWidthCutSite.toInt(), btHeightCutSite.toInt(), squareWidth.toInt(), squareWidth.toInt(), matrix, true)
        //初始化绘制纹理
        val bitmapShader: BitmapShader = BitmapShader(squareBT, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        //初始化目标bitmap
        val targetBitmap: Bitmap = Bitmap.createBitmap(edgeWidth, edgeWidth, Bitmap.Config.ARGB_8888)
        //初始化目标画布
        val targetCanvas: Canvas = Canvas(targetBitmap)
        //初始化画笔
        val paint: Paint = Paint()
        paint.isAntiAlias = true
        paint.shader = bitmapShader
        //用画笔绘制圆形图
        targetCanvas.drawRoundRect(RectF(0f, 0f, edgeWidth.toFloat(), edgeWidth.toFloat()), radius.toFloat(), radius.toFloat(), paint)
        return targetBitmap
    }
}
