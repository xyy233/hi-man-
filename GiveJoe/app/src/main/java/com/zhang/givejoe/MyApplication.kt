package com.zhang.givejoe

import android.app.Application


/**
 * Created by zhiya.zhang
 * on 2017/6/2 14:35.
 */
class MyApplication : Application() {

    companion object {
        private var instance: Application? = null
        fun instance() = instance!!
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        val handler=CrashHandler
        handler.init(applicationContext)
    }
}