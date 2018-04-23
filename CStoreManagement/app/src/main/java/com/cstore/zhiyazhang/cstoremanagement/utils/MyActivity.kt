package com.cstore.zhiyazhang.cstoremanagement.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView

/**
 * Created by zhiya.zhang
 * on 2017/6/2 14:10.
 * 此activity承担setContentView的工作
 * 以及完整退出应用步骤，确认退出时发送广播通知所有注册的activity退出
 */
abstract class MyActivity : AppCompatActivity(), GenericView {
    var is_back = false
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

    /**
     * 初始化三部曲,其实data在view前该获得一次的。。。
     * 不管了，就当做获得未经网络的数据为initView内的部分
     */
    protected abstract fun initView()

    protected abstract fun initClick()
    protected abstract fun initData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //获得布局
        setContentView(layoutId)
        //注册监听
        val filter = IntentFilter()
        filter.addAction(EXIT_APP_ACTION)
        registerReceiver(mBroadcastReceiver, filter)

        initView()
        initClick()
        initData()
    }

    override fun onStart() {
        is_back = false
        Thread(Runnable {
            Thread.sleep(800)
            is_back = true
        }).start()
        super.onStart()
    }

    override fun onBackPressed() {
        if (is_back) {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        //删掉内容
        MyHandler.OnlyMyHandler.cleanAL()
        MyHandler.OnlyMyHandler.removeCallbacksAndMessages(null)
        //释放监听
        unregisterReceiver(mBroadcastReceiver)
        super.onDestroy()
    }
}