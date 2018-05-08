package com.cstore.zhiyazhang.cstoremanagement.model.inverror

import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.bean.InvErrorBean
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.GsonUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.Companion.ERROR
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.Companion.SUCCESS
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import java.text.DecimalFormat

/**
 * Created by zhiya.zhang
 * on 2018/2/2 16:37.
 */
class InvErrorMoel {

    /**
     * 直接得到Fragment
     */
    fun getAllFragment(handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            if (!invErrorFirst(ip, msg, handler)) return@Runnable
            val allData = getAllData(ip, msg, handler) ?: return@Runnable
            msg.obj=allData
            msg.what=SUCCESS
            handler.sendMessage(msg)
        }).start()
    }

    /**
     * 得到数据
     */
    private fun getAllData(ip: String, msg: Message, handler: MyHandler): ArrayList<InvErrorBean>? {
        val allData = ArrayList<InvErrorBean>()
        allData.addAll(getArrayData(ip, msg, handler, MySql.negativeInv) ?: return null)
        allData.addAll(getArrayData(ip, msg, handler, MySql.outL1) ?: return null)
        allData.addAll(getArrayData(ip, msg, handler, MySql.genOut) ?: return null)
        allData.addAll(getArrayData(ip, msg, handler, MySql.noSales) ?: return null)
        allData.addAll(getArrayData(ip, msg, handler, MySql.highInv) ?: return null)
        allData.addAll(getArrayData(ip, msg, handler, MySql.errorStatus) ?: return null)
        //取消掉商品卡提示
        //allData.addAll(getArrayData(ip, msg, handler, MySql.needRemove) ?: return null)
        val df = DecimalFormat("#.00")
        //此处处理日期有可能为空，不管，处理后就强制不为空了
        allData.forEach {
            it.dms=df.format(it.dms).toDouble()
            if (it.saleDate == null && it.saleDate == "null" || it.saleDate == "" || it.saleDate == "700101") it.saleDate = ""
            if (it.dlvDate == null && it.dlvDate == "null" || it.dlvDate == "" || it.dlvDate == "700101") it.dlvDate = ""
            if (it.marketDate == null && it.marketDate == "null" || it.marketDate == "" || it.marketDate == "700101") it.marketDate = ""
        }
        return allData
    }

    /**
     * 获得具体数据
     * @param sql 获得数据的sql语句
     */
    private fun getArrayData(ip: String, msg: Message, handler: MyHandler, sql: String): ArrayList<InvErrorBean>? {
        val sqlResult = SocketUtil.initSocket(ip, sql).inquire()
        if (sqlResult == "[]" || sqlResult == "") return ArrayList()
        return try {
            GsonUtil.getInvError(sqlResult)
        } catch (e: Exception) {
            msg.obj = e.message.toString()
            msg.what = ERROR
            handler.sendMessage(msg)
            null
        }
    }

    /**
     * 执行查询第一步sql操作
     */
    private fun invErrorFirst(ip: String, msg: Message, handler: MyHandler): Boolean {
        val sqlResult = SocketUtil.initSocket(ip, MySql.invErrorFirst).inquire()
        var result = -1
        try {
            result = sqlResult.toInt()
        } catch (e: Exception) {
            msg.obj = sqlResult
            msg.what = ERROR
            handler.sendMessage(msg)
            return false
        }
        return result != -1
    }
}
