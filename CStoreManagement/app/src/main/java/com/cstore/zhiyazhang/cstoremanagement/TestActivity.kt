package com.cstore.zhiyazhang.cstoremanagement

import android.os.Bundle
import android.widget.Toast

import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity

/**
 * Created by zhiya.zhang
 * on 2017/6/7 14:40.
 */

class TestActivity(override val layoutId: Int = R.layout.app_bar_home) : MyActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val x="asdflksajer"
        if("b" in x){
            Toast.makeText(this@TestActivity, "有", Toast.LENGTH_SHORT).show()
        }else
            Toast.makeText(this@TestActivity, "没有", Toast.LENGTH_SHORT).show()
    }
}
