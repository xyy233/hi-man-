package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2017/7/3 10:30.
 */
data class UpdateBean(
        val version: String,
        @SerializedName("ver_number")val versionNumber:Int,
        val downloadUrl: String,
        @SerializedName("alpha_version") val alphaVersion:String,
        @SerializedName("alpha_ver_number") val alphaVerNumber:Int,
        @SerializedName("alpha_downloadUrl") val alphaDownloadUrl:String
        ) : Serializable
