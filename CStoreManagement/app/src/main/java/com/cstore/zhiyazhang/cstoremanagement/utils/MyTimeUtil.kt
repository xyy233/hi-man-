package com.cstore.zhiyazhang.cstoremanagement.utils

import android.annotation.SuppressLint
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.layout_date.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by zhiya.zhang
 * on 2017/6/23 10:57.
 */
object MyTimeUtil {
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
    private fun getStringByDate(date: Date): String {
        return SimpleDateFormat("yyyy-MM-dd HH.mm.ss", Locale.CHINA).format(date)
    }

    private fun getStringByDate2(date: Date): String {
        return SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(date)
    }

    private fun getYMDStringByDate(date: Date): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(date)
    }

    private fun getYMDStringByDate2(date: Date): String {
        return SimpleDateFormat("yyyy/MM/dd", Locale.CHINA).format(date)
    }

    fun getYMDStringByDate3(date: Date): String {
        return SimpleDateFormat("yyyyMMdd", Locale.CHINA).format(date)
    }

    private fun getTimeByDate(date: Date): String {
        return SimpleDateFormat("HH:mm", Locale.CHINA).format(date)
    }

    /**
     * 删除时间只要年月日
     */
    fun deleteTime(date: String): String {
        if (date.isEmpty() || date == "null") {
            return "无"
        }
        return getYMDStringByDate(getDateByString(date))
    }

    /**
     * 删除年月日只要时间
     */
    fun deleteDate(date: String): String {
        if (date.isEmpty() || date == "null") {
            return "无"
        }
        return getTimeByDate(getDateByString(date))
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
    private fun getDateByString(date: String): Date {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).parse(date)
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
        return SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.CHINA).parse(date).time / 1000
    }

    /**
     * Long时间转换为String时间MM/dd/yyy HH:mm:ss

     * @param date 时间
     * *
     * @return String
     */
    fun getStringByLong(date: Long?): String {
        return SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.CHINA).format(Date(date!! * 1000))
    }

    /**

     * @return 获得当前日期的日
     */
    val todayDay: Int
        @SuppressLint("WrongConstant")
        get() = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    val nowHour: Int
        get() = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    val nowMonth: Int
        get() {
            return Calendar.getInstance().get(Calendar.MONTH) + 1
        }

    private val nowYear: Int
        get() {
            return Calendar.getInstance().get(Calendar.YEAR)
        }

    fun dayOfYear(): String {
        return Calendar.getInstance().get(Calendar.DAY_OF_YEAR).toString().padStart(3, '0')
    }

    fun nowYear(): String {
        return nowYear.toString().substring(2)
    }

    /**
     * @return 获得当前时间
     */
    val nowTimeString: String
        get() = getStringByDate(Date(System.currentTimeMillis()))

    val nowTimeString2: String
        get() = getStringByDate2(Date(System.currentTimeMillis()))

    val nowDate: String
        get() = getYMDStringByDate(Date(System.currentTimeMillis()))

    val nowDate2: String
        get() = getYMDStringByDate2(Date(System.currentTimeMillis()))

    val nowDate3: String
        get() = getYMDStringByDate3(Date(System.currentTimeMillis()))

    val tomorrowDate: String
        get() {
            val date = Date()//取时间
            val calendar: Calendar = GregorianCalendar()
            calendar.time = date
            calendar.add(Calendar.DATE, 1)//把日期往后增加一天.整数往后推,负数往前移动
            return SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(calendar.time)
        }

    val tomorrowDate2: String
        get() {
            val date = Date()//取时间
            val calendar: Calendar = GregorianCalendar()
            calendar.time = date
            calendar.add(Calendar.DATE, 1)//把日期往后增加一天.整数往后推,负数往前移动
            return SimpleDateFormat("yyyyMMdd", Locale.CHINA).format(calendar.time)
        }

    /**
     * 通过String日期得到Calendar格式的日期
     */
    fun getCalendarByString(data: String): Calendar {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse(data)
        val result = Calendar.getInstance()
        result.time = date
        return result
    }

    /**
     * 写入日期到TextView插件
     */
    fun setTextViewDate(dateUtil: LinearLayout, nowDate: String) {
        val calendar = getCalendarByString(nowDate)
        val year = "${calendar.get(Calendar.YEAR)}年"

        val month = if (calendar.get(Calendar.MONTH) + 1 < 10) "0${calendar.get(Calendar.MONTH) + 1}月"
        else "${calendar.get(Calendar.MONTH) + 1}月"

        val day = if (calendar.get(Calendar.DAY_OF_MONTH) < 10) "0${calendar.get(Calendar.DAY_OF_MONTH)}"
        else calendar.get(Calendar.DAY_OF_MONTH).toString()

        dateUtil.year.text = year
        dateUtil.month.text = month
        dateUtil.day.text = day
    }

    /**
     * 得到TextView插件的日期
     */
    fun getTextViewDate(dateUtil: LinearLayout): String {
        return "${dateUtil.year.text.toString().replace("年", "")}-${dateUtil.month.text.toString().replace("月", "")}-${dateUtil.day.text}"
    }

    /**
     * 获得周一日期
     * @param isWeek isWeek为摇摆数+1为当前周加一天，本周为0，上周为-1
     */
    fun getWeekMondayDate(isWeek: Int): String {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.add(Calendar.WEEK_OF_YEAR, isWeek)
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        return SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(cal.time)
    }

    /**
     * 获得周日日期
     * @param isWeek isWeek为摇摆数+1为当前周加一天，本周为0，上周为-1
     */
    fun getWeekSundayDate(isWeek: Int): String {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.add(Calendar.WEEK_OF_YEAR, isWeek)
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        return SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(cal.time)
    }

    @SuppressLint("SimpleDateFormat")
            /**
             * 根据日期获得日期周的周日
             */
    fun getWeekSundayByDate(date: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val nowDate = sdf.parse(date)
        val cal = Calendar.getInstance()
        cal.time = nowDate
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        return SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(cal.time)
    }

    /**
     * 获得两个时间相差的小时数
     * @param dateStart 开始时间
     * @param dateEnd 结束时间
     */
    fun getDiscrepantHours(dateStart: String, dateEnd: String): Int {
        val startDate = getDateByString(dateStart)
        val endDate = getDateByString(dateEnd)
        return ((endDate.time - startDate.time) / 1000 / 60 / 60).toInt()
    }

    @SuppressLint("SimpleDateFormat")
            /**
             * 得到当前是周几
             */
    fun getNowWeek(data: String): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val date = sdf.parse(data)
        val cal = Calendar.getInstance()
        cal.time = date
        return cal.get(Calendar.DAY_OF_WEEK)
    }
}
