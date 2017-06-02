package com.zhiyazhang.mykotlinapplication.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import butterknife.ButterKnife
import butterknife.Unbinder

/**
 * Created by zhiya.zhang
 * on 2017/6/2 14:10.
 * 此activity承担ButterKnife注入框架的setcontent和bind步骤
 * 以及完整退出应用步骤，确认退出时发送广播通知所有注册的activity退出
 */
abstract class MyActivity : AppCompatActivity() {
    var mUnbinder: Unbinder? = null
    private val EXIT_APP_ACTION = "finish"
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
        //注册监听
        val filter = IntentFilter()
        filter.addAction(EXIT_APP_ACTION)
        registerReceiver(mBroadcastReceiver, filter)
        //获得布局
        setContentView(layoutId)
        mUnbinder = ButterKnife.bind(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        //释放布局
        mUnbinder?.unbind()
        //释放监听
        unregisterReceiver(mBroadcastReceiver)
    }
}