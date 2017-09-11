package com.cstore.zhiyazhang.cstoremanagement.model.Acceptance

import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.bean.AcceptanceBean
import com.cstore.zhiyazhang.cstoremanagement.bean.AcceptanceItemBean
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.MyHandler.ERROR1
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.MyHandler.SUCCESS
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil

/**
 * Created by zhiya.zhang
 * on 2017/9/11 11:30.
 */
class AcceptanceModel : AcceptanceInterface {
    override fun getAcceptanceList(date: String, handler: MyHandler.MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val result = SocketUtil.initSocket(ip, MySql.getAcceptanceList(date)).inquire()
            if (!SocketUtil.judgmentNull(result, msg, handler)) return@Runnable

            val abs = ArrayList<AcceptanceBean>()
            try {
                abs.addAll(SocketUtil.getAcceptance(result))
            } catch (e: Exception) {
            }
            if (abs.isEmpty()) {
                msg.obj = result
                msg.what = ERROR1
                handler.sendMessage(msg)
            }

            abs.forEach {
                val allItems = getAcceptanceItemList(it, date, handler, msg, ip)
                if (allItems.size == 0) return@Runnable
                it.allItems = allItems
            }

            msg.obj = abs
            msg.what = SUCCESS
            handler.sendMessage(msg)

        }).start()
    }

    /**
     * 得到配送表下的商品
     */
    private fun getAcceptanceItemList(ab: AcceptanceBean, date: String, handler: MyHandler.MyHandler, msg: Message, ip: String): ArrayList<AcceptanceItemBean> {
        val abs = ArrayList<AcceptanceItemBean>()
        val result = SocketUtil.initSocket(ip, MySql.getAcceptanceItemList(ab, date)).inquire()
        try {
            abs.addAll(SocketUtil.getAcceptanceItem(result))
        } catch (e: Exception) {
        }
        if (abs.isEmpty()) {
            msg.obj = result
            msg.what = ERROR1
            handler.sendMessage(msg)
            return abs
        } else {
            return abs
        }
    }

    override fun updateAcceptance(ab: AcceptanceBean, handler: MyHandler.MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val sql = getUpdateSql(ab)
            val result = SocketUtil.initSocket(ip, sql).inquire()
            if (result == "0") {
                msg.obj = result
                msg.what = SUCCESS
                handler.sendMessage(msg)
            } else {
                msg.obj = result
                msg.what = ERROR1
                handler.sendMessage(msg)
            }
        }).start()
    }

    /**
     * 得到更新用的sql语句,只会有更新，创建都是确定当场保存下来
     */
    private fun getUpdateSql(ab: AcceptanceBean): String {
        val result: StringBuilder = StringBuilder()
        result.append(MySql.affairHeader)//添加事务头
        var allDlvQTY = 0//所有商品的验收量
        var allDCQTY = 0//所有商品的大仓出货量
        var allStoreUnitPrice: Double = 0.00//验收单总计的零售价格
        var costTotal = 0.00
        ab.allItems.forEach {
            //在view层判断修改过的话0也存储
            allDCQTY += it.dctrsQuantity
            allDlvQTY += it.dlvQuantity
            it.retailTotal = (it.storeUnitPrice * it.dlvQuantity)//修改当前商品的零售小计
            allStoreUnitPrice += it.retailTotal
            it.totalUnitCost = it.unitCost * it.dlvQuantity//得到当前商品总成本价
            costTotal += it.totalUnitCost
            it.varQuantity = it.trsQuantity - it.dctrsQuantity//得到差异值
            result.append(MySql.updateAcceptanceItem(it))//添加修改商品的sql语句到事务中去
        }
        //写入成本总计
        ab.costTotal = costTotal
        //写入零售小计
        ab.retailTotal = allStoreUnitPrice
        //不是转次日的就去修改验收状态
        if (ab.dlvStatus != 3) {
            //相同就是未修正的验收，不相同且不为0就是已修正过的
            if (allDlvQTY == allDCQTY) ab.dlvStatus = 1 else if (allDlvQTY != 0) ab.dlvStatus = 0
        }
        result.append(MySql.updateAcceptance(ab))//添加修改配送单的sql
        result.append(MySql.affairFoot)//添加事务脚
        return result.toString()
    }

}

interface AcceptanceInterface {
    /**
     * 得到配送列表
     */
    fun getAcceptanceList(date: String, handler: MyHandler.MyHandler)

    /**
     * 更新验收
     */
    fun updateAcceptance(ab: AcceptanceBean, handler: MyHandler.MyHandler)

/*    */
    /**
     * 创建配送
     */
    /*
        fun createAcceptance()*/
}