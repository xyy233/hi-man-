package com.zhiyazhang.mykotlinapplication.view.activity

import android.os.Bundle
import com.zhiyazhang.mykotlinapplication.R
import com.zhiyazhang.mykotlinapplication.utils.MyActivity

/**
 * Created by zhiya.zhang
 * on 2017/6/2 16:39.
 */
class TestActivity : MyActivity() {
    var mList = arrayListOf("axx", "bxx", "cxx")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override val layoutId: Int get() = R.layout.activity_signin

}

