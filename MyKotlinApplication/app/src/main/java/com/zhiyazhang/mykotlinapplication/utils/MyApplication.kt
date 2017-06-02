package com.zhiyazhang.mykotlinapplication.utils

import android.app.Application
import android.content.Context
import com.uuzuche.lib_zxing.activity.ZXingLibrary
import com.zhiyazhang.mykotlinapplication.R
import com.zhy.http.okhttp.OkHttpUtils
import okhttp3.OkHttpClient
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.concurrent.TimeUnit

/**
 * Created by zhiya.zhang
 * on 2017/6/2 14:35.
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ZXingLibrary.initDisplayOpinion(this)
        val okhttp: OkHttpClient = OkHttpClient
                .Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .build()
        OkHttpUtils.initClient(okhttp)
    }

    fun getIP(): String {
        var result: String = ""
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is InetAddress) {
                        val ip = inetAddress.hostAddress
                        val ips = ip.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        result = ips[0] + "." + ips[1] + "." + ips[2] + ".100"
                    }
                }
            }
        } catch (ex: SocketException) {
            result = this.resources.getString(R.string.abc_action_bar_home_description)
        }
        return result
    }

    /**
     * 获得全局context
     * @return 获得全局application的context对象
     */
    fun getContext(): Context {
        return applicationContext
    }
}