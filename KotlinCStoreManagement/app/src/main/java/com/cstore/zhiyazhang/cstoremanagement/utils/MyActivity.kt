package com.zhiyazhang.mykotlinapplication.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * Created by zhiya.zhang
 * on 2017/6/2 14:10.
 * 此activity承担ButterKnife注入框架的setcontent和bind步骤
 * 以及完整退出应用步骤，确认退出时发送广播通知所有注册的activity退出
 */
abstract class MyActivity : AppCompatActivity() {
    protected val EXIT_APP_ACTION = "finish"
    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context is AppCompatActivity?) context?.finish()
        }
    }

    /**
     * 获得布局R.layout.?
     */
    protected abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //获得布局
        setContentView(layoutId)
        //注册监听
        val filter = IntentFilter()
        filter.addAction(EXIT_APP_ACTION)
        registerReceiver(mBroadcastReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        //释放监听
        unregisterReceiver(mBroadcastReceiver)
    }
}