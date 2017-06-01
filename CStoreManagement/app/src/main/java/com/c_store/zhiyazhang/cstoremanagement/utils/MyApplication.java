package com.c_store.zhiyazhang.cstoremanagement.utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.c_store.zhiyazhang.cstoremanagement.R;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.zhy.http.okhttp.OkHttpUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by zhiya.zhang
 * on 2017/5/15 9:00.
 */

public class MyApplication extends Application {
    public static Context context;
    public String myIP;

    @Override
    public void onCreate() {
        super.onCreate();
        ZXingLibrary.initDisplayOpinion(this);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .build();
        OkHttpUtils.initClient(okHttpClient);
        context = getApplicationContext();
        //getIxP();
    }

    /**
     * 原打算就第一次进入application就获得ip，之后一直使用此ip，但是不现实，如果在使用者都不清楚的情况下变换ip为移动流量就没辙
     * 未来可以把逻辑做完善， 暂时思路如下：
     * 首次进入应用获取ip，之后通过ip检测前两位是否为192.168，如不是就代表为外网，while五次，每次检查，五次都不是192.168就土司报错
     * 重写onStart，每次onStart检测ip是否正确，不正确就执行while五次方法，指导当前ip正确，之后即便用户连接是自己内网而非公司内网造成网络请求失败也不管且管不了
     * 现在先用每次网络请求都重新获得IP（一定要改，这又是1%性能，优化100个问题就提高一倍性能！）
     */
    public void getIxP() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {  //IPv4地址
                        String ip = inetAddress.getHostAddress();
                        String[] ips = ip.split("\\.");
                        myIP = ips[0] + "." + ips[1] + "." + ips[2] + ".100";
                    }
                }
            }
        } catch (SocketException ex) {
            Log.d("IP", ex.toString());
        }
    }

    /**
     *  获得当前ipv4地址
     * @return ip地址或错误信息
     */
    public static String getIP() {
        String result = "";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {  //IPv4地址
                        String ip = inetAddress.getHostAddress();
                        String[] ips = ip.split("\\.");
                        result = ips[0] + "." + ips[1] + "." + ips[2] + ".100";
                    }
                }
            }
        } catch (SocketException ex) {
            result = context.getResources().getString(R.string.notFindIP);
        }
        return result;
    }

    /**
     * 获得全局context
     * @return 获得全局application的context对象
     */
    public static Context getContext() {
        return context;
    }
}
