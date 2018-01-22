package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2018/1/18 16:26.
 */
data class TrsBean(
        @SerializedName("trsid") val trsId: Int,
        @SerializedName("busidate") val busiDate: String,
        @SerializedName("trsstoreid") val trsStoreId: String,
        @SerializedName("trsnumber") var trsNumber: String,
        @SerializedName("trsquantity") var trsQty: Int,
        @SerializedName("trsitem") var trsItem: Int,
        @SerializedName("storeunitprice") var storeUnitPrice: Double,
        var items: ArrayList<TrsItemBean>
) : Serializable


data class TrsItemBean(
        @SerializedName("trsid") val trsId: Int,
        @SerializedName("trsstoreid") val trsStoreId: String,
        @SerializedName("trsnumber") var trsNumber: String,
        @SerializedName("trsquantity") var trsQty: Int,
        @SerializedName("trsitem") var trsItem: Int,
        @SerializedName("tax_sell_tot") val taxSellTot: Double,
        @SerializedName("vendorid") val vendorId: String,
        @SerializedName("supplierid") val supplierId: String,
        val total: Double,
        /**
         * 0=已存在 1=新的
         */
        var action: Int
) : PluItemBean()

data class TrsfItemBean(
        @SerializedName("trsnumber") val trsNumber: String,
        val cnt: Int
) : Serializable