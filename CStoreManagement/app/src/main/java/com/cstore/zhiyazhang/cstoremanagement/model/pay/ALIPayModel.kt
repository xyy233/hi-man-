package com.cstore.zhiyazhang.cstoremanagement.model.pay

import android.content.Context
import android.os.Message
import android.util.Log
import com.alipay.api.AlipayClient
import com.alipay.api.DefaultAlipayClient
import com.alipay.api.request.AlipayTradeCancelRequest
import com.alipay.api.request.AlipayTradePayRequest
import com.alipay.api.request.AlipayTradeQueryRequest
import com.alipay.api.request.AlipayTradeRefundRequest
import com.alipay.api.response.AlipayTradePayResponse
import com.alipay.api.response.AlipayTradeQueryResponse
import com.alipay.api.response.AlipayTradeRefundResponse
import com.cstore.zhiyazhang.cstoremanagement.bean.ALIPaySqlBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ALIRequestBean
import com.cstore.zhiyazhang.cstoremanagement.bean.PosBean
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.sql.ALIPayDao
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.GsonUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.OnlyMyHandler.ERROR
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.OnlyMyHandler.SUCCESS
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import com.cstore.zhiyazhang.cstoremanagement.view.pay.PayCollectActivity
import com.google.gson.Gson

/**
 * Created by zhiya.zhang
 * on 2018/1/11 10:36.
 */
class ALIPayModel(context: Context) : ALIPayInterface {

    private val dao = ALIPayDao(context)

    override fun aliCollectMoney(activity: PayCollectActivity, code: String, money: Double, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val outTradeNo = WXPayModel.getShoppingId(activity, ip, msg, handler)
            if (outTradeNo == "-1") {
                return@Runnable
            }
            try {
                val aliPay = getAliPay()

                val requestBean = getAliData(code, money, outTradeNo)
                val data = Gson().toJson(requestBean)
                val request = AlipayTradePayRequest()
                request.bizContent = data

                val aliResponse: AlipayTradePayResponse = aliPay.execute(request)
                val response = GsonUtil.getAli(aliResponse.body).payResponse!!

                //查询是否成功
                responseIsSuccess(response, aliPay, requestBean, activity, ip, msg, handler)
            } catch (e: Exception) {
                msg.obj = e.message
                msg.what = ERROR
                handler.sendMessage(msg)
            }
        }).start()
    }

    override fun aliRefund(activity: PayCollectActivity, tradeNo: String, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            try {
                val aliPay = getAliPay()
                var totalFee = ""
                //查询订单获得金额
                for (i in 0..2) {
                    val request = AlipayTradeQueryRequest()
                    request.bizContent = "{\"out_trade_no\":\"$tradeNo\"}"
                    val aliResponse = aliPay.execute(request)
                    val nowResponse = GsonUtil.getAli(aliResponse.body).queryResponse!!
                    if (nowResponse.code == "10000") {
                        if (nowResponse.tradeStatus == "TRADE_SUCCESS") {
                            totalFee = nowResponse.totalAmount
                            break
                        } else {
                            msg.obj = nowResponse.tradeStatus
                            msg.what = ERROR
                            handler.sendMessage(msg)
                            return@Runnable
                        }
                    } else if (nowResponse.code == "40004") {
                        msg.obj = nowResponse.subMsg
                        msg.what = ERROR
                        handler.sendMessage(msg)
                        return@Runnable
                    }
                    Thread.sleep(1000 * 10)
                }

                val request = AlipayTradeRefundRequest()
                request.bizContent = "{\"out_trade_no\":\"$tradeNo\",\"refund_amount\":\"$totalFee\"}"
                for (i in 0..2) {
                    val aliResponse = aliPay.execute(request)
                    val response = GsonUtil.getAli(aliResponse.body).refundResponse!!
                    when {
                        response.code == "10000" ->//退款成功
                        {
                            refundDone(response, handler, msg, ip)
                            return@Runnable
                        }
                        else -> {
                            if (response.subCode == "ACQ.SYSTEM_ERROR") {
                                if (i == 2) {
                                    msg.obj = "系统超时,请检查网络或稍后重试"
                                    msg.what = ERROR
                                    handler.sendMessage(msg)
                                    return@Runnable
                                }
                                //重试
                                Thread.sleep(5000)
                            } else {
                                //失败了
                                msg.obj = response.subMsg
                                msg.what = ERROR
                                handler.sendMessage(msg)
                                return@Runnable
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                msg.obj = e.message
                msg.what = ERROR
                handler.sendMessage(msg)
            }
        }).start()
    }

    private fun refundDone(response: AlipayTradeRefundResponse, handler: MyHandler, msg: Message, ip: String) {
        val sql = MySql.refoundCall(response.tradeNo, " ", "支付宝")
        val result = SocketUtil.initSocket(ip, sql).inquire()
        if (result == "0") {
            msg.obj = response.refundFee
            msg.what = SUCCESS
            handler.sendMessage(msg)
        } else {
            msg.obj = "退款成功，保存数据失败：$result"
            msg.what = ERROR
            handler.sendMessage(msg)
        }
    }

    private fun responseIsSuccess(response: AlipayTradePayResponse, aliPay: AlipayClient, requestBean: ALIRequestBean, activity: PayCollectActivity, ip: String, msg: Message, handler: MyHandler) {
        when {
            response.code == "10000" -> //支付成功
                payDone(activity.getPos(), response, handler, msg, ip)
            response.code == "40004" -> {
                //支付失败
                msg.obj = response.subMsg
                msg.what = ERROR
                handler.sendMessage(msg)
            }
            response.code == "10003" || response.code == "20000" -> //等待用户付款 || 未知异常
                inquirePay(aliPay, requestBean, activity, ip, msg, handler)
            else -> {
                //鬼知道发生了什么
                msg.obj = response.subMsg
                msg.what = ERROR
                handler.sendMessage(msg)
            }
        }
    }

    /**
     * 查询是否交易完成
     */
    private fun inquirePay(aliPay: AlipayClient, requestBean: ALIRequestBean, activity: PayCollectActivity, ip: String, msg: Message, handler: MyHandler) {
        //查询12次,每隔10s查一次
        for (i in 0..11) {
            Thread.sleep(1000 * 10)
            val request = AlipayTradeQueryRequest()
            request.bizContent = "{\"out_trade_no\":\"${requestBean.outTradeNo}\"}"
            val aliResponse = aliPay.execute(request)
            val nowResponse = GsonUtil.getAli(aliResponse.body).queryResponse!!
            when (nowResponse.code) {
                "10000" -> {
                    when (nowResponse.tradeStatus) {
                        "TRADE_CLOSED" -> {
                            msg.obj = "未付款交易超时关闭，或支付完成后全额退款"
                            msg.what = ERROR
                            handler.sendMessage(msg)
                            return
                        }
                        "TRADE_SUCCESS" -> {
                            payDone(activity.getPos(), nowResponse, handler, msg, ip)
                            return
                        }
                        "TRADE_FINISHED" -> {
                            msg.obj = "交易结束，不可退款"
                            msg.what = ERROR
                            handler.sendMessage(msg)
                            return
                        }
                    }
                }
                "40004" -> {
                    msg.obj = nowResponse.subMsg
                    msg.what = ERROR
                    handler.sendMessage(msg)
                    return
                }
            //"10003" -> continue  还在等待用户付款就不管，自动跳出去做
            //"20000" -> continue  未知异常就不管，自动跳出去做
            }
            if (i == 11) {
                //多次都没有得到结果就去撤销
                cancelPay(nowResponse, aliPay, requestBean, activity, ip, msg, handler)
            }
        }
    }

    /**
     * 撤销订单
     */
    private fun cancelPay(response: AlipayTradeQueryResponse, aliPay: AlipayClient, requestBean: ALIRequestBean, activity: PayCollectActivity, ip: String, msg: Message, handler: MyHandler) {
        val request = AlipayTradeCancelRequest()
        request.bizContent = "{\"out_trade_no\":\"${requestBean.outTradeNo}\"}"
        val aliResponse = aliPay.execute(request)
        val nowResponse = GsonUtil.getAli(aliResponse.body).cancelResponse!!
        when (nowResponse.code) {
            "10000" -> {
                reverseDone(ip, activity.getPos(), response)
                msg.obj = "因${response.subMsg} 已撤销此次交易！"
                msg.what = ERROR
                handler.sendMessage(msg)
            }
            else -> {
                //如果要重试就重试
                if (nowResponse.retryFlag != null && nowResponse.retryFlag == "Y") {
                    cancelPay(response, aliPay, requestBean, activity, ip, msg, handler)
                } else {
                    //不知道发生了什么
                    //这里录入数据库
                    val aliBean = getAliPayBean(activity.getPos(), response, 3, nowResponse.subMsg)
                    dao.insertSql(aliBean)
                    msg.obj = "因${nowResponse.subMsg},撤销失败,请联系系统部！"
                    msg.what = ERROR
                    handler.sendMessage(msg)
                }
            }
        }
    }

    private fun reverseDone(ip: String, pos: PosBean, aliResult: AlipayTradeQueryResponse) {
        val sql = MySql.reverseCall
        val result = SocketUtil.initSocket(ip, sql).inquire()
        if (result != "0") {
            val aliBean = getAliPayBean(pos, aliResult, 4, result)
            dao.insertSql(aliBean)
        }
    }

    /**
     * 直接交易成功
     */
    private fun payDone(pos: PosBean, response: AlipayTradePayResponse, handler: MyHandler, msg: Message, ip: String) {
        try {
            val oneSql = MySql.payDoneCall("支付宝", response.totalAmount.toDouble())
            val callResult = SocketUtil.initSocket(ip, oneSql).inquire()
            if (callResult == "0") {
                val twoSql = MySql.createAliPayDone(pos, response, false)
                val aliResult = SocketUtil.initSocket(ip, twoSql).inquire()
                if (aliResult != "1") {
                    val aliBean = getAliPayBean(pos, response, 2, aliResult)
                    dao.insertSql(aliBean)
                }
            } else {
                val aliBean = getAliPayBean(pos, response, 1, callResult)
                dao.insertSql(aliBean)
            }
            msg.obj = response.receiptAmount
            msg.what = MyHandler.SUCCESS
            handler.sendMessage(msg)
        } catch (e: Exception) {
            Log.e("AliPayModel", e.message)
        }
    }

    /**
     * 查询到交易成功
     */
    private fun payDone(pos: PosBean, response: AlipayTradeQueryResponse, handler: MyHandler, msg: Message, ip: String) {
        try {
            val oneSql = MySql.payDoneCall("支付宝", response.totalAmount.toDouble())
            val callResult = SocketUtil.initSocket(ip, oneSql).inquire()
            if (callResult == "0") {
                val twoSql = MySql.createAliPayDone(pos, response, false)
                val aliResult = SocketUtil.initSocket(ip, twoSql).inquire()
                if (aliResult != "1") {
                    val aliBean = getAliPayBean(pos, response, 2, aliResult)
                    dao.insertSql(aliBean)
                }
            } else {
                val aliBean = getAliPayBean(pos, response, 1, callResult)
                dao.insertSql(aliBean)
            }
            msg.obj = response.receiptAmount
            msg.what = MyHandler.SUCCESS
            handler.sendMessage(msg)
        } catch (e: Exception) {
            Log.e("AliPayModel", e.message)
        }
    }

    private fun getAliData(code: String, money: Double, outTradeNo: String): ALIRequestBean {
        return ALIRequestBean(outTradeNo, "bar_code", code, "${User.getUser().storeName}-超市-线下门店支付", money, "3m")
    }

    companion object {
        private val appId = "2013122600002470"
        private val privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCkf4CBnoJe207lg6SoyLyHKoK3m36QzGQdlMjc7bhhEaG1tHvwzxHhAsf/8iXSdo7KTNJ47s1sLPEsJj1ENnOEmeIjJHiEsq5qm9LygSK9ZVqBnT9zUmMGGwcifanczdRHoeOzDRGWRMDjKLZ7aIWQmw1p7suK6/NeE1aij6j/ajZCGkVszj/n+qF1M2WSW712Vzvq5zii2NTfQOMSpGTfIC3wf8949DmNgMQmExPaYbcZM3ohODpH+RzmtiO3j8Rx7Z0EofzQu6RDK6BiFNW9qGvif13eNrLkXwZ2BXuCe9pcwvs7+uh3dX6mUAIdmnaE70C/rLgjrVsXpa9blFItAgMBAAECggEAHimm9Z2MCDqsZ9dQrQZJ03sPBv3DImfn/6iVgDUyttHV1gynIUvG2nx5Ecxj9Qh6PEwD19rp3ekLu+2zFDvryKey2IDKfIKLCs9Ryde1+AaKpvOfe9TihW8VStTB/dPcFdpwdxdxXx1yRPTlKRHMU+yqc/8uYWXMdTaUjzBKa7LRuXswB0CXPx7+7V3wzlVMnsnm/APD+Y+6t5zjxXKs9WFxHIBvgua5m3DDiuo80TqDXHFfsPO6dho+ogEEn3jbwqYTFQSeAgH1FOnsXUFLNZuyaxgFQ6i2V9yg1Cd1Q5odxfXCTSjdJRE708cJUqBlsfBF9gXRbkr6tMlJBUmnvQKBgQDVb9/UGBUL+1xFX7uY54oRGsnX2096BDA0P7mGEp3XJzx5+I171oIfeeRtAt1fKeq14wViEhN0HgO4MeaA0tV+453o5u5GpBEiKpRqjXDCUeJfOemD61xuouu7klEmTYoC33mR3ByJDCLa7tZgVXDvrNebvAeuZEzg5LREQNxTywKBgQDFTUGBJlrG0NAUikamQHiw5LDdjffoQq2QVxs92QKPrVCHxATnG+68XEoGfZqPhtTalwejkQqVbQbz7A6+g2IdQsYcu9zuw349Az6rHzsFJmQpMBtO2Iswc+sLTd+MS6VQFb36FqrgY9Zb8jX4k/8rUZBJVyhiN5pmj/7SefNi5wKBgG1JVG2QSy6QbUWkaDU50WtCsTlStVY/0MLgIkmxPJrPH1tA1okjZAtj6X+b6OfyWZj6fmYh5U4elD77ZhBuZB0NxWxc2oLXPWKmNMp+U89cCDJEP/ppSDHqQBQSLnUTXOhtrxztfLr7uNkrVB+NgD9o3BmE5NX5y5eX42nTYD2TAoGBAKRZ9nbevD9hMfPqO2/BxMeVuL7Vw+x1np7d8JNUcg29EZgGcQ8S9YtyVTeS6W0lo6lypapa57YRW/lUafPI/bHiLaVB2IgL0NyCF3H0UfW4RTcKG7VSLJ/v75s7AzyaxtovQlFREIZao/VzjgHDRouPJeHx+HHYX4WP7XTmoReBAoGAcLCsz96iRu4TohplzUm3z2K2eEKqeIPp9hNVF1lQuWok86Fy1DwxG6OQdzXkllafFZwvZUFS2IqWY4Mjiv+mEZsleK3oesfaQ3Hd6ztEHDZyxOfY8/KhErTLDvnMy9Z0EbXgYAF993QgLapisrBqRPaxGFHCrY3J9gwWgeFnpRc="
        private val publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnHQmuRuog9zNcdb8e+Zfa4BK9uLsQt4YUtA0l38MdNplA13QqrJ0e9ox1ODR024NyJButxNwnB0xi9SEpMsnuSrqC6LRBbkUUQOdsKmpx8GFYOXMoRjLrIMA5A5FiRWoN4/uy2xsAJyPgvOv3qbp1MSjCXQnSWcrxzhkBMMJ8Tf+4dlGOVmnuK6Lf7Ub8UIyhEspomQgEc5J4NYWsgg6CZp7SV0KJt/C6zhokT4Srru0OlkXo+f30sw41YxCFirEdbTs4arSikzUdJs5ALAcFX+9Yigy5Qvq/PiVx/2JcG6DXyfYK7mXJ8/iLzfaTL2YMGRs++OG0VCkrj74klR6MQIDAQAB"
        private fun getAliPay(): AlipayClient {
            return DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appId, privateKey, "json", "UTF-8", publicKey, "RSA2")
        }

        fun getAliPayBean(pos: PosBean, querBean: AlipayTradeQueryResponse, theStep: Int, errorMessage: String): ALIPaySqlBean {
            val outTradeNo = querBean.outTradeNo
            val tradeNo = querBean.tradeNo
            val storeId = User.getUser().storeId
            val assPos = pos.assPos
            val nextTranNo = pos.nextTranNo
            val totalFee = querBean.totalAmount.toDouble()
            val buyId = querBean.buyerLogonId
            return ALIPaySqlBean(outTradeNo, tradeNo, MyApplication.getOnlyid(), totalFee, storeId, assPos, nextTranNo, "01", buyId, theStep, errorMessage, 0, 0, 0, null)
        }

        fun getAliPayBean(pos: PosBean, querBean: AlipayTradePayResponse, theStep: Int, errorMessage: String): ALIPaySqlBean {
            val outTradeNo = querBean.outTradeNo
            val tradeNo = querBean.tradeNo
            val storeId = User.getUser().storeId
            val assPos = pos.assPos
            val nextTranNo = pos.nextTranNo
            val totalFee = querBean.totalAmount.toDouble()
            val buyId = querBean.buyerLogonId
            return ALIPaySqlBean(outTradeNo, tradeNo, MyApplication.getOnlyid(), totalFee, storeId, assPos, nextTranNo, "01", buyId, theStep, errorMessage, 0, 0, 0, null)
        }
    }
}

interface ALIPayInterface {
    fun aliCollectMoney(activity: PayCollectActivity, code: String, money: Double, handler: MyHandler)

    fun aliRefund(activity: PayCollectActivity, tradeNo: String, handler: MyHandler)
}