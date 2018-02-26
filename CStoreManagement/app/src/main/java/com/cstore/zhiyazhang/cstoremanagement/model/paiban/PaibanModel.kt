package com.cstore.zhiyazhang.cstoremanagement.model.paiban

import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.bean.PaibanBean
import com.cstore.zhiyazhang.cstoremanagement.bean.SortPaiban
import com.cstore.zhiyazhang.cstoremanagement.bean.UtilBean
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.GsonUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.OnlyMyHandler.ERROR
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.OnlyMyHandler.SUCCESS
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil

/**
 * Created by zhiya.zhang
 * on 2018/2/8 11:32.
 */
class PaibanModel {

    /**
     * 根据日期得到数据
     */
    fun getDataByDate(beginDate: String, endDate: String, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val isPaiban = getIsPaiban(ip, msg, handler) ?: return@Runnable
            val paibanData = getPaibanData(beginDate, endDate, ip, msg, handler, isPaiban) ?: return@Runnable
            paibanData.sortBy { it.employeeId }
            val result = ArrayList<SortPaiban>()
            var id = ""
            //循环所有数据
            for (pb in paibanData) {
                //如果记录id和循环id不同就代表是新的人需要添加
                if (id != pb.employeeId) {
                    id = pb.employeeId
                    //查询到这个新的人的所有数据添加进去
                    val sp = SortPaiban(paibanData.filter { it.employeeId == id } as ArrayList<PaibanBean>)
                    result.add(sp)
                }
            }
            msg.obj = result
            msg.what = SUCCESS
            handler.sendMessage(msg)
        }).start()
    }

    /**
     * 获得店长是否能排班
     */
    private fun getIsPaiban(ip: String, msg: Message, handler: MyHandler): String? {
        val sql = MySql.getIsPaiban
        val sqlResult = SocketUtil.initSocket(ip, sql).inquire()
        val result = ArrayList<UtilBean>()
        if (sqlResult == "[]" || sqlResult == "") {
            return null
        }
        try {
            result.addAll(GsonUtil.getUtilBean(sqlResult))
        } catch (e: Exception) {
        }
        if (result.isEmpty()) {
            msg.obj = sqlResult
            msg.what = ERROR
            handler.sendMessage(msg)
            return null
        }
        return result[0].value
    }

    /**
     * 获得排班数据
     */
    private fun getPaibanData(beginDate: String, endDate: String, ip: String, msg: Message, handler: MyHandler, isPaiban: String): ArrayList<PaibanBean>? {
        val sql = MySql.getPaibanData(beginDate, endDate, isPaiban)
        val sqlResult = SocketUtil.initSocket(ip, sql).inquire()
        val result = ArrayList<PaibanBean>()
        if (sqlResult == "[]" || sqlResult == "") {
            return result
        }
        try {
            result.addAll(GsonUtil.getPaiban(sqlResult))
        } catch (e: Exception) {
        }
        if (result.isEmpty()) {
            msg.obj = sqlResult
            msg.what = ERROR
            handler.sendMessage(msg)
            return null
        }
        return result
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