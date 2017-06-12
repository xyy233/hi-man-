package com.cstore.zhiyazhang.cstoremanagement

import android.os.Bundle
import android.widget.Toast

import com.zhiyazhang.mykotlinapplication.utils.MyActivity

/**
 * Created by zhiya.zhang
 * on 2017/6/7 14:40.
 */

class TestActivity(override val layoutId: Int = R.layout.app_bar_home) : MyActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(this@TestActivity, "test", Toast.LENGTH_SHORT).show()
    }
}
