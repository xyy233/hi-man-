package com.cstore.zhiyazhang.cstoremanagement.utils

import android.graphics.*

/**
 * Created by zhiya.zhang
 * on 2017/10/12 12:12.
 */
class MyImage{
    companion object {
        /**
         * 反转图片
         * @param flag 0为水平反转，1为垂直反转
         */
        fun reverseBitmap(bmp:Bitmap, flag:Int):Bitmap?{
            var floats:FloatArray? = null
            when (flag) {
                0 -> floats = floatArrayOf(-1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)
                1 -> floats = floatArrayOf(1f, 0f, 0f, 0f, -1f, 0f, 0f, 0f, 1f)
            }
            if (floats!=null){
                val matrix=Matrix()
                //反转图片，讲道理是不用的但是反转了好看点
                matrix.setValues(floats)
                return Bitmap.createBitmap(bmp,0,0,bmp.width,bmp.height,matrix,true)
            }
            return null
        }

        /**
         * 创建水印
         */
        fun createWatermark(bmp: Bitmap?, markText: String):Bitmap?{
            if (bmp==null)return null
            //创建相同大小图
            val markBmp = Bitmap.createBitmap(bmp.width,bmp.height,Bitmap.Config.ARGB_8888)
            //画图
            val canvas=Canvas(markBmp)
            canvas.drawBitmap(bmp,0f,0f,null)
            //文字开始的坐标，默认左上角
            //创建画笔
            val mPaint=Paint()
            //文字矩阵区域
            val textBounds=Rect()
            //水印字体大小
            mPaint.textSize=10f
            //阴影
            mPaint.setShadowLayer(0.5f,0f,1f,Color.BLACK)
            //抗锯齿
            mPaint.isAntiAlias=true
            //水印区域
            mPaint.getTextBounds(markText,0,markText.length,textBounds)
            //水印颜色
            mPaint.color=Color.WHITE
            //图片小鱼水印不绘制水印
            if (textBounds.width()>bmp.width||textBounds.height()>bmp.height)return bmp
            //文字开始的坐标
            val textX=bmp.width-textBounds.width()-10f
            val textY=bmp.height-textBounds.height()+6f
            //画文字
            canvas.drawText(markText,textX,textY,mPaint)
            //保存图片
            canvas.save(Canvas.ALL_SAVE_FLAG)
            canvas.restore()
            return markBmp
        }
    }
}