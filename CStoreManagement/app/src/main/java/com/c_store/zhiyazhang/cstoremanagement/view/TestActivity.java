package com.c_store.zhiyazhang.cstoremanagement.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.c_store.zhiyazhang.cstoremanagement.R;
import com.c_store.zhiyazhang.cstoremanagement.utils.activity.MyActivity;

/**
 * Created by zhiya.zhang
 * on 2017/6/9 16:23.
 */

@SuppressLint("Registered")
public class TestActivity extends MyActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.test;
    }
}
