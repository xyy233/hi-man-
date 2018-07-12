package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2017/7/25 14:03.
 */
data class OrderCategoryBean(
        /**
         * 类号
         */
        @SerializedName("categorynumber") val categoryId: String,
        /**
         * 类名
         */
        @SerializedName("categoryname") val categoryName: String,
        /**
         * 可订商品数
         */
        @SerializedName("tot_sku") val allSku: Int,
        /**
         * 已订商品总项数
         */
        @SerializedName("ord_sku") var ordSku: Int,
        /**
         * 已订商品总数
         */
        @SerializedName("ord_count") var ordCount:Int,
        /**
         * 已订总金额
         */
        @SerializedName("amt") var ordPrice: Double,
        /**
         * 在adapter中确认是否修改颜色
         */
        var isChangeColor: Boolean = false,
        /**
         * 用于数据库判断是否删除
         */
        var createDay: Int = 0
) : Serializable

