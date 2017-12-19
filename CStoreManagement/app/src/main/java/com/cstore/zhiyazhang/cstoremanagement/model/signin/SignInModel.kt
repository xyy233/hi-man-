package com.cstore.zhiyazhang.cstoremanagement.model.signin

import android.os.Looper
import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.sql.ContractTypeDao
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.cstore.zhiyazhang.cstoremanagement.utils.GsonUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.OnlyMyHandler.ERROR
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.OnlyMyHandler.SUCCESS
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil

/**
 * Created by zhiya.zhang
 * on 2017/6/7 17:22.
 */
class SignInModel : SignInInterface {

    override fun login( uid:String, password:String, myHandler: MyHandler.OnlyMyHandler) {
        Thread(Runnable {
            Looper.prepare()
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, myHandler)) return@Runnable
            val data = SocketUtil.initSocket(ip, MySql.signIn(uid)).inquire()
            if (!SocketUtil.judgmentNull(data, msg, myHandler)) return@Runnable

            val users = ArrayList<User>()
            try {
                users.addAll(GsonUtil.getUser(data))
            } catch (e: Exception) { //不进行操作，无法转换就代表数据错误，直接在下面的错误抛出
            }
            if (users.isEmpty()) {//不是用户信息
                msg.obj = data
                msg.what = ERROR
                myHandler.sendMessage(msg)
            } else if (users[0].password != password) {
                msg.obj = MyApplication.instance().applicationContext.resources.getString(R.string.pwdError)//密码错误
                msg.what = ERROR
                myHandler.sendMessage(msg)
            } else {
                //判断和之前的用户店号是否一样，不一样要删除本地数据库
                val oldUser = User.getUser()
                if (oldUser.storeId != users[0].storeId) {
                    val cd = ContractTypeDao(MyApplication.instance().applicationContext)
                    cd.editSQL(null, "deleteTable")
                }
                val calendar = CStoreCalendar.setCStoreCalendar()
                if (calendar == CStoreCalendar.SUCCESS_MSG) {
                    if (!CStoreCalendar.judgmentStatus()){
                        msg.obj = CStoreCalendar.ERROR_MSG2
                        msg.what = ERROR
                        myHandler.sendMessage(msg)
                    }else{
                        msg.obj = users[0]
                        msg.what = SUCCESS
                        myHandler.sendMessage(msg)
                    }
                } else {
                    msg.obj = calendar
                    msg.what = ERROR
                    myHandler.sendMessage(msg)
                }
            }
            Looper.loop()
        }).start()
    }
}

interface SignInInterface {
    fun login( uid:String, password:String, myHandler: MyHandler.OnlyMyHandler)
}