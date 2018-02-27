package com.cstore.zhiyazhang.cstoremanagement.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by zhiya.zhang
 * on 2018/2/27 10:40.
 */

public class MyJavaFun {
    /**
     * 获取txt文件内容
     * @param file 要获取内容的文件
     * @return 以字符串显示的文件内容
     */
    public static String getTxtFileMessage(File file) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
