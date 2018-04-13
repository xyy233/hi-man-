package com.cstore.zhiyazhang.cstoremanagement.model.personnel

import android.graphics.Bitmap
import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.*
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.OnlyMyHandler.ERROR
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.OnlyMyHandler.SUCCESS
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil

/**
 * Created by zhiya.zhang
 * on 2017/10/12 13:46.
 */
class CheckInModel : CheckInInterface {
    override fun checkInUser(type: Int, uId: String, bmp: Bitmap, handler: MyHandler.OnlyMyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            if (!judgmentUser(ip, uId, msg, handler)) return@Runnable

            val userData = SocketUtil.initSocket(ip, MySql.signIn(uId)).inquire()
            if (userData == "" || userData == "[]") {
                msg.obj = MyApplication.instance().applicationContext.getString(R.string.idError)
                msg.what = MyHandler.ERROR
                handler.sendMessage(msg)
                return@Runnable
            } else if (userData == "0") {
                msg.obj = MyApplication.instance().applicationContext.getString(R.string.socketError)
                msg.what = MyHandler.ERROR
                handler.sendMessage(msg)
                return@Runnable
            }
            val user = GsonUtil.getUser(userData)[0]
            val watermarkText = uId + "  ${user.name}" + "\n" + MyTimeUtil.nowTimeString
            val waterBmp = MyImage.createWatermark(bmp, watermarkText)
            val date = MyTimeUtil.nowTimeString2
            //这里创建文件夹的日期要加一，不知道为什么，但是在考勤里面拿资料的时候就是日期+1的
            val address = if (type == 0) {
                "fileput C:\\rtcvs\\arr_photo\\${MyTimeUtil.tomorrowDate2}\\${User.getUser().storeId + uId + date}.jpg\u0004"
            } else {
                "fileput C:\\rtcvs\\sign\\${MyTimeUtil.nowDate3}\\${User.getUser().storeId + uId + date}_daye.jpg\u0004"
            }
            val data = SocketUtil.initSocket(ip).inquire(waterBmp, address)
            if (data == "0") {
                if (type == 0) {
                    val nowResult = SocketUtil.initSocket(ip, MySql.getInsCheckIn(uId, date)).inquire()
                    if (nowResult == "1") {
                        msg.what = SUCCESS
                    } else
                        msg.what = ERROR
                    msg.obj = nowResult
                } else {
                    if (MyTimeUtil.nowHour >= 23) {
                        val ads = "fileput C:\\rtcvs\\sign\\${MyTimeUtil.tomorrowDate2}\\${User.getUser().storeId + uId + date}_daye.jpg\u0004"
                        val d = SocketUtil.initSocket(ip).inquire(waterBmp, ads)
                        if (d == "0") {
                            msg.what = SUCCESS
                        } else {
                            msg.what = ERROR
                        }
                        msg.obj = d
                    } else {
                        msg.what = SUCCESS
                        msg.obj = data
                    }
                }
            } else {
                msg.obj = data
                msg.what = ERROR
            }
            handler.sendMessage(msg)
        }).start()
    }

    /**
     * 检测输入的用户是否存在
     */
    private fun judgmentUser(ip: String, uId: String, msg: Message, handler: MyHandler.OnlyMyHandler): Boolean {
        val data = SocketUtil.initSocket(ip, MySql.signIn(uId)).inquire()
        return if (data == "" || data == "[]") {
            msg.obj = MyApplication.instance().applicationContext.getString(R.string.idError)
            msg.what = MyHandler.ERROR
            handler.sendMessage(msg)
            false
        } else if (data == "0") {
            msg.obj = MyApplication.instance().applicationContext.getString(R.string.socketError)
            msg.what = MyHandler.ERROR
            handler.sendMessage(msg)
            false
        } else true
    }

}

interface CheckInInterface {
    /**
     * 签到
     */
    fun checkInUser(type: Int, uId: String, bmp: Bitmap, handler: MyHandler.OnlyMyHandler)
}