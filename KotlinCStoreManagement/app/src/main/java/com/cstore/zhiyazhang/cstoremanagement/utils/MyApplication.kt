package com.zhiyazhang.mykotlinapplication.utils

import android.app.Application
import com.cstore.zhiyazhang.cstoremanagement.R
import com.uuzuche.lib_zxing.activity.ZXingLibrary
import com.zhy.http.okhttp.OkHttpUtils
import okhttp3.OkHttpClient
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.concurrent.TimeUnit




/**
 * Created by zhiya.zhang
 * on 2017/6/2 14:35.
 */
class MyApplication : Application() {

    companion object {
        private var instance: Application? = null
        fun instance() = instance!!

        fun getIP(): String {
            var result: String = ""
            try {
                val en = NetworkInterface.getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val intf = en.nextElement()
                    val enumIpAddr = intf.inetAddresses
                    val name = intf.displayName
                    while (enumIpAddr.hasMoreElements()) {
                        val inetAddress = enumIpAddr.nextElement()
                        if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                            val ip = inetAddress.hostAddress
                            val ips = ip.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            result = ips[0] + "." + ips[1] + "." + ips[2] + ".100"
                            if (name.indexOf("wlan")!=-1&&result!=""){
                                return result
                            }
                        }
                    }
                }
            } catch (ex: SocketException) {
                result = instance!!.applicationContext.resources.getString(R.string.notFindIP)
            }
            return result
        }

        fun getVersion(): String? {
            try {
                val manager = instance!!.packageManager
                val info = manager.getPackageInfo(instance!!.packageName, 0)
                return info.versionName
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }

    override fun onCreate() {
        super.onCreate()
        //LeakCanary.install(this)
        instance = this
        ZXingLibrary.initDisplayOpinion(this)
        val okHttp: OkHttpClient = OkHttpClient
                .Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .build()
        OkHttpUtils.initClient(okHttp)
    }

}