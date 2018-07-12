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
        @SerializedName("rtnquantity") var rtnQuantity: Int,
        /**
         * 零售小计
         */
        var total: Float,
        /**
         * 配送商id
         */
        @SerializedName("vendorid") val vendorId: String,
        /**
         * 品项数
         */
        @SerializedName("itempln") var itemCount: Int,
        var allItem: ArrayList<ReturnPurchaseItemBean>
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
        @SerializedName("requestnumber") var requestNumber: String?,
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
        @SerializedName("plnrtnunitquantity") val plnRtnUnitQuantity: Int,
        /**
         * 退货数，正宫
         */
        @SerializedName("plnrtnquantity") var plnRtnQuantity: Int,
        /**
         * 预约日期
         */
        @SerializedName("plnrtndate0") var plnRtnDate0: String?,
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
         * 退货原因Id
         */
        @SerializedName("reasonnumber") var reasonNumber: String,
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
        @SerializedName("sell_cost") val sellCost: Double,
        /**
         * 历史净进货累计
         */
        val lsjjh: Int,
        /**
         * 退货原因Name,根据id获得
         */
        var reasonName: String,
        /**
         * 修改次数，用来确认是否有修改，增减量正常+-，修改原因直接+
         */
        var editCount: Int
) : Serializable

/**
 * 退货原因
 */
data class ReasonBean(
        @SerializedName("reasonnumber") val reasonId: String,
        @SerializedName("reasonname") val reasonName: String
) : Serializable {
    companion object {
        private val staticReason = ArrayList<ReasonBean>()

        fun getAllReason(): ArrayList<ReasonBean> {
            return staticReason
        }

        fun getReason(reasonId: String): ReasonBean {
            val reason = staticReason.filter { it.reasonId == reasonId }
            if (reason.isEmpty()) {
                return ReasonBean("", "")
            }
            return reason[0]
        }

        fun setReason(data: ArrayList<ReasonBean>) {
            staticReason.clear()
            staticReason.addAll(data)
        }
    }
}