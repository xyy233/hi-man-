package com.cstore.zhiyazhang.cstorepay.util

import com.cstore.zhiyazhang.cstorepay.R
import com.cstore.zhiyazhang.cstorepay.bean.PayMsgBean

/**
 * Created by zhiya.zhang
 * on 2018/6/21 16:27.
 */
object QRCodeResolve {
    /**
     * 解析扫码内容返回支付信息对象
     */
    fun getPayMsg(msg: String): PayMsgBean? {
        val data = msg.split("\n")
        if (data.size < 13) {
            MyToast.getLongToast(MyApplication.instance().applicationContext.getString(R.string.not_read_pay_msg))
            return null
        }
        try {
            return PayMsgBean(
                    data[0],
                    data[1],
                    data[2],
                    data[3],
                    data[4],
                    data[5],
                    data[6],
                    data[7],
                    data[8],
                    data[9],
                    data[10],
                    data[11],
                    data[12].toDouble()
            )
        } catch (e: Exception) {
            MyToast.getLongToast(MyApplication.instance().applicationContext.getString(R.string.not_read_pay_msg))
            return null
        }
    }
}