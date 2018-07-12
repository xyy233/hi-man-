package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2018/5/20 10:26.
 */
/**
 * 冰箱本体
 */
data class ShelvesBean(
        @SerializedName("shelvesid") val shelvesId: String,
        @SerializedName("shelves_name") val shelvesName: String,
        @SerializedName("shelves_address") val shelvesAddress: String
) : Serializable

/**
 * 冰箱内商品本体
 */
data class ShelvesItemBean(
        @SerializedName("big_id") val bigId: String,
        @SerializedName("big_name") val big_name: String,
        @SerializedName("itemno") val itemNo: String,
        @SerializedName("item_name") val itemName: String,
        /**
         * 库存
         */
        @SerializedName("end_qty") val endQty: Int,
        /**
         * 销售数量
         */
        @SerializedName("sal_qty") val selQty: Int,
        /**
         * 用来判断是否是标题栏
         */
        var isTitle: Boolean?
) : Serializable

data class ShelvesResult(
        val code: Int,
        @SerializedName("dis_time") val disTime: String,
        val rows: ArrayList<ShelvesBean>
) : Serializable

data class ShelvesItemResult(
        val code: Int,
        @SerializedName("dis_time") val disTime: String,
        val rows: ArrayList<ShelvesItemBean>
) : Serializable