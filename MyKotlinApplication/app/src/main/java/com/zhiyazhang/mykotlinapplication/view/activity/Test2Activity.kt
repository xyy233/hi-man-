package com.zhiyazhang.mykotlinapplication.view.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewTreeObserver
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.zhiyazhang.mykotlinapplication.R
import com.zhiyazhang.mykotlinapplication.utils.MyActivity
import com.zhiyazhang.mykotlinapplication.utils.MyBitMap
import kotlinx.android.synthetic.main.activity_signin.*

/**
 * Created by zhiya.zhang
 * on 2017/6/5 13:33.
 */

@SuppressLint("Registered")
class Test2Activity : MyActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Glide.with(this).load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1496647187067&di=616226ed39062b46b8f295c2bd5528f9&imgtype=0&src=http%3A%2F%2Fimg.pconline.com.cn%2Fimages%2Fupload%2Fupc%2Ftx%2Fphotoblog%2F1510%2F22%2Fc9%2F14310102_1445499289899_mthumb.jpg")
                .asBitmap().into(object : SimpleTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>) {
                val vto: ViewTreeObserver = test_img.viewTreeObserver
                vto.addOnGlobalLayoutListener {
                    test_img.setImageBitmap(MyBitMap().circleBitmap(resource, test_img.width, 400))
                }
            }
        })
    }

    override val layoutId: Int
        get() = R.layout.activity_signin
}

