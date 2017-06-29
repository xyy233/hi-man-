package com.cstore.zhiyazhang.cstoremanagement.utils

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by zhiya.zhang
 * on 2017/6/23 10:57.
 */
object MyTimeUtil{
    /**
     * 时间转为Long

     * @param date 时间
     * *
     * @return long
     */
    fun getLongByDate(date: Date): Long {
        return date.time / 1000
    }

    /**
     * Long转为时间

     * @param date 时间
     * *
     * @return date
     */
    fun getDateByLong(date: Long?): Date {
        return Date(date!! * 1000)
    }

    /**
     * 时间转为String

     * @param date 时间
     * *
     * @return string
     */
    fun getStringByDate(date: Date): String {
        return SimpleDateFormat("MM/dd/yyy HH:mm:ss").format(date)
    }

    /**
     * String转为时间

     * @param date 时间
     * *
     * @return date
     * *
     * @throws ParseException 转换失败
     */
    @Throws(ParseException::class)
    fun getDateByString(date: String): Date {
        return SimpleDateFormat("MM/dd/yyy HH:mm:ss").parse(date)
    }

    /**
     * String时间MM/dd/yyy HH:mm:ss转换为Long

     * @param date 时间
     * *
     * @return Long
     * *
     * @throws ParseException 转换失败
     */
    @Throws(ParseException::class)
    fun getLongByString(date: String): Long {
        return SimpleDateFormat("MM/dd/yyy HH:mm:ss").parse(date).time / 1000
    }

    /**
     * Long时间转换为String时间MM/dd/yyy HH:mm:ss

     * @param date 时间
     * *
     * @return String
     */
    fun getStringByLong(date: Long?): String {
        return SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(Date(date!! * 1000))
    }

    /**

     * @return 获得当前日期的日
     */
    val todayDay: Int
        @SuppressLint("WrongConstant")
        get() = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
}
