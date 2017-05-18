package com.c_store.zhiyazhang.cstoremanagement.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.c_store.zhiyazhang.cstoremanagement.R;
import com.c_store.zhiyazhang.cstoremanagement.utils.activity.MyActivity;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import butterknife.BindView;

/**
 * Created by zhiya.zhang
 * on 2017/5/12 15:45.
 */

public class SearchContractActivity extends MyActivity {
    @BindView(R.id.search_edit)
    EditText searchEdit;
    @BindView(R.id.search_btn)
    Button searchBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchContractActivity.this, ContractActivity2.class);
                i.putExtra("is_search", true);
                i.putExtra("search_message", searchEdit.getText().toString());
                startActivity(i);
                finish();
            }
        });
        //qrcode
        CaptureFragment captureFragment = new CaptureFragment();
        CodeUtils.setFragmentArgs(captureFragment, R.layout.my_camera);
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, captureFragment).commit();
    }

    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Intent i = new Intent(SearchContractActivity.this, ContractActivity.class);
            i.putExtra("is_search", true);
            i.putExtra("search_message", result);
            startActivity(i);
            finish();
        }

        @Override
        public void onAnalyzeFailed() {
            Toast.makeText(SearchContractActivity.this, "二维码扫描器找不到目标", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_contract;
    }
}
