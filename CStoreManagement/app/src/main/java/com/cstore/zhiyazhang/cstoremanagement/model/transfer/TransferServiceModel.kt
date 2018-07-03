package com.cstore.zhiyazhang.cstoremanagement.model.transfer

import android.content.Intent
import android.os.Message
import android.util.Log
import com.cstore.zhiyazhang.cstoremanagement.bean.*
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.transfer.TransferModel.Companion.getTrsNumber
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.sql.TranDao
import com.cstore.zhiyazhang.cstoremanagement.url.AppUrl
import com.cstore.zhiyazhang.cstoremanagement.utils.*
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.Companion.ERROR
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.Companion.SUCCESS
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import com.cstore.zhiyazhang.cstoremanagement.view.LivingService
import com.cstore.zhiyazhang.cstoremanagement.view.transfer.TransferErrorService
import com.google.gson.Gson
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
//                .addHeader(AppUrl.TRANDATE, "20180524")
//                .addHeader(AppUrl.STOREHEADER, "010802")
                .addHeader(AppUrl.STOREHEADER, User.getUser().storeId)
                .addHeader(AppUrl.HOUR, "0")
                .build()
                .execute(object : MyStringCallBack(myListener) {
                    override fun onResponse(response: String, id: Int) {
                        myListener.listenerSuccess(response)
                    }
                })
    }


    private fun judgmentIsRequestNumber(data: TransServiceBean, msg: Message, handler: MyHandler): Boolean {
        val response = OkHttpUtils
                .get()
                .url(AppUrl.CAN_CREATE_TRANS)
                //测试
//                .addHeader(AppUrl.STOREHEADER, "010802")
                .addHeader(AppUrl.STOREHEADER, User.getUser().storeId)
                .addHeader(AppUrl.TRSSTOREID, data.trsStoreId)
                .addHeader(AppUrl.DISTIME, data.disTime)
                .build()
                .execute()
        if (response.isSuccessful) {
            val trs = Gson().fromJson(response.body().string(), TransCanCreate::class.java)
            if (trs.code == 0) {
                if (!trs.canCreate) {
                    msg.what = ERROR
                    msg.obj = "已产生数据，不能创建！请退出重进！"
                    handler.sendMessage(msg)
                    return false
                }
                return true
            } else {
                msg.what = ERROR
                msg.obj = response.body().string().toString()
                handler.sendMessage(msg)
                return false
            }
        } else {
            msg.what = ERROR
            val x = response.body().string().toString()
            msg.obj = x.substring(x.indexOf("HTTP Status") + 25, x.indexOf("</h1><div class=\"line\">"))
            handler.sendMessage(msg)
            return false
        }
    }

    override fun getJudgment(user: User, myListener: MyListener) {
        val tag = TransTag.getTransTag(true)
        Log.e("TranService", tag.hour)
        OkHttpUtils
                .get()
                .url(AppUrl.JUDGMENT_TRANS)
                //测试
//                .addHeader(AppUrl.STOREHEADER, "120901")
                .addHeader(AppUrl.STOREHEADER, User.getUser().storeId)
                .addHeader(AppUrl.HOUR, tag.hour)
                .build()
                .execute(object : MyStringCallBack(myListener) {
                    override fun onResponse(response: String, id: Int) {
                        myListener.listenerSuccess(response)
                    }
                })
    }

    override fun serviceDoneTrs(db: TranDao, datas: ArrayList<TransServiceBean>, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            for (data in datas) {
                try {
                    val sql = getServiceCreateTrsSql(data)
                    if (data.isSc == 0) {
                        data.isSc = goSC(data.requestNumber!!, sql, ip)
                    }
                    if (data.isInt == 0) {
                        data.isInt = goInternet(data)
                    }
                    db.updateSQL(data, data.isSc!!, data.isInt!!)
                } catch (e: Exception) {
                }
            }
            msg.obj = "SUCCESS"
            msg.what = SUCCESS
            handler.sendMessage(msg)
        }).start()
    }

    override fun doneTrs(db: TranDao, data: TransServiceBean, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val sql = getCreateTrsSql(db, data, ip, msg, handler)
            if (sql == "ERROR") return@Runnable
            val isSc = goSC(data.requestNumber!!, sql, ip)
            val isInt = goInternet(data)
            db.updateSQL(data, isSc, isInt)

            //如果有异常产生就去开启服务
            try {
                if (isSc == 0 || isInt == 0) {
                    if (!LivingService.isServiceWorked(MyApplication.instance().applicationContext, TransferErrorService.TAG)) {
                        MyApplication.instance().applicationContext.startService(Intent(MyApplication.instance().applicationContext, TransferErrorService::class.java))
                    }
                }
            } catch (e: Exception) {
            }
            msg.obj = "SUCCESS"
            msg.what = SUCCESS
            handler.sendMessage(msg)
        }).start()
    }

    /**
     * 创建sc数据得到结果
     */
    private fun goSC(requestNumber: String, sql: String, ip: String): Int {
        return try {
            SocketUtil.initSocket(ip, sql).inquire()
            //通过查询是否已有此单号判断是否创建成功
            val judgementSql = MySql.judgmentTrsNumber(requestNumber)
            val jResult = SocketUtil.initSocket(ip, judgementSql).inquire()
            val jr = GsonUtil.getUtilBean(jResult)
            //创建成功
            if ((jr[0].value)!!.toInt() != 0) {
                1
            } else 0
        } catch (e: Exception) {
            0
        }
    }

    /**
     * 创建总部数据得到结果
     */
    private fun goInternet(data: TransServiceBean): Int {
        return try {
            val resultData = requestData(data, 0)
            if (resultData == "SUCCESS") {
                1
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }

    /*override fun doneTrs(db: TranDao, data: TransServiceBean, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            //不知道为什么门市有异常，测试是没问题的，先禁止掉
            //2018-06-07 修改测试，稳了？发个版本瞧瞧
            if (!judgmentIsRequestNumber(data, msg, handler)) return@Runnable
            val sql = getCreateTrsSql(data, ip, msg, handler)
            if (sql == "ERROR") return@Runnable
            //改为先上传总部再插入数据库,只有上传总部成功才能插入数据库
            val resultData = requestData(data, 0)
            if (resultData != "SUCCESS") {
                msg.what = ERROR
                msg.obj = resultData
                handler.sendMessage(msg)
                return@Runnable
            }
            val result = SocketUtil.initSocket(ip, sql).inquire()
            try {
                //通过查询是否已有此单号判断是否创建成功
                val judgementSql = MySql.judgmentTrsNumber(data.requestNumber!!)
                val jResult = SocketUtil.initSocket(ip, judgementSql).inquire()
                val jr = GsonUtil.getUtilBean(jResult)
                if ((jr[0].value)!!.toInt() == 0) {
                    //把总部数据覆盖回去
                    data.requestNumber = null
                    requestData(data, 0)
                    msg.obj = "创建订单异常！请重试！$result"
                    msg.what = ERROR
                    handler.sendMessage(msg)
                    return@Runnable
                } else {
                    msg.obj = "SUCCESS"
                    msg.what = SUCCESS
                    handler.sendMessage(msg)
                }
            } catch (e: Exception) {
                //把总部数据覆盖回去
                data.requestNumber = null
                requestData(data, 0)
                msg.obj = "服务器异常，确定连在内网中，确定服务器正常"
                msg.what = ERROR
                handler.sendMessage(msg)
            }
        }).start()
    }*/

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
                                    val storeUnitPrice = it.value3
                                    i.inv = inv!!.toInt()
                                    i.storeUnitPrice = storeUnitPrice!!.toDouble()
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
                    "网络异常！"
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
    private fun getCreateTrsSql(db: TranDao, data: TransServiceBean, ip: String, msg: Message, handler: MyHandler): String {
        //先得到sc最大单号
        var trsNumber = getTrsNumber(ip, msg, handler)
        if (trsNumber == "ERROR") return trsNumber
        //得到本地sql最大单号
        trsNumber = db.getSQLiteRequestNumber(trsNumber, msg, handler)
        if (trsNumber == "ERROR") return trsNumber
        data.requestNumber = trsNumber
        val result = StringBuilder()
        data.items.forEach {
            result.append(MySql.createTrs(it, trsNumber, data.trsStoreId, MyTimeUtil.nowDate))
        }
        result.append("\u0004")
        return result.toString()
    }

    /**
     * 得到创建调出单的sql语句
     */
    private fun getServiceCreateTrsSql(data: TransServiceBean): String {
        val result = StringBuilder()
        data.items.forEach {
            result.append(MySql.createTrs(it, data.requestNumber!!, data.trsStoreId, data.busiDate ?: MyTimeUtil.nowDate))
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
    fun doneTrs(db: TranDao, data: TransServiceBean, handler: MyHandler)

    fun serviceDoneTrs(db: TranDao, datas: ArrayList<TransServiceBean>, handler: MyHandler)
}