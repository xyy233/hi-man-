package com.cstore.zhiyazhang.cstoremanagement.bean

import com.alipay.api.response.AlipayTradeCancelResponse
import com.alipay.api.response.AlipayTradePayResponse
import com.alipay.api.response.AlipayTradeQueryResponse
import com.alipay.api.response.AlipayTradeRefundResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2018/1/15 12:28.
 */
class AliQueryBean(
        @SerializedName("alipay_trade_query_response") val queryResponse: AlipayTradeQueryResponse?,
        @SerializedName("alipay_trade_pay_response")val payResponse:AlipayTradePayResponse?,
        @SerializedName("alipay_trade_refund_response")val refundResponse: AlipayTradeRefundResponse?,
        @SerializedName("alipay_trade_cancel_response")val cancelResponse: AlipayTradeCancelResponse?,
        val sign: String
) : Serializable


