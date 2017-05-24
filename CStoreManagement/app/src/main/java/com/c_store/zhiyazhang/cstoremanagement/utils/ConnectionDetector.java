package com.c_store.zhiyazhang.cstoremanagement.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.c_store.zhiyazhang.cstoremanagement.R;

/**
 * Created by zhiya.zhang
 * on 2017/5/19 10:29.
 * 检测网络工具类
 */

public class ConnectionDetector {
    private volatile static ConnectionDetector connectionDetector;
    private ConnectionDetector(){}
    public static ConnectionDetector getConnectionDetector(){
        if (connectionDetector==null){
            synchronized (ConnectionDetector.class){
                if (connectionDetector==null){
                    connectionDetector=new ConnectionDetector();
                }
            }
        }
        return connectionDetector;
    }

    private static final Context context=MyApplication.getContext();

    public static boolean isOnline() {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        Toast.makeText(context,
                context.getResources().getString(R.string.noItent),
                Toast.LENGTH_LONG)
                .show();
        return false;
    }
}
