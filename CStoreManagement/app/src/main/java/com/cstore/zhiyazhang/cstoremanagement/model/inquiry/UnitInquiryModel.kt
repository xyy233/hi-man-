package com.cstore.zhiyazhang.cstoremanagement.model.inquiry

import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.bean.AdjustmentBean
import com.cstore.zhiyazhang.cstoremanagement.bean.UnitInquiryBean
import com.cstore.zhiyazhang.cstoremanagement.model.adjustment.AdjustmentModel
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
 * on 2018/1/30 11:12.
 */
class UnitInquiryModel : UnitInquiryInterface {
    override fun getData(data: String, type: Int, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val sql = MySql.getUnitPlu(data, type)
            val sqlResult = SocketUtil.initSocket(ip, sql).inquire()
            if (!SocketUtil.judgmentNull(sqlResult, msg, handler)) return@Runnable
            val result = ArrayList<UnitInquiryBean>()
            try {
                result.addAll(GsonUtil.getUnitInquiry(sqlResult))
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

    override fun saveInv(data: UnitInquiryBean, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val ad = getInvBean(data, msg, ip, handler) ?: return@Runnable
            ad.currStockQTY = data.invQty
            ad.adjQTY = ad.currStockQTY - ad.actStockQTY
            if (AdjustmentModel().createAdjustment(ad, msg, ip, handler)) {
                msg.obj = data
                msg.what = SUCCESS
                handler.sendMessage(msg)
            }
        }).start()
    }

    override fun saveMinQty(data: UnitInquiryBean, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val sql = getSaveMin(data)
            val sqlResult = SocketUtil.initSocket(ip, sql).inquire()
            if (sqlResult == "0") {
                msg.what = SUCCESS
            } else {
                msg.what = ERROR
            }
            msg.obj = sqlResult
            handler.sendMessage(msg)
        }).start()
    }

    /**
     * 得到商品用于库调的数据
     */
    private fun getInvBean(data: UnitInquiryBean, msg: Message, ip: String, handler: MyHandler): AdjustmentBean? {
        val sql = MySql.searchAdjustment(CStoreCalendar.getCurrentDate(0), data.pluId)
        val result = SocketUtil.initSocket(ip, sql).inquire()
        if (result == "" || result == "[]") {
            msg.obj = "查不到此商品库存数据"
            msg.what = ERROR
            handler.sendMessage(msg)
            return null
        }
        val ad = ArrayList<AdjustmentBean>()
        try {
            ad.addAll(GsonUtil.getAdjustment(result))
        } catch (e: Exception) {
        }
        return if (ad.isEmpty()) {
            msg.obj = result
            msg.what = ERROR
            null
        } else {
            ad[0]
        }
    }

    private fun getSaveMin(data: UnitInquiryBean): String {
        val result = StringBuilder()
        result.append(MySql.affairHeader)
        result.append(MySql.getDeleteMin(data.pluId))
        result.append(MySql.getInsertMin(data.minQty, data.pluId))
        result.append(MySql.affairFoot)
        return result.toString()
    }
}

interface UnitInquiryInterface {
    /**
     * 得到数据
     * @param data 搜索关键字
     * @param type 品号品名搜索传递0 条码档期搜索传1
     */
    fun getData(data: String, type: Int, handler: MyHandler)

    /**
     * 保存库存
     */
    fun saveInv(data: UnitInquiryBean, handler: MyHandler)

    /**
     * 保存自设最小量
     */
    fun saveMinQty(data: UnitInquiryBean, handler: MyHandler)
}