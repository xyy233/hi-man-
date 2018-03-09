package com.cstore.zhiyazhang.cstoremanagement.bean

import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2018/3/6 10:54.
 */
data class AttendanceBean(
        @SerializedName("busidate") var busiDate: String,
        @SerializedName("storeid") val storeId: String,
        @SerializedName("workdate") var workDate: String,
        @SerializedName("emp_no") val uId: String,
        @SerializedName("employeename") val uName: String,
        /**
         * 早班时间
         */
        @SerializedName("zb_hr") val zbHour: Double,
        /**
         * 晚班时间
         */
        @SerializedName("wb_hr") val wbHour: Double,
        /**
         * 一般大夜时间
         */
        @SerializedName("dy_hr") val dyHour: Double,
        /**
         * 常日班时间
         */
        @SerializedName("cr_hr") val crHour: Double,
        /**
         * 单人大夜时间
         */
        @SerializedName("dr_hr") val drHour: Double,
        /**
         * 含国定假日工时
         */
        @SerializedName("f_hr") val fHour: Double,
        var status: Int,
        var bbType: UserBBTypeBean?,
        /**
         * 是否是单人大夜
         */
        var drdy: Boolean?
) : Serializable

/**
 * 排班数据
 */
data class PaibanAttendanceBean(
        @SerializedName("pb_on") val pbStart: String,
        @SerializedName("pb_off") val pbEnd: String,
        @SerializedName("work_on") val workStart: String,
        @SerializedName("work_off") val workEnd: String,
        val rid: String,
        /**
         * 打卡上班照片
         */
        @SerializedName("on_jpg") val onJpg: String,
        /**
         * 打卡下班照片
         */
        @SerializedName("off_jpg") val offJpg: String,
        var onBitmap: Bitmap?,
        var offBitmap: Bitmap?
) : Serializable

/**
 * 排班类型
 */
data class UserBBTypeBean(
        @SerializedName("bbtype") val bbType: String,
        @SerializedName("banbiename") val bbName: String,
        /**
         * 排班时数
         */
        @SerializedName("paiban_hr") val paibanHour: Double,
        /**
         * 上班时数
         */
        @SerializedName("day_hr") val dayHour: Double,
        /**
         * 含国定假日时数
         */
        @SerializedName("feria_hr") val fHour: Double
) : Serializable