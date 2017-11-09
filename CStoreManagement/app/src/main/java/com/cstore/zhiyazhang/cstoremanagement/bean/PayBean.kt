package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2017/11/8 13:39.
 */
data class PayBean(
        /**
         * 购买者id
         */
        @SerializedName("openid") val openId: String,
        /**
         * 店号
         */
        @SerializedName("storeid") val storeId: String,
        /**
         * 条码
         */
        @SerializedName("bar_code") val barCode: String,
        /**
         * 品号
         */
        @SerializedName("itemno") val itemId: String,
        /**
         * 品名
         */
        @SerializedName("itemname") val itemName: String,
        /**
         * 数量
         */
        var quantity: Int,
        /**
         * 单价
         */
        @SerializedName("store_price") val storePrice: Double,
        /**
         * 促销数量
         */
        @SerializedName("mm_quantity") val mmQuantity: Int,
        /**
         * 优惠金额
         */
        @SerializedName("disc_amt") val discAmt: Double,
        /**
         * 项次
         */
        @SerializedName("line_seq") val lineSeq: Int
):Serializable