package com.zhiyazhang.mykotlinapplication.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast

/**
 * Created by zhiya.zhang
 * on 2017/6/9 9:02.
 */

object MyToast {
    private var toast: Toast? = null

    @SuppressLint("ShowToast")
    fun getShortToast(context: Context, msg: String) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT)
        } else {
            toast!!.setText(msg)
            toast!!.duration = Toast.LENGTH_SHORT
        }
        toast!!.show()
    }

    @SuppressLint("ShowToast")
    fun getLongToast(context: Context, msg: String) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_LONG)
        } else {
            toast!!.setText(msg)
            toast!!.duration = Toast.LENGTH_LONG
        }
        toast!!.show()
    }
}
