package com.cstore.zhiyazhang.cstoremanagement.utils.socket;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
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

    public static StringBuilder readerToString(Reader reader, StringBuilder content) throws IOException {
        char[] cbuf = new char[512];
        int len = -1;
        while ((len = reader.read(cbuf)) > -1) {
            content.append(cbuf, 0, len);
        }
        return content;
    }

    public static String inquire(BufferedReader br) throws IOException {
        String sqlResult;
        while ((sqlResult = br.readLine()) != null) {

        }
        return sqlResult;
    }
}
