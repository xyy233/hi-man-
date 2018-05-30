package com.cstore.zhiyazhang.cstoremanagement.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by zhiya.zhang
 * on 2017/11/15 10:06.
 */

public class JavaFunUtil {

    /**
     * inputStream转换成byte
     *
     * @param input 就是input
     * @return 转换完的byte
     * @throws IOException 流错误
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    /**
     * inputStream转换成byte
     *
     * @return 转换好的文件
     * @throws IOException 流错误
     */
    public static File toFile(InputStream ins) throws IOException {
        File file = new File("");
        OutputStream os = new FileOutputStream(file);
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        ins.close();
        return file;
    }

    public static String judgmentKey() {
        try {
            //获得外接USB输入设备的信息
            Process p = Runtime.getRuntime().exec("cat /proc/bus/input/devices");
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            StringBuilder result = new StringBuilder();
            while ((line = in.readLine()) != null) {
                String deviceInfo = line.trim();
                result.append(deviceInfo).append("\n");
                Log.e("Key", deviceInfo);
            }
            return result.toString();
        } catch (Exception e) {
            return "";
        }
    }
}
