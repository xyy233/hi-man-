package com.cstore.zhiyazhang.cstoremanagement.utils.socket;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by zhiya.zhang
 * on 2018/3/8 17:38.
 */

public class SocketUtilJava {

    public static Bitmap inquire(Socket socket, String address) throws IOException {
        OutputStream os = socket.getOutputStream();
        String msg = "fileget " + address;
        os.write(msg.getBytes());
        socket.shutdownOutput();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        InputStream is = socket.getInputStream();
        byte[] buf = new byte[1024];
        int len;
        while (-1 != (len = is.read(buf)))
            bos.write(buf, 0, len);
        byte[] b = bos.toByteArray();
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    public static String[] inquireF(Socket socket, String address) throws IOException {
        OutputStream os = socket.getOutputStream();
        String msg = "filelist " + address;
        os.write(msg.getBytes());
        socket.shutdownOutput();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        InputStream is = socket.getInputStream();
        byte[] buf = new byte[1024];
        int len;
        while (-1 != (len = is.read(buf)))
            bos.write(buf, 0, len);
        String b = bos.toString();
        return new Gson().fromJson(b, String[].class);
    }
}
