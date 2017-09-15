package com.zhang.givejoe

import android.content.Context
import android.widget.Toast

/**
 * Created by zhiya.zhang
 * on 2017/9/15 11:51.
 */
object CrashHandler:Thread.UncaughtExceptionHandler {
    fun init(context:Context){
        Thread.setDefaultUncaughtExceptionHandler(this)
    }
    override fun uncaughtException(thread: Thread, ex: Throwable) {
        Toast.makeText(MyApplication.instance().applicationContext,ex.message,Toast.LENGTH_LONG).show()
    }
}