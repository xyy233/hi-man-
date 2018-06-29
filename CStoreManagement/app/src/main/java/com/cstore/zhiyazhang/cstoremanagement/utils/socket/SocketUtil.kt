package com.cstore.zhiyazhang.cstoremanagement.utils.socket

import android.content.Context
import android.graphics.Bitmap
import android.net.wifi.WifiManager
import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import java.io.*
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException

/**
 * Created by zhiya.zhang
 * on 2017/5/19 15:32.
 * Socket工具类
 * @author zhiya.zhang
 * *
 * @since 1.1
 */

internal class SocketUtil {
    private var mySocket: Socket
    private var os: OutputStream? = null
    private var bw: BufferedWriter? = null
    private var `is`: InputStream? = null
    private var br: BufferedReader? = null
    private var loadingTime: Int

    private constructor(ip: String, msg: String) {
        mySocket = Socket()
        host = ip
        message = msg
        loadingTime = 10//默认超时时间为10s
    }

    private constructor(ip: String, msg: String, lt: Int) {
        mySocket = Socket()
        host = ip
        message = msg
        loadingTime = lt
    }

    companion object {
        private val PORT = 50000//49999
        private val REQUEST_ERROR = "服务器连接超时"
        private val SOCKET_ERROR = "服务器异常，确定连在内网中，确定服务器正常"
        private var message = ""
        lateinit private var host: String


        /**
         * 初始化Socket
         */
        @JvmStatic
        fun initSocket(ip: String): SocketUtil {
            return SocketUtil(ip, "")
        }

        /**
         * 初始化Socket
         */
        @JvmStatic
        fun initSocket(ip: String, message: String): SocketUtil {
            return SocketUtil(ip, message)
        }

        /**
         * 初始化Socket，附带设置等待时间,时间是以秒为单位
         */
        @JvmStatic
        fun initSocket(ip: String, message: String, loadingTime: Int): SocketUtil {
            return SocketUtil(ip, message, loadingTime)
        }

        /**
         * 判断ip是否正确
         */
        @JvmStatic
        fun judgmentIP(ip: String, msg: Message, handler: MyHandler): Boolean {

            val wifiName = (MyApplication.instance().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager).connectionInfo?.ssid
            return if (wifiName == null || wifiName == "") {
                msg.obj = MyApplication.instance().getString(R.string.cannt_mobile)
                msg.what = MyHandler.ERROR
                handler.sendMessage(msg)
                false
            } else if (ip == MyApplication.instance().getString(R.string.notFindIP)) {
                msg.obj = ip
                msg.what = MyHandler.ERROR
                handler.sendMessage(msg)
                false
            } else true
        }

        /**
         * 判断得到的数据是否有值
         */
        @JvmStatic
        fun judgmentNull(data: String, msg: Message, handler: MyHandler): Boolean {
            return if (data == "" || data == "[]") {
                msg.obj = MyApplication.instance().applicationContext.getString(R.string.noMessage)
                msg.what = MyHandler.ERROR
                handler.sendMessage(msg)
                false
            } else true
        }
    }

    /**
     * 关闭各种流
     */
    private fun closeSocket(socket: Socket) {
        try {
            bw!!.close()
        } catch (e: Exception) {
        }

        try {
            br!!.close()
        } catch (e: Exception) {
        }

        try {
            os!!.close()
        } catch (e: Exception) {
        }

        try {
            `is`!!.close()
        } catch (e: Exception) {
        }

        try {
            socket.close()
        } catch (e: Exception) {
        }
    }

    /**
     * 执行Socket操作,这不是异步，在外层要开thread
     * @return 如果是insert这类修改的，修改成功几个就是返回数字字符串几，如果是在事务内的返回的是0
     */
    fun inquire(): String {
        try {
            mySocket.connect(InetSocketAddress(host, PORT), loadingTime * 1000)
            mySocket.soTimeout = loadingTime * 1000

            os = mySocket.getOutputStream()
            bw = BufferedWriter(OutputStreamWriter(os!!))
            `is` = mySocket.getInputStream()
            br = BufferedReader(InputStreamReader(`is`!!))

            bw!!.write(message)
            bw!!.flush()
            mySocket.shutdownOutput()//可以不用关，这只是个人习惯关闭而已

            var receive: String? = null
            while (receive == null) {
                receive = br!!.readLine()
            }

            return receive
        } catch (ste: SocketTimeoutException) {
            return REQUEST_ERROR
        } catch (ioe: IOException) {
            return SOCKET_ERROR
        } catch (e: Exception) {
            return e.message!!
        } finally {
            closeSocket(mySocket)
        }
    }

    /**
     * 发送图片
     */
    fun inquire(bmp: Bitmap, address: String): String {
        try {
            mySocket.connect(InetSocketAddress(host, PORT), loadingTime * 1000)
            mySocket.soTimeout = loadingTime * 1000
            os = mySocket.getOutputStream()
            os!!.write((address + "\u0004").toByteArray())
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, os)
//            bw = BufferedWriter(OutputStreamWriter(os!!))
            `is` = mySocket.getInputStream()
            br = BufferedReader(InputStreamReader(`is`!!))
            mySocket.shutdownOutput()
            var receive: String? = null
            while (receive == null) {
                receive = br!!.readLine()
            }
            return receive
        } catch (ste: SocketTimeoutException) {
            return REQUEST_ERROR
        } catch (ioe: IOException) {
            return SOCKET_ERROR
        } catch (e: Exception) {
            return e.message!!
        } finally {
            closeSocket(mySocket)
        }
    }

    /**
     * 得到文件下所有文件名
     */
    fun getAllFileName(address: String): String {
        try {
            mySocket.connect(InetSocketAddress(host, PORT), loadingTime * 1000)
            mySocket.soTimeout = loadingTime * 1000
            os = mySocket.getOutputStream()
            bw = BufferedWriter(OutputStreamWriter(os!!))
            `is` = mySocket.getInputStream()
            br = BufferedReader(InputStreamReader(`is`!!))
            os!!.write(address.toByteArray())
            mySocket.shutdownOutput()//可以不用关，这只是个人习惯关闭而已
            var receive: String? = null
            while (receive == null) {
                receive = br!!.readLine()
            }
            return receive
        } catch (ste: SocketTimeoutException) {
            return REQUEST_ERROR
        } catch (ioe: IOException) {
            return SOCKET_ERROR
        } catch (e: Exception) {
            return e.message!!
        } finally {
            closeSocket(mySocket)
        }
    }

    /**
     * 得到图片
     */
    fun inquire(address: String): Bitmap? {
        return try {
            mySocket.connect(InetSocketAddress(host, PORT), loadingTime * 1000)
            mySocket.soTimeout = loadingTime * 1000
            SocketUtilJava.inquire(mySocket, address)
        } catch (ste: SocketTimeoutException) {
            null
        } catch (ioe: IOException) {
            null
        } catch (e: Exception) {
            null
        } finally {
            closeSocket(mySocket)
        }
    }

    /**
     * 得到图片地址
     */
    fun inquireF(address: String): Array<String>? {
        return try {
            mySocket.connect(InetSocketAddress(host, PORT), loadingTime * 1000)
            mySocket.soTimeout = loadingTime * 1000
            SocketUtilJava.inquireF(mySocket, address)
        } catch (ste: SocketTimeoutException) {
            null
        } catch (ioe: IOException) {
            null
        } catch (e: Exception) {
            null
        } finally {
            closeSocket(mySocket)
        }
    }
}
