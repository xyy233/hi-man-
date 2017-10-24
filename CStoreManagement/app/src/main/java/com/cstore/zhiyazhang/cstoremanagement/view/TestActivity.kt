package com.cstore.zhiyazhang.cstoremanagement.view

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_test.*

/**
 * Created by zhiya.zhang
 * on 2017/10/17 14:54.
 */

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        test_btn.setOnClickListener {
            Thread(runTask).start()
        }
    }

    private val handler=Handler()

    private val runTask= Runnable {
        val x = SocketUtil.initSocket("192.168.3.100","")
                .getAllFileName("filelist c:/rtcvs/arr_photo/20171023\u0004")
        val i=Gson().fromJson<ArrayList<String>>(x, object : TypeToken<ArrayList<String>>(){}.type)
        i
                .map { "fileget c:/rtcvs/arr_photo/20171023/$it" }
                .forEach {
                    val b=SocketUtil.initSocket("192.168.3.100","").inquire(it)
                    handler.post {
                        if (b==null){
                            Glide.with(this@TestActivity).load(R.mipmap.load_error).into(test_img)
                        }else{
                            Glide.with(this@TestActivity).load(b).into(test_img)
                        }
                    }
                }
    }
}
