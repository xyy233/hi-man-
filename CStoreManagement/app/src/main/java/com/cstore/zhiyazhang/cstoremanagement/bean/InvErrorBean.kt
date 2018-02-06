package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2018/2/2 16:00.
 */
data class InvErrorBean(
        /**
         * 1=负库存
         * 2=L1缺货
         * 3=一般缺货
         * 4=滞销
         * 5=高库存
         * 6=状态6、8
         * 7=需移除
         */
        val flag: Int,
        @SerializedName("itemnumber") val pluId: String,
        @SerializedName("pluname") val pluName: String,
        @SerializedName("categorynumber") val categoryNumber: String,
        @SerializedName("categoryname") val categoryName: String,
        @SerializedName("ordermode") val orderMode: String,
        @SerializedName("class_type") val classType: String,
        @SerializedName("nosaleday") val noSaleDay: Int,
        @SerializedName("sfqty") val sfQty: Int,
        val status: Int,
        @SerializedName("market_date") var marketDate: String,
        @SerializedName("layclass") val layClass: String,
        @SerializedName("storeunitprice") val storeUnitPrice: Double,
        @SerializedName("send_type") val sendType: String,
        var dms: Double,
        @SerializedName("befinvquantity") val befInvQuantity: Int,
        val days: String,
        @SerializedName("returntype") val returnType: String,
        val dlv: Int,
        @SerializedName("unitname") val unitName: String,
        @SerializedName("sale_date") var saleDate: String,
        @SerializedName("dlv_date") var dlvDate: String,
        @SerializedName("unit_class") val unitClass: Int,
        @SerializedName("minimaorderquantity") val minImaOrderQuantity: Int,
        @SerializedName("th_code") val thCode: String,
        @SerializedName("th_code1") val thCode1: String,
        @SerializedName("invquantity") val inv: Int,
        @SerializedName("orderseq") val orderSeq: Int,
        @SerializedName("display_yn") val disPlayYN: String
) : Serializable