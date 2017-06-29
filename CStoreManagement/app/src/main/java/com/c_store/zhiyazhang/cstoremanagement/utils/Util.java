package com.c_store.zhiyazhang.cstoremanagement.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zhiya.zhang
 * on 2017/5/26 15:15.
 */

public class Util {
    /**
     * 时间转为Long
     *
     * @param date 时间
     * @return long
     */
    public static Long getLongByDate(Date date) {
        return date.getTime() / 1000;
    }

    /**
     * Long转为时间
     *
     * @param date 时间
     * @return date
     */
    public static Date getDateByLong(Long date) {
        return new Date(date * 1000);
    }

    /**
     * 时间转为String
     *
     * @param date 时间
     * @return string
     */
    public static String getStringByDate(Date date) {
        return new SimpleDateFormat("MM/dd/yyy HH:mm:ss").format(date);
    }

    /**
     * String转为时间
     *
     * @param date 时间
     * @return date
     * @throws ParseException 转换失败
     */
    public static Date getDateByString(String date) throws ParseException {
        return new SimpleDateFormat("MM/dd/yyy HH:mm:ss").parse(date);
    }

    /**
     * String时间MM/dd/yyy HH:mm:ss转换为Long
     *
     * @param date 时间
     * @return Long
     * @throws ParseException 转换失败
     */
    public static Long getLongByString(String date) throws ParseException {
        return (new SimpleDateFormat("MM/dd/yyy HH:mm:ss").parse(date).getTime()) / 1000;
    }

    /**
     * Long时间转换为String时间MM/dd/yyy HH:mm:ss
     *
     * @param date 时间
     * @return String
     */
    public static String getStringByLong(Long date) {
        return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date(date * 1000));
    }

    /**
     *
     * @return 获得当前日期的日
     */
    @SuppressLint("WrongConstant")
    public static int getTodayDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }
}
