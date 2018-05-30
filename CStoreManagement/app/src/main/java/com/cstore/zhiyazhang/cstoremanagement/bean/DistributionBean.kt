package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2018/5/22 12:26.
 */
data class DistributionBean(
        @SerializedName("shelves_id") val shelvesId: String,
        @SerializedName("shelves_name") val shelvesName: String,
        @SerializedName("dis_time") val disTime: String,
        @SerializedName("trs_in_qty") val trsInQty: String,
        @SerializedName("trs_in_amount") val trsInAmount: String
) : Serializable

//接收配送点的
data class DistributionResult(
        val code: Int,
        var rows: ArrayList<DistributionBean>?
) : Serializable

data class DistributionItemBean(
        @SerializedName("itemno") val itemNo: String,
        @SerializedName("trs_qty") val trsQty: Int,
        @SerializedName("scan_qty") var scanQty: Int?,
        @SerializedName("item_name") val itemName: String,
        @SerializedName("big_name") val bigName: String,
        val category: String,
        @SerializedName("date_type") val dateType: String,
        var isTitle: Boolean?,
        var barContext: ArrayList<String>?,
        var newScan: Boolean?
) : Serializable

//接收详细参数的
data class DistributionItemResult(
        val code: Int,
        val rows: ArrayList<DistributionItemBean>
) : Serializable

data class DistributionRequestBean(
        @SerializedName("zx_storeid") val zxStoreId: String,
        @SerializedName("wx_storeid") val wxStoreId: String,
        @SerializedName("dis_time") val disTime: String,
        @SerializedName("itemno") val itemNo: String,
        @SerializedName("trs_qty") val trsQty: Int,
        @SerializedName("scan_qty") val scanQty: Int?,
        @SerializedName("bar_context") val barContext: ArrayList<String>?
) : Serializable

//发往服务器修改用的
data class DistributionRequestResult(
        val code: Int,
        val rows: ArrayList<DistributionRequestBean>
) : Serializable