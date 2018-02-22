package com.cstore.zhiyazhang.cstoremanagement.model.paiban

import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.bean.PaibanBean
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.GsonUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.OnlyMyHandler.ERROR
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.OnlyMyHandler.SUCCESS
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import java.util.*

/**
 * Created by zhiya.zhang
 * on 2018/2/8 11:32.
 */
class PaibanModel {

    /**
     * 根据日期得到时间
     */
    fun getDataByDate(beginDate: String, endDate:String, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val sql = MySql.getPaibanData(beginDate, endDate)
            val sqlResult = SocketUtil.initSocket(ip, sql).inquire()
            val result = ArrayList<PaibanBean>()
            if (sqlResult == "[]" || sqlResult == "") {
                msg.obj = result
                msg.what = SUCCESS
                handler.sendMessage(msg)
                return@Runnable
            }
            try {
                result.addAll(GsonUtil.getPaiban(sqlResult))
            } catch (e: Exception) {
            }
            if (result.isEmpty()) {
                msg.obj = sqlResult
                msg.what = ERROR
            } else {
                msg.obj = result
                msg.what = SUCCESS
            }
            handler.sendMessage(msg)
        }).start()
    }

    fun createPaiban(data: PaibanBean, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val sql = MySql.createPaiban(data)
            val sqlResult = SocketUtil.initSocket(ip, sql).inquire()
            if (sql == "1") {
                msg.what = SUCCESS
            } else {
                msg.what = ERROR
            }
            msg.obj = sqlResult
            handler.sendMessage(msg)
        }).start()
    }

    fun updatePaiban(data: PaibanBean, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val sql = MySql.updatePaiban(data)
            val sqlResult = SocketUtil.initSocket(ip, sql).inquire()
            if (sql == "1") {
                msg.what = SUCCESS
            } else {
                msg.what = ERROR
            }
            msg.obj = sqlResult
            handler.sendMessage(msg)
        }).start()
    }

    fun deletePaiban(data: PaibanBean, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val sql = MySql.deletePaiban(data)
            val sqlResult = SocketUtil.initSocket(ip, sql).inquire()
            if (sql == "1") {
                msg.what = SUCCESS
            } else {
                msg.what = ERROR
            }
            msg.obj = sqlResult
            handler.sendMessage(msg)
        }).start()
    }


}