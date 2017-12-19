package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2017/11/8 13:39.
 */
data class PayBean(
        /**
         * 购买者id
         */
        @SerializedName("openid") val openId: String,
        /**
         * 店号
         */
        @SerializedName("storeid") val storeId: String,
        /**
         * 条码
         */
        @SerializedName("bar_code") val barCode: String,
        /**
         * 品号
         */
        @SerializedName("itemno") val itemId: String,
        /**
         * 品名
         */
        @SerializedName("itemname") val itemName: String,
        /**
         * 数量
         */
        var quantity: Int,
        /**
         * 单价
         */
        @SerializedName("store_price") val storePrice: Double,
        /**
         * 促销数量
         */
        @SerializedName("mm_quantity") val mmQuantity: Int,
        /**
         * 优惠金额
         */
        @SerializedName("disc_amt") var discAmt: Double,
        /**
         * 项次
         */
        @SerializedName("line_seq") val lineSeq: Int,
        /**
         * 促销编码
         */
        @SerializedName("discountid") val discountId: String?,
        /**
         * 到期日
         */
        @SerializedName("dead_datetime") val deadDatetime: String?,
        /**
         * 是否享有日期优惠
         */
        @SerializedName("date_dis_yn") val dateDisYN: String?
) : Serializable

data class AliPayBean(
        @SerializedName("storenumber") val storeId: String,
        @SerializedName("posnumber") val posNumber: String,
        @SerializedName("transactionnumber") val transactionNumber: String,
        val seq: String,
        @SerializedName("trade_no") val tradeNo: String,
        @SerializedName("buyer_logon_id") val buyUserId: String,
        @SerializedName("total_fee") val total: Double,
        @SerializedName("systemdate") val createDate: String
) : Serializable

data class WechatPayBean(
        @SerializedName("storenumber") val storeId: String,
        @SerializedName("posnumber") val posNumber: String,
        @SerializedName("transactionnumber") val transactionNumber: String,
        val seq: String,
        @SerializedName("total_fee") val total: Double,
        @SerializedName("transaction_id") val transactionId: String,
        @SerializedName("systemdate") val createDate: String,
        @SerializedName("refund_id") val refundId: String,
        @SerializedName("openid") val openId: String,
        @SerializedName("coupon_fee") val coupon: String,
        @SerializedName("non_coupond_fee") val nonCoupond: String
) : Serializable

data class WXPaySqlBean(
        //商户订单号
        val outTradeNo: String,
        //微信订单号
        val transactionId: String,
        //手机唯一id
        val telSeq: String,
        //总价
        val totalFee: Double,
        //店号
        val storeId: String,
        //pos号
        val assPos: Int,
        //组成单号的
        val nextTranNo: Int,
        //请求次数
        val seq: String,
        //用户id
        val openId: String,
        //优惠金额
        val couponFee: Double,
        //当前步骤数
        var theStep: Int,
        //出错信息
        var errorMessage: String,
        //是否处理完毕
        var isDone: Int,
        //是否上传完毕
        var isUpload: Int,
        //尝试次数
        var uploadCount: Int,
        //创建时间
        val createTime: String?
) : Serializable