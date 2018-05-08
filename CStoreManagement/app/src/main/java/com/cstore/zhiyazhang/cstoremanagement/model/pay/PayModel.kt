package com.cstore.zhiyazhang.cstoremanagement.model.pay

import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.PayBean
import com.cstore.zhiyazhang.cstoremanagement.bean.UtilBean
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.GsonUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.Companion.ERROR
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.Companion.SUCCESS
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil

/**
 * Created by zhiya.zhang
 * on 2017/11/8 14:03.
 */
class PayModel : PayInterface {

    override fun getCommodity(barCode: String, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            getCommodity(ip, msg, handler, barCode, 1)
        }).start()
    }

    override fun searchCommodity(key: String, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val barCode = SocketUtil.initSocket(ip, MySql.searchPlunumber(key)).inquire()
            if (!SocketUtil.judgmentNull(barCode, msg, handler)) return@Runnable
            val barCodes = ArrayList<UtilBean>()
            try {
                barCodes.addAll(GsonUtil.getUtilBean(barCode))
            } catch (e: Exception) {
            }
            if (barCodes.isEmpty()) {
                msg.obj = barCode
                msg.what = ERROR
                handler.sendMessage(msg)
                return@Runnable
            }
            if (barCodes[0].value == null) {
                msg.obj = MyApplication.instance().applicationContext.getString(R.string.noMessage)
                msg.what = ERROR
                handler.sendMessage(msg)
                return@Runnable
            }
            getCommodity(ip, msg, handler, barCodes[0].value!!, 1)
        }).start()
    }

    override fun updateCommodity(barCode: String, count: Int, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            getCommodity(ip, msg, handler, barCode, count)
        }).start()
    }

    private fun getCommoditySql(barCode: String, count: Int): String {
        val result = StringBuilder()
        result.append(MySql.insertShoppingBasket(barCode, count))
        result.append(MySql.callShoppingBasket())
        result.append(MySql.getShoppingBasket(barCode))
        return result.toString()
    }

    /**
     * 得到商品
     */
    private fun getCommodity(ip: String, msg: Message, handler: MyHandler, barCode: String, count: Int) {
        val sql = getCommoditySql(barCode, count)
        val result = SocketUtil.initSocket(ip, sql).inquire()
        if (!SocketUtil.judgmentNull(result, msg, handler)) return
        val items = ArrayList<PayBean>()
        try {
            items.addAll(GsonUtil.getPay(result))
        } catch (e: Exception) {
        }
        if (items.isEmpty()) {
            msg.obj = result
            msg.what = ERROR
        } else {
            msg.obj = items
            msg.what = MyHandler.SUCCESS
        }
        handler.sendMessage(msg)
    }

    override fun deleteData(handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val sql=MySql.deleteBasket
            val result=SocketUtil.initSocket(ip,sql).inquire()
            try {
                val count=result.toInt()
                //转换成int成功就是代表sql执行成功了
                msg.obj=count
                msg.what=SUCCESS
            }catch (e:Exception){
                //转换失败就是出错了
                msg.obj=e.message
                msg.what= ERROR
            }
            handler.sendMessage(msg)
        }).start()
    }
}

interface PayInterface {
    /**
     * 得到商品
     */
    fun getCommodity(barCode: String, handler: MyHandler)

    /**
     * 搜索得到商品
     */
    fun searchCommodity(key: String, handler: MyHandler)

    /**
     * 更新商品
     */
    fun updateCommodity(barCode: String, count: Int, handler: MyHandler)

    /**
     * 清空数据
     */
    fun deleteData(handler: MyHandler)
}