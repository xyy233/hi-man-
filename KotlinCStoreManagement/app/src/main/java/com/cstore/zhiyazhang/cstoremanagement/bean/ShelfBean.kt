package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2017/8/2 17:15.
 */
data class ShelfBean(
        @SerializedName("gondranumber") val shelfId: String,
        @SerializedName("gondraname") val shelfName: String
) : Serializable

data class SelfBean(
        @SerializedName("midcategorynumber") val selfId: String,
        @SerializedName("categoryname") val selfName: String
) : Serializable

data class NOPBean(
        @SerializedName("in_th_code") val nopId: String,
        @SerializedName("title") val nopName: String
) : Serializable