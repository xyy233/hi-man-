package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2017/9/26 10:49.
 */
data class AdjustmentBean(
        @SerializedName("itemnumber") val itemId:String,
        @SerializedName("pluname") val itemName:String,
        @SerializedName("currstockquantity") var currStockQTY:Int,//账上库存
        @SerializedName("actstockquantity") var actStockQTY:Int,//实际库存
        @SerializedName("adjquantity") var adjQTY:Int,//修改量=账上-实际
        @SerializedName("shipnumber") val shipNumber:Int,//鬼知道
        @SerializedName("adjreasonnumber") val adjReasonNumber:Int,//调整原因,默认11，11为门店调整
        @SerializedName("storeunitprice") val storeUnitPrice:Double,//零售价
        @SerializedName("unitcost") val unitCost:Double,//成本
        var isChange:Boolean//确认是否修改
):Serializable