/*
package com.cstore.zhiyazhang.cstoremanagement.model.scrap

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapContractBean
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zhiyazhang.mykotlinapplication.utils.MyApplication

*/
/**
 * Created by zhiya.zhang
 * on 2017/8/8 8:38.
 *//*

class BarCodeScrapModel:BarCodeScrapInterface{
    override fun getScrap(user: User, barCode: String, myListener: MyListener) {
        val ip = MyApplication.getIP()
        if (ip == MyApplication.instance().getString(R.string.notFindIP)) {
            myListener.listenerFailed(ip)
            return
        }
        SocketUtil.getSocketUtil(ip).inquire(MySql.getScrapByMessage(barCode), 10, @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> try {
                        if (msg.obj as String == "" || msg.obj as String == "[]") {
                            myListener.listenerFailed(MyApplication.instance().applicationContext.resources.getString(R.string.noMessage))
                        } else {
                            myListener.listenerSuccess(Gson().fromJson<ArrayList<ScrapContractBean>>(msg.obj as String, object : TypeToken<ArrayList<ScrapContractBean>>() {}.type)[0])
                        }
                    } catch (e: Exception) {
                        myListener.listenerFailed(msg.obj as String)
                    }
                    else -> myListener.listenerFailed(msg.obj as String)
                }
            }
        })
    }

}

interface BarCodeScrapInterface{
    fun getScrap(user:User,barCode:String,myListener: MyListener)
}
*/
