package com.cstore.zhiyazhang.cstoremanagement.model.ordercategory

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.OrderCategoryBean
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zhiyazhang.mykotlinapplication.utils.MyApplication

/**
 * Created by zhiya.zhang
 * on 2017/7/25 13:43.
 */
class OrderCategoryModel : OrderCategoryInterface {
    override fun getAllCategory(user: User, listener: MyListener) {
        val ip = MyApplication.getIP()
        if (ip == MyApplication.instance().getString(R.string.notFindIP)) {
            listener.contractFailed(ip)
            return
        }
        SocketUtil.getSocketUtil(ip).inquire(MySql.getAllCategory(), 10, @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> try {
                        when (msg.obj as String) {
                            "" -> listener.contractFailed(MyApplication.instance().applicationContext.resources.getString(R.string.noMessage))
                            "[]" -> listener.contractFailed(MyApplication.instance().applicationContext.resources.getString(R.string.noMessage))
                            else -> {
                                listener.contractSuccess(Gson().fromJson<ArrayList<OrderCategoryBean>>(msg.obj as String, object : TypeToken<ArrayList<OrderCategoryBean>>() {}.type))
                            }
                        }
                    } catch (e: Exception) {
                        listener.contractFailed(msg.obj as String)
                    }
                    else -> listener.contractFailed(msg.obj as String)
                }
            }
        })
    }

}

interface OrderCategoryInterface {
    fun getAllCategory(user: User, listener: MyListener)
}