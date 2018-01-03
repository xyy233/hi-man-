package com.cstore.zhiyazhang.cstoremanagement.model.returnpurchase

import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.bean.ReturnExpiredBean
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.GsonUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.OnlyMyHandler.ERROR
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.OnlyMyHandler.SUCCESS
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil.Companion.judgmentNull

/**
 * Created by zhiya.zhang
 * on 2018/1/3 10:51.
 */
class ReturnExpiredModel : ReturnExpiredInterface {
    override fun getAll(handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val sql = MySql.expiredReturnGetAll()
            val result = SocketUtil.initSocket(ip, sql).inquire()
            if (!judgmentIsNull(result, msg, handler)) return@Runnable
            val reb = ArrayList<ReturnExpiredBean>()
            try {
                reb.addAll(GsonUtil.getReturnExpired(result))
            } catch (e: Exception) {
            }
            if (reb.isEmpty()) {
                msg.obj = result
                msg.what = ERROR
            } else {
                msg.obj = reb
                msg.what = SUCCESS
            }
            handler.sendMessage(msg)
        }).start()
    }

    override fun searchExpiredCommodity(data: String, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val sql = MySql.expiredReturnGetCommodity(data)
            val result = SocketUtil.initSocket(ip, sql).inquire()
            if (!judgmentNull(result, msg, handler)) return@Runnable
            val reb = ArrayList<ReturnExpiredBean>()
            try {
                reb.addAll(GsonUtil.getReturnExpired(result))
            } catch (e: Exception) {
            }
            if (reb.isEmpty()) {
                msg.obj = result
                msg.what = ERROR
            } else {
                msg.obj = reb
                msg.what = SUCCESS
            }
            handler.sendMessage(msg)
        }).start()
    }

    private fun judgmentIsNull(data: String, msg: Message, handler: MyHandler): Boolean {
        return if (data == "[]") {
            msg.obj = ArrayList<ReturnExpiredBean>()
            msg.what = SUCCESS
            handler.sendMessage(msg)
            false
        } else true
    }

}

interface ReturnExpiredInterface {
    fun getAll(handler: MyHandler)

    fun searchExpiredCommodity(data: String, handler: MyHandler)
}