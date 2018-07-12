package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2017/9/8 17:04.
 */
data class AcceptanceBean(
        @SerializedName("requestnumber") val distributionId: String, //配送号
        @SerializedName("vendorid") val vendorId: String, //配送商id
        @SerializedName("vendorname") val vendorName: String, //配送商名字
        @SerializedName("orditemqty") var ordItemQTY: Int, //品项
        @SerializedName("dlvitemqty") var dlvItemQTY: Int, //验收项
        @SerializedName("ordquantity") var ordQuantity: Int, //订货总数
        @SerializedName("retailtotal") var retailTotal: Double, //零售小计
        @SerializedName("dlvquantity") var dlvQuantity: Int, //验收总数
        @SerializedName("sellcost_tot") var sellCostTot: Double, //含税成本
        @SerializedName("end_dlvtimes") val endDlvTimes: String, //验收截止时间
        @SerializedName("actdlvtime") val actDlvTime: String, //验收时间
        @SerializedName("dlvstatus") var dlvStatus: Int, //0:未验收 1:未修正 2:有修正 3:转次日
        @SerializedName("costtotal") var costTotal: Double, //成本总计
        var allItems: ArrayList<AcceptanceItemBean>,//所有商品，后期插入
        var isChange:Boolean=false//用来判断是否有修改过的
) : Serializable

data class ReturnAcceptanceBean(
        @SerializedName("requestnumber") val distributionId: String,//单号
        @SerializedName("rtndate") val rtnDate:String,//退货日
        @SerializedName("plnrtndate") val plnRtnDate:String,//原退货日
        @SerializedName("prertndate") val preRtnDate:String,//不知道
        @SerializedName("plnrtnitemqty") var plnRtnItemQTY:Int,//退货品项数
        @SerializedName("actrtnitemqty") var actRtnItemQTY:Int,//实退品项数
        @SerializedName("retailtotal") var retailTotal:Double,//零售小计
        @SerializedName("rtnstatus") var rtnStatus:Int,//验收状态
        @SerializedName("actrtntime") val actRtnTime:String,//验收时间
        @SerializedName("vendorid") val vendorId:String,//配送商id
        @SerializedName("vendorname") val vendorName:String,//配送商名
        @SerializedName("sellcost_tot") var sellCostTot:Double,//含税总成本
        @SerializedName("rtnquantity") var rtnQuantity:Int,//实退总数
        @SerializedName("ordquantity") var ordQuantity: Int,//预退总数
        var costTotal: Double,//成本总计
        var allItems: ArrayList<ReturnAcceptanceItemBean>,
        var isChange: Boolean=false
):Serializable

data class AcceptanceItemBean(
        @SerializedName("itemnumber")
        val itemId: String, //品号
        @SerializedName("pluname")
        val itemName: String, //品名
        @SerializedName("hqquantity")
        val hqQuantity: Int, //强配量
        @SerializedName("dctrsquantity")
        val dctrsQuantity: Int, //大仓出货量
        @SerializedName("trsquantity")
        var trsQuantity: Int, //到货量
        @SerializedName("varquantity")
        var varQuantity: Int, //差异量
        @SerializedName("dlvquantity")
        var dlvQuantity: Int, //验收量
        @SerializedName("tax_sell_cost")
        val taxSellCost: Double, //含税单价
        @SerializedName("storeunitprice")
        val storeUnitPrice: Double, //零售价
        @SerializedName("storeunitprice_now")
        val storeUnitPriceNow: Double, //当前零售价
        @SerializedName("retailtotal")
        var retailTotal: Double, //零售小计
        @SerializedName("requestnumber")
        var distributionId: String, //配送号
        @SerializedName("vendorid")
        val vendorId: String, //配送商id
        @SerializedName("unitcost")
        val unitCost: Double, //成本
        @SerializedName("ordqutity")
        val ordQuantity: Int, //订量
        @SerializedName("totalunitcost")
        var totalUnitCost: Double, //总成本
        @SerializedName("shipnumber")
        val shipNumber: Int, //鬼知道是什么
        @SerializedName("supplierid")
        val supplierId: String, //鬼知道是什么
        @SerializedName("sell_cost")
        val sellCost: Double,//成本
        var isChange: Boolean=false
) : Serializable

data class ReturnAcceptanceItemBean(
        @SerializedName("itemnumber")
        val itemId: String, //品号
        @SerializedName("pluname")
        val itemName: String, //品名
        @SerializedName("rtndate")
        val rtnDate: String,
        @SerializedName("ordquantity")
        var ordQuantity: Int, //预退数
        @SerializedName("requestnumber")
        var distributionId: String, //单号
        @SerializedName("supplierid")
        val supplierId: String, //不知道
        @SerializedName("rtnquantity")
        var rtnQuantity: Int, //实退数
        @SerializedName("storeunitprice")
        var storeUnitPrice: Double, //零售价
        @SerializedName("shipnumber")
        val shipNumber: Int, //不知道
        @SerializedName("vendorid")
        val vendorId: String, //配送商id
        @SerializedName("unitcost")
        val unitCost: Double, //单价
        @SerializedName("sellcost")
        val sellCost: Double, //成本
        @SerializedName("tax_sell_cost")
        val taxSellCost: Double, //含税成本
        @SerializedName("tax_sell_cost_total")
        var taxSellCostTotal: Double, //含税总成本
        @SerializedName("old_plnquantity")
        val oldPlnQuantity: Int, //老预约数？
        @SerializedName("totalunitcost")
        var unitCostTotal: Double, //总单价
        @SerializedName("total")
        var StoreUnitPriceTotal: Double, //零售小计
        var isChange: Boolean=false
) : Serializable

data class VendorBean(
        @SerializedName("vendorid")
        val vendorId: String,
        @SerializedName("vendornames")
        val vendorName: String
):Serializable