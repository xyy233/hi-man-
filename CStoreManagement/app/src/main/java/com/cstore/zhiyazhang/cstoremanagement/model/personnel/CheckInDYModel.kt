package com.cstore.zhiyazhang.cstoremanagement.model.personnel

import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.bean.CheckInBean
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.Companion.ERROR
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.Companion.SUCCESS
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil

/**
 * Created by zhiya.zhang
 * on 2018/3/29 11:41.
 */
class CheckInDYModel : CheckInDYInterface {
    override fun getPhotoByDate(date: String, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val path = "C:\\rtcvs\\sign\\$date"
            val dataPath = SocketUtil.initSocket(ip).inquireF(path)
            val result = ArrayList<CheckInBean>()
            if (dataPath == null) {
                msg.obj = "获得照片异常"
                msg.what = ERROR
                handler.sendMessage(msg)
                return@Runnable
            } else if (dataPath.isEmpty()) {
                msg.obj = result
                msg.what = SUCCESS
                handler.sendMessage(msg)
                return@Runnable
            }
            dataPath.forEach {
                val btm = SocketUtil.initSocket(ip).inquire(path + "\\" + it)
                if (btm != null) result.add(CheckInBean(it, btm))
            }
            result.sortByDescending { it.fileName }
            msg.obj = result
            msg.what = SUCCESS
            handler.sendMessage(msg)
        }).start()
    }

}

interface CheckInDYInterface {
    fun getPhotoByDate(date: String, handler: MyHandler)
}