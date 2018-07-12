package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2017/9/26 8:59.
 */
data class GondraBean(
        @SerializedName("gondranumber") val gondraId:String,
        @SerializedName("gondraname") val gondraName:String
):Serializable