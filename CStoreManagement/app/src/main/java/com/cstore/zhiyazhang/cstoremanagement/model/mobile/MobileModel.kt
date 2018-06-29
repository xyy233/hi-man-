package com.cstore.zhiyazhang.cstoremanagement.model.mobile

import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.bean.*
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.GsonUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.Companion.ERROR
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.Companion.SUCCESS
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil

/**
 * Created by zhiya.zhang
 * on 2018/6/28 10:07.
 */
class MobileModel : MobileInterface {
    override fun getMobileData(handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val sql = MySql.getMobileData()
            val sqlResult = SocketUtil.initSocket(ip, sql).inquire()
            val result = ArrayList<MobileDetailBean>()
            if (sqlResult == "" || sqlResult == "[]") {
                msg.obj = result
                msg.what = SUCCESS
                handler.sendMessage(msg)
                return@Runnable
            }
            val jsonResult = ArrayList<MobileDetailGsonBean>()
            try {
                jsonResult.addAll(GsonUtil.getMobileDetail(sqlResult))
            } catch (e: Exception) {
            }
            if (jsonResult.size > 0) {
                result.addAll(MobileDetailBean.getMobileDetailByMobileGson(jsonResult))
                msg.obj = result
                msg.what = SUCCESS
            } else {
                msg.obj = sqlResult
                msg.what = ERROR
            }
            handler.sendMessage(msg)
        }).start()
    }

    override fun getMobileItem(code: String, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val sql = MySql.getMobileItem(code)
            val sqlResult = SocketUtil.initSocket(ip, sql).inquire()
            if (!SocketUtil.judgmentNull(sqlResult, msg, handler)) return@Runnable
            val result = ArrayList<MobilePluBean>()
            try {
                result.addAll(GsonUtil.getMobilePlu(sqlResult))
            } catch (e: Exception) {
            }
            if (result.size > 0) {
                msg.obj = result[0]
                msg.what = SUCCESS
            } else {
                msg.obj = sqlResult
                msg.what = ERROR
            }
            handler.sendMessage(msg)
        }).start()
    }

    override fun getMobileGondola(handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val sql = MySql.getMobileGondola()
            val sqlResult = SocketUtil.initSocket(ip, sql).inquire()
            val jsonResult = ArrayList<MobileGondolaGsonBean>()
            try {
                jsonResult.addAll(GsonUtil.getMobileGondola(sqlResult))
            } catch (e: Exception) {
            }
            if (jsonResult.size > 0) {
                val result = ArrayList<MobileGondolaBean>()
                result.addAll(MobileGondolaBean.getMobileGondolaByMobileGson(jsonResult))
                msg.obj = result
                msg.what = SUCCESS
            } else {
                msg.obj = sqlResult
                msg.what = ERROR
            }
            handler.sendMessage(msg)
        }).start()
    }

    override fun getMobilInsert(data: MobileDetailBean, mjbRtnMsg: String, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val tranNo = getMaxTranNo(msg, ip, handler)
            if (tranNo == "ERROR") return@Runnable
            data.tranNo = tranNo
            val sql = MySql.getMobileInsert(data, mjbRtnMsg)
            val sqlResult = SocketUtil.initSocket(ip, sql).inquire()
            var result = -1
            try {
                result = sqlResult.toInt()
            } catch (e: Exception) {
            }
            if (result >= 0) {
                //成功
                if (getMobileBean(ip, tranNo)) {
                    msg.what = SUCCESS
                    msg.obj = result
                } else {
                    msg.what = ERROR
                    msg.obj = sqlResult
                }
            } else {
                msg.what = ERROR
                msg.obj = sqlResult
            }
            handler.sendMessage(msg)
        }).start()
    }

    private fun getMobileBean(ip: String, tranNo: String): Boolean {
        val sql = MySql.getMobileIsExist(tranNo)
        val sqlResult = SocketUtil.initSocket(ip, sql).inquire()
        if (sqlResult == "" || sqlResult == "[]") return false
        val result = ArrayList<MobileDetailGsonBean>()
        try {
            result.addAll(GsonUtil.getMobileDetail(sqlResult))
        } catch (e: Exception) {
        }
        return result.size > 0
    }

    /**
     * 得到当前下单单号
     */
    private fun getMaxTranNo(msg: Message, ip: String, handler: MyHandler): String {
        val sql = MySql.getMobileMaxTranNo()
        val sqlResult = SocketUtil.initSocket(ip, sql).inquire()
        if (sqlResult == "" || sqlResult == "[]") return "${MyTimeUtil.nowDate3}01"
        val sqlObj = ArrayList<UtilBean>()
        try {
            sqlObj.addAll(GsonUtil.getUtilBean(sqlResult))
        } catch (e: Exception) {
        }
        return if (sqlObj.size > 0) {
            var num = sqlObj[0].value!!.substring(8).toInt()
            num++
            "${MyTimeUtil.nowDate3}${num.toString().padStart(2, '0')}"
        } else {
            msg.obj = sqlResult
            msg.what = ERROR
            handler.sendMessage(msg)
            "ERROR"
        }
    }

}

interface MobileInterface {
    /**
     * 得到历史数据
     */
    fun getMobileData(handler: MyHandler)

    /**
     * 扫码得到数据
     */
    fun getMobileItem(code: String, handler: MyHandler)

    /**
     * 得到货架
     */
    fun getMobileGondola(handler: MyHandler)

    /**
     * 创建
     */
    fun getMobilInsert(data: MobileDetailBean, mjbRtnMsg: String, handler: MyHandler)
}