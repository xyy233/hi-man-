package com.cstore.zhiyazhang.cstoremanagement.model.transfer

import android.os.Message
import android.util.Log
import com.cstore.zhiyazhang.cstoremanagement.bean.*
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.transfer.TransferModel.Companion.getTrsNumber
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.url.AppUrl
import com.cstore.zhiyazhang.cstoremanagement.utils.GsonUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.Companion.ERROR
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.Companion.SUCCESS
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import com.google.gson.Gson
import com.cstore.zhiyazhang.cstoremanagement.utils.MyStringCallBack
import com.zhy.http.okhttp.OkHttpUtils
import okhttp3.MediaType

/**
 * Created by zhiya.zhang
 * on 2018/5/10 10:09.
 */
class TransferServiceModel : TransferServiceInterface {
    override fun getAllTrs(user: User, myListener: MyListener) {
        OkHttpUtils
                .get()
                .url(AppUrl.GET_ALL_TRANS)
                //测试
//                .addHeader(AppUrl.STOREHEADER, "130902")
                .addHeader(AppUrl.STOREHEADER, User.getUser().storeId)
                .addHeader(AppUrl.HOUR, "0")
                .build()
                .execute(object : MyStringCallBack(myListener) {
                    override fun onResponse(response: String, id: Int) {
                        myListener.listenerSuccess(response)
                    }
                })
    }

    override fun getJudgment(user: User, myListener: MyListener) {
        val tag = TransTag.getTransTag(true)
        OkHttpUtils
                .get()
                .url(AppUrl.JUDGMENT_TRANS)
                //测试
//                .addHeader(AppUrl.STOREHEADER, "130902")
                .addHeader(AppUrl.STOREHEADER, User.getUser().storeId)
                .addHeader(AppUrl.HOUR, tag.hour)
                .build()
                .execute(object : MyStringCallBack(myListener) {
                    override fun onResponse(response: String, id: Int) {
                        myListener.listenerSuccess(response)
                    }
                })
    }

    override fun doneTrs(data: TransServiceBean, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val sql = getCreateTrsSql(data, ip, msg, handler)
            if (sql == "ERROR") return@Runnable
            val result = SocketUtil.initSocket(ip, sql).inquire()
            try {
                //通过强制转换为数字判断是否正确
                result.toInt()
                val resultData = requestData(data, 0)
                msg.obj = resultData
                msg.what = SUCCESS
                handler.sendMessage(msg)
            } catch (e: Exception) {
                msg.obj = result
                msg.what = ERROR
                handler.sendMessage(msg)
            }
        }).start()
    }

    /**
     * 获得中卫商品库存、零售
     */
    override fun getZWInv(tr: TransResult, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val sql = MySql.zwSearchInv(tr)
            val sqlResult = SocketUtil.initSocket(ip, sql).inquire()
            val result = ArrayList<UtilBean>()
            try {
                result.addAll(GsonUtil.getUtilBean(sqlResult))
            } catch (e: Exception) {
            }
            if (result.isEmpty()) {
                msg.obj = sqlResult
                msg.what = ERROR
            } else {
                //分配库存
                tr.rows
                        .flatMap { it.items }
                        .forEach { i ->
                            try {
                                result.filter { it.value == i.itemNo }.forEach {
                                    val inv = it.value2
                                    val sellCost = it.value3
                                    i.inv = inv!!.toInt()
                                    i.sellCost = sellCost!!.toDouble()
                                }
                            } catch (e: Exception) {
                            }
                        }
                msg.obj = tr
                msg.what = SUCCESS
            }
            handler.sendMessage(msg)
        }).start()
    }

    /**
     * 回传已处理的调拨
     */
    private fun requestData(data: TransServiceBean, retryCount: Int): String {
        val d = Gson().toJson(data)
        val response = OkHttpUtils
                .postString()
                .url(AppUrl.UPDATE_TRANS)
                .content(d)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute()
        return if (response.code() != 200) {
            val count = retryCount + 1
            if (count == 10) {
                try {
                    val x = response.body().string().toString()
                    x.substring(x.indexOf("HTTP Status 500 - ") + 18, x.indexOf("</h1><div class=\"line\">"))
                } catch (e: Exception) {
                    "此单已处理,数据上传至总部异常"
                }
            } else {
                requestData(data, count)
            }
        } else {
            "SUCCESS"
        }
    }

    /**
     * 得到创建调出单的sql语句
     */
    private fun getCreateTrsSql(data: TransServiceBean, ip: String, msg: Message, handler: MyHandler): String {
        val trsNumber = getTrsNumber(ip, msg, handler)
        if (trsNumber == "ERROR") return trsNumber
        data.requestNumber = trsNumber
        val result = StringBuilder()
        data.items.forEach {
            result.append(MySql.createTrs(it, trsNumber, data.trsStoreId))
        }
        result.append("\u0004")
        return result.toString()
    }

}

interface TransferServiceInterface {
    /**
     * 获得所有数据
     */
    fun getAllTrs(user: User, myListener: MyListener)

    /**
     * 获得库存
     */
    fun getZWInv(tr: TransResult, handler: MyHandler)

    /**
     * 获得判断数据
     */
    fun getJudgment(user: User, myListener: MyListener)

    /**
     * 确定订单，去创建调出单
     */
    fun doneTrs(data: TransServiceBean, handler: MyHandler)
}