package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2017/11/3 17:05.
 */

/**
 * 退货原因
 */
data class ReasonBean(
        @SerializedName("reasonnumber") val reasonId:String,
        @SerializedName("reasonname") val reasonName:String
):Serializable