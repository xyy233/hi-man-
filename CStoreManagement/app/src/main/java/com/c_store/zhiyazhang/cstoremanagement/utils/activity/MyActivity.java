package com.c_store.zhiyazhang.cstoremanagement.utils.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.c_store.zhiyazhang.cstoremanagement.utils.static_variable.MyVariable.EXIT_APP_ACTION;

/**
 * Created by zhiya.zhang
 * on 2017/5/8 12:25.
 * 此activity承担ButterKnife注入框架的setcontent和bind步骤
 * 以及完整退出应用步骤，确认退出时发送广播通知所有注册的activity退出
 */

public abstract class MyActivity extends AppCompatActivity {
    Unbinder mUnbinder;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (context != null) {
                if (context instanceof AppCompatActivity) {
                    ((AppCompatActivity) context).finish();
                }
            }
        }
    };

    protected abstract int getLayoutId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册监听
        IntentFilter filter = new IntentFilter();
        filter.addAction(EXIT_APP_ACTION);
        registerReceiver(mBroadcastReceiver, filter);
        //获得布局
        setContentView(getLayoutId());
        mUnbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放布局
        mUnbinder.unbind();
        //释放监听
        unregisterReceiver(mBroadcastReceiver);
    }
}
