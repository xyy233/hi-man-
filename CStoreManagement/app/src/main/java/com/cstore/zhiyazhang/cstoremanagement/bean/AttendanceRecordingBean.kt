package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2018/3/12 17:04.
 */
data class AttendanceRecordingBean(
        @SerializedName("employeeid") val uId: String,
        @SerializedName("employeename") val uName: String,
        /**
         * 早晚班工时
         */
        @SerializedName("tot_day") val totDay: String,
        /**
         * 一般大夜
         */
        @SerializedName("tot_night") val totNight: String,
        /**
         * 单人大夜
         */
        @SerializedName("tot_danren") val totDanren: String,
        /**
         * 时数小计
         */
        @SerializedName("tot_hr") val totHr: String,
        /**
         * 事假时数
         */
        @SerializedName("voc_ahr") val vocAHr: String,
        /**
         * 病假时数
         */
        @SerializedName("voc_bhr") val vocBHr: String,
        /**
         * 代休时数
         */
        @SerializedName("voc_xhr") val voccXHr: String,
        /**
         * 婚假时数
         */
        @SerializedName("voc_jhr") val vocJHr: String,
        /**
         * 其他请假时数
         */
        @SerializedName("voc_otherhr") val vocOtherHr: String,
        /**
         * 请假小计
         */
        @SerializedName("voc_tot") val vocTot: String,
        /**
         * 迟到次数
         */
        @SerializedName("voc_ctimes") val vocCTimes: String,
        /**
         * 迟到时数
         */
        @SerializedName("voc_chr") val vocCHr: String,
        /**
         * 平日加班时数
         */
        @SerializedName("tot_common_added") val totCommonAdded: String,
        /**
         * 法定假日加班时数
         */
        @SerializedName("tot_feria") val totFeria: String,
        /**
         * 加班小计
         */
        @SerializedName("tot_added") val totAdded: String
) : Serializable