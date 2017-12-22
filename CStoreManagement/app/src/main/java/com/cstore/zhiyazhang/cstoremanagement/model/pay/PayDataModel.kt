package com.cstore.zhiyazhang.cstoremanagement.model.pay

import com.cstore.zhiyazhang.cstoremanagement.bean.CashPaySqlBean
import com.cstore.zhiyazhang.cstoremanagement.bean.WXPaySqlBean
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.sql.CashPayDao
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.sql.WXPayDao
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.wechat.WXPayConfigImpl
import com.github.wxpay.sdk.WXPay

/**
 * Created by zhiya.zhang
 * on 2017/12/18 15:52.
 */
class PayDataModel : PayDataInterface {
    override fun goWXData(dao: WXPayDao, bean: WXPaySqlBean, listener: MyListener) {
        val ip = MyApplication.getIP()
        try {
            startWXStep(dao, bean, ip, listener)
            listener.listenerSuccess(bean)
        } catch (e: Exception) {
            stepError(e.message.toString(), dao, bean, listener)
        }
    }

    override fun goCashData(dao: CashPayDao, bean: CashPaySqlBean, listener: MyListener) {
        val ip = MyApplication.getIP()
        try {
            startCashStep(dao, bean, ip, listener)
            listener.listenerSuccess(bean)
        } catch (e: Exception) {
            stepError(e.message.toString(), dao, bean, listener)
        }
    }

    /**
     * 开始执行微信操作步骤,1和3内部成功递归
     */
    private fun startWXStep(dao: WXPayDao, bean: WXPaySqlBean, ip: String, listener: MyListener) {
        //未完成的才去执行
        if (bean.isDone == 0) {
            when (bean.theStep) {
                1 -> {
                    //微信收款完成执行存储过程
                    firstStep(dao, bean, ip, listener)
                }
                2 -> {
                    //单独保存在微信记录中
                    secondStep(dao, bean, ip, listener)
                }
                3 -> {
                    //向微信提交撤销订单
                    thirdStep(dao, bean, ip, listener)
                }
                4 -> {
                    //修改app_pos和清空shopping_basket的存储过程
                    fourthStep(dao, bean, ip, listener)
                }
            }
        }
    }

    private fun startCashStep(dao: CashPayDao, bean: CashPaySqlBean, ip: String, listener: MyListener) {
        if (bean.isDone == 0) {
            when (bean.theStep) {
                1 -> {
                    firstStep(dao, bean, ip, listener)
                }
            }
        }
    }

    /**
     * 执行步骤4的操作
     * 修改app_pos和清空shopping_basket的存储过程
     */
    private fun fourthStep(dao: WXPayDao, bean: WXPaySqlBean, ip: String, listener: MyListener) {
        val result = SocketUtil.initSocket(ip, MySql.reverseCall).inquire()
        if (result == "0") {
            //完成
            bean.isDone = 1
            dao.updateSQL(bean)
        } else {
            stepError(result, dao, bean, listener)
        }
    }

    /**
     * 执行步骤3的操作
     * 向微信提交撤销订单
     */
    private fun thirdStep(dao: WXPayDao, bean: WXPaySqlBean, ip: String, listener: MyListener) {
        val wxPay = WXPay(WXPayConfigImpl.getInstance())
        val tradeNo = HashMap<String, String>()
        tradeNo.put("out_trade_no", bean.outTradeNo)
        for (i in 0..2) {
            val result = wxPay.reverse(tradeNo)
            if (result["return_code"] == "SUCCESS") {
                if (result["result_code"] == "SUCCESS") {
                    //成功
                    bean.theStep = 4
                    startWXStep(dao, bean, ip, listener)
                } else {
                    if (result["err_code_des"] == "SYSTEMERROR" || result["recall"] == "Y") {
                        //未知错误或需要重调就去重新调用
                        Thread.sleep(5000)
                        continue
                    } else {
                        //出错
                        stepError(result["err_code_des"]!!, dao, bean, listener)
                    }
                }
            } else {
                //一开始就错
                stepError(result["return_msg"]!!, dao, bean, listener)
            }
        }
        //超时或连续三次未知
        stepError("超时或连续三次未知", dao, bean, listener)
    }

    /**
     * 执行步骤2的操作
     * 单独保存在微信记录中
     */
    private fun secondStep(dao: WXPayDao, bean: WXPaySqlBean, ip: String, listener: MyListener) {
        val result = SocketUtil.initSocket(ip, MySql.createWXPayDone(bean)).inquire()
        if (result == "1") {
            //执行第二步成功，结束
            bean.isDone = 1
            dao.updateSQL(bean)
        } else {
            //出错记录数据吐错
            stepError(result, dao, bean, listener)
        }
    }

    /**
     * 执行步骤1的操作
     * 微信收款完成执行存储过程
     */
    private fun firstStep(dao: WXPayDao, bean: WXPaySqlBean, ip: String, listener: MyListener) {
        val result = SocketUtil.initSocket(ip, MySql.payDoneCall("微信", bean.totalFee)).inquire()
        if (result == "0") {
            //执行第一步成功执行第二步
            bean.theStep = 2
            dao.updateSQL(bean)
            startWXStep(dao, bean, ip, listener)
        } else {
            stepError(result, dao, bean, listener)
        }
    }

    private fun firstStep(dao: CashPayDao, bean: CashPaySqlBean, ip: String, listener: MyListener) {
        val result = SocketUtil.initSocket(ip, MySql.payDoneCall("现金", bean.totalFee)).inquire()
        if (result == "0") {
            bean.isDone = 1
            dao.updateSQL(bean)
        } else {
            stepError(result, dao, bean, listener)
        }
    }

    /**
     * 出错后操作
     */
    private fun stepError(message: String, dao: WXPayDao, bean: WXPaySqlBean, listener: MyListener) {
        //出错记录数据,尝试次数+1
        bean.uploadCount++
        bean.errorMessage += ",$message"
        dao.updateSQL(bean)
        //吐错
        listener.listenerOther(bean)
    }

    private fun stepError(message: String, dao: CashPayDao, bean: CashPaySqlBean, listener: MyListener) {
        bean.uploadCount++
        bean.errorMessage += ",$message"
        dao.updateSQL(bean)
        listener.listenerOther(bean)
    }

}

interface PayDataInterface {
    /**
     * 处理微信数据
     */
    fun goWXData(dao: WXPayDao, bean: WXPaySqlBean, listener: MyListener)

    /**
     * 处理现金数据
     */
    fun goCashData(dao: CashPayDao, bean: CashPaySqlBean, listener: MyListener)
}