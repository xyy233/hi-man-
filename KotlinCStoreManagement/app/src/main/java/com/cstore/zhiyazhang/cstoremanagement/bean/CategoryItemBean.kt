package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2017/7/26 15:45.
 */
data class CategoryItemBean(
        /**
         * 品号
         */
        @SerializedName("itemnumber") val itemId: String,
        /**
         * 品名
         */
        @SerializedName("pluname") val itemName: String,
        /**
         * 零售价
         */
        @SerializedName("storeunitprice") val itemPrice: Double,
        /**
         * 预计强配
         */
        @SerializedName("quantity") val itemExpected: Int,
        /**
         * 库存
         */
        @SerializedName("invquantity") val itemInv: Int,
        /**
         * 我的加量
         */
        @SerializedName("ordactualquantity") var orderQTY: Int,
        /**
         * 在途
         */
        @SerializedName("dlv_qty") val dlvQTY: Int,
        /**
         * 明日预测
         */
        @SerializedName("d1_dfs") val itemTomorrow: Int,
        /**
         * 每次增加量
         */
        @SerializedName("increaseorderquantity") val stepQty: Int,
        /**
         * 最小订量
         */
        @SerializedName("minimaorderquantity") val minQty: Int,
        /**
         * 最大订量
         */
        @SerializedName("maximaorderquantity") val maxQty: Int,
        /**
         * 修改次数
         */
        var changeCount: Int = 0
) : Serializable