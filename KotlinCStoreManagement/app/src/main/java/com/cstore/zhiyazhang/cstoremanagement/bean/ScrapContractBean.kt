package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

/**
 * Created by zhiya.zhang
 * on 2017/7/10 11:38.
 */
data class ScrapContractBean(
        @SerializedName("itemnumber")
        var scrapId: String, //品号
        @SerializedName("pluname")
        var scrapName: String, //品名
        @SerializedName("categorynumber")
        var categoryId: String, //类号
        @SerializedName("storeunitprice")
        var unitPrice: Double, //商品单价
        @SerializedName("unitcost")
        var unitCost: Double, //成本
        @SerializedName("sell_cost")
        var sellCost: Double, //卖价
        @SerializedName("citem_yn")
        var citemYN: String, //是否承包
        @SerializedName("recycle_yn")
        var recycleYN: String, //是否回收
        @SerializedName("barcode_yn")
        var barcodeYN: String, //是否有条形码
        @SerializedName("mrk_date")
        var mrkDate: String, //报废时间
        @SerializedName("sale_date")
        var saleDate: String, //销售时间
        @SerializedName("dlv_date")
        var dlvDate: String, //验收时间
        var nowMrkCount: Int, //现在的数量
        var createDay: Int, //数据库中的创建日期
        var isNew: Int, //是否是新添加的 2=老的
        var isScrap: Int//是否已报废 0=未报废
) : Serializable

data class ScrapSQLBean(var params: ArrayList<MRKBean>, var sql: String)

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
 @SerializedName("recordnumber")
 var recordNumber: Int,
 @SerializedName("itemnumber")
 var itemNumber: String,
 @SerializedName("storeunitprice")
 var storeUnitPrice: Double,
 @SerializedName("unitcost")
 var unitCost: Double,
 @SerializedName("mrkquantity")
 var mrkQuantity: Int,
 @SerializedName("updateuserid")
 var updateUserId: String,
 @SerializedName("citem_yn")
 var citemYN: String,
 @SerializedName("sell_cost")
 var sellCost: Double,
 @SerializedName("recycle_yn")
 var recycleYN: String,
 @SerializedName("busidate")
 var busiDate: String,
 @SerializedName("shipnumber")
 var shipNumber: String,
 @SerializedName("mrkreasonnumber")
 var mrkReasonNumber: String,
 @SerializedName("updatedatetime")
 var updateDateTime: String) : Serializable {


    override fun toString(): String {
        return "MRKBean{" +
                "storeId='" + storeId + '\'' +
                ", busiDate='" + busiDate + '\'' +
                ", recordNumber=" + recordNumber +
                ", itemNumber='" + itemNumber + '\'' +
                ", shipNumber='" + shipNumber + '\'' +
                ", storeUnitPrice=" + storeUnitPrice +
                ", unitCost=" + unitCost +
                ", mrkQuantity=" + mrkQuantity +
                ", mrkReasonNumber='" + mrkReasonNumber + '\'' +
                ", updateUserId='" + updateUserId + '\'' +
                ", updateDateTime='" + updateDateTime + '\'' +
                ", citemYN='" + citemYN + '\'' +
                ", sellCost=" + sellCost +
                ", recycleYN='" + recycleYN + '\'' +
                '}'
    }
}
