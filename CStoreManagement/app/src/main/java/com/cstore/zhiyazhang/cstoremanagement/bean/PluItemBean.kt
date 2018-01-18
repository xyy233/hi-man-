package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2018/1/18 16:11.
 */
open class PluItemBean : Serializable {
    @SerializedName("itemnumber")
    val pluId: String? = null
    @SerializedName("pluname")
    val pluName: String? = null
    @SerializedName("storeunitprice")
    val storeUnitPrice: Double? = null
    @SerializedName("shipnumber")
    val shipNumber: Int? = null
    @SerializedName("unitcost")
    val unitCost: Double? = null
    @SerializedName("sell_cost")
    val sellCost: Double? = null
    @SerializedName("inv_qty")
    val invQty: Int? = null
}
