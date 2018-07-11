package com.cstore.zhiyazhang.cstoremanagement.utils

import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.Companion.ERROR
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2017/9/12 14:21.
 * 0营业换日  1会计换日  2订货换日  3验收换日  4情报换日  5订单上传换日  6打卡换日
 */
object CStoreCalendar {

    private val data: ArrayList<CStoreCalendarBean> = ArrayList()//换日数据
    private var date: String? = null//上一次执行获得换日的日期
    val SUCCESS_MSG = "success"
    val ERROR_MSG2 = "换日失败！请停止操作并退出重进应用！"
    val ERROR_MSG = "获得换日表失败,请停止操作并退出重进应用"

    fun getCStoreCalendar(): ArrayList<CStoreCalendarBean>? {
        return data
    }

    /**
     * 没开线程，是要在执行线程的时候执行
     */
    fun setCStoreCalendar(): String {
        var result = ""
        var i = 0
        while (result == "" || result == "[]" || result == "0") {
            result = SocketUtil.initSocket(MyApplication.getIP(), MySql.cstoreCalendar).inquire()
            i++
            if (i == 2) break
        }
        if (i == 2) {
            return ERROR_MSG
        }
        data.clear()
        val jsonData = ArrayList<CStoreCalendarBean>()
        try {
            jsonData.addAll(GsonUtil.getCstoreCalendar(result))
        } catch (e: Exception) {

        }
        if (jsonData.size == 0) {
            return ERROR_MSG
        }
        data.addAll(jsonData)
        date = MyTimeUtil.nowDate
        return SUCCESS_MSG
    }

    /**
     * 类内部获取换日异常时开线程去获得数据
     */
    private fun threadSetCStoreCalendar() {
        Thread(Runnable {
            var result = ""
            var i = 0
            while (result == "" || result == "[]" || result == "0") {
                result = SocketUtil.initSocket(MyApplication.getIP(), MySql.cstoreCalendar).inquire()
                i++
                if (i == 2) break
            }
            if (i == 2) return@Runnable
            val jsonData = ArrayList<CStoreCalendarBean>()
            try {
                jsonData.addAll(GsonUtil.getCstoreCalendar(result))
            } catch (e: Exception) {
            }
            if (jsonData.size == 0) return@Runnable
            data.addAll(jsonData)
            date = MyTimeUtil.nowDate
            return@Runnable
        }).start()
    }

    /**
     * 得到日期
     */
    @JvmStatic
    fun getCurrentDate(type: Int): String {
        if (data.isNotEmpty()) {
            //如果有的话在这里就已经return了
            data.filter { it.dateType == type }.forEach { return it.currentDate }
        }
        threadSetCStoreCalendar()
        return errorGetCurrentDate(type)
    }

    /**
     * 得到换日时间
     */
    fun getChangeTime(type: Int): Int {
        if (data.isNotEmpty()) {
            data.filter { it.dateType == type }.forEach { return getHourByString(it.changeTime) }
        }
        threadSetCStoreCalendar()
        return errorGetChangeTime(type)
    }

    /**
     * 用于获得换日日期异常后的获得方式
     */
    private fun errorGetCurrentDate(type: Int): String {
        val nowHour = MyTimeUtil.nowHour
        val nowDate = MyTimeUtil.nowDate
        val tomorrowDate = MyTimeUtil.tomorrowDate
        val dayAfterTomorrowDate = MyTimeUtil.dayAfterTomorrowDate
        val changeHour = errorGetChangeTime(type)
        //如果是订货换日本来就要加一天的，因此订货换日时间选择明天或后天
        val result = if (type != 2) (if (nowHour > changeHour) tomorrowDate else nowDate) else (if (nowHour > changeHour) dayAfterTomorrowDate else tomorrowDate)
        return result
    }

    /**
     * 用于获得换日日期或时间异常后的获得换日时间方式
     */
    private fun errorGetChangeTime(type: Int): Int {
        return when (type) {
            0 -> 23
            1 -> 17
            2 -> 5
            3 -> 18
            4 -> 23
            5 -> 18
            6 -> 12
            else -> 0
        }
    }


    fun getNowStatus(type: Int): Int {
        if (data.isNotEmpty()) {
            data.filter { it.dateType == type }.forEach { return it.sceodResult }
        }
        return 0
    }

    /**
     * 检查状态是否有换日失败的
     */
    fun judgmentStatus(): Boolean {
        if (data.isNotEmpty()) {
            data.filter { it.sceodResult == 2 }.forEach {
                return false
            }
        }
        threadSetCStoreCalendar()
        return true
    }

    /**
     * 把string时间截出时间来
     */
    private fun getHourByString(date: String): Int {
        return date.substring(0, 2).toInt()
    }

    /**
     * 判断是否能执行创建或修改操作
     */
    fun judgmentCalender(date: String, msg: Message, handler: MyHandler, type: Int): Boolean {
        if (CStoreCalendar.setCStoreCalendar() != SUCCESS_MSG) {
            msg.obj = ERROR_MSG
            msg.what = ERROR
            handler.sendMessage(msg)
            return false
        }
        //时间不对或或换日状态异常就报错
        if (CStoreCalendar.getNowStatus(type) != 0 || CStoreCalendar.getCurrentDate(type) != date) {
            val errorMsg = when {
                CStoreCalendar.getNowStatus(type) == 1 -> "正在换日中，请等待换日完成！"
                CStoreCalendar.getNowStatus(type) == 2 -> ERROR_MSG
                else -> "当前日期不能进行操作"
            }
            msg.obj = errorMsg
            msg.what = ERROR
            handler.sendMessage(msg)
            return false
        }
        return true
    }
}

data class CStoreCalendarBean(
        @SerializedName("storeid")
        val storeId: String,//店号
        @SerializedName("datetype")
        val dateType: Int,//换日类型
        @SerializedName("currentdate")
        val currentDate: String,//当前换日日期
        @SerializedName("changetime")
        val changeTime: String,//换日时间
        @SerializedName("sceodresult")
        val sceodResult: Int//换日是否成功 0:成功 1:正在换日 2:换日失败
) : Serializable