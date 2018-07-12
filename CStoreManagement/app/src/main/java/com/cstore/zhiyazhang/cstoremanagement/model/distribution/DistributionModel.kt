package com.cstore.zhiyazhang.cstoremanagement.model.distribution

import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.bean.DistributionItemBean
import com.cstore.zhiyazhang.cstoremanagement.bean.DistributionRequestResult
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.url.AppUrl
import com.cstore.zhiyazhang.cstoremanagement.utils.GsonUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.Companion.ERROR
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.Companion.SUCCESS
import com.cstore.zhiyazhang.cstoremanagement.utils.MyStringCallBack
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import com.google.gson.Gson
import com.zhy.http.okhttp.OkHttpUtils
import okhttp3.MediaType

/**
 * Created by zhiya.zhang
 * on 2018/5/22 13:49.
 */
class DistributionModel : DistributionInterface {
    override fun getDistribution(listener: MyListener) {
        OkHttpUtils
                .get()
                .url(AppUrl.GET_DISTRIBUTION)
//                .addHeader(AppUrl.STOREHEADER, "130902")
                .addHeader(AppUrl.STOREHEADER, User.getUser().storeId)
                .build()
                .execute(object : MyStringCallBack(listener) {
                    override fun onResponse(response: String, id: Int) {
                        listener.listenerSuccess(response)
                    }
                })
    }

    override fun getDistributionItem(shelvesId: String, disTime: String, listener: MyListener) {
        OkHttpUtils
                .get()
                .url(AppUrl.GET_DISTRIBUTION_ITEM)
//                .addHeader(AppUrl.STOREHEADER, "130902")
                .addHeader(AppUrl.STOREHEADER, User.getUser().storeId)
                .addHeader(AppUrl.SHELVES_ID, shelvesId)
                .addHeader(AppUrl.DISTIME, disTime)
                .build()
                .execute(object : MyStringCallBack(listener) {
                    override fun onResponse(response: String, id: Int) {
                        listener.listenerSuccess(response)
                    }
                })
    }

    override fun saveDistribution(shelvesId: String, disTime: String, data: DistributionRequestResult, listener: MyListener) {
        val d = Gson().toJson(data)
        OkHttpUtils
                .postString()
                .url(AppUrl.GET_DISTRIBUTION_SAVE)
                .content(d)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
//                .addHeader(AppUrl.STOREHEADER, "130902")
                .addHeader(AppUrl.STOREHEADER, User.getUser().storeId)
                .addHeader(AppUrl.SHELVES_ID, shelvesId)
                .addHeader(AppUrl.DISTIME, disTime)
                .build()
                .execute(object : MyStringCallBack(listener) {
                    override fun onResponse(response: String, id: Int) {
                        listener.listenerSuccess(response)
                    }
                })
    }

    override fun addDistributionItem(key: String, barDate: String, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val sql = MySql.getDistributionItem(key)
            val sqlResult = SocketUtil.initSocket(ip, sql).inquire()
            if (!SocketUtil.judgmentNull(sqlResult, msg, handler)) return@Runnable
            val db = ArrayList<DistributionItemBean>()
            try {
                db.addAll(GsonUtil.getDistributionItemBean(sqlResult))
            } catch (e: Exception) {
            }
            if (db.isEmpty()) {
                msg.obj = sqlResult
                msg.what = ERROR
            } else {
                val result = db[0]
                result.barContext = ArrayList()
                result.barContext!!.add(barDate)
                msg.obj = result
                msg.what = SUCCESS
            }
            handler.sendMessage(msg)
        }).start()
    }

}

interface DistributionInterface {
    /**
     * 获得配送点
     */
    fun getDistribution(listener: MyListener)

    /**
     * 获得商品明细
     */
    fun getDistributionItem(shelvesId: String, disTime: String, listener: MyListener)

    /**
     * 保存商品
     */
    fun saveDistribution(shelvesId: String, disTime: String, data: DistributionRequestResult, listener: MyListener)

    /**
     * 扫描添加商品
     */
    fun addDistributionItem(key: String, barDate: String, handler: MyHandler)
}