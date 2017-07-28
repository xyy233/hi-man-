package com.cstore.zhiyazhang.cstoremanagement.model.signin

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zhiyazhang.mykotlinapplication.utils.MyApplication
import java.lang.Exception

/**
 * Created by zhiya.zhang
 * on 2017/6/7 17:22.
 */
class SignInModel : SignInInterface {
    override fun login(uid: String, password: String, listener: MyListener) {
        val ip = MyApplication.getIP()
        if (ip == MyApplication.instance().getString(R.string.notFindIP)){
            listener.contractFailed(ip)
            return
        }
        SocketUtil.getSocketUtil(ip).inquire(MySql.SignIn(uid), 10, @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> try {
                        if (msg.obj as String == "" || msg.obj as String == "[]") {
                            listener.contractFailed(MyApplication.instance().applicationContext.resources.getString(R.string.idError))
                        }else{
                            val users = Gson().fromJson<List<User>>(msg.obj as String, object : TypeToken<List<User>>() {

                            }.type)
                            if (users[0].password != password) {
                                listener.contractFailed(MyApplication.instance().applicationContext.resources.getString(R.string.pwdError))
                            } else {
                                listener.contractSuccess(users[0])
                            }
                        }
                    } catch (e: Exception) {
                        listener.contractFailed(msg.obj as String)
                    }
                    else->listener.contractFailed(msg.obj as String)
                }
            }
        })
    }
}

interface SignInInterface {
    fun login(uid: String, password: String, listener: MyListener)
}