package com.cstore.zhiyazhang.cstoremanagement.view.update;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.cstore.zhiyazhang.cstoremanagement.R;
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast;
import com.cstore.zhiyazhang.cstoremanagement.utils.NumberProgressBar;

import java.util.Objects;

/**
 * Created by zhiya.zhang
 * on 2018/6/4 15:10.
 */

public class DownloadActivity extends Activity {
    private NumberProgressBar npb;
    private boolean isBindService;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DownloadService.DownloadBinder binder = (DownloadService.DownloadBinder) service;
            DownloadService downloadService = binder.getService();

            //接口回调，下载进度
            downloadService.setOnProgressListener(new DownloadService.OnProgressListener() {
                @Override
                public void onProgress(float fraction) {
                    npb.setProgress((int) (fraction * 100));

                    //判断是否真的下载完成进行安装了，以及是否注册绑定过服务
                    if (fraction == DownloadService.Companion.getUNBIND_SERVICE() && isBindService) {
                        unbindService(conn);
                        isBindService = false;
                        finish();
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        npb = findViewById(R.id.npb);
        String url = getIntent().getStringExtra("url");
        if (url == null || Objects.equals(url, "")) {
            url = "http://watchstore.rt-store.com:8081/CStoreManagement.apk";
        }
        String name = getIntent().getStringExtra("name");
        if (name == null || Objects.equals(name, "")) {
            name = "CStoreManagement.apk";
        }
        Intent i = new Intent(this, DownloadService.class);
        i.putExtra(DownloadService.Companion.getBUNDLE_KEY_DOWNLOAD_URL(), url);
        i.putExtra(DownloadService.Companion.getBUNDLE_KEY_DOWNLOAD_NAME(), name);
        isBindService = bindService(i, conn, BIND_AUTO_CREATE);
    }

    @Override
    public void onBackPressed() {
        MyToast.INSTANCE.getLongToast("请等待下载完成!");
    }
}
