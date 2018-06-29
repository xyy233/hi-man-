package com.cstore.zhiyazhang.cstorepay.ali

import com.cstore.zhiyazhang.cstorepay.bean.PayMsgBean
import com.cstore.zhiyazhang.cstorepay.util.MD5Util
import com.cstore.zhiyazhang.cstorepay.util.MyHandler
import com.zhy.http.okhttp.OkHttpUtils

/**
 * Created by zhiya.zhang
 * on 2018/6/25 15:35.
 */
class AliPayUtil constructor(private val msgBean: PayMsgBean) {
    private val key = msgBean.alipaySecurityCode
    private val partner = msgBean.alipayPartnerId
    private val seller_id = msgBean.alipaySellerId
    private val dynamic_id_type = "qr_code"
    private val product_code = "BARCODE_PAY_OFFLINE"
    private val it_b_pay = "5m"
    private val sign_type = "MD5"
    private val _input_charset = "utf-8"
    private val notify_url = "https://mapi.alipay.com/gateway.do?"

    fun getCreateAndPay(msg: String, outTradeNo: String, handler: MyHandler) {
        val service = "alipay.acquire.createandpay"
        val subject = "C-Store ${msgBean.storeId} consumption"
        val p = LinkedHashMap<String, String>()
        p.put("_input_charset", _input_charset)
        p.put("dynamic_id", msg)
        p.put("dynamic_id_type", dynamic_id_type)
        p.put("it_b_pay", it_b_pay)
        p.put("out_trade_no", outTradeNo)
        p.put("partner", partner)
        p.put("product_code", product_code)
        p.put("seller_id", seller_id)
        p.put("service", service)
        p.put("subject", subject)
        p.put("total_fee", msgBean.payAmount.toString())
        p.put("sign", buildRequestMysign(p))
        p.put("sign_type", sign_type)
        buildRequest(p)
    }

    private fun buildRequestMysign(p: HashMap<String, String>): String {
        var result = ""
        p.forEach {
            result += "${it.key}=${it.value}&"
        }
        result = (result.substring(0, result.length - 1))
        return sign(result)
    }

    private fun sign(text: String): String {
        val data = text + key
        return MD5Util.encrypt(data)
    }

    private fun buildRequest(p: Map<String, String>): String {
        val h = HashMap<String, String>()
        h.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
        h.put("User-Agent", "Mozilla/4.0")
        val response = OkHttpUtils
                .post()
                .url(notify_url)
                .params(p)
                .headers(h)
                .build()
                .execute()
        val x = response.body().string()
        return x
    }
}