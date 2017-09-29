package com.cstore.zhiyazhang.cstoremanagement.utils

import android.annotation.SuppressLint
import android.os.Handler
import android.widget.Toast

/**
 * Created by zhiya.zhang
 * on 2017/6/9 9:02.
 */

object MyToast {
    private var toast: Toast? = null
    private val handler=Handler()

    @SuppressLint("ShowToast")
    fun getShortToast(msg: String) {
        if (toast == null) {
            toast = Toast.makeText(MyApplication.instance().applicationContext, msg, Toast.LENGTH_SHORT)
        } else {
            toast!!.setText(msg)
            toast!!.duration = Toast.LENGTH_SHORT
        }
        handler.run { toast!!.show() }
    }

    @SuppressLint("ShowToast")
    fun getLongToast(msg: String) {
        if (toast == null) {
            toast = Toast.makeText(MyApplication.instance().applicationContext, msg, Toast.LENGTH_LONG)
        } else {
            toast!!.setText(msg)
            toast!!.duration = Toast.LENGTH_LONG
        }
        handler.run { toast!!.show() }
    }
}
