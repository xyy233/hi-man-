package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2018/1/2 15:37.
 */
data class ReturnExpiredBean(
        /**
         * 添加时间
         */
        @SerializedName("plnrtndate") val plnRtnDate: String,
        /**
         * 品号
         */
        @SerializedName("itemnumber") val itemNumber: String,
        /**
         * 品名
         */
        @SerializedName("pluname") val pluName: String,
        /**
         * 配送id
         */
        @SerializedName("vendorid") val vendorId: String,
        /**
         * 配送名
         */
        @SerializedName("vendorname") val vendorName: String,
        /**
         * 鬼知道什么id
         */
        @SerializedName("supplierid") val supplierId: String,
        /**
         * 退货类型
         * 1清场品  2新/专案   3返品   4啤酒   5总部紧急召回   未设定
         */
        @SerializedName("return_attr") val returnAttr: String,
        /**
         * 退货数量
         */
        @SerializedName("plnrtnquantity") var plnRtnQTY: Int,
        /**
         * 零售价
         */
        @SerializedName("storeunitprice") val storeUnitPrice: Double,
        /**
         * 鬼知道什么价格
         */
        @SerializedName("basic_cost") val basicCost: Double,
        /**
         * 成本价
         */
        @SerializedName("sell_cost") val sellCost: Double,
        /**
         * 退货原因
         */
        @SerializedName("reasonnumber") val reasonNumber: String,
        /**
         * 含税成本
         */
        @SerializedName("tax_sell_cost") val taxSellCost: Double,
        /**
         * 库存
         */
        @SerializedName("stock_qty") val stockQTY: Int,
        /**
         * 订量
         */
        @SerializedName("dlv_qty") val dlvQTY: Int?,
        /**
         *销量
         */
        @SerializedName("sale_qty") val saleQTY: Int?,
        /**
         *退量
         */
        @SerializedName("rtn_qty") val rtnQTY: Int?,
        /**
         * 鬼知道什么量
         */
        @SerializedName("hq_ordqty") val hqOrdQTY: Int?,
        /**
         * 数据状态
         */
        @SerializedName("data_status") val dataStatus: String,
        /**
         * 退的时间，是今天
         */
        @SerializedName("rtndate") val rtnDate: String,
        /**
         * 鬼知道干吗用的
         */
        @SerializedName("check_yn") val checkYN: String,
        /**
         * 用来判断是否可退
         */
        @SerializedName("vendor_yn") val vendorYN: String?,
        /**
         * 用来判断是否是停售品，是的话不可退
         */
        @SerializedName("stop_th_code") val stopThCode: String?,
        /**
         * 用来判断退货档期，有的话不可退
         */
        @SerializedName("out_th_code") val outThCode: String?,
        /**
         * 用来判断是否修改
         */
        var editCount: Int,
        /****************************以下为仅查看的数据**********************************/
        @SerializedName("price_amt") val priceAmt: Double?,
        @SerializedName("plnrtnstatus2") val rtnStatus: String?
) : Serializable

