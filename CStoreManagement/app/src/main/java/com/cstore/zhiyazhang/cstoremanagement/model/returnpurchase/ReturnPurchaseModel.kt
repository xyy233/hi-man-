package com.cstore.zhiyazhang.cstoremanagement.model.returnpurchase

import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.*
import com.cstore.zhiyazhang.cstoremanagement.bean.ReasonBean.Companion.setReason
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.cstore.zhiyazhang.cstoremanagement.utils.GsonUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.Companion.ERROR
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.Companion.SUCCESS
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil

/**
 * Created by zhiya.zhang
 * on 2017/10/30 16:08.
 * 退货，C#里xml用的换日是type=5的订单上传换日，不过实际测下来是订货换日，搞不懂
 */
class ReturnPurchaseModel : ReturnPurchaseInterface {

    /**
     * 得到退货原因
     */
    private fun getReason(ip: String, msg: Message, handler: MyHandler): Boolean {
        val result = SocketUtil.initSocket(ip, MySql.getReason).inquire()
        if (!SocketUtil.judgmentNull(result, msg, handler)) return false
        val rs = ArrayList<ReasonBean>()
        try {
            rs.addAll(GsonUtil.getReason(result))
        } catch (e: Exception) {
        }
        return if (rs.isEmpty()) {
            msg.obj = result
            msg.what = ERROR
            handler.sendMessage(msg)
            false
        } else {
            setReason(rs)
            true
        }
    }

    override fun getReturnPurchaseList(date: String, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            if (!getReason(ip, msg, handler)) {
                return@Runnable
            }
            val result = SocketUtil.initSocket(ip, MySql.getReturnPurchase(date)).inquire()
            val rps = ArrayList<ReturnedPurchaseBean>()
            if (result == "[]") {
                msg.obj = rps
                msg.what = SUCCESS
                handler.sendMessage(msg)
                return@Runnable
            }
            try {
                rps.addAll(GsonUtil.getReturnPurchase(result))
            } catch (e: Exception) {
            }
            if (rps.isEmpty()) {
                msg.obj = result
                msg.what = ERROR
                handler.sendMessage(msg)
            } else {
                try {
                    rps.forEach {
                        val allItems = getReturnPurchaseItemList(it, date, ip)
                        it.allItem = allItems
                    }
                } catch (e: Exception) {
                    msg.obj = e.message
                    msg.what = ERROR
                    handler.sendMessage(msg)
                    return@Runnable
                }
                msg.obj = rps
                msg.what = SUCCESS
                handler.sendMessage(msg)
            }
        }).start()
    }

    /**
     * 获得退货单下的商品
     */
    private fun getReturnPurchaseItemList(rp: ReturnedPurchaseBean, date: String, ip: String): ArrayList<ReturnPurchaseItemBean> {
        val rps = ArrayList<ReturnPurchaseItemBean>()
        val result = SocketUtil.initSocket(ip, MySql.getReturnPurchaseItem(date, rp.vendorId, rp.requestNumber)).inquire()
        if (result == "[]") return rps
        rps.addAll(GsonUtil.getReturnPurchaseItem(result))
        rps.forEach {
            //分配原因
            val reasonName = ReasonBean.getReason(it.reasonNumber).reasonName
            if (reasonName == "") {
                it.reasonName = it.reasonNumber
            } else {
                it.reasonName = reasonName
            }
        }
        rps.sortBy { it.itemNumber }
        return rps
    }

    override fun getVendor(handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val result = SocketUtil.initSocket(ip, MySql.getReturnVendor).inquire()
            if (!SocketUtil.judgmentNull(result, msg, handler)) return@Runnable
            val vb = ArrayList<VendorBean>()
            try {
                vb.addAll(GsonUtil.getVendor(result))
            } catch (e: Exception) {
            }
            if (vb.isEmpty()) {
                msg.obj = result
                msg.what = ERROR
                handler.sendMessage(msg)
            } else {
                msg.obj = vb
                msg.what = SUCCESS
                handler.sendMessage(msg)
            }
        }).start()
    }

    override fun getCommodity(rpb: ReturnedPurchaseBean?, vendorId: String, type: Int, judgmentInv:Boolean, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val result =
                    if (type == 0) SocketUtil.initSocket(ip, MySql.getRecentlyCommodity(rpb, vendorId, judgmentInv)).inquire()
                    else SocketUtil.initSocket(ip, MySql.getLongCommodity(rpb, vendorId, judgmentInv)).inquire()
            val rpib = ArrayList<ReturnPurchaseItemBean>()
            if (result == "[]") {
                msg.obj = rpib
                msg.what = SUCCESS
                handler.sendMessage(msg)
                return@Runnable
            }
            try {
                rpib.addAll(GsonUtil.getReturnPurchaseItem(result))
                if (rpb != null) {
                    rpib.forEach { it.requestNumber = rpb.requestNumber }
                }
            } catch (e: Exception) {
            }
            if (rpib.isEmpty()) {
                msg.obj = result
                msg.what = ERROR
                handler.sendMessage(msg)
            } else {
                msg.obj = rpib
                msg.what = SUCCESS
                handler.sendMessage(msg)
            }
        }).start()
    }

    override fun createReturnPurchase(date: String, rpb: ArrayList<ReturnPurchaseItemBean>, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            //用的时间是订货换日，type=2
            if (!CStoreCalendar.judgmentCalender(date, msg, handler, 2)) return@Runnable
            //先判断是否这个商品是否重复,避免两人操作造成冲突
            val returnPurchaseSql = SocketUtil.initSocket(ip, MySql.judgmentReturnPurchase(rpb)).inquire()
            val returnPurchase = ArrayList<ReturnPurchaseItemBean>()
            try {
                returnPurchase.addAll(GsonUtil.getReturnPurchaseItem(returnPurchaseSql))
            } catch (e: Exception) {
            }
            if (returnPurchase.isNotEmpty()) {
                msg.obj = "已有此商品的退货单，不能重复创建！"
                msg.what = ERROR
                handler.sendMessage(msg)
                return@Runnable
            }
            //开始创建,检测是全新建还是插入老单
            val sql = if (rpb[0].requestNumber == null) {
                //新建单子和商品
                //开始创建，先检测今天是否有配送单得到单号
                getCreateReturnPurchaseSql(ip, date, rpb, 0)
            } else {
                //新商品插入老单
                getCreateReturnPurchaseSql(ip, date, rpb, 2)
            }
            if (sql == "0") {
                msg.obj = MyApplication.instance().applicationContext.getString(R.string.socketError)
                msg.what = ERROR
                handler.sendMessage(msg)
                return@Runnable
            }
            val result = SocketUtil.initSocket(ip, sql).inquire()
            if (result == "0") {
                msg.what = SUCCESS
            } else {
                msg.what = ERROR
            }
            msg.obj = result
            handler.sendMessage(msg)
        }).start()
    }

    /**
     * 创建退货的sql语句
     * @param date 预约日期
     * @param rpb 要保存的数据
     * @param type 0=新建  1=更新  2=新商品插入老单
     */
    private fun getCreateReturnPurchaseSql(ip: String, date: String, rpb: ArrayList<ReturnPurchaseItemBean>, type: Int): String {
        when (type) {
            0 -> {
                //新建
                val numHeader = MySql.getNowNum(date)
                //得到单号
                val distributionId = getDistribution(ip, date, numHeader)
                if (distributionId == "0") return distributionId
                //得到最大排序id
                var maxRecordNumber = getMaxRecordNumber(ip, date)
                if (maxRecordNumber == 0) return "0"
                //得到单号尾数
                val numFoot = distributionId.substring(distributionId.length - 5).toInt()
                val newHeader = numHeader.substring(0, numHeader.length - 1)
                //得到当前单号
                val nowId = newHeader + (numFoot + 1).toString()
                val result = StringBuilder()
                result.append(MySql.affairHeader)
                rpb.forEach {
                    it.recordNumber = maxRecordNumber
                    it.requestNumber = nowId
                    maxRecordNumber++
                    result.append(MySql.createReturnPurchase(it))
                }
                result.append(MySql.affairFoot)
                return result.toString()
            }
            1 -> {
                //更新
                val result = StringBuilder()
                result.append(MySql.affairHeader)
                rpb.forEach {
                    result.append(MySql.updateReturnPurchase(it))
                }
                result.append(MySql.affairFoot)
                return result.toString()
            }
            else -> {
                //得到最大排序id
                var maxRecordNumber = getMaxRecordNumber(ip, date)
                if (maxRecordNumber == 0) return "0"
                //新商品插入老单
                val result = StringBuilder()
                result.append(MySql.affairHeader)
                rpb.forEach {
                    it.recordNumber = maxRecordNumber
                    maxRecordNumber++
                    result.append(MySql.createReturnPurchase(it))
                }
                result.append(MySql.affairFoot)
                return result.toString()
            }
        }
    }

    /**
     * 得到最大的RecordNumber,数据库中主键并用来排序的存在
     */
    private fun getMaxRecordNumber(ip: String, date: String): Int {
        val result = SocketUtil.initSocket(ip, MySql.getMaxRecordNumber(date)).inquire()
        if (result == "0") {
            return 0
        } else {
            val ub = ArrayList<UtilBean>()
            try {
                ub.addAll(GsonUtil.getUtilBean(result))
            } catch (e: Exception) {
            }
            if (ub[0].value == null) {
                return 1
            }
            return ub[0].value!!.toInt() + 1
        }
    }

    /**
     * 得到requestID,退货单号
     */
    private fun getDistribution(ip: String, date: String, numHeader: String): String {
        val result = SocketUtil.initSocket(ip, MySql.getMaxReturnPurchaseId(date, numHeader)).inquire()
        if (result == "0") {
            return result
        } else {
            val ub = ArrayList<UtilBean>()
            try {
                ub.addAll(GsonUtil.getUtilBean(result))
            } catch (e: Exception) {
            }
            if (ub[0].value == null) {
                return numHeader + "0000"
            }
            return ub[0].value!!
        }
    }

    override fun updateReturnPurchase(date: String, rpb: ReturnedPurchaseBean, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            if (!CStoreCalendar.judgmentCalender(date, msg, handler, 2)) return@Runnable
            val result = SocketUtil.initSocket(ip, getCreateReturnPurchaseSql(ip, date, rpb.allItem, 1)).inquire()
            if (result == "0") {
                msg.what = SUCCESS
            } else {
                msg.what = ERROR
            }
            msg.obj = result
            handler.sendMessage(msg)
        }).start()
    }

}

interface ReturnPurchaseInterface {
    /**
     * 得到退货列表
     */
    fun getReturnPurchaseList(date: String, handler: MyHandler)

    /**
     * 得到配送商
     */
    fun getVendor(handler: MyHandler)

    /**
     * 得到配送商下的商品
     * @param type 0=短期商品  1=长期商品
     */
    fun getCommodity(rpb: ReturnedPurchaseBean?, vendorId: String, type: Int, judgmentInv:Boolean, handler: MyHandler)

    /**
     * 创建退货
     */
    fun createReturnPurchase(date: String, rpb: ArrayList<ReturnPurchaseItemBean>, handler: MyHandler)

    /**
     * 更新退货
     */
    fun updateReturnPurchase(date: String, rpb: ReturnedPurchaseBean, handler: MyHandler)
}

