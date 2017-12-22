package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2017/10/30 15:45.
 * 退货
 */
class ReturnedPurchaseBean(
        /**
         * 单号
         */
        @SerializedName("requestnumber") val requestNumber: String,
        /**
         * 退货验收日
         */
        @SerializedName("rtndate") val rtnDate: String,
        /**
         * 配送商名
         */
        @SerializedName("vendorname") val vendorName: String,
        /**
         * 预退数
         */
        @SerializedName("rtnquantity") val rtnQuantity: Int,
        /**
         * 零售小计
         */
        val total: Float,
        /**
         * 配送商id
         */
        @SerializedName("vendorid") val vendorId: String,
        /**
         * 品项数
         */
        @SerializedName("itempln") val itemCount: Int,
        var allItem:ArrayList<ReturnPurchaseItemBean>
) : Serializable

/**
 * 退货单下商品
 */
class ReturnPurchaseItemBean(
        /**
         * 排序用ID
         */
        @SerializedName("recordnumber") var recordNumber: Int,
        /**
         * 单号
         */
        @SerializedName("requestnumber") var requestNumber: String,
        /**
         * 品号
         */
        @SerializedName("itemnumber") val itemNumber: String,
        /**
         * 零售小计
         */
        val total: Float,
        /**
         * 零售价
         */
        @SerializedName("storeunitprice") val storeUnitPrice: Double,
        /**
         * 成本
         */
        @SerializedName("unitcost") val unitCost: Double,
        /**
         * 退货数，备胎
         */
        @SerializedName("plnrtnunitquantity") val plnRtnUnitQuantity: Double,
        /**
         * 退货数，正宫
         */
        @SerializedName("plnrtnquantity") val plnRtnQuantity: Double,
        /**
         * 预约日期
         */
        @SerializedName("plnrtndate0") val plnRtnDate0: String,
        /**
         * 验收日期
         */
        @SerializedName("plnrtndate") val plnRtnDate: String,
        /**
         * 配送商id
         */
        @SerializedName("vendorid") val vendorId: String,
        /**
         * 什么ID来着
         */
        @SerializedName("supplierid") val supplierId: String,
        /**
         * 品名
         */
        @SerializedName("pluname") val pluName: String,
        /**
         * 库存
         */
        @SerializedName("invquantity") val invQuantity: Int,
        /**
         * 退货原因
         */
        @SerializedName("reasonnumber") val reasonNumber: String,
        /**
         * 什么号来着
         */
        @SerializedName("shipnumber") val shipNumber: String,
        /**
         * 含税单价
         */
        @SerializedName("tax_sell_cost") val taxSellCost: Double,
        /**
         * 卖价
         */
        @SerializedName("sell_cost") val sellCost: Double
) : Serializable

/**
 * 退货原因
 */
data class ReasonBean(
        @SerializedName("reasonnumber") val reasonId:String,
        @SerializedName("reasonname") val reasonName:String
):Serializable