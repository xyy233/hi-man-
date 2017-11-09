package com.cstore.zhiyazhang.cstoremanagement.model.pay

import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.bean.PayBean
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.GsonUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.OnlyMyHandler.ERROR1
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.OnlyMyHandler.SUCCESS
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil

/**
 * Created by zhiya.zhang
 * on 2017/11/8 14:03.
 */
class PayModel : PayInterface {
    override fun wechatCollectMoney(code: String, money: Double, handler: MyHandler) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun alipayCollectMoney(code: String, money: Double, handler: MyHandler) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCommodity(barCode: String, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val sql = getCommoditySql(barCode)
            val result = SocketUtil.initSocket(ip, sql).inquire()
            if (!SocketUtil.judgmentNull(result, msg, handler)) return@Runnable
            val items = ArrayList<PayBean>()
            try {
                items.addAll(GsonUtil.getPay(result))
            } catch (e: Exception) {
            }
            if (items.isEmpty()) {
                msg.obj = result
                msg.what = ERROR1
            } else {
                msg.obj = items
                msg.what = SUCCESS
            }
            handler.sendMessage(msg)
        }).start()
    }

    private fun getCommoditySql(barCode: String): String {
        val result = StringBuilder()
        result.append(MySql.affairHeader)
        result.append(MySql.insertShoppingBasket(barCode, 1))
        result.append(MySql.getShoppingBasket(barCode))
        result.append(MySql.affairFoot)
        return result.toString()
    }

}

interface PayInterface {
    /**
     * 微信收款
     */
    fun wechatCollectMoney(code: String, money: Double, handler: MyHandler)

    /**
     * 支付宝收款
     */
    fun alipayCollectMoney(code: String, money: Double, handler: MyHandler)

    /**
     * 得到商品
     */
    fun getCommodity(barCode: String, handler: MyHandler)
}