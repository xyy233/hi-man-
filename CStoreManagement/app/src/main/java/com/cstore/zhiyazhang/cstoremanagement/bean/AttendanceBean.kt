package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2018/3/6 10:54.
 */
data class AttendanceBean(
        @SerializedName("busidate") val busiDate: String,
        @SerializedName("storeid") val storeId: String,
        @SerializedName("workdate") val workDate: String,
        @SerializedName("emp_no") val uId: String,
        @SerializedName("employeename") val uName: String,
        @SerializedName("zb_hr") val zbHour: Double,
        @SerializedName("wb_hr") val wbHour: Double,
        /**
         * 工时
         */
        @SerializedName("dy_hr") val dayHour: Double,
        @SerializedName("cr_hr") val crHour: Double,
        @SerializedName("dr_hr") val drHour: Double,
        @SerializedName("f_hr") val fHour: Double,
        val status: Int,
        var pbData: UserAttendanceBean?
) : Serializable

data class UserAttendanceBean(
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
        @SerializedName("off_jpg") val offJpg: String
) : Serializable