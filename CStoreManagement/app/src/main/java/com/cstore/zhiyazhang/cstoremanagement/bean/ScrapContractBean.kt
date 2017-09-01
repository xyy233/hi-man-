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

data class ScrapHotBean(
        @SerializedName("midcategorynumber")
        val sId:String,
        @SerializedName("categoryname")
        val sName:String
):Serializable