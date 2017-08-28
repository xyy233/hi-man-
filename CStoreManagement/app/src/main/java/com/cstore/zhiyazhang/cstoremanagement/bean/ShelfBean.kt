package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2017/8/2 17:15.
 */
data class ShelfBean(
        @SerializedName("gondranumber") val shelfId: String,
        @SerializedName("gondraname") val shelfName: String,
        @SerializedName("tot_sku") val allSku: Int,
        @SerializedName("ord_sku") val ordSku: Int,
        @SerializedName("amt") val ordPrice: Double
) : Serializable

data class SelfBean(
        @SerializedName("midcategorynumber") val selfId: String,
        @SerializedName("categoryname") val selfName: String,
        @SerializedName("tot_sku") val allSku: Int,
        @SerializedName("ord_sku") val ordSku: Int,
        @SerializedName("amt") val ordPrice: Double
) : Serializable

data class NOPBean(
        @SerializedName("in_th_code") val nopId: String,
        @SerializedName("title") val nopName: String,
        @SerializedName("tot_sku") val allSku: Int,
        @SerializedName("ord_sku") val ordSku: Int,
        @SerializedName("amt") val ordPrice: Double
) : Serializable

data class FreshGroup(
        @SerializedName("categorynumber") val categoryId: String,
        @SerializedName("midcategorynumber") val midId: String,
        @SerializedName("name") val name: String,
        @SerializedName("tot_sku") val allSku: Int,
        @SerializedName("ord_sku") val ordSku: Int,
        @SerializedName("amt") val ordPrice: Double
) : Serializable