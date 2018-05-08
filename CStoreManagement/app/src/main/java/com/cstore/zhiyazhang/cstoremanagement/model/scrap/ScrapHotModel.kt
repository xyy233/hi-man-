package com.cstore.zhiyazhang.cstoremanagement.model.scrap

import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapContractBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapHotBean
import com.cstore.zhiyazhang.cstoremanagement.model.scrap.ScrapModel.Companion.RECODE_ERROR
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.GsonUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.Companion.ERROR
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.Companion.SUCCESS
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil

/**
 * Created by zhiya.zhang
 * on 2017/9/1 12:16.
 */
class ScrapHotModel : ScrapHotInterface {

    override fun getMidCategory(handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val result = SocketUtil.initSocket(ip, MySql.getScrap80).inquire()
            if (!SocketUtil.judgmentNull(result, msg, handler)) return@Runnable

            val scrapHots = ArrayList<ScrapHotBean>()
            try {
                scrapHots.addAll(GsonUtil.getScrapHot(result))
            } catch (e: Exception) {
            }
            if (scrapHots.isEmpty()) {
                msg.obj = result
                msg.what = ERROR
                handler.sendMessage(msg)
            } else {
                msg.obj = scrapHots
                msg.what = SUCCESS
                handler.sendMessage(msg)
            }
        }).start()
    }

    override fun getHotItem(midId: String, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val result = SocketUtil.initSocket(ip, MySql.getScrap80Item(midId)).inquire()
            if (!SocketUtil.judgmentNull(result, msg, handler)) return@Runnable

            val scraps = ArrayList<ScrapContractBean>()
            try {
                scraps.addAll(GsonUtil.getScrap(result))
            } catch (e: Exception) {
                msg.obj = result
                msg.what = ERROR
                handler.sendMessage(msg)
                return@Runnable
            }
            scraps.forEach { if (it.mrkCount == 0) it.action = 0 else it.action = 1 }
            msg.obj = scraps
            msg.what = SUCCESS
            handler.sendMessage(msg)
        }).start()
    }

    override fun submitScrap(data: ArrayList<ScrapContractBean>, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable

            //检查是否有错误参数
            val judgment = ScrapModel.judgmentData(ip, data)
            if (judgment == ScrapModel.SCRAP_DATA_ERROR) {
                msg.obj = judgment
                msg.what = ERROR
                handler.sendMessage(msg)
                return@Runnable
            }

            //得到recodeNumber
            val recodeNumber = ScrapModel.getRecodeNumber(ip)
            if (recodeNumber.contains(RECODE_ERROR)) {
                msg.obj = recodeNumber
                msg.what = ERROR
                handler.handleMessage(msg)
                return@Runnable
            }

            val sql = ScrapModel.getSubmitString(data, recodeNumber.toInt())

            val result = SocketUtil.initSocket(ip, sql).inquire()
            if (!SocketUtil.judgmentNull(result, msg, handler)) return@Runnable
            if (result == "0") {
                msg.obj = result
                msg.what = SUCCESS
                handler.sendMessage(msg)
            } else {
                msg.obj = result
                msg.what = ERROR
                handler.sendMessage(msg)
            }
        }).start()
    }
}

interface ScrapHotInterface {
    /**
     * 得到热食的中类
     */
    fun getMidCategory(handler: MyHandler)

    /**
     * 得到热食的商品
     */
    fun getHotItem(midId: String, handler: MyHandler)

    /**
     * 提交报废信息
     */
    fun submitScrap(data: ArrayList<ScrapContractBean>, handler: MyHandler)
}