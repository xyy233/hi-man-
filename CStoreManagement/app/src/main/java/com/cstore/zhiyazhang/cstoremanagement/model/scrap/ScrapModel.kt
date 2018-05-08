package com.cstore.zhiyazhang.cstoremanagement.model.scrap

import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapContractBean
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.GsonUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.Companion.ERROR
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.Companion.SUCCESS
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.ScrapView

/**
 * Created by zhiya.zhang
 * on 2017/8/21 16:20.
 */
class ScrapModel(private val sView: ScrapView) : ScrapInterface {

    override fun getAllScrap(handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val data = SocketUtil.initSocket(ip, MySql.allScrap(sView.getDate())).inquire()
            if (!SocketUtil.judgmentNull(data, msg, handler)) return@Runnable

            val scraps = ArrayList<ScrapContractBean>()
            try {
                scraps.addAll(GsonUtil.getScrap(data))
            } catch (e: Exception) {
            }
            if (scraps.isEmpty()) {
                msg.obj = data
                msg.what = ERROR
                handler.sendMessage(msg)
            } else {
                msg.obj = scraps
                msg.what = SUCCESS
                handler.sendMessage(msg)
            }
        }).start()
    }

    override fun searchScrap(message: String, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val data = SocketUtil.initSocket(ip, MySql.getScrapByMessage(message)).inquire()
            if (!SocketUtil.judgmentNull(data, msg, handler)) return@Runnable

            val scraps = ArrayList<ScrapContractBean>()
            try {
                scraps.addAll(GsonUtil.getScrap(data))
            } catch (e: Exception) {
            }
            if (scraps.isEmpty()) {
                msg.obj = data
                msg.what = ERROR
                handler.sendMessage(msg)
            } else {
                msg.obj = scraps
                msg.what = SUCCESS
                handler.sendMessage(msg)
            }
        }).start()
    }

    override fun submitScraps(data: ArrayList<ScrapContractBean>, reCode: Int, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            if (data.size < 1) {
                msg.obj = MyApplication.instance().applicationContext.resources.getString(R.string.noEditMsg)
                msg.what = ERROR
                handler.sendMessage(msg)
                return@Runnable
            }
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable

            //检查是否有错误参数
            val judgment = judgmentData(ip, data)
            if (judgment == SCRAP_DATA_ERROR) {
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

            //得到提交数据的sql语句
            val sql = getSubmitString(data, recodeNumber.toInt())

            val sqlData = SocketUtil.initSocket(ip, sql).inquire()
            if (!SocketUtil.judgmentNull(sqlData, msg, handler)) return@Runnable
            if (sqlData == "0") {
                msg.obj = sqlData
                msg.what = SUCCESS
                handler.sendMessage(msg)
            } else {
                msg.obj = sqlData
                msg.what = ERROR
                handler.sendMessage(msg)
            }
        }).start()
    }

    companion object {
        val RECODE_ERROR = "-100"
        val SCRAP_DATA_ERROR = "error"

        /**
         * 得到当前最大的recodeNumber
         */
        fun getRecodeNumber(ip: String): String {
            val result = SocketUtil.initSocket(ip, MySql.getRecodeNumber).inquire()
            if (result==""||result=="[]")return "0"
            return try {
                val count=result.substring(0,result.length-2).substring(17).toInt()
                if (count==0) "0" else{
                    count.toString()
                }
            }catch (e:Exception){
                RECODE_ERROR + result + e.message
            }

        }

        /**
         * 判断是否上传的数据不对
         * 有可能多人操作造成数据不同步
         */
        fun judgmentData(ip: String, data: ArrayList<ScrapContractBean>): String {
            val result = SocketUtil.initSocket(ip, MySql.allScrap(MyTimeUtil.nowDate)).inquire()
            if (result == "" || result == "0") return ""
            val allScraps = ArrayList<ScrapContractBean>()
            try {
                allScraps.addAll(GsonUtil.getScrap(result))
            } catch (e: Exception) {
                return result
            }
            //循环提交数据
            for (scrap in data) {
                //搜索所有数据库存在的数据
                val s = allScraps.filter { it.scrapId == scrap.scrapId }
                //如果不存在就检查提交的动作是否为update，如果是update就报错
                if (s.isEmpty()) {
                    if (scrap.action == 1) return SCRAP_DATA_ERROR
                }
                //遍历所有数据检查提交的动作是否为insert，如果是insert就报错
                s.forEach { if (scrap.action == 0) return SCRAP_DATA_ERROR }
            }
            return ""
        }

        /**
         * 从list中得到要用的sql语句
         */
        fun getSubmitString(data: ArrayList<ScrapContractBean>, reCode: Int): String {
            val result: StringBuilder = StringBuilder()
            result.append(MySql.affairHeader)
            val a = data.sortedByDescending { it.recordNumber }[0].recordNumber
            var i: Int
            i = if (a > reCode) a else reCode
            data.forEach {
                //0==insert 1==update 2==delete 3==none
                when (it.action) {
                    0 -> {
                        i++
                        it.recordNumber = i
                        result.append(MySql.insertScrap(it))
                    }
                    1 -> result.append(MySql.updateScrap(it))
                    2 -> result.append(MySql.deleteScrap(it))
                }
            }
            result.append(MySql.affairFoot)
            return result.toString()
        }
    }
}

interface ScrapInterface {
    /**
     * 得到当前所有的报废信息
     */
    fun getAllScrap(handler: MyHandler)

    /**
     * 搜索报废品
     */
    fun searchScrap(message: String, handler: MyHandler)

    /**
     * 提交所有报废
     */
    fun submitScraps(data: ArrayList<ScrapContractBean>, reCode: Int, handler: MyHandler)
}