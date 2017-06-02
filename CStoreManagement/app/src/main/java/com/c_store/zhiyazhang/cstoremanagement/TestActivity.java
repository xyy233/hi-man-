package com.c_store.zhiyazhang.cstoremanagement;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.c_store.zhiyazhang.cstoremanagement.utils.activity.MyActivity;

import butterknife.BindView;

/**
 * Created by zhiya.zhang
 * on 2017/6/2 10:15.
 */

public class TestActivity extends MyActivity {


    @BindView(R.id.button)
    Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TestActivity.this, "test", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.test;
    }
}
