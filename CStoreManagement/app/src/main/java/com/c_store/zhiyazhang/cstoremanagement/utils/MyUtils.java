package com.c_store.zhiyazhang.cstoremanagement.utils;

import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by zhiya.zhang
 * on 2017/5/22 12:17.
 */

public class MyUtils {
    public static String getIP() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {  //IPv4地址
                        String ip=inetAddress.getHostAddress();
                        String[] ips = ip.split("\\.");
                        return ips[0] + "." + ips[1] + "." + ips[2] + ".100";
                    }
                }
            }
        } catch (SocketException ex) {
            Log.d("IP", ex.toString());
        }
        return "";
    }
}
