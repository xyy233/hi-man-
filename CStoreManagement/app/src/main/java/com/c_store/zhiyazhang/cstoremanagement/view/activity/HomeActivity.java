package com.c_store.zhiyazhang.cstoremanagement.view.activity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.c_store.zhiyazhang.cstoremanagement.R;
import com.c_store.zhiyazhang.cstoremanagement.utils.activity.MyActivity;

import butterknife.BindView;
import butterknife.OnClick;

import static com.c_store.zhiyazhang.cstoremanagement.utils.static_variable.MyVariable.EXIT_APP_ACTION;

/**
 * Created by zhiya.zhang
 * on 2017/6/6 9:42.
 */

@SuppressLint("Registered")
public class HomeActivity extends MyActivity {

    @BindView(R.id.my_toolbar)
    Toolbar myToolbar;
    @BindView(R.id.gg1)
    LinearLayout gg1;
    @BindView(R.id.gg2)
    LinearLayout gg2;
    @BindView(R.id.gg3)
    LinearLayout gg3;
    @BindView(R.id.gg4)
    LinearLayout gg4;
    @BindView(R.id.gg5)
    LinearLayout gg5;
    @BindView(R.id.gg6)
    LinearLayout gg6;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        myToolbar.setTitle(getResources().getString(R.string.app_name));
        myToolbar.setLogo(R.mipmap.ic_logo);
        setSupportActionBar(myToolbar);
    }

    @OnClick({R.id.gg1, R.id.gg2, R.id.gg3, R.id.gg4, R.id.gg5, R.id.gg6})
    public void Onclick(View view) {
        switch (view.getId()) {
            case R.id.gg1:
                Toast.makeText(this, "未完成", Toast.LENGTH_SHORT).show();
                break;
            case R.id.gg2:
                Toast.makeText(this, "未完成", Toast.LENGTH_SHORT).show();
                break;
            case R.id.gg3:
                startActivity(new Intent(HomeActivity.this, ContractTypeActivity.class),
                        ActivityOptions.makeSceneTransitionAnimation(HomeActivity.this, gg3, "gg3").toBundle());
                break;
            case R.id.gg4:
                Toast.makeText(this, "未完成", Toast.LENGTH_SHORT).show();
                break;
            case R.id.gg5:
                Toast.makeText(this, "未完成", Toast.LENGTH_SHORT).show();
                break;
            case R.id.gg6:
                Toast.makeText(this, "未完成", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    /*
* 截取返回键做退出
* */
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("请确认退出系统？")
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(EXIT_APP_ACTION);
                        sendBroadcast(intent);//发送退出系统广播  每个接收器都会收到 调动finish（）关闭activity
                        finish();
                    }
                })
                .setNegativeButton("按错了", null)
                .show();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_home2;
    }
}
