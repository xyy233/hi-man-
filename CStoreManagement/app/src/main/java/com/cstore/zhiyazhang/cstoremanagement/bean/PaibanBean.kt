package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2018/2/8 11:37.
 */
data class PaibanBean(
        @SerializedName("storeid") val storeId: String?,
        /**
         * 考勤日
         */
        @SerializedName("systemdate") val systemDate: String?,
        /**
         * 考勤人号码
         */
        @SerializedName("employeeid") val employeeId: String,
        @SerializedName("employeename") val employeeName: String,
        @SerializedName("begindatetime") val beginDateTime: String?,
        @SerializedName("enddatetime") val endDateTime: String?,
        /**
         * 大夜时数
         */
        @SerializedName("danren_hr") val danrenHr: Double?,
        @SerializedName("updatedatetime") val updateDateTime: String?
) : Serializable

data class SortPaiban(
        val data: ArrayList<PaibanBean>
) : Serializable