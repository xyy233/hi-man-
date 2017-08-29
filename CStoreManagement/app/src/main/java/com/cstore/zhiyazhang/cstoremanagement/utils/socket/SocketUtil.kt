package com.cstore.zhiyazhang.cstoremanagement.utils.socket

import android.os.Looper
import android.os.Message
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

internal class SocketUtil private constructor(ip: String, msg: String, lt: Int) {

    init {
        mySocket = Socket()
        host = ip
        message = msg
        loadingTime = lt
    }

    companion object {
        lateinit private var mySocket: Socket
        private val PORT = 49999
        private val REQUEST_ERROR = "服务器连接超时"
        private val SOCKET_ERROR = "服务器异常，确定连在内网中，确定服务器正常"
        private val NULL_HOST = "host为空"
        private var message = ""
        private var loadingTime = 10
        lateinit private var myHandler: MyHandler.MyHandler
        lateinit private var host: String
        private var os: OutputStream? = null
        private var bw: BufferedWriter? = null
        private var `is`: InputStream? = null
        private var br: BufferedReader? = null
        private val myRunnable = Runnable {
            try {
                Looper.prepare()
                mySocket.connect(InetSocketAddress(host, PORT), loadingTime * 1000)
                mySocket.soTimeout = loadingTime * 1000
                os = mySocket.getOutputStream()
                bw = BufferedWriter(OutputStreamWriter(os!!))
                `is` = mySocket.getInputStream()
                br = BufferedReader(InputStreamReader(`is`!!))
                bw!!.write(message)
                bw!!.flush()
                var receive: String? = null
                while (receive == null) {
                    receive = br!!.readLine()
                }
                val msg = Message()
                msg.obj = receive
                msg.what = 0
                myHandler.sendMessage(msg)
            } catch (ste: SocketTimeoutException) {
                val msg = Message()
                msg.obj = REQUEST_ERROR
                msg.what = 1
                myHandler.sendMessage(msg)
            } catch (ioe: IOException) {
                val msg = Message()
                msg.obj = SOCKET_ERROR
                msg.what = 1
                myHandler.sendMessage(msg)
            } catch (e: Exception) {
                val msg = Message()
                msg.obj = e.message
                msg.what = 1
                myHandler.sendMessage(msg)
            } finally {
                Looper.loop()
                closeSocket()
            }
        }

        private fun closeSocket() {
            try {
                bw!!.close()
            } catch (ignored: IOException) {
            }

            try {
                br!!.close()
            } catch (ignored: IOException) {
            }

            try {
                os!!.close()
            } catch (ignored: IOException) {
            }

            try {
                `is`!!.close()
            } catch (ignored: IOException) {

            }

            try {
                mySocket.close()
            } catch (ignored: IOException) {

            }

        }

        fun initSocket(ip: String, message: String, loadingTime: Int): SocketUtil {
            return SocketUtil(ip, message, loadingTime)
        }

    }

    fun inquire():String {
        /*val thread = Thread(myRunnable)
        thread.start()*/
        mySocket.connect(InetSocketAddress(host, PORT), loadingTime * 1000)
        mySocket.soTimeout = loadingTime * 1000
        os = mySocket.getOutputStream()
        bw = BufferedWriter(OutputStreamWriter(os!!))
        `is` = mySocket.getInputStream()
        br = BufferedReader(InputStreamReader(`is`!!))
        bw!!.write(message)
        bw!!.flush()
        var receive: String? = null
        while (receive == null) {
            receive = br!!.readLine()
        }
        return receive
    }
}
