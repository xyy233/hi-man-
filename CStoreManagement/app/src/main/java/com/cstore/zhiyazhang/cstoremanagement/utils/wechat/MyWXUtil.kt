package com.cstore.zhiyazhang.cstoremanagement.utils.wechat

import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.bean.PosBean
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.model.pay.WXPayModel.Companion.getWXPayBean
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.sql.WXPayDao
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.OnlyMyHandler.ERROR
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import com.github.wxpay.sdk.WXPay
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by zhiya.zhang
 * on 2017/11/16 9:02.
 */
object MyWXUtil {

    private val success = "SUCCESS"

    /**
     * 开始收钱，30s等待时间，内置重试以及撤销
     * @param data 要发过去的数据
     * @param wxPay 微信的通信工具
     * @param msg 线程通信的信息
     * @param handler 线程通信的通道
     * @return 如果为空或两个code不为SUCCESS代表出错了直接结束就好，因为已经在内部处理过msg了，否则就是交易成功
     */
    fun microPay(ip: String, pos: PosBean, dao: WXPayDao, data: HashMap<String, String>, wxPay: WXPay, msg: Message, handler: MyHandler): HashMap<String, String>? {
        var remainingTimeMs: Int = 30 * 1000
        var startTimestampMs: Long = 0
        var lastResult: Map<String, String>? = null
        //这里不用做循环的，懒得弄了，反正逻辑走下来只会跑一遍，以后心情好再改。。。要是没改= =额。。就当他没循环过也可以的，查询那里把循环做掉了
        while (true) {
            //每次重试都要单号都要加一
            //data.put("out_trade_no", (data["out_trade_no"]!!.toLong() + 1).toString())
            startTimestampMs = System.currentTimeMillis()
            val readTimeoutMs = remainingTimeMs - 10000
            if (readTimeoutMs > 1000) {
                try {
                    lastResult = wxPay.microPay(data, 10000, readTimeoutMs)
                    val returnCode = lastResult["return_code"]
                    if (returnCode == "SUCCESS") {
                        val resultCode = lastResult["result_code"]
                        val errCode = lastResult["err_code"]
                        if (resultCode == "SUCCESS") {
                            //成功
                            break
                        } else {
                            //出问题
                            if (errCode == "SYSTEMERROR" || errCode == "BANKERROR" || errCode == "USERPAYING") {
                                //支付结果为未知，开始查询,只要执行一次就行，查询内部会自己循环
                                lastResult = doOrderQuery(ip, pos, dao, data, wxPay, msg, handler)
                                break
                            } else {
                                //支付失败，撤销
                                wxReverse(ip, pos, dao, lastResult["err_code_des"], data, wxPay, msg, handler)
                                break
                            }
                        }
                    } else {
                        //一开始就错了,撤销
                        wxReverse(ip, pos, dao, lastResult["return_msg"], data, wxPay, msg, handler)
                        break
                    }
                } catch (e: Exception) {
                    //崩溃了，执行查询
                    wxReverse(ip, pos, dao, e.message, data, wxPay, msg, handler)
                    break
                }
            } else {
                //超时了还没结果去做撤销啦
                wxReverse(ip, pos, dao, "交易超时", data, wxPay, msg, handler)
                break
            }
        }
        return if (lastResult != null) {
            lastResult as HashMap<String, String>
            lastResult.put("seq", "01")
            lastResult
        } else {
            null
        }
    }

    /**
     * 查询订单
     */
    fun doOrderQuery(data: HashMap<String, String>, wxPay: WXPay, msg: Message, handler: MyHandler): Map<String, String> {
        var lastResult: Map<String, String>? = null
        try {
            for (i in 0..5) {
                lastResult = wxPay.orderQuery(data, 10000, 10000)
                if (lastResult["return_code"] == "SUCCESS") {
                    if (lastResult["result_code"] == "SUCCESS") {
                        val tradeState = lastResult["trade_state"]
                        if (tradeState == "SUCCESS") {
                            return lastResult
                        } else {
                            msg.obj = lastResult["trade_state_desc"]
                            msg.what = ERROR
                            handler.sendMessage(msg)
                            break
                        }
                    } else {
                        //系统异常，重试
                        if (lastResult["err_code"]=="SYSTEMERROR"){
                            Thread.sleep(5000)
                            continue
                        }else if (lastResult["err_code"]=="ORDERNOTEXIST"){
                            msg.obj = "订单不存在！"
                            msg.what = ERROR
                            handler.sendMessage(msg)
                            break
                        }else{
                            msg.obj = lastResult["err_code_des"]
                            msg.what = ERROR
                            handler.sendMessage(msg)
                            break
                        }
                    }
                } else {
                    msg.obj = lastResult["return_msg"]
                    msg.what = ERROR
                    handler.sendMessage(msg)
                    break
                }
            }
        } catch (e: Exception) {
            msg.obj = e.message
            msg.what = ERROR
            handler.sendMessage(msg)
        }
        return HashMap()
    }

    /**
     * 收款-》查询订单,附带异常处理
     */
    private fun doOrderQuery(ip: String, pos: PosBean, dao: WXPayDao, outTradeNo: HashMap<String, String>, wxPay: WXPay, msg: Message, handler: MyHandler): Map<String, String>? {
        var lastResult: Map<String, String>? = null
        try {
            val finalTradeNo = HashMap<String, String>()
            finalTradeNo.put("out_trade_no", outTradeNo["out_trade_no"]!!)
            //查询最多执行6次,总计三十秒等待时间
            for (i in 0..5) {
                //微信建议休眠十秒,在此休眠5s，避免多余等待
                Thread.sleep(5000)
                //未超时开始查询
                lastResult = wxPay.orderQuery(finalTradeNo, 10000, 10000)
                if (lastResult["return_code"] == "SUCCESS") {
                    if (lastResult["result_code"] == "SUCCESS") {
                        val tradeState = lastResult["trade_state"]
                        if (tradeState == "SUCCESS") {
                            //交易完成！
                            return lastResult
                        } else {
                            //交易状态未完成,用户还在输入密码
                            if (tradeState == "USERPAYING") {
                                continue
                            } else {
                                //是其他错误，如果是撤销就返回提示已撤销，如果是其他的就去执行一遍撤销
                                return if (tradeState == "REVOKED") {
                                    msg.obj = "已撤销当前订单！${lastResult["trade_state_desc"]}"
                                    msg.what = ERROR
                                    handler.sendMessage(msg)
                                    lastResult = null
                                    lastResult
                                } else {
                                    wxReverse(ip, pos, dao, lastResult["trade_state_desc"], outTradeNo, wxPay, msg, handler)
                                    lastResult = null
                                    lastResult
                                }
                            }
                        }
                    } else {
                        //系统异常，重试
                        if (lastResult["err_code"]=="SYSTEMERROR"){
                            continue
                        }else{
                            //出错了做撤销处理
                            wxReverse(ip, pos, dao, lastResult["err_code_des"], outTradeNo, wxPay, msg, handler)
                            lastResult = null
                            return lastResult
                        }
                    }
                } else {
                    //报错了,做撤销处理
                    wxReverse(ip, pos, dao, lastResult["return_msg"], outTradeNo, wxPay, msg, handler)
                    lastResult = null
                    return lastResult
                }
            }
            //超时了没结果就去做撤销
            wxReverse(ip, pos, dao, "交易超时", outTradeNo, wxPay, msg, handler)
            lastResult = null
            return lastResult
        } catch (e: Exception) {
            //查询出错，去做撤销
            wxReverse(ip, pos, dao, e.message, outTradeNo, wxPay, msg, handler)
            lastResult = null
            return lastResult
        }
    }


    /**
     * 撤销订单
     * @param outTradeNo 商户订单号
     * @param wxPay 微信的通信工具
     */
    private fun wxReverse(ip: String, pos: PosBean, dao: WXPayDao, errorMessage: String?, outTradeNo: HashMap<String, String>, wxPay: WXPay, msg: Message, handler: MyHandler) {
        try {
            //微信建议要15s后调用，在这里用1秒试试
            Thread.sleep(1000)
            //创建撤销需要的数据，只需要商户订单号
            val finalTradeNo = HashMap<String, String>()
            finalTradeNo.put("out_trade_no", outTradeNo["out_trade_no"]!!)
            //撤销最多执行三次
            var i = 0
            while (i < 3) {
                i++
                val result = wxPay.reverse(finalTradeNo)
                if (result["return_code"] == "SUCCESS") {
                    if (result["result_code"] == "SUCCESS") {
                        reverseDone(ip, pos, dao, result)
                        //成功撤销
                        msg.obj = "因$errorMessage 已撤销此次交易！"
                        msg.what = MyHandler.ERROR
                        handler.sendMessage(msg)
                        return
                    } else {
                        if (result["err_code_des"] == "SYSTEMERROR" || result["recall"] == "Y") {
                            //未知错误或需要重调就去重新调用
                            Thread.sleep(5000)
                            continue
                        } else {
                            val wxBean = getWXPayBean(pos, result, 3, result["err_code_des"]!!)
                            dao.insertSql(wxBean)
                            //出错直接丢出
                            msg.obj = result["err_code_des"]
                            msg.what = MyHandler.ERROR
                            handler.sendMessage(msg)
                            return
                        }
                    }
                } else {

                    val wxBean = getWXPayBean(pos, result, 3, result["return_msg"]!!)
                    dao.insertSql(wxBean)

                    //从一开始就错了
                    msg.obj = result["return_msg"]
                    msg.what = MyHandler.ERROR
                    handler.sendMessage(msg)
                    return
                }
            }

            val wxBean = getWXPayBean(pos, outTradeNo, 3, "连接超时")
            dao.insertSql(wxBean)
            //超时
            msg.obj = "连接超时，因未知原因交易失败，无法判断是否成功撤销，请联系系统部"
            msg.what = MyHandler.ERROR
            handler.sendMessage(msg)
        } catch (e: Exception) {
            val wxBean = getWXPayBean(pos, outTradeNo, 3, e.message.toString())
            dao.insertSql(wxBean)
            //撤销出错了
            msg.obj = "交易失败，无法判断是否成功撤销 ${e.message}"
            msg.what = MyHandler.ERROR
            handler.sendMessage(msg)
        }
    }

    /**
     * 撤销成功执行sql
     * 如果异常记录结果就行了不用返回错误
     */
    private fun reverseDone(ip: String, pos: PosBean, dao: WXPayDao, wxResult: Map<String, String>) {
        val sql = MySql.reverseCall
        val result = SocketUtil.initSocket(ip, sql).inquire()
        if (result != "0") {
            val wxBean = getWXPayBean(pos, wxResult, 4, result)
            dao.insertSql(wxBean)
        }
    }

    /**
     * 退款
     * @param data 必须包含out_trade_no、total_fee可以包含coupon_fee，订单号，价格，优惠卷
     */
    fun wxRefund(data: Map<String, String>, wxPay: WXPay, msg: Message, handler: MyHandler): Map<String, String> {
        try {
            val finalTradeNo = getRefundData(data)
            for (i in 0..2) {
                val result = wxPay.refund(finalTradeNo)
                if (result["return_code"] == success) {
                    if (result["result_code"] == success) {
                        //退款成功
                        return result
                    } else {
                        //退款失败，根据返回内容决定是否重试
                        val errorCode = result["err_code"]
                        if (errorCode == "SYSTEMERROR" || errorCode == "BIZERR_NEED_RETRY") {
                            if (i == 2) {
                                //超时了
                                msg.obj = "系统超时,请检查网络或稍后重试"
                                msg.what = ERROR
                                handler.sendMessage(msg)
                                break
                            }
                            Thread.sleep(5000)
                            continue
                        } else {
                            //不需要重试,就是出问题了
                            msg.obj = result["err_code_des"].toString()
                            msg.what = ERROR
                            handler.sendMessage(msg)
                            break
                        }
                    }
                } else {
                    //一开始就出错了
                    msg.obj = result["return_msg"].toString()
                    msg.what = ERROR
                    handler.sendMessage(msg)
                    break
                }
            }
        } catch (e: Exception) {
            msg.obj = "退款失败,${e.message.toString()}"
            msg.what = ERROR
            handler.sendMessage(msg)
        }
        return HashMap<String, String>()
    }

    /**
     * 生成退款需要的Map数据
     */
    private fun getRefundData(data: Map<String, String>): Map<String, String> {
        //创建撤销需要的数据，需要商户订单号,商户退款单号,订单金额,退款金额
        val outTradeNo = data["out_trade_no"]
        val random = getRandom()
        //退款单号先随便弄一下，用5000+年月日+8位随机数
        val outRefundNo = "5000${MyTimeUtil.nowDate3}$random"
        //订单金额
        val totalFee = data["total_fee"]
        val couponFee = data["coupon_fee"]
        //退款金额
        val refundFee = if (data["coupon_fee"] != null) {
            (totalFee!!.toLong() - couponFee!!.toLong()).toString()
        } else {
            totalFee!!.toString()
        }
        val finalTradeNo = HashMap<String, String>()
        finalTradeNo.put("out_trade_no", outTradeNo!!)
        finalTradeNo.put("out_refund_no", outRefundNo)
        finalTradeNo.put("total_fee", totalFee)
        finalTradeNo.put("refund_fee", refundFee)
        return finalTradeNo
    }

    fun getRandom(): String {
        val numberChar = "0123456789"
        val sb = StringBuilder()
        val random = Random()
        for (i in 0..7) {
            val length = numberChar.length
            val ranInt = random.nextInt(length)
            val number = numberChar[ranInt]
            sb.append(number)
        }
        return sb.toString()
    }


    /**
     * 生成微信收款的map数据
     */
    fun getWXData(code: String, money: Double, outTradeNo: String): HashMap<String, String> {
        val result = HashMap<String, String>()
        result.put("device_info", MyApplication.getOnlyid())
        result.put("body", "${User.getUser().storeName}-超市-线下门店支付")
        result.put("out_trade_no", outTradeNo)
        result.put("total_fee", (money * 100).toInt().toString())
        result.put("spbill_create_ip", MyApplication.getMyIP())
        result.put("auth_code", code)
        return result
    }
}