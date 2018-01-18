package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2018/1/18 16:26.
 */
data class TrsItemBean(
        @SerializedName("trsid") val trsId: Int,
        @SerializedName("trsstoreid") val trsStoreId: String,
        @SerializedName("trsnumber") val trsNumber: String,
        @SerializedName("trsquantity") var trsQty: Int,
        @SerializedName("trsitem") var trsItem: Int,
        @SerializedName("tax_sell_tot") val taxSellTot: Double,
        @SerializedName("vendorid") val vendorId: String,
        @SerializedName("supplierid") val supplierId: String,
        var action: Int,
        var changeCount: Int
) : PluItemBean()

data class TrsfItemBean(
        @SerializedName("trsnumber") val trsNumber: String,
        val cnt: Int
) : Serializable