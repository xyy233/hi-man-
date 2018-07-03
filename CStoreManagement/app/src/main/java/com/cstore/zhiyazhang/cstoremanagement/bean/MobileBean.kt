package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2018/6/21 10:49.
 */
data class MobileBean(
        /**
         * 站点ID，由魔急便分配
         */
        val stationId: String,
        /**
         * 司机编码
         */
        val driverCode: String,
        /**
         * 货架编码
         */
        val shelfSn: String,
        val maxAmt: Double,
        /**
         * 商品列表
         */
        val goodsList: ArrayList<MobilePluBean>
) : Serializable

data class MobilePluBean(
        @SerializedName("itemnumber") val pluId: String,
        @SerializedName("pluname") val pluName: String,
        /**
         * 条码
         */
        @SerializedName("plunumber") val goodsBarcode: String,
        @SerializedName("storeunitprice") val storeUnitPrice: Double,
        /**
         * 数量
         */
        var goodsNum: Int?,
        /**
         * 生产日期
         */
        var beginTime: String?
) : Serializable

/**
 * 从SC拉取的数据，及保存到sc的数据格式
 */
data class MobileDetailBean(
        /**
         * 单号，YYYYMMDD + 序号
         */
        @SerializedName("tran_no") var tranNo: String?,
        /**
         * 司机编号
         */
        @SerializedName("divercode") val driverCode: String,
        /**
         * 到店时间
         */
        @SerializedName("arrive_time") var arriveTime: String?,
        /**
         * 可取货的最大金额
         */
        @SerializedName("max_amt") val maxAmt: Double,
        /**
         * 操作码，0：取货  1：退货
         */
        @SerializedName("op_code") val opCode: String,
        val detail: ArrayList<MobileDetailItemBean>
) : Serializable {
    companion object {
        /**
         * 把从sql得到的数据变成实际使用的对象
         */
        fun getMobileDetailByMobileGson(data: ArrayList<MobileDetailGsonBean>): ArrayList<MobileDetailBean> {
            data.sortBy { it.tranNo }
            val result = ArrayList<MobileDetailBean>()
            var tranNo = ""
            data.forEach {
                if (it.tranNo != tranNo) {
                    tranNo = it.tranNo
                    val mdb = MobileDetailBean(tranNo, it.driverCode, it.arriveTime, it.maxAmt, it.opCode, ArrayList())
                    result.add(mdb)
                }
                if (result.size > 0) {
                    val mdib = MobileDetailItemBean(tranNo, it.qty, it.maxQty)
                    result[result.size - 1].detail.add(mdib)
                }
            }
            return result
        }
    }
}

data class MobileDetailItemBean(
        /**
         * 品号
         */
        @SerializedName("itemno") val itemNo: String,
        /**
         * 取货或退货的实际数量
         */
        val qty: Int,
        /**
         * 退货的应退数量
         */
        @SerializedName("max_qty") val maxQty: Int
) : Serializable

/**
 * 用于从sc获得数据后转成json的bean
 */
data class MobileDetailGsonBean(
        /**
         * 单号，YYYYMMDD + 序号
         */
        @SerializedName("tran_no") val tranNo: String,
        /**
         * 司机编号
         */
        @SerializedName("divercode") val driverCode: String,
        /**
         * 到店时间
         */
        @SerializedName("arrive_time") val arriveTime: String,
        /**
         * 可取货的最大金额
         */
        @SerializedName("max_amt") val maxAmt: Double,
        /**
         * 操作码，0：取货  1：退货
         */
        @SerializedName("op_code") val opCode: String,
        /**
         * 品号
         */
        @SerializedName("itemno") val itemNo: String,
        /**
         * 取货或退货的实际数量
         */
        val qty: Int,
        /**
         * 退货的应退数量
         */
        @SerializedName("max_qty") val maxQty: Int
) : Serializable

data class MobileGondolaBean(
        val gondolaId: String,
        val gondolaName: String,
        val detail: ArrayList<MobileGondolaItemBean>
) : Serializable {
    companion object {
        fun getMobileGondolaByMobileGson(data: ArrayList<MobileGondolaGsonBean>): ArrayList<MobileGondolaBean> {
            data.sortBy { it.gondolaId }
            val result = ArrayList<MobileGondolaBean>()
            var gondolaId = ""
            data.forEach {
                if (it.gondolaId != gondolaId) {
                    gondolaId = it.gondolaId
                    val mgb = MobileGondolaBean(gondolaId, it.gondolaName, ArrayList())
                    result.add(mgb)
                }
                if (result.size > 0) {
                    val mgib = MobileGondolaItemBean(it.itemNo, it.itemName, it.storeUnitPrice, it.qty)
                    result[result.size - 1].detail.add(mgib)
                }
            }
            return result
        }
    }
}

data class MobileGondolaItemBean(
        val itemNo: String,
        val itemName: String,
        val storeUnitPrice: Double,
        val qty: Int
) : Serializable

data class MobileGondolaGsonBean(
        @SerializedName("itemnumber") val itemNo: String,
        @SerializedName("pluname") val itemName: String,
        @SerializedName("storeunitprice") val storeUnitPrice: Double,
        @SerializedName("gondolaid") val gondolaId: String,
        @SerializedName("gondolaname") val gondolaName: String,
        @SerializedName("qty") val qty: Int
) : Serializable

data class MobileDriverBean(
        val data: MobileDataBean,
        @SerializedName("err_msg")val errMsg:String,
        @SerializedName("err_code")val errCode:String,
        val success:Boolean
) : Serializable

data class MobileErrorBean(
        @SerializedName("err_msg")val errMsg:String,
        @SerializedName("err_code")val errCode:String,
        val success:Boolean
):Serializable

data class MobileDataBean(
        val driverName: String
) : Serializable