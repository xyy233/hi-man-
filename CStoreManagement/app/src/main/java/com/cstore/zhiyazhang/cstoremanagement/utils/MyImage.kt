package com.cstore.zhiyazhang.cstoremanagement.utils

import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import java.io.File
import java.io.FileOutputStream

/**
 * Created by zhiya.zhang
 * on 2017/10/12 12:12.
 */
class MyImage {
    companion object {
        /**
         * 反转图片
         * @param flag 0为水平反转，1为垂直反转
         */
        fun reverseBitmap(bmp: Bitmap, flag: Int): Bitmap? {
            var floats: FloatArray? = null
            when (flag) {
                0 -> floats = floatArrayOf(-1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)
                1 -> floats = floatArrayOf(1f, 0f, 0f, 0f, -1f, 0f, 0f, 0f, 1f)
            }
            if (floats != null) {
                val matrix = Matrix()
                //反转图片，讲道理是不用的但是反转了好看点
                matrix.setValues(floats)
                return Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, true)
            }
            return null
        }

        /**
         * 创建水印
         */
        fun createWatermark(bmp: Bitmap, markText: String): Bitmap {
            //创建相同大小图
            val markBmp = Bitmap.createBitmap(bmp.width, bmp.height, Bitmap.Config.ARGB_8888)
            //画图
            val canvas = Canvas(markBmp)
            canvas.drawBitmap(bmp, 0f, 0f, null)
            //文字开始的坐标，默认左上角
            //创建画笔,因为用StaticLayout显示换行，所以环卫TextPaint
            val mPaint = TextPaint()
            //文字矩阵区域
            val textBounds = Rect()
            //水印字体大小
            mPaint.textSize = 10f
            //阴影
            mPaint.setShadowLayer(0.5f, 0f, 1f, Color.BLACK)
            //抗锯齿
            mPaint.isAntiAlias = true
            //水印区域
            mPaint.getTextBounds(markText, 0, markText.length, textBounds)
            //水印颜色
            mPaint.color = Color.WHITE

            //图片小于水印不绘制水印
            if (textBounds.width() > bmp.width || textBounds.height() > bmp.height) return bmp
            //文字开始的坐标
            val textX = bmp.width - textBounds.width() + 40f
            val textY = bmp.height - textBounds.height() - 20f
            val myStaticLayout = StaticLayout(markText, mPaint, canvas.width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false)
            //画文字
//            canvas.drawText(markText,textX,textY,mPaint)
            //保存图片
            //canvas.save(Canvas.ALL_SAVE_FLAG)
//            canvas.save()
            canvas.translate(textX, textY)
            myStaticLayout.draw(canvas)
//            canvas.restore()
            return markBmp
        }

        fun saveImage(bmp: Bitmap, fileName: String): Boolean {
            val appDir = File(Environment.getExternalStorageDirectory(), "CStore")
            if (!appDir.exists()) appDir.mkdir()
            val file = File(appDir, fileName)
            try {
                val fos = FileOutputStream(file)
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
                fos.close()
                MediaStore.Images.Media.insertImage(MyApplication.instance().applicationContext.contentResolver, file.absolutePath, fileName, null)
                MyApplication.instance().applicationContext.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.absolutePath)))
            } catch (e: Exception) {
                return false
            }
            return true
        }
    }
}