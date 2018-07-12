package com.cstore.zhiyazhang.cstoremanagement.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhiya.zhang
 * on 2018/7/2 14:38.
 */

public class MJBMD5Util {

    public static String sign(Map<String, String> paramValues, String appId, String secret) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        List<String> ignoreParamNames = new LinkedList<>();
        return sign(paramValues, ignoreParamNames, appId, secret);
    }

    private static String sign(Map<String, String> paramValues, List<String> ignoreParamNames, String appId, String secret) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        List<String> paramNames = new ArrayList<>(paramValues.size());
        paramNames.addAll(paramValues.keySet());
        if (ignoreParamNames != null && ignoreParamNames.size() > 0) {
            paramNames.removeAll(ignoreParamNames);
        }
        Collections.sort(paramNames);
        sb.append(secret);
        for (String paramName : paramNames) {
            sb.append(paramName).append(paramValues.get(paramName));
        }
        sb.append(secret);

        MessageDigest md = MessageDigest.getInstance("SHA-1");
        return byte2hex(md.digest(sb.toString().getBytes("UTF-8")));
    }

    private static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }
}
