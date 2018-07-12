package com.cstore.zhiyazhang.cstoremanagement.model.pay

import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.bean.CashPaySqlBean
import com.cstore.zhiyazhang.cstoremanagement.bean.PosBean
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.sql.CashPayDao
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.Companion.SUCCESS
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import com.cstore.zhiyazhang.cstoremanagement.view.pay.PayCollectActivity

/**
 * Created by zhiya.zhang
 * on 2017/12/20 12:06.
 */
class CashPayModel : CashPayInterface {

    /**
     * 得到写入本机数据库的对象
     */
    companion object {
        fun getCashPayBean(outTradeNo: String, totalFee: Double, pos: PosBean, theStep: Int, errorMessage: String): CashPaySqlBean {
            val storeId = User.getUser().storeId
            val assPos = pos.assPos
            val nextTranNo = pos.nextTranNo
            return CashPaySqlBean(outTradeNo, MyApplication.getOnlyid(), totalFee, storeId, assPos, nextTranNo, "01", theStep, errorMessage, 0, 0, 0, null)
        }
    }

    override fun cashCollect(dao: CashPayDao, activity: PayCollectActivity, money: Double, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            //写入pos信息到activity
            val outTranNo = WXPayModel.getShoppingId(activity, ip, msg, handler)
            if (outTranNo == "-1") return@Runnable
            val sql = MySql.payDoneCall("现金", money)
            val callResult = SocketUtil.initSocket(ip, sql).inquire()
            if (callResult != "0") {
                //失败
                val cashBean = getCashPayBean(outTranNo, money, activity.getPos(), 1, callResult)
                dao.insertSql(cashBean)
            }
            msg.obj = callResult
            msg.what = SUCCESS
            handler.sendMessage(msg)
        }).start()
    }

}

interface CashPayInterface {
    /**
     * 现金收款
     */
    fun cashCollect(dao: CashPayDao, activity: PayCollectActivity, money: Double, handler: MyHandler)

    /**
     * 退款先不做
     */
}