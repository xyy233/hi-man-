package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2018/1/18 16:11.
 *
 * 废弃，Gson对Kotlin的父类不支持，未来还是每次重写
 */
open class PluItemBean : Serializable {
    @SerializedName("itemnumber")
    var pluId: String? = null
    @SerializedName("pluname")
    var pluName: String? = null
    @SerializedName("storeunitprice")
    var storeUnitPrice: Double? = null
    @SerializedName("shipnumber")
    var shipNumber: String? = null
    @SerializedName("unitcost")
    var unitCost: Double? = null
    @SerializedName("sell_cost")
    var sellCost: Double? = null
    @SerializedName("inv_qty")
    var invQty: Int? = null
    @SerializedName("vendorid")
    var vendorId: String? = null
    @SerializedName("supplierid")
    var supplierId: String? = null
    @SerializedName("signtype")
    var signType: String? = null
    @SerializedName("stocktype")
    var stockType: String? = null
    var editCount: Int = 0

/*    *//**
     * 想不到办法用泛型，只能继承一个复制一个过去了
     *//*
    private fun setData(data: Any, pluId: String, pluName: String, storeUnitPrice: Double, shipNumber: String, unitCost: Double, sellCost: Double, invQty: Int, vendorId: String, supplierId: String, signType: String, stockType: String): Any {
        data as PluItemBean
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
    */
}
