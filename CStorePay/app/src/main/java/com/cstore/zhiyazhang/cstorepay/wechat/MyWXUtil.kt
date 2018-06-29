import android.os.Message
import com.cstore.zhiyazhang.cstorepay.bean.PayMsgBean
import com.cstore.zhiyazhang.cstorepay.util.MyApplication
import com.cstore.zhiyazhang.cstorepay.util.MyHandler
import com.cstore.zhiyazhang.cstorepay.util.MyHandler.Companion.ERROR
import com.cstore.zhiyazhang.cstorepay.util.MyHandler.Companion.SUCCESS
import com.github.wxpay.sdk.WXPay

object MyWXUtil {
    private val success = "SUCCESS"
    private val error = "ERROR"
    private val systemError = "SYSTEMERROR"
    private val bankError = "BANKERROR"
    private val userPaying = "USERPAYING"
    private val rtnCode = "return_code"
    private val rstCode = "result_code"
    private val errCode = "err_code"

    /**
     * 开始收钱，30s等待时间，内置重试以及撤销
     * @return 如果为空或两个code不为SUCCESS代表出错了直接结束就好，因为已经在内部处理过msg了，否则就是交易成功
     */
    fun microPay(data: HashMap<String, String>, wxPay: WXPay, handler: MyHandler): HashMap<String, String>? {
        val msg = Message()
        var lastResult: Map<String, String>? = null
        val timeoutMs = 10000
        try {
            lastResult = wxPay.microPay(data, timeoutMs, timeoutMs * 3)
            val returnCode = lastResult[rtnCode]
            if (returnCode == success) {
                val resultCode = lastResult[rstCode]
                val errCode = lastResult[errCode]
                if (resultCode != success) {
                    //异常
                    if (errCode == systemError || errCode == bankError || errCode == userPaying) {
                        //支付结果为未知，开始查询,只要执行一次就行，查询内部会自己循环
                        lastResult = doOrderQuery(data, wxPay, msg, handler)
                    } else {
                        //支付失败，撤销
                        wxReverse(lastResult["err_code_des"], data, wxPay, msg, handler)
                    }
                } else {
                    //成功
                    msg.what = SUCCESS
                    msg.obj = "收款完成！"
                    handler.sendMessage(msg)
                }
            } else {
                //撤销
                wxReverse(lastResult["err_code_des"], data, wxPay, msg, handler)
            }
        } catch (e: Exception) {
            //崩溃了，执行查询
            wxReverse(e.message, data, wxPay, msg, handler)
        }
        return if (lastResult != null) {
            lastResult as HashMap<String, String>
            lastResult.put("seq", "01")
            lastResult
        } else {
            null
        }
    }

    private fun doOrderQuery(data: HashMap<String, String>, wxPay: WXPay, msg: Message, handler: MyHandler): Map<String, String>? {
        var lastResult: Map<String, String>? = null
        val timeoutMs = 100000
        try {
            val finalTradeNo = HashMap<String, String>()
            finalTradeNo.put("out_trade_no", data["out_trade_no"]!!)
            for (i in 0..5) {
                //微信建议休眠十秒,在此休眠5s，避免多余等待
                Thread.sleep(5000)
                lastResult = wxPay.orderQuery(finalTradeNo, timeoutMs, timeoutMs * 3)
                if (lastResult[rtnCode] == success) {
                    if (lastResult[rstCode] == success) {
                        val tradeState = lastResult["trade_state"]
                        if (tradeState == success) {
                            //交易完成
                            return lastResult
                        } else {
                            //用户还在支付
                            if (tradeState == userPaying) {
                                continue
                            } else {
                                return if (tradeState == "REVOKED") {
                                    msg.obj = "已撤销当前订单！${lastResult["trade_state_desc"]}"
                                    msg.what = ERROR
                                    handler.sendMessage(msg)
                                    lastResult = null
                                    lastResult
                                } else {
                                    wxReverse(lastResult["trade_state_desc"], data, wxPay, msg, handler)
                                    lastResult = null
                                    lastResult
                                }
                            }
                        }
                    } else {
                        if (lastResult[errCode] == systemError) {
                            continue
                        } else {
                            //出错了做撤销处理
                            wxReverse(lastResult["err_code_des"], data, wxPay, msg, handler)
                            lastResult = null
                            return lastResult
                        }
                    }
                } else {
                    //报错了,做撤销处理
                    wxReverse(lastResult["return_msg"], data, wxPay, msg, handler)
                    lastResult = null
                    return lastResult
                }
            }
            //超时了没结果就去做撤销
            wxReverse("交易超时", data, wxPay, msg, handler)
            lastResult = null
            return lastResult
        } catch (e: Exception) {
            //查询出错，去做撤销
            wxReverse(e.message, data, wxPay, msg, handler)
            lastResult = null
            return lastResult
        }
    }

    /**
     * 撤销订单
     * @param outTradeNo 商户订单号
     * @param wxPay 微信的通信工具
     */
    private fun wxReverse(errorMessage: String?, outTradeNo: HashMap<String, String>, wxPay: WXPay, msg: Message, handler: MyHandler) {
        try {
            //微信建议要15s后调用，在这里用1秒试试
            Thread.sleep(1000)
            //创建撤销需要的数据，只需要商户订单号
            val finalTradeNo = HashMap<String, String>()
            finalTradeNo.put("out_trade_no", outTradeNo["out_trade_no"]!!)
            for (i in 0..2) {
                val result = wxPay.reverse(finalTradeNo)
                if (result[rtnCode] == success) {
                    if (result[rstCode] == success) {
                        //成功撤销
                        msg.obj = "因$errorMessage 已撤销此次交易！"
                        msg.what = ERROR
                        handler.sendMessage(msg)
                        return
                    } else {
                        if (result["err_code_des"] == "SYSTEMERROR" || result["recall"] == "Y") {
                            //未知错误或需要重调就去重新调用
                            Thread.sleep(5000)
                            continue
                        } else {
                            //出错直接丢出
                            msg.obj = result["err_code_des"]
                            msg.what = MyHandler.ERROR
                            handler.sendMessage(msg)
                            return
                        }
                    }
                } else {
                    //从一开始就错了
                    msg.obj = result["return_msg"]
                    msg.what = MyHandler.ERROR
                    handler.sendMessage(msg)
                    return
                }
            }
            //超时
            msg.obj = "连接超时，因未知原因交易失败，无法判断是否成功撤销，请联系系统部"
            msg.what = MyHandler.ERROR
            handler.sendMessage(msg)
        } catch (e: Exception) {
            //撤销出错了
            msg.obj = "交易失败，无法判断是否成功撤销 ${e.message}"
            msg.what = ERROR
            handler.sendMessage(msg)
        }
    }

    fun getWXData(data: PayMsgBean, code: String, outTradeNo: String): HashMap<String, String> {
        val result = HashMap<String, String>()
        result.put("device_info", MyApplication.getOnlyid())
        result.put("body", "喜士多-${data.storeName}-线下门店支付")
        result.put("out_trade_no", outTradeNo)
        result.put("total_fee", (data.payAmount * 100).toInt().toString())
        result.put("spbill_create_ip", MyApplication.getMyIP())
        result.put("auth_code", code)
        return result
    }

    /**
     * 存粹查询订单
     *//*
    fun doOrderQuery(data: HashMap<String, String>, wxPay: WXPay, msg: Message, handler: MyHandler): Map<String, String> {
        var lastResult: Map<String, String>? = null
        val timeoutMs = 10000
        try {
            for (i in 0..5) {
                lastResult = wxPay.orderQuery(data, timeoutMs, timeoutMs * 3)
                if (lastResult[rtnCode] == success) {
                    if (lastResult[rstCode] == success) {
                        val tradeState = lastResult["trade_state"]
                        if (tradeState == success) {
                            return lastResult
                        } else {
                            msg.obj = lastResult["trade_state_desc"]
                            msg.obj = ERROR
                            handler.sendMessage(msg)
                            break
                        }
                    } else {
                        //系统异常，重试
                        if (lastResult[errCode] == systemError) {
                            Thread.sleep(5000)
                            continue
                        }else if (lastResult[errCode]=="ORDERNOTEXIST"){
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
    }*/
}