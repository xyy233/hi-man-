package com.cstore.zhiyazhang.cstorepay.ali

import android.os.Message
import com.cstore.zhiyazhang.cstorepay.bean.AliPayBean
import com.cstore.zhiyazhang.cstorepay.bean.PayMsgBean
import com.cstore.zhiyazhang.cstorepay.util.MD5Util
import com.cstore.zhiyazhang.cstorepay.util.MyHandler
import com.cstore.zhiyazhang.cstorepay.util.MyHandler.Companion.ERROR
import com.cstore.zhiyazhang.cstorepay.util.MyHandler.Companion.SUCCESS
import com.cstore.zhiyazhang.cstorepay.util.XMLToJson
import com.google.gson.Gson
import com.zhy.http.okhttp.OkHttpUtils

/**
 * Created by zhiya.zhang
 * on 2018/6/25 15:35.
 */
class AliPayUtil constructor(private val msgBean: PayMsgBean) {
    private val key = msgBean.alipaySecurityCode
    private val partner = msgBean.alipayPartnerId
    private val sellerId = msgBean.alipaySellerId
    private val dynamicIdType = "qr_code"
    private val productCode = "BARCODE_PAY_OFFLINE"
    private val it_b_pay = "5m"
    private val sign_type = "MD5"
    private val _input_charset = "utf-8"
    private val notify_url = "https://mapi.alipay.com/gateway.do?"
    private val ORDER_FAIL = "ORDER_FAIL"//下单失败
    private val ORDER_SUCCESS_PAY_SUCCESS = "ORDER_SUCCESS_PAY_SUCCESS"//下单成功并且成功支付
    private val ORDER_SUCCESS_PAY_FAIL = "ORDER_SUCCESS_PAY_FAIL"//下单成功支付失败
    private val ORDER_SUCCESS_PAY_INPROCESS = "ORDER_SUCCESS_PAY_INPROCESS"//下单成功支付处理中
    private val UNKNOWN = "UNKNOWN"//处理结果未知

    fun getCreateAndPay(msg: String, outTradeNo: String, handler: MyHandler) {
        val m = Message()
        try {
            val p = buildPayRequestPara(msg, outTradeNo)
            val aliResult = buildRequest(p)
            if (aliResult.substring(0, 5) == "ERROR") {
                m.obj = "异常信息：$aliResult"
                m.what = ERROR
                handler.sendMessage(m)
                return
            }
            val payJson = XMLToJson.xmlToJson(aliResult)
            val ali = Gson().fromJson(payJson, AliPayBean::class.java).alipay
            if (ali.is_success == "F") {
                if (ali.error != null) {
                    m.obj = "异常信息：${ali.error}"
                } else {
                    m.obj = "异常信息：$payJson"
                }
                m.what = ERROR
                handler.sendMessage(m)
                return
            }
            val resultCode = ali.response!!.alipay!!.result_code
            when (resultCode) {
                ORDER_FAIL -> {
                    val errorDetail = ali.response.alipay!!.detail_error_des
                    m.obj = "异常信息：$errorDetail"
                    m.what = ERROR
                    handler.sendMessage(m)
                }
                ORDER_SUCCESS_PAY_SUCCESS -> {
                    m.obj = "交易完成！"
                    m.what = SUCCESS
                    handler.sendMessage(m)
                }
                ORDER_SUCCESS_PAY_FAIL -> {
                    //撤销
                    cancel(outTradeNo, handler)
                }
                ORDER_SUCCESS_PAY_INPROCESS -> {
                    //查询
                    quire(outTradeNo, handler, 0)
                }
                UNKNOWN -> {
                    //查询
                    quire(outTradeNo, handler, 0)
                }
            }
        } catch (e: Exception) {
            m.obj = "异常信息：${e.message.toString()}"
            m.what = ERROR
            handler.sendMessage(m)
        }
    }

    /**
     * 查询
     */
    private fun quire(outTradeNo: String, handler: MyHandler, count: Int) {
        if (count == 8) {
            //只有八次机会总计40s，超时就撤销
            cancel(outTradeNo, handler)
            return
        }
        val m = Message()
        try {
            val p = buildQuirePara(outTradeNo)
            val aliResult = buildRequest(p)
            if (aliResult.substring(0, 5) == "ERROR") {
                m.obj = "异常信息：$aliResult"
                m.what = ERROR
                handler.sendMessage(m)
                return
            }
            val payJson = XMLToJson.xmlToJson(aliResult)
            val ali = Gson().fromJson(payJson, AliPayBean::class.java).alipay
            if (ali.is_success == "F") {
                if (ali.error != null) {
                    m.obj = "异常信息：${ali.error}"
                } else {
                    m.obj = "异常信息：$payJson"
                }
                m.what = ERROR
                handler.sendMessage(m)
                return
            }
            val resultCode = ali.response!!.alipay!!.result_code
            when (resultCode) {
                "SUCCESS" -> {
                    val status = ali.response.alipay!!.trade_status
                    if (status == "WAIT_BUYER_PAY") {
                        Thread.sleep(5000)
                        quire(outTradeNo, handler, count + 1)
                    }
                    m.obj = "交易完成！"
                    m.what = SUCCESS
                }
                "FAIL" -> {
                    m.obj = "交易查询失败：${ali.response.alipay!!.detail_error_des}，请查询客户是否支付完成"
                    m.what = ERROR
                }
            }
            handler.sendMessage(m)
        } catch (e: Exception) {
            m.obj = "异常信息：${e.message.toString()}"
            m.what = ERROR
            handler.sendMessage(m)
        }
    }

    /**
     * 撤销
     */
    private fun cancel(outTradeNo: String, handler: MyHandler) {
        val m = Message()
        try {
            val p = buildCancelPara(outTradeNo)
            val aliResult = buildRequest(p)
            if (aliResult.substring(0, 5) == "ERROR") {
                m.obj = "异常信息：$aliResult"
                m.what = ERROR
                handler.sendMessage(m)
                return
            }
            val payJson = XMLToJson.xmlToJson(aliResult)
            val ali = Gson().fromJson(payJson, AliPayBean::class.java).alipay
            if (ali.is_success == "F") {
                if (ali.error != null) {
                    m.obj = "异常信息：${ali.error}"
                } else {
                    m.obj = "异常信息：$payJson"
                }
                m.what = ERROR
                handler.sendMessage(m)
                return
            }
            val resultCode = ali.response!!.alipay!!.result_code
            when (resultCode) {
                "SUCCESS" -> {
                    m.obj = "交易失败！已撤销此单！"
                }
                "FAIL" -> {
                    m.obj = "交易失败！尝试撤销失败：${ali.response.alipay!!.detail_error_des}"
                }
            }
            m.what = ERROR
            handler.sendMessage(m)
        } catch (e: Exception) {
            m.obj = "异常信息：${e.message.toString()}"
            m.what = ERROR
            handler.sendMessage(m)
        }
    }

    private fun buildQuirePara(outTradeNo: String): Map<String, String> {
        val service = "alipay.acquire.query"
        val p = LinkedHashMap<String, String>()
        p.put("_input_charset", _input_charset)
        p.put("out_trade_no", outTradeNo)
        p.put("partner", partner)
        p.put("service", service)
        p.put("sign", buildRequestMysign(p))
        p.put("sign_type", sign_type)
        return p
    }

    private fun buildCancelPara(outTradeNo: String): Map<String, String> {
        val service = "alipay.acquire.cancel"
        val p = LinkedHashMap<String, String>()
        p.put("_input_charset", _input_charset)
        p.put("out_trade_no", outTradeNo)
        p.put("partner", partner)
        p.put("service", service)
        p.put("sign", buildRequestMysign(p))
        p.put("sign_type", sign_type)
        return p
    }

    private fun buildPayRequestPara(msg: String, outTradeNo: String): Map<String, String> {
        val service = "alipay.acquire.createandpay"
        val subject = "C-Store ${msgBean.storeId} consumption"
        val p = LinkedHashMap<String, String>()
        p.put("_input_charset", _input_charset)
        p.put("dynamic_id", msg)
        p.put("dynamic_id_type", dynamicIdType)
        p.put("it_b_pay", it_b_pay)
        p.put("out_trade_no", outTradeNo)
        p.put("partner", partner)
        p.put("product_code", productCode)
        p.put("seller_id", sellerId)
        p.put("service", service)
        p.put("subject", subject)
        p.put("total_fee", msgBean.payAmount.toString())
        p.put("sign", buildRequestMysign(p))
        p.put("sign_type", sign_type)
        return p
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
        return if (response.code() == 200) {
            response.body().string().replace("\n", "").replace("\t", "")
        } else {
            val x = response.body().string()
            "ERROR${x.substring(x.indexOf("HTTP Status") + 25, x.indexOf("</h1><div class=\"line\">"))}"
        }
    }
}