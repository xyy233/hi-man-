package com.cstore.zhiyazhang.cstoremanagement.utils

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
                            if (name.indexOf("wlan") != -1 && result != "") {
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

        fun getVersionNum(): Int {
            val manager = instance().packageManager
            val info = manager.getPackageInfo(instance().packageName, 0)
            return info.versionCode
        }
    }

    override fun onCreate() {
        super.onCreate()
        //LeakCanary.install(this)
        instance = this
        ZXingLibrary.initDisplayOpinion(this)
        val okHttp: OkHttpClient = OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .build()
        OkHttpUtils.initClient(okHttp)
        //全局错误信息收集先关闭
        //Thread.setDefaultUncaughtExceptionHandler(GlobalException.instance)
    }

}
/*
*//**
 * 全局错误信息收集
 *//*
class GlobalException private constructor() : Thread.UncaughtExceptionHandler {
    private val mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    companion object {
        @get:Synchronized val instance = GlobalException()
    }

    override fun uncaughtException(t: Thread?, ex: Throwable?) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(t, ex)
        } else {
            try {
                Thread.sleep(3000)
            } catch (e: InterruptedException) {
                Log.e("GlobalException", "error : ", e)
            }

            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(1)
        }
    }

    fun handleException(ex: Throwable?): Boolean {
        if (ex == null) return false
        thread {
            kotlin.run {
                Looper.prepare()
                MyToast.getLongToast("很抱歉,程序出现异常,即将退出。")
                Looper.loop()
            }
        }.start()
        saveCrashInfoFile(ex)
        return true
    }

    *//**
     * 记录错误信息
     *//*
    private fun saveCrashInfoFile(ex: Throwable) {
        val writer = StringWriter()
        val printWriter = PrintWriter(writer)
        ex.printStackTrace(printWriter)
        var cause: Throwable? = ex.cause
        while (cause != null) {
            cause.printStackTrace(printWriter)
            cause = cause.cause
        }
        printWriter.close()
        val result: String = writer.toString()
        try {
            val timestamp = System.currentTimeMillis()
            val formatter = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
            val time = formatter.format(Date())
            val fileName = "crash-$time-$timestamp.log"
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                val path = "/cstore/crash/"
                val dir = File(path)
                if (!dir.exists()) {
                    dir.mkdirs()
                }
                val fos = FileOutputStream(path + fileName)
                fos.write(result.toByteArray())
                fos.close()
            }
        } catch (e: Exception) {
        }
    }
}*/