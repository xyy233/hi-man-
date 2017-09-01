package com.cstore.zhiyazhang.cstoremanagement.model.signin

import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.sql.ContractTypeDao
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.MyHandler.ERROR1
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.MyHandler.SUCCESS
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.SignInView
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication

/**
 * Created by zhiya.zhang
 * on 2017/6/7 17:22.
 */
class SignInModel : SignInInterface {

    override fun login(sView: SignInView, gView: GenericView, myHandler: MyHandler.MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip,msg,myHandler))return@Runnable
            val data = SocketUtil.initSocket(ip, MySql.SignIn(sView.uid), 10).inquire()
            if (!SocketUtil.judgmentNull(data,msg,myHandler))return@Runnable

            val users = ArrayList<User>()
            try {
                users.addAll(SocketUtil.getUser(data))
            }catch (e:Exception) { //不进行操作，无法转换就代表数据错误，直接在下面的错误抛出
            }
            if (users.isEmpty()) {//不是用户信息
                msg.obj = data
                msg.what = ERROR1
                myHandler.sendMessage(msg)
            } else if (users[0].password != sView.password) {
                msg.obj = MyApplication.instance().applicationContext.resources.getString(R.string.pwdError)//密码错误
                msg.what = ERROR1
                myHandler.sendMessage(msg)
            } else {
                //判断和之前的用户店号是否一样，不一样要删除本地数据库
                val oldUser = User.getUser()
                if (oldUser.storeId != users[0].storeId) {
                    val cd = ContractTypeDao(MyApplication.instance().applicationContext)
                    cd.editSQL(null, "deleteTable")
                }
                if (oldUser.uId != users[0].uId) {
                    sView.saveUser(users[0])
                }
                msg.obj = users[0]
                msg.what = SUCCESS
                myHandler.sendMessage(msg)
            }

        }).start()
    }
}

interface SignInInterface {
    fun login(sView: SignInView, gView: GenericView, myHandler: MyHandler.MyHandler)
}