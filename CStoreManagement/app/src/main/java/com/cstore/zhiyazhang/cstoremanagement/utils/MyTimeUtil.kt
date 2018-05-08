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
        return date.time
    }

    /**
     * Long转为时间

     * @param date 时间
     * *
     * @return date
     */
    fun getDateByLong(date: Long): Date {
        return Date(date)
    }

    /**
     * 时间转为String

     * @param date 时间
     * *
     * @return string
     */
    fun getStringByDate(date: Date): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(date)
    }

    fun getStringByDate3(date: Date): String {
        return SimpleDateFormat("yyyy-MM-dd HH.mm.ss", Locale.CHINA).format(date)
    }

    fun getStringByDate2(date: Date): String {
        return SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(date)
    }

    public fun getYMDStringByDate(date: Date): String {
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
    @JvmStatic
    fun deleteTime(date: String): String {
        if (date.isEmpty() || date == "null") {
            return "无"
        }
        return try {
            getYMDStringByDate(getDateByString(date))
        } catch (e: Exception) {
            date
        }
    }

    /**
     * 删除年月日只要时间
     */
    fun deleteDate(date: String): String {
        if (date.isEmpty() || date == "null") {
            return "无"
        }
        return try {
            getTimeByDate(getDateByString(date))
        } catch (e: Exception) {
            date
        }
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

    fun getDateByString3(date: String): Date {
        return SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).parse(date)
    }

    @JvmStatic
    fun getDateByString2(data: String): Date {
        return SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse(data)
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

    val nowYear: Int
        get() {
            return Calendar.getInstance().get(Calendar.YEAR)
        }

    fun dayOfYear(): String {
        return Calendar.getInstance().get(Calendar.DAY_OF_YEAR).toString().padStart(3, '0')
    }

    fun nowYear(): String {
        return nowYear.toString().substring(2)
    }

    fun nowMonth(): String {
        val result = nowMonth
        return if (result < 10) {
            "0$result"
        } else {
            result.toString()
        }
    }

    /**
     * @return 获得当前时间
     */
    val nowTimeString: String
        get() = getStringByDate(Date(System.currentTimeMillis()))

    val nowTimeString2: String
        get() = getStringByDate2(Date(System.currentTimeMillis()))

    val nowTimeString3: String
        get() = getStringByDate3(Date(System.currentTimeMillis()))

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
     * 通过String日期得到Calendar格式的日期，包含小时分秒
     */
    fun getCalendarByStringHMS(data: String): Calendar {
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).parse(data)
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
        val data = cal.time
//        cal.firstDayOfWeek = Calendar.MONDAY
        if (isWeek != 0) cal.add(Calendar.WEEK_OF_YEAR, isWeek)
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val data2 = cal.time
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

    /**
     * 在日期上添加时间
     * @param type respectively add year or month or day
     * @return format is Date
     */
    fun getAddDate(type: String, date: Date, addNumber: Int): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.firstDayOfWeek = Calendar.MONDAY
        when (type) {
            "year" -> {
                cal.add(Calendar.YEAR, addNumber)
            }
            "month" -> {
                cal.add(Calendar.MONTH, addNumber)
            }
            "day" -> {
                cal.add(Calendar.DAY_OF_MONTH, addNumber)
            }
        }
        return cal.time
    }

    /**
     * 得到当前月份的最大日期
     * @param date 基数日期
     */
    fun getMaxDateByNowMonth(date: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        cal.set(Calendar.DAY_OF_MONTH, maxDay)
        return cal.time
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
        cal.add(Calendar.WEEK_OF_YEAR, 1)
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
             * 得到是周几
             */
    fun getNowWeek(data: String): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val date = sdf.parse(data)
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.time = date
        return cal.get(Calendar.DAY_OF_WEEK) - 1
    }

    /**
     * 日期添加天数的时间
     * @param date 要操作的日期
     * @param day 要添加的天数
     */
    fun dateAddDay(date: String, day: Int): String {
        if (day == 0) return date
        val nowDate = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse(date)
        val calendar = Calendar.getInstance()
        calendar.time = nowDate
        calendar.add(Calendar.DATE, day)
        return SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(calendar.time)
    }

    /**
     * 两个日期相差多少小时
     * @param beginDate 被减时间
     * @param endDate 基数时间
     */
    fun getHourPoor(beginDate: String, endDate: String): Int {
        val diff = getDateByString(endDate).time - getDateByString(beginDate).time
        val hour = diff / 1000 / 60 / 60
        return hour.toInt()
    }

    /**
     * 两个日期相差多少分钟
     * @param beginDate 被减时间
     * @param endDate 基数时间
     */
    fun getMinutePoor(beginDate: String, endDate: String): Int {
        val diff = getDateByString(endDate).time - getDateByString(beginDate).time
        val minute = diff / 1000 / 60
        return minute.toInt()
    }

    /**
     * 两个日期相差多少天
     * @param beginDate 被减时间
     * @param endDate 基数时间
     */
    fun getDayPoor(beginDate: String, endDate: String): Int {
        val diff = getDateByString(endDate).time - getDateByString(beginDate).time
        val day = diff / 1000 / 60 / 60 / 24
        return day.toInt()
    }

    /**
     * 截取日期的 '日'
     */
    fun getDayByDate(data: String): Int {
        val calendar = Calendar.getInstance()
        val date = getDateByString(data)
        calendar.time = date
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * 截取日期的 '时'
     */
    fun getHourByDate(data: String): Int {
        val calendar = Calendar.getInstance()
        val date = getDateByString(data)
        calendar.time = date
        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    /**
     * 截取日期的 '分'
     */
    fun getMinuteByDate(data: String): Int {
        val calendar = Calendar.getInstance()
        val date = getDateByString(data)
        calendar.time = date
        return calendar.get(Calendar.MINUTE)
    }


    /**
     * 判断是否添加0
     */
    fun isAddZero(value: Int): String {
        return if (value < 10) {
            "0$value"
        } else {
            value.toString()
        }
    }

    /**
     * 根据月份得到上个月月份
     */
    fun getLastMonthByMonth(month: String): String {
        return if (month == "1") {
            "12"
        } else {
            (month.toInt() - 1).toString()
        }
    }
}
