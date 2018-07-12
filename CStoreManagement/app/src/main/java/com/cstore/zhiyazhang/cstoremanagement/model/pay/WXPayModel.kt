package com.cstore.zhiyazhang.cstoremanagement.model.pay

import android.content.Context
import android.os.Message
import android.util.Log
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.PosBean
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.bean.WXPaySqlBean
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.sql.WXPayDao
import com.cstore.zhiyazhang.cstoremanagement.utils.GsonUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.Companion.ERROR
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.Companion.SUCCESS
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.wechat.MyWXUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.wechat.WXPayConfigImpl
import com.cstore.zhiyazhang.cstoremanagement.view.pay.PayCollectActivity
import com.github.wxpay.sdk.WXPay

/**
 * Created by zhiya.zhang
 * on 2017/11/15 16:38.
 */
class WXPayModel(context: Context) : PayMoneyInterface {
    private val dao = WXPayDao(context)

    companion object {
        /**
         * 得到写入本机数据库的对象
         */
        fun getWXPayBean(posBean: PosBean, payData: Map<String, String>, theStep: Int, errorMessage: String): WXPaySqlBean {
            val outTradeNo = payData["out_trade_no"]!!
            val storeId = User.getUser().storeId
            val assPos = posBean.assPos
            val nextTranNo = posBean.nextTranNo
            val totalFee = payData["total_fee"]!!.toDouble() / 100
            val transactionId = payData["transaction_id"]!!
            var seq = payData["seq"]
            if (seq == null) {
                seq = "01"
            }
            val openId = if (payData["openid"] == null) {
                "null"
            } else {
                payData["openid"]!!
            }
            var couponFee = 0.0
            if (payData["coupon_fee"] != null) {
                couponFee = payData["coupon_fee"]!!.toDouble() / 100
            }
            return WXPaySqlBean(outTradeNo, transactionId, MyApplication.getOnlyid(), totalFee, storeId, assPos, nextTranNo, seq, openId, couponFee, theStep, errorMessage, 0, 0, 0, null)
        }

        /**
         * 得到下单商户订单号，-1就是出错
         */
        fun getShoppingId(activity: PayCollectActivity, ip: String, msg: Message, handler: MyHandler): String {
            val sql = MySql.callPayShopping() + MySql.getShoppingId()
            val shoppingIdResult = SocketUtil.initSocket(ip, sql).inquire()
            if (shoppingIdResult == "" || shoppingIdResult == "[]") {
                msg.obj = MyApplication.instance().applicationContext.getString(R.string.noMessage)
                msg.what = MyHandler.ERROR
                handler.sendMessage(msg)
                return "-1"
            }
            val ids = ArrayList<PosBean>()
            try {
                ids.addAll(GsonUtil.getPos(shoppingIdResult))
            } catch (e: Exception) {
            }
            return if (ids.isEmpty()) {
                msg.obj = shoppingIdResult
                msg.what = MyHandler.ERROR
                handler.sendMessage(msg)
                "-1"
            } else {
                if (ids.size != 0) {
                    activity.setPos(ids[0])
                    User.getUser().storeId + ids[0].assPos + ids[0].nextTranNo + "01"
                } else {
                    msg.obj = MyApplication.instance().applicationContext.getString(R.string.noMessage)
                    msg.what = MyHandler.ERROR
                    handler.sendMessage(msg)
                    "-1"
                }
            }
        }
    }

    override fun wechatCollectMoney(activity: PayCollectActivity, code: String, money: Double, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler))return@Runnable
            val outTradeNo = getShoppingId(activity, ip, msg, handler)
            if (outTradeNo == "-1") {
                return@Runnable
            }
            try {
                val data = MyWXUtil.getWXData(code, money, outTradeNo)
                //用不了sandbox，sandbox需要他的key
                val wxPay = WXPay(WXPayConfigImpl.getInstance())
                //得到微信的返回结果
                val wxResult = MyWXUtil.microPay(ip, activity.getPos(), dao, data, wxPay, msg, handler)
                //只有不为空且两个返回信息都为成功才是真的成功,这里不用管失败的情况，失败处理已经在内部处理过了
                if (wxResult != null && wxResult["return_code"] == "SUCCESS" && wxResult["result_code"] == "SUCCESS") {
                    payDone(activity.getPos(), wxResult, handler, msg, ip)
                }
            } catch (e: Exception) {
                msg.obj = e.message
                msg.what = MyHandler.ERROR
                handler.sendMessage(msg)
            }
        }).start()
    }

    override fun doOrderQuery(outTranNo: String, handler: MyHandler): Map<String, String> {
        val msg = Message()
        try {
            val wxPay = WXPay(WXPayConfigImpl.getInstance())
            val data = HashMap<String, String>()
            data.put("out_trade_no", outTranNo)
            return MyWXUtil.doOrderQuery(data, wxPay, msg, handler)
        } catch (e: Exception) {
            msg.obj = e.message
            msg.what = ERROR
            handler.sendMessage(msg)
        }
        return HashMap<String, String>()
    }

    fun wechatRefund(data: Map<String, String>, handler: MyHandler) {
        val msg = Message()
        val ip = MyApplication.getIP()
        if (!SocketUtil.judgmentIP(ip, msg, handler)) return
        try {
            val wxPay = WXPay(WXPayConfigImpl.getInstance())
            val wxResult = MyWXUtil.wxRefund(data, wxPay, msg, handler)
            if (wxResult.isEmpty()) return
            val sql = MySql.refoundCall(data["transaction_id"]!!, wxResult["refund_id"]!!, "微信")//MySql.createWXPayDone(activity.getPos(), wxResult, true) + MySql.updateAppPos
            val result = SocketUtil.initSocket(ip, sql).inquire()
            //如果是之前sql这里是等于2才是执行成功，存储过程无返回也不知道修改数据，所以是0
            if (result == "0") {
                //成功
                msg.obj = wxResult
                msg.what = SUCCESS
                handler.sendMessage(msg)
            } else {
                //失败
                msg.obj = "退款成功，保存数据失败：$result"
                msg.what = ERROR
                handler.sendMessage(msg)
            }
        } catch (e: Exception) {
            msg.obj = e.message
            msg.what = ERROR
            handler.sendMessage(msg)
        }
    }

    override fun wechatRefund(activity: PayCollectActivity, data: Map<String, String>, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            try {
                val wxPay = WXPay(WXPayConfigImpl.getInstance())
                val wxResult = MyWXUtil.wxRefund(data, wxPay, msg, handler)
                if (wxResult.isEmpty()) {
                    return@Runnable
                }
                /*不用更新pos信息了，现在使用存储过程执行，直接用上一个next_tranno操作
                //更新pos信息
                if (!updatePos(activity, ip, msg, handler)) {
                    return@Runnable
                }*/
                val sql = MySql.refoundCall(wxResult["transaction_id"]!!, wxResult["refund_id"]!!, "微信")//MySql.createWXPayDone(activity.getPos(), wxResult, true) + MySql.updateAppPos
                val result = SocketUtil.initSocket(ip, sql).inquire()
                //如果是之前sql这里是等于2才是执行成功，存储过程无返回也不知道修改数据，所以是0
                if (result == "0") {
                    //成功
                    msg.obj = wxResult
                    msg.what = SUCCESS
                    handler.sendMessage(msg)
                } else {
                    //失败
                    msg.obj = "退款成功，保存数据失败：$result"
                    msg.what = ERROR
                    handler.sendMessage(msg)
                }
            } catch (e: Exception) {
                msg.obj = e.message
                msg.what = ERROR
                handler.sendMessage(msg)
            }
        }).start()
    }

    /**
     * 更新app_pos并写入最新数据
     */
    private fun updatePos(activity: PayCollectActivity, ip: String, msg: Message, handler: MyHandler): Boolean {
        //先要更新app_pos的next_tranno再重新获得最新数据'
        val posSql = MySql.updateAss2() + MySql.getShoppingId()
        val posResult = SocketUtil.initSocket(ip, posSql).inquire()
        if (posResult == "" || posResult == "[]") {
            msg.obj = MyApplication.instance().applicationContext.getString(R.string.noMessage)
            msg.what = MyHandler.ERROR
            handler.sendMessage(msg)
            return false
        }
        val ids = ArrayList<PosBean>()
        try {
            ids.addAll(GsonUtil.getPos(posResult))
        } catch (e: Exception) {
        }
        return if (ids.isEmpty()) {
            msg.obj = posResult
            msg.what = MyHandler.ERROR
            handler.sendMessage(msg)
            false
        } else {
            if (ids.size != 0) {
                activity.setPos(ids[0])
                true
            } else {
                msg.obj = MyApplication.instance().applicationContext.getString(R.string.noMessage)
                msg.what = MyHandler.ERROR
                handler.sendMessage(msg)
                false
            }
        }
    }




    /**
     * 交易完成后的操作,这一步如果出错了也没事，不过要保存记录有机会就执行，因为钱是已经收到了的，只是往数据库中插入的时候失败了
     */
    private fun payDone(pos: PosBean, payData: Map<String, String>, handler: MyHandler, msg: Message, ip: String) {
        try {
            //先去执行存储过程把数据保存在posul_trandtl表中
            val oneSql = MySql.payDoneCall("微信", payData["total_fee"]!!.toDouble() / 100)
            val callResult = SocketUtil.initSocket(ip, oneSql).inquire()
            if (callResult == "0") {
                    //再单独保存在posul_weixin_detail表中
                    val twoSql = MySql.createWXPayDone(pos, payData, false)
                    val wxResult = SocketUtil.initSocket(ip, twoSql).inquire()
                    if (wxResult != "1") {
                        //保存sql失败了
                        val wxBean = getWXPayBean(pos, payData, 2, wxResult)
                        dao.insertSql(wxBean)
                    }
            } else {
                //存储过程就失败了
                val wxBean = getWXPayBean(pos, payData, 1, callResult)
                dao.insertSql(wxBean)
            }
            //不管是否保存失败，用户已经支付成功，直接返回成功信息
            msg.obj = payData
            msg.what = MyHandler.SUCCESS
            handler.sendMessage(msg)
        } catch (e: Exception) {
            Log.e("WXPayModel", e.message)
        }
    }


    override fun deleteBasket(handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val sqlResult = deleteBasket(ip)
            if (sqlResult == "0") {
                //正确
                msg.obj = sqlResult
                msg.what = SUCCESS
                handler.sendMessage(msg)
                return@Runnable
            }
            msg.obj = sqlResult
            msg.what = ERROR
            handler.sendMessage(msg)
        }).start()
    }

    /**
     * 清空basket实际操作，单独拉出来是为了方便在线程里执行
     */
    private fun deleteBasket(ip: String): String {
        val sql = MySql.deleteBasket
        return SocketUtil.initSocket(ip, sql).inquire()
    }

    override fun updateAppPos(handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val sql = MySql.updateAppPos
            val sqlResult = SocketUtil.initSocket(ip, sql).inquire()
            if (sqlResult == "0") {
                //正确
                msg.obj = sqlResult
                msg.what = SUCCESS
                handler.sendMessage(msg)
                return@Runnable
            }
            msg.obj = sqlResult
            msg.what = ERROR
            handler.sendMessage(msg)
        }).start()
    }
}

interface PayMoneyInterface {
    /**
     * 微信收款
     */
    fun wechatCollectMoney(activity: PayCollectActivity, code: String, money: Double, handler: MyHandler)

    /**
     * 微信退款
     */
    fun wechatRefund(activity: PayCollectActivity, data: Map<String, String>, handler: MyHandler)

    /**
     * 扫描后取消交易，清空扫描的商品，此时需要把表里的数据也删掉
     */
    fun deleteBasket(handler: MyHandler)

    /**
     * 撤销订单后需要修改app_pos的next_tranno，不然再次提交订单微信会报错订单号重复
     */
    fun updateAppPos(handler: MyHandler)

    /**
     * 查询订单
     */
    fun doOrderQuery(outTranNo: String, handler: MyHandler): Map<String, String>
}