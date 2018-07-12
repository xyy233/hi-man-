package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2018/1/19 14:52.
 */
data class OStoreBean(
        @SerializedName("sotreid") val storeId: String,
        @SerializedName("ostoreid") val oStoreId: String,
        @SerializedName("ostorename") val oStoreName: String,
        @SerializedName("ostore_attr") val oStoreAttr: String
) : Serializable