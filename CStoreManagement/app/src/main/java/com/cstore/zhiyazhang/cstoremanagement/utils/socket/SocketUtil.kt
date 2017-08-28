package com.cstore.zhiyazhang.cstoremanagement.utils.socket

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Toast
import com.zhiyazhang.mykotlinapplication.utils.MyApplication
import java.io.*
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException

/**
 * Created by zhiya.zhang
 * on 2017/5/19 15:32.
 * Socket工具类，未解决问题，如何才能保持全局只开启一次socket，被shutdown之后还能不用重新new直接开启
 * @author zhiya.zhang
 * *
 * @since 1.0
 */

class SocketUtil private constructor(host: String) {

    companion object {
        private var mySocket: Socket? = null
        private var host: String? = null
        private val port = 49999
        @Volatile private var socketUtil: SocketUtil? = null
        private val mContext = MyApplication.instance().applicationContext
        private val REQUEST_ERROR = "服务器连接超时"
        private val SOCKET_ERROR = "服务器异常，确定连在内网中，确定服务器正常"
        private val NULL_HOST = "host为空"
        private var os: OutputStream? = null
        private var myBW: BufferedWriter? = null
        private var `is`: InputStream? = null
        private var myBR: BufferedReader? = null

        /**
         * 单列模式,写入host

         * @param host ip_host
         * *
         * @return SocketUtil的单列
         */
        fun getSocketUtil(host: String): SocketUtil {
            if (SocketUtil.socketUtil == null || SocketUtil.mySocket == null) {
                synchronized(SocketUtil::class.java) {
                    if (SocketUtil.socketUtil == null || SocketUtil.mySocket == null) {
                        SocketUtil.socketUtil = SocketUtil(host)
                    }
                }
            }
            return SocketUtil.socketUtil!!
        }
    }

    init {
        SocketUtil.host = host
        mySocket = Socket()
    }

    /**
     * 发送数据,不返回数据

     * @param message 要传递给服务器的数据
     */
    @Synchronized fun send(message: String) {
        Thread(Runnable {
            try {
                if (host == null) {
                    Toast.makeText(mContext, NULL_HOST, Toast.LENGTH_SHORT).show()
                    return@Runnable
                }
                Looper.prepare()
                //连接服务器 并设置连接超时为5秒
                if (!mySocket!!.isConnected) {
                    mySocket!!.connect(InetSocketAddress(host, port), 5000)
                }
                //socket的输入流和输出流
                os = mySocket!!.getOutputStream()
                myBW = BufferedWriter(OutputStreamWriter(os!!))
                `is` = mySocket!!.getInputStream()
                myBR = BufferedReader(InputStreamReader(`is`!!))
                //对socket进行读写
                myBW!!.write(message)
                myBW!!.flush()
                mySocket!!.shutdownOutput()
            } catch (aa: SocketTimeoutException) {
                Toast.makeText(mContext, REQUEST_ERROR, Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Toast.makeText(mContext, SOCKET_ERROR, Toast.LENGTH_SHORT).show()
            } finally {
                mySocket = null
                Looper.loop()
                //关闭各种流
                closeSocket()
            }
        }).start()
    }

    /**
     * 向服务器查询数据，且将服务器发回的字符串通过handler传回去
     * 0==成功 1==超时 2==服务器异常
     * @param message  要传递给服务器的数据
     * @param loadingTime 链接超时秒数
     * @param mHandler 初始化时需传入发送的消息与用来响应返回数据的handler
     */
    @Synchronized fun inquire(message: String, loadingTime: Int, mHandler: Handler) {
        Thread(Runnable {
            try {
                Looper.prepare()
                if (host == null) {
                    Toast.makeText(mContext, NULL_HOST, Toast.LENGTH_SHORT).show()
                    return@Runnable
                }
                //连接服务器 并设置连接超时为 loadingTime * 1000
                if (!mySocket!!.isConnected) {
                    mySocket!!.connect(InetSocketAddress(host, port), loadingTime * 1000)
                    mySocket!!.soTimeout = loadingTime * 1000
                }
                //socket的输入流和输出流
                val os = mySocket!!.getOutputStream()
                val myBW = BufferedWriter(OutputStreamWriter(os))
                val `is` = mySocket!!.getInputStream()
                val myBR = BufferedReader(InputStreamReader(`is`))

                //对socket进行读写
                myBW.write(String(message.toByteArray(charset("utf-8"))))
                myBW.flush()
                mySocket!!.shutdownOutput()

                var receive: String? = null

                while (receive == null) {
                    receive = myBR.readLine()
                }

                //定义消息
                val msg = Message()
                msg.obj = receive
                msg.what = 0

                //把信息返回给Handler
                mHandler.sendMessage(msg)
            } catch (aa: SocketTimeoutException) {
                val msg = Message()
                msg.obj = REQUEST_ERROR
                msg.what = 1
                mHandler.sendMessage(msg)
            } catch (e: IOException) {
                val msg = Message()
                msg.obj = SOCKET_ERROR
                msg.what = 2
                mHandler.sendMessage(msg)
            } catch (e: Exception) {
                val msg = Message()
                msg.obj = e.message
                msg.what = 2
                mHandler.sendMessage(msg)
            } finally {
                mySocket = null
                Looper.loop()
                //关闭各种流
                closeSocket()
            }
        }).start()
    }

    /**
     * 关闭各种流
     */
    @Synchronized private fun closeSocket() {
        try {
            myBW!!.close()
        } catch (ignored: IOException) {
        }

        try {
            myBR!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            os!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            `is`!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            mySocket!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}
