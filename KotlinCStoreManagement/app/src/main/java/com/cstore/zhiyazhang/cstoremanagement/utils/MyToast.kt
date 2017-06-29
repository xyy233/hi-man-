package com.cstore.zhiyazhang.cstoremanagement.utils

import android.annotation.SuppressLint
import android.widget.Toast

import com.zhiyazhang.mykotlinapplication.utils.MyApplication

/**
 * Created by zhiya.zhang
 * on 2017/6/9 9:02.
 */

object MyToast {
    private var toast: Toast? = null

    @SuppressLint("ShowToast")
    fun getShortToast(msg: String) {
        if (toast == null) {
            toast = Toast.makeText(MyApplication.instance().applicationContext, msg, Toast.LENGTH_SHORT)
        } else {
            toast!!.setText(msg)
            toast!!.duration = Toast.LENGTH_SHORT
        }
        toast!!.show()
    }

    @SuppressLint("ShowToast")
    fun getLongToast(msg: String) {
        if (toast == null) {
            toast = Toast.makeText(MyApplication.instance().applicationContext, msg, Toast.LENGTH_LONG)
        } else {
            toast!!.setText(msg)
            toast!!.duration = Toast.LENGTH_LONG
        }
        toast!!.show()
    }
}
