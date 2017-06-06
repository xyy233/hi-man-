package com.c_store.zhiyazhang.cstoremanagement.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by zhiya.zhang
 * on 2017/6/1 12:26.
 * 先执行getInstance再去执行其他操作
 * <p>
 * 使用方法:
 * 需要得到明天的时间
 * Date date = MyDate.getInstance().addDay(1).getDate();
 * 需要得到昨天的时间
 * Date date = MyDate.getInstance().addDay(-1).getDate();
 * 方法可以修改 年、月、日、时、分。
 * </p>
 * <p>
 * 得到String类型年月日使用方法:
 * 需要得到明天的String类型 yyyy-MM-dd hh:mm:ss 时间
 * String needDate = MyDate.getInstance().addDay(1).getStringDateByDate();
 * 需要得到已有Date类型数据的String类型 yyyy-MM-dd hh:mm:ss 时间
 * String needDate = MyDate.getStringDateByDate(yourDate);
 * 需要得到昨天的String类型的自定格式时间
 * String needDate = MyDate.getInstance().addDay(-1).getStringDateByDate("yyyy-MM-dd");
 * 需要得到已有Date类型数据的String类型自定格式美国地区时间
 * String needDate = MyDate.getStringDateByDate(yourDate, "yyyy-MM-dd", Locale.US);
 * 其他用法自行查看
 * </p>
 */

public class MyDate {
    private static MyDate instance;
    private Date date;

    private MyDate() {
    }

    /**
     * 得到当前时间
     *
     * @return Date格式时间
     */
    public static MyDate getInstance() {
        if (instance == null) {
            instance = new MyDate();
        }
        instance.date = new Date();
        return instance;
    }

    public Date getDate() {
        return instance.date;
    }

    /**
     * 得到当前时间加n天
     *
     * @param day 添加的天数可以为负，既为减天数、加天数
     * @return Date格式时间
     */
    @SuppressLint("WrongConstant")
    public MyDate addDay(int day) {
        //先把date转为calendar
        Calendar cal = getCalendarByDate(instance.date);
        //calender修改天数
        cal.add(Calendar.DATE, day);
        //转回date
        instance.date = getDateByCalendar(cal);
        return instance;
    }

    /**
     * 得到当前时间加n小时
     *
     * @param hour 添加的小时数可以为负，既为减小时数、加小时数
     * @return Date格式时间
     */
    @SuppressLint("WrongConstant")
    public MyDate addHour(int hour) {
        //先把date转为calendar
        Calendar cal = getCalendarByDate(instance.date);
        //calender修改小时数
        cal.add(Calendar.HOUR, hour);
        //转回date
        instance.date = getDateByCalendar(cal);
        return instance;
    }

    /**
     * 得到当前时间加n月
     *
     * @param month 添加的月数可以为负，既为减月数、加月数
     * @return Date格式时间
     */
    @SuppressLint("WrongConstant")
    public MyDate addMonth(int month) {
        //先把date转为calendar
        Calendar cal = getCalendarByDate(instance.date);
        //calender修改月数
        cal.add(Calendar.MONTH, month);
        //转回date
        instance.date = getDateByCalendar(cal);
        return instance;
    }

    /**
     * 得到当前时间加n年
     *
     * @param year 添加的年数可以为负，既为减年数、加年数
     * @return Date格式时间
     */
    @SuppressLint("WrongConstant")
    public MyDate addYear(int year) {
        //先把date转为calendar
        Calendar cal = getCalendarByDate(instance.date);
        //calender修改年数
        cal.add(Calendar.YEAR, year);
        //转回date
        instance.date = getDateByCalendar(cal);
        return instance;
    }

    /**
     * Calendar转换成Date
     *
     * @param cal 传入的Calendar时间数据
     * @return 转换完毕的Date数据
     */
    public Date getDateByCalendar(Calendar cal) {
        return cal.getTime();
    }

    /**
     * Date转换成Calendar
     *
     * @param date 传入的Date时间数据
     * @return 转换完毕的Calendar数据
     */
    public Calendar getCalendarByDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    /**
     * 使用之前设置的Date数据转换为 年-月-日 时-分-秒 格式的中国时间
     *
     * @return String类型时间
     */
    public String getStringDateByDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA);
        return df.format(instance.date);
    }

    /**
     * 使用之前设置的Date数据转换为指定的日期格式的中国时间
     *
     * @param dateFormat 日期格式，列： yyyy-MM-dd hh:mm:ss
     * @return String类型时间
     */
    public String getStringDateByDate(String dateFormat) {
        SimpleDateFormat df = new SimpleDateFormat(dateFormat, Locale.CHINA);
        return df.format(instance.date);
    }

    /**
     * 使用之前设置的Date数据转换为 年-月-日 时-分-秒 格式的指定区域时间
     *
     * @param locale 区域时间，列： Local.CHINA   Locale.US
     * @return String类型时间
     */
    public String getStringDateByDate(Locale locale) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", locale);
        return df.format(instance.date);
    }

    /**
     * 使用之前设置的Date数据转换为指定的日期格式的指定区域时间
     *
     * @param dateFormat 日期格式，列： yyyy-MM-dd hh:mm:ss
     * @param locale     区域时间，列： Local.CHINA   Locale.US
     * @return String类型时间
     */
    public String getStringDateByDate(String dateFormat, Locale locale) {
        SimpleDateFormat df = new SimpleDateFormat(dateFormat, locale);
        return df.format(instance.date);
    }

    /**
     * 使用传进来的Date数据转换为 年-月-日 时-分-秒 格式的中国时间
     *
     * @param date 从方法外传递进来的date数据
     * @return String类型时间
     */
    public static String getStringDateByDate(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA);
        return df.format(date);
    }

    /**
     * 使用传进来的Date数据转换为指定格式的中国时间
     *
     * @param date       从方法外传递进来的date数据
     * @param dateFormat 日期格式，列： yyyy-MM-dd hh:mm:ss
     * @return String类型时间
     */
    public static String getStringDateByDate(Date date, String dateFormat) {
        SimpleDateFormat df = new SimpleDateFormat(dateFormat, Locale.CHINA);
        return df.format(date);
    }

    /**
     * 使用传进来的Date数据转换为 年-月-日 时-分-秒 格式的指定区域时间
     *
     * @param locale 区域时间，列： Local.CHINA   Locale.US
     * @return String类型时间
     */
    public static String getStringDateByDate(Date date, Locale locale) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", locale);
        return df.format(date);
    }

    /**
     * 使用传进来的Date数据转换为指定格式的指定区域时间
     *
     * @param date       从方法外传递进来的date数据
     * @param dateFormat 日期格式，列： yyyy-MM-dd hh:mm:ss
     * @param locale     区域时间，列： Local.CHINA   Locale.US
     * @return String类型时间
     */
    public static String getStringDateByDate(Date date, String dateFormat, Locale locale) {
        SimpleDateFormat df = new SimpleDateFormat(dateFormat, locale);
        return df.format(date);
    }



    /***************************END.有需要再加*****************************************/
}
