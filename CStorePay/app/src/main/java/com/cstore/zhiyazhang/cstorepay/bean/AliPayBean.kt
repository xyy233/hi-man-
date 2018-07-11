package com.cstore.zhiyazhang.cstorepay.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2018/7/4 10:17.
 */
data class AliPayBean(
        @SerializedName("alipay") val alipay: AliPayDetail
) : Serializable


data class AliPayDetail(
        val is_success: String,
        val response: AliPayDetailAli?,
        val error: String?
) : Serializable

data class AliPayDetailAli(@SerializedName("alipay") val alipay: AlipayResponseDetail?) : Serializable

data class AlipayResponseDetail(
        @SerializedName("result_code") val result_code: String,
        @SerializedName("buyer_logon_id") val buyerLogonId: String?,
        @SerializedName("buyer_user_id") val buyer_user_id: String?,
        @SerializedName("detail_error_code") val detail_error_code: String?,
        @SerializedName("detail_error_des") val detail_error_des: String?,
        @SerializedName("out_trade_no") val trade_no: String,
        @SerializedName("trade_status") val trade_status: String?
) : Serializable