package com.cstore.zhiyazhang.cstorepay.util

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.hardware.input.InputManager
import android.provider.Settings
import com.cstore.zhiyazhang.cstorepay.R
import com.uuzuche.lib_zxing.activity.ZXingLibrary
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.https.HttpsUtils
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
        /**
         * 你爸爸的！明明是kotlin官方推荐的静态单列application还提示会导致内存泄漏逗我哈？
         */
        private var instance: MyApplication? = null

        @JvmStatic
        fun instance() = instance!!

        @JvmStatic
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

        @JvmStatic
        fun getVersionNum(): Int {
            val manager = instance().packageManager
            val info = manager.getPackageInfo(instance().packageName, 0)
            return info.versionCode
        }

        /**
         * 获得手机序列号
         */
        @SuppressLint("HardwareIds")
        @JvmStatic
        fun getOnlyid(): String {
            return Settings.Secure.getString(instance().contentResolver, Settings.Secure.ANDROID_ID)
        }

        /**
         * 判断扫描枪
         */
        fun usbGunJudgment(): Boolean {
            return try {
                val im = instance!!.applicationContext.getSystemService(Context.INPUT_SERVICE) as InputManager
                val devices = im.inputDeviceIds
                devices
                        .map { im.getInputDevice(it) }
                        .map { it.name.toLowerCase() }
                        .any { it.indexOf("barcode") != -1 || it.indexOf("bar code") != -1 || it.indexOf("scanner") != -1 }
            } catch (e: Exception) {
                false
            }
        }

        fun getMyIP(): String {
            var result = ""
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
                            if (name.indexOf("wlan") != -1 && result != "") {
                                return ip
                            }
                        }
                    }
                }
            } catch (ex: SocketException) {
                result = instance!!.applicationContext.resources.getString(R.string.notFindIP)
            }
            return result
        }
    }

    override fun onCreate() {
        super.onCreate()

        /*LeakCanary.install(this)
        Stetho.initializeWithDefaults(this)*/

        instance = this

        ZXingLibrary.initDisplayOpinion(this)
        val sslParams = HttpsUtils.getSslSocketFactory(null, null, null)
        val okHttp: OkHttpClient = OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .build()
        OkHttpUtils.initClient(okHttp)
    }
}
