package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2017/7/10 11:38.
 */

/**
 * 得到报废的时候用
 */
data class ScrapContractBean(
        @SerializedName("itemnumber")
        val scrapId: String, //品号
        @SerializedName("busidate")
        var busiDate:String?,//报废日期
        @SerializedName("pluname")
        val scrapName: String, //品名
        @SerializedName("storeunitprice")
        val unitPrice: Double, //商品单价
        @SerializedName("unitcost")
        val unitCost: Double, //成本
        @SerializedName("sell_cost")
        val sellCost: Double, //卖价
        @SerializedName("citem_yn")
        val citemYN: String, //是否承包
        @SerializedName("recycle_yn")
        val recycleYN: String, //是否回收
        @SerializedName("barcode_yn")
        val barcodeYN: String, //是否有条形码
        @SerializedName("mrkquantity")
        var mrkCount: Int, //现在的数量
        @SerializedName("recordnumber")
        var recordNumber:Int=0,
        /**
         * 0==insert 1==update 2==delete 3==none
         */
        var action:Int=0,
        var editCount:Int=0//本次操作修改的量
) : Serializable

/**
 * 提交报废的时候用
 */
data class MRKBean
/**

 * @param storeId 商店id
 * *
 * @param recordNumber 今天的第几个
 * *
 * @param itemNumber 品号
 * *
 * @param storeUnitPrice 单价
 * *
 * @param unitCost 成本
 * *
 * @param mrkQuantity 报废数
 * *
 * @param updateUserId 更新操作人
 * *
 * @param citemYN 是否是承包
 * *
 * @param sellCost 卖的成本
 * *
 * @param recycleYN 是否回收
 */
(@SerializedName("storeid")
 var storeId: String,
 @SerializedName("busidate")
 var busiDate: String,
 @SerializedName("recordnumber")
 var recordNumber: Int,
 @SerializedName("itemnumber")
 var itemNumber: String,
 @SerializedName("shipnumber")
 var shipNumber: String,
 @SerializedName("storeunitprice")
 var storeUnitPrice: Double,
 @SerializedName("unitcost")
 var unitCost: Double,
 @SerializedName("mrkquantity")
 var mrkQuantity: Int,
 @SerializedName("mrkreasonnumber")
 var mrkReasonNumber: String,
 @SerializedName("updateuserid")
 var updateUserId: String,
 @SerializedName("updatedatetime")
 var updateDateTime: String,
 @SerializedName("citem_yn")
 var citemYN: String,
 @SerializedName("sell_cost")
 var sellCost: Double,
 @SerializedName("recycle_yn")
 var recycleYN: String) : Serializable
