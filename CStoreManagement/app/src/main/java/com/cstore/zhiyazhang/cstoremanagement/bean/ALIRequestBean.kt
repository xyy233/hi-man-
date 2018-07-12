package com.cstore.zhiyazhang.cstoremanagement.bean

import com.alipay.api.response.AlipayTradePayResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2018/1/11 10:45.
 */
data class ALIRequestBean(
        /**
         * 商户订单号
         */
        @SerializedName("out_trade_no") val outTradeNo: String,
        /**
         * 支付场景
         * 条码支付，取值：bar_code
         * 声波支付，取值：wave_code
         */
        val scene: String,
        /**
         * 支付授权码,扫的用户码
         */
        @SerializedName("auth_code") val authCode: String,
        /**
         * 订单标题
         */
        val subject: String,
        /**
         * 订单总金额
         */
        @SerializedName("total_amount") val totalAmount: Double,
        /**
         * 该笔订单允许的最晚付款时间,3m
         */
        @SerializedName("timeout_express") val timeoutExpress: String
) : Serializable

data class ALIResponseBean(
        @SerializedName("alipay_trade_pay_response") val response: AlipayTradePayResponse,
        val sign: String
) : Serializable