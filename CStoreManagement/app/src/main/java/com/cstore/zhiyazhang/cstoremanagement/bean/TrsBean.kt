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
        @SerializedName("trsstoreid") var trsStoreId: String,
        @SerializedName("trsnumber") var trsNumber: String,
        @SerializedName("trsquantity") var trsQty: Int,
        @SerializedName("tax_sell_tot") val taxSellTot: Double,
        var total: Double,
        /**
         * 0=已存在 1=新的
         */
        var action: Int
) : PluItemBean() {
    fun setData(data: TrsItemBean, pluId: String, pluName: String, storeUnitPrice: Double, shipNumber: String, unitCost: Double, sellCost: Double, invQty: Int, vendorId: String, supplierId: String, signType: String, stockType: String): TrsItemBean {
        data.pluId = pluId
        data.pluName = pluName
        data.storeUnitPrice = storeUnitPrice
        data.shipNumber = shipNumber
        data.unitCost = unitCost
        data.sellCost = sellCost
        data.invQty = invQty
        data.vendorId = vendorId
        data.supplierId = supplierId
        data.signType = signType
        data.stockType = stockType
        return data
    }
}

data class TrsfItemBean(
        @SerializedName("trsnumber") val trsNumber: String,
        val cnt: Int
) : Serializable