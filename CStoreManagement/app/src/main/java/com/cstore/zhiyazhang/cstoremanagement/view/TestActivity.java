package com.cstore.zhiyazhang.cstoremanagement.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.cstore.zhiyazhang.cstoremanagement.view.instock.scrap.ScrapActivity;

/**
 * Created by zhiya.zhang
 * on 2017/10/17 14:54.
 */

public class TestActivity extends AppCompatActivity {
    Button btn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TestActivity.this, ScrapActivity.class));
            }
        });

    }
}
