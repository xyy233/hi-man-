package com.cstore.zhiyazhang.cstoremanagement.model.transfer

import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.*
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.GsonUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.OnlyMyHandler.ERROR
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.OnlyMyHandler.SUCCESS
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil

/**
 * Created by zhiya.zhang
 * on 2018/1/18 17:12.
 */
class TransferModel : TransferInterface {
    override fun getAllTrs(date: String, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val sql = MySql.getTrs(date)
            val result = SocketUtil.initSocket(ip, sql).inquire()
            val tib = ArrayList<TrsBean>()
            if (result == "[]") {
                msg.obj = tib
                msg.what = SUCCESS
                handler.sendMessage(msg)
                return@Runnable
            }
            try {
                tib.addAll(GsonUtil.getTrs(result))
            } catch (e: Exception) {
            }
            if (tib.isEmpty()) {
                msg.obj = result
                msg.what = ERROR
            } else {
                tib.forEach {
                    val items = getTrsItem(it.trsNumber, date, ip, msg, handler) ?: return@Runnable
                    it.items = items
                }
                msg.obj = tib
                msg.what = SUCCESS
            }
            handler.sendMessage(msg)
        }).start()
    }

    /**
     * 获得调拨单下的商品
     */
    private fun getTrsItem(trsNumber: String, date: String, ip: String, msg: Message, handler: MyHandler): ArrayList<TrsItemBean>? {
        val sql = MySql.getTrsItems(date, trsNumber)
        val sqlResult = SocketUtil.initSocket(ip, sql).inquire()
        val result = ArrayList<TrsItemBean>()
        try {
            result.addAll(GsonUtil.getTrsItem(sqlResult))
        } catch (e: Exception) {
        }
        if (sqlResult == "[]") {
            return result
        }
        return if (result.isEmpty()) {

            msg.obj = sqlResult
            msg.what = ERROR
            handler.sendMessage(msg)
            null
        } else {
            result.forEach { it.action = 0 }
            result
        }
    }

    override fun searchCommodity(data: String, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val sql = MySql.getTrsCommodity(data)
            val result = SocketUtil.initSocket(ip, sql).inquire()
            val tib = ArrayList<PluItemBean>()
            if (result == "[]") {
                msg.obj = MyApplication.instance().applicationContext.getString(R.string.noMessage)
                msg.what = ERROR
                handler.sendMessage(msg)
                return@Runnable
            }
            try {
                tib.addAll(GsonUtil.getPlu(result))
            } catch (e: Exception) {
            }
            if (tib.isEmpty()) {
                msg.obj = result
                msg.what = ERROR
            } else {
                msg.obj = tib
                msg.what = SUCCESS
            }
            handler.sendMessage(msg)
        }).start()
    }

    override fun editTrs(data: ArrayList<TrsItemBean>, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val sql = geteditTrsSql(data, ip, msg, handler)
            if (sql == "ERROR") return@Runnable
            val result = SocketUtil.initSocket(ip, sql).inquire()
            try {
                //返回数字修改了多少，强制转换判断是否成功，不成功就是报错
                val count = result.toInt()
                msg.obj = count
                msg.what = SUCCESS
                handler.sendMessage(msg)
            } catch (e: Exception) {
                msg.obj = result
                msg.what = ERROR
                handler.sendMessage(msg)
            }
        }).start()
    }

    override fun getAllTrsf(handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val sqlResult = SocketUtil.initSocket(ip, MySql.getTrsf).inquire()
            val result = ArrayList<TrsfItemBean>()
            if (sqlResult == "[]") {
                msg.obj = result
                msg.what = SUCCESS
                handler.sendMessage(msg)
                return@Runnable
            }
            try {
                result.addAll(GsonUtil.getTrsf(sqlResult))
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

    override fun createTrsf(data: ArrayList<TrsfItemBean>, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val sql = getCreateTrsfSql(data)
            val sqlResult = SocketUtil.initSocket(ip, sql).inquire()
            try {
                val x = sqlResult.toInt()
                msg.obj = x
                msg.what = SUCCESS
            } catch (e: Exception) {
                msg.obj = sqlResult
                msg.what = ERROR
            }
            handler.sendMessage(msg)
        }).start()
    }

    override fun searchStore(data: String, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val sql = MySql.searchStore(data)
            val sqlResult = SocketUtil.initSocket(ip, sql).inquire()
            if (sqlResult == "[]") {
                msg.obj = "无店号为:${data}的门市"
                msg.what = ERROR
                handler.sendMessage(msg)
                return@Runnable
            }
            val result = ArrayList<OStoreBean>()
            try {
                result.addAll(GsonUtil.getOStore(sqlResult))
            } catch (e: Exception) {
            }
            if (result.isEmpty()) {
                msg.obj = sqlResult
                msg.what = ERROR
            } else {
                msg.obj = result[0]
                msg.what = SUCCESS
            }
            handler.sendMessage(msg)
        }).start()
    }

    private fun getCreateTrsfSql(data: ArrayList<TrsfItemBean>): String {
        val result = StringBuilder()
        data.forEach {
            result.append(MySql.createTrsf(it))
        }
        return data.toString()
    }

    /**
     * 得到编辑的操作语句
     */
    private fun geteditTrsSql(data: ArrayList<TrsItemBean>, ip: String, msg: Message, handler: MyHandler): String {
        val result = StringBuilder()
        val createData = data.filter { it.action == 1 }
        //获得这一单的单号

        val trsNumber = if (data[0].trsNumber != "") {
            //有单号
            data[0].trsNumber
        } else {
            getTrsNumber(ip, msg, handler)
        }
        if (trsNumber == "ERROR") return trsNumber

        //添加创建语句
        createData.forEach {
            it.trsNumber = trsNumber
            result.append(MySql.createTrs(it))
        }
        //添加更新语句
        data.filter { it.action == 0 }.forEach {
            result.append(MySql.updateTrs(it))
        }
        result.append("\u0004")
        return result.toString()
    }

    /**
     * 创建时获得单号
     */
    private fun getTrsNumber(ip: String, msg: Message, handler: MyHandler): String {
        val sql = MySql.getMaxTrsNumber
        val sqlResult = SocketUtil.initSocket(ip, sql).inquire()
        //有单号
        val values = ArrayList<UtilBean>()
        try {
            values.addAll(GsonUtil.getUtilBean(sqlResult))
        } catch (e: Exception) {
        }
        if (values.isEmpty()) {
            msg.obj = sqlResult
            msg.what = ERROR
            handler.sendMessage(msg)
            return "ERROR"
        }
        if (values[0].value == null || values[0].value == "null") {
            //没有单号，新建
            return trsNumberIsExits((MyTimeUtil.dayOfYear() + "1").toInt(), ip, msg, handler)
        }
        val number = values[0].value!!.substring(8, 12).toInt()
        return trsNumberIsExits(number + 1, ip, msg, handler)
    }

    /**
     * 检查是否有重复的单号，重复就+1
     */
    private fun trsNumberIsExits(trsNumber: Int, ip: String, msg: Message, handler: MyHandler): String {
        var finalTrsNum = trsNumber
        var checkOrder = false
        var count = 0
        while (!checkOrder) {
            val sql = MySql.isExistTrs(finalTrsNum.toString())
            val result = SocketUtil.initSocket(ip, sql).inquire()
            if (result == "[]") {
                checkOrder = true
            } else {
                finalTrsNum++
            }
            if (count == 10) {
                msg.obj = "检查重复单号出错：$result"
                msg.what = ERROR
                handler.sendMessage(msg)
                return "ERROR"
            }
            count++
        }
        return "${User.getUser().storeId}${MyTimeUtil.nowYear()}${finalTrsNum.toString().padStart(4, '0')}"
    }

}

interface TransferInterface {
    /**
     * 得到所有调出单
     * @param date 选择的时间
     */
    fun getAllTrs(date: String, handler: MyHandler)

    /**
     * 搜索商品
     * @param data 品号或条码
     */
    fun searchCommodity(data: String, handler: MyHandler)

    /**
     * 修改调出品，创建或更新
     */
    fun editTrs(data: ArrayList<TrsItemBean>, handler: MyHandler)

    /**
     * 得到所有调入单
     */
    fun getAllTrsf(handler: MyHandler)

    /**
     * 选中的调拨单转入
     */
    fun createTrsf(data: ArrayList<TrsfItemBean>, handler: MyHandler)

    /**
     * 根据店号查找门市确定是否存在
     * @param data 店号
     */
    fun searchStore(data: String, handler: MyHandler)
}