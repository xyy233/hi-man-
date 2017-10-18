package com.cstore.zhiyazhang.template.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Objects;

/**
 * Created by zhiya.zhang
 * on 2017/10/17 17:19.
 */

public class IpAddress {
    public static String getIP(){
        String result="" ;
        try{
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()){
                NetworkInterface intf = en.nextElement();
                Enumeration<InetAddress> enumIpAddress=intf.getInetAddresses();
                String name=intf.getDisplayName();
                while(enumIpAddress.hasMoreElements()){
                    InetAddress inetAddress=enumIpAddress.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address){
                        String ip=inetAddress.getHostAddress();
                        String[] ips=ip.split("\\.");
                        result=ips[0] + "." + ips[1] + "." + ips[2] + ".100";
                        //小米的限额用心导致这里需要判断是否为wifi信号
                        if (name.contains("wlan") && !Objects.equals(result, "")){
                            return result;
                        }
                    }
                }
            }
        }catch (SocketException ex){
            result="不能获得IP地址，请检查网络";
        }
        return result;
    }
}
