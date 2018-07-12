package com.cstore.zhiyazhang.cstoremanagement.model.attendance

import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.bean.AttendanceRecordingBean
import com.cstore.zhiyazhang.cstoremanagement.bean.WorkHoursBean
import com.cstore.zhiyazhang.cstoremanagement.model.paiban.PaibanModel
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.GsonUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.Companion.ERROR
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.Companion.SUCCESS
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil

/**
 * Created by zhiya.zhang
 * on 2018/3/12 17:16.
 */
class AttendanceRecordingModel : AttendanceRecordingInterface {

    override fun getWorkHours(date: String, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val sql = MySql.getAttendanceWorkHours(date)
            val sqlResult = SocketUtil.initSocket(ip, sql).inquire()
            val result = ArrayList<WorkHoursBean>()
            if (sqlResult == "[]" || sqlResult == "") {
                msg.obj = result
                msg.what = SUCCESS
                handler.sendMessage(msg)
                return@Runnable
            }
            try {
                result.addAll(GsonUtil.getWorkHoursBean(sqlResult))
            } catch (e: Exception) {
            }
            if (result.size > 0) {
                msg.obj = result
                msg.what = SUCCESS
            } else {
                msg.obj = sqlResult
                msg.what = ERROR
            }
            handler.sendMessage(msg)
        }).start()
    }

    override fun getRecordingData(beginDate: String, endDate: String, workHours: Double, handler: MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val isPaiban = PaibanModel.getIsPaiban(ip, msg, handler) ?: return@Runnable
            val isShowManager = isPaiban == "Y"
            val sql = MySql.getAttendanceRecordingData(beginDate, endDate, workHours, isShowManager)
            val sqlResult = SocketUtil.initSocket(ip, sql).inquire()
            val result = ArrayList<AttendanceRecordingBean>()
            try {
                result.addAll(GsonUtil.getAttendanceRecordingBean(sqlResult))
            } catch (e: Exception) {
            }
            if (result.size > 0) {
                msg.obj = result
                msg.what = SUCCESS
            } else {
                msg.obj = sqlResult
                msg.what = ERROR
            }
            handler.sendMessage(msg)
        }).start()
    }

}

interface AttendanceRecordingInterface {
    /**
     * 获得工时
     * @param date yyyyMM格式日期
     */
    fun getWorkHours(date: String, handler: MyHandler)

    /**
     * 得到详细考勤数据
     */
    fun getRecordingData(beginDate: String, endDate: String, workHours: Double, handler: MyHandler)
}