package com.c_store.zhiyazhang.cstoremanagement.utils.socket;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.c_store.zhiyazhang.cstoremanagement.utils.MyApplication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by zhiya.zhang
 * on 2017/5/19 15:32.
 * Socket工具类，未解决问题，如何才能保持全局只开启一次socket，被shutdown之后还能不用重新new直接开启
 * @author zhiya.zhang
 * @since 1.0
 */

public class SocketUtil {
    private static Socket mySocket;
    private static String host;
    private static final int port = 49999;
    private static final int SUCCESS = 1;
    private static final int INTERNET_ERROR = 0;
    private static final int SERVER_ERROR = -1;
    private static final int CODE_ERROR = -2;
    private volatile static SocketUtil socketUtil;
    private static final Context mContext = MyApplication.getContext();
    private static final String REQUEST_ERROR = "服务器连接超时，确定连在内网中，确定服务器正常";
    private static final String SOCKET_ERROR = "请求出错";
    private static final String NULL_HOST = "host为空";
    private static OutputStream os;
    private static BufferedWriter myBW;
    private static InputStream is;
    private static BufferedReader myBR;

    private SocketUtil(String host) {
        SocketUtil.host = host;
        mySocket = new Socket();
    }

    /**
     * 单列模式,写入host
     *
     * @param host ip_host
     * @return SocketUtil的单列
     */
    public static SocketUtil getSocketUtil(String host) {
        if (socketUtil == null || mySocket == null) {
            synchronized (SocketUtil.class) {
                if (socketUtil == null || mySocket == null) {
                    socketUtil = new SocketUtil(host);
                }
            }
        }
        return socketUtil;
    }

    /**
     * 发送数据,不返回数据
     *
     * @param message 要传递给服务器的数据
     */
    public synchronized void send(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (host == null) {
                        Toast.makeText(mContext, NULL_HOST, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Looper.prepare();
                    //连接服务器 并设置连接超时为5秒
                    if (!mySocket.isConnected()) {
                        mySocket.connect(new InetSocketAddress(host, port), 5000);
                    }
                    //socket的输入流和输出流
                    os = mySocket.getOutputStream();
                    myBW = new BufferedWriter(new OutputStreamWriter(os));
                    is = mySocket.getInputStream();
                    myBR = new BufferedReader(new InputStreamReader(is));
                    //对socket进行读写
                    myBW.write(message);
                    myBW.flush();
                    mySocket.shutdownOutput();
                } catch (SocketTimeoutException aa) {
                    Toast.makeText(mContext, REQUEST_ERROR, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(mContext, SOCKET_ERROR, Toast.LENGTH_SHORT).show();
                } finally {
                    mySocket = null;
                    Looper.loop();
                    //关闭各种流
                    closeSocket();
                }
            }
        }).start();
    }

    /**
     * 向服务器查询数据，且将服务器发回的字符串通过handler传回去
     *
     * @param message  要传递给服务器的数据
     * @param mHandler 初始化时需传入发送的消息与用来响应返回数据的handler
     */
    public synchronized void inquire(final String message, final Handler mHandler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (host == null) {
                        Toast.makeText(mContext, NULL_HOST, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Looper.prepare();
                    //连接服务器 并设置连接超时为5秒
                    if (!mySocket.isConnected()) {
                        mySocket.connect(new InetSocketAddress(host, port), 5000);
                    }
                    //socket的输入流和输出流
                    OutputStream os = mySocket.getOutputStream();
                    BufferedWriter myBW = new BufferedWriter(new OutputStreamWriter(os));
                    InputStream is = mySocket.getInputStream();
                    BufferedReader myBR = new BufferedReader(new InputStreamReader(is));
                    //对socket进行读写
                    myBW.write(new String(message.getBytes("utf-8")));
                    myBW.flush();
                    mySocket.shutdownOutput();

                    String receive = "";
                    String content;
                    //循环到得到数据或者超时
                    while ((content = myBR.readLine()) != null) {
                        receive += content;
                    }
                    //定义消息
                    Message msg = new Message();
                    msg.obj = receive;
                    //把信息返回给Handler
                    msg.what=0;
                    mHandler.sendMessage(msg);
                } catch (SocketTimeoutException aa) {
                    Message msg = new Message();
                    msg.obj = REQUEST_ERROR;
                    msg.what=1;
                    mHandler.sendMessage(msg);
                } catch (IOException e) {
                    Message msg = new Message();
                    msg.obj = SOCKET_ERROR;
                    msg.what=2;
                    mHandler.sendMessage(msg);
                } finally {
                    mySocket = null;
                    Looper.loop();
                    //关闭各种流
                    closeSocket();
                }
            }
        }).start();
    }

    /**
     * 关闭各种流
     */
    private synchronized void closeSocket() {
        try {
            if (myBW != null) {
                myBW.close();
            }
        } catch (IOException ignored) {
        }

        try {
            if (myBR != null) {
                myBR.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (os != null) {
                os.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (mySocket != null) {
                mySocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
