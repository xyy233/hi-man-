package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2018/1/29 17:30.
 *
 * 单品查询
 */
data class UnitInquiryBean(
        @SerializedName("itemnumber") val pluId: String,
        @SerializedName("pluname") val pluName: String,
        @SerializedName("storeunitprice") val storeUnitPrice: Double,
        @SerializedName("vendorid") val vendorId: String,
        @SerializedName("supplierid") val supplierId: String,
        @SerializedName("unitcost") val unitCost: Double,
        @SerializedName("shipnumber") val shipNumber: Int,
        @SerializedName("storeunitcost") val storeUnitCost: Double,
        @SerializedName("storesell_cost") val storeSellCost: Double,
        @SerializedName("ordertype") val orderType: String,
        @SerializedName("returntype") val returnType: String,
        @SerializedName("saletype") val saleType: String,
        @SerializedName("mrktype") val mrkType: String,
        @SerializedName("trstype") val trsType: String,
        @SerializedName("orderbegindate") val orderBeginDate: String,
        @SerializedName("orderenddate") val orderEndDate: String,
        @SerializedName("returnbegindate") val returnBeginDate: String,
        @SerializedName("returnenddate") val returnEndDate: String,
        @SerializedName("salebegindate") val saleBeginDate: String,
        @SerializedName("saleenddate") val saleEndDate: String,
        @SerializedName("vendorname") val vendorName: String,
        @SerializedName("suppliername") val supplierName: String,
        @SerializedName("status") val status: String,
        @SerializedName("out_th_code") val outThCode: String,
        @SerializedName("stop_th_code") val stopThCode: String,
        @SerializedName("in_th_code") val inThCode: String,
        @SerializedName("stockchange") val stockChange: String,
        @SerializedName("s_returntype") val sReturnType: String,
        @SerializedName("return_attr") val returnAttr: Int,
        @SerializedName("invquantity") var invQty: Int,
        val dms: String,
        val dma: String,
        @SerializedName("item_level") val itemLevel: String,
        @SerializedName("keep_days") val keepDays: Int,
        @SerializedName("min_qty") var minQty: Int,
        @SerializedName("go_market_days") val goMarketDays: Int
) : Serializable