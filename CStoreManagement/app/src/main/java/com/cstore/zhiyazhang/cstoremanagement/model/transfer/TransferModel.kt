package com.cstore.zhiyazhang.cstoremanagement.model.transfer

import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.TrsItemBean
import com.cstore.zhiyazhang.cstoremanagement.bean.TrsfItemBean
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.GsonUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.OnlyMyHandler.ERROR
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.OnlyMyHandler.SUCCESS
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
            val tib = ArrayList<TrsItemBean>()
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
                tib.forEach { it.action = 1 }
                msg.obj = tib
                msg.what = SUCCESS
            }
            handler.sendMessage(msg)
        }).start()
    }

    override fun searchCommodity(data: String, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val sql = MySql.getTrsCommodity(data)
            val result = SocketUtil.initSocket(ip, sql).inquire()
            val tib = ArrayList<TrsItemBean>()
            if (result == "[]") {
                msg.obj = MyApplication.instance().applicationContext.getString(R.string.noMessage)
                msg.what = ERROR
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
                tib.forEach { it.action = 0 }
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
            val sql = geteditTrsSql(data, ip)

        }).start()
    }

    /**
     * 得到编辑的操作语句
     */
    private fun geteditTrsSql(data: ArrayList<TrsItemBean>, ip: String): String {
        val result = StringBuilder()
        val createData = data.filter { it.action == 0 }
        //获得这一单的单号
        val trsNumber = getTrsNumber(ip)

        //添加创建语句
        createData.forEach {
            result.append(MySql.createTrs(it))
        }
        //添加更新语句
        data.filter { it.action == 1 }.forEach {
            result.append(MySql.updateTrs(it))
        }
        result.append("\u0004")
        return result.toString()
    }

    /**
     * 创建时获得单号
     */
    private fun getTrsNumber(ip: String): String {
        val sql = MySql.getMaxTrsNumber
        val sqlResult = SocketUtil.initSocket(ip,sql).inquire()
        if (sqlResult=="[]"){
            //没有单号，新建
        }
        //有单号，+1后截取使用
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllTrsf() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createTrsf(data: ArrayList<TrsfItemBean>, handler: MyHandler) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
    fun getAllTrsf()

    /**
     * 选中的调拨单转入
     */
    fun createTrsf(data: ArrayList<TrsfItemBean>, handler: MyHandler)
}