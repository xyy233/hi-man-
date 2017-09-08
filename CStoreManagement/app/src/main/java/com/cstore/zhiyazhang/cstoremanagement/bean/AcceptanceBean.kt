package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2017/9/8 17:04.
 */
data class AcceptanceBean(
        @SerializedName("requestnumber")
        val acceptanceId: String,
        @SerializedName("vendorname")
        val vendorName: String,
        @SerializedName("orditemqty")
        val ordItemQTY: Int, //品项
        @SerializedName("ordquantity")
        val ordQuantity: Int, //订货总数
        @SerializedName("retailtotal")
        val retailTotal: Double, //零售小计
        @SerializedName("dlvquantity")
        val dlvQuantity: Int, //验收总数
        @SerializedName("sellcost_tot")
        val sellCostTot: Double, //含税成本
        @SerializedName("statuscaption")
        val statusCaption: Int, //0:未验收 1:未修正 2:有修正 3:转次日
        @SerializedName("vendorid")
        val vendorId: String
) : Serializable

data class AcceptanceItemBean(
        @SerializedName("itemnumber")
        val itemId: String,
        @SerializedName("pluname")
        val itemName: String,
        @SerializedName("hqquantity")
        val hqQuantity: Int, //强配量
        @SerializedName("dctrsquantity")
        val dctrsQuantity: Int, //DC出货量
        @SerializedName("varquantity")
        val varQuantity: Int, //到货量
        @SerializedName("dlvquantity")
        val dlvQuantity: Int, //验收量
        @SerializedName("tax_sell_cost")
        val taxSellCost: Double//含税单价
) : Serializable