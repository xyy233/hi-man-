package com.cstore.zhiyazhang.cstoremanagement.utils

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.OnlyMyHandler.ERROR
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
    val ERROR_MSG2 = "换日失败！请停止操作并退出重进应用或联系系统部！"
    val ERROR_MSG = "获得换日表失败,请停止操作并退出重进应用或联系系统部"

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
            Log.e("CStoreCalendar", "获得换日表失败")
            Looper.prepare()
            Looper.loop()
            return ERROR_MSG
        }
        data.clear()
        data.addAll(GsonUtil.getCstoreCalendar(result))
        date = MyTimeUtil.nowDate
        return SUCCESS_MSG
    }

    /**
     * 得到日期
     */
    @JvmStatic
    fun getCurrentDate(type: Int): String {
        if (data.isNotEmpty()){
            //如果有的话在这里就已经return了
            data.filter { it.dateType == type }.forEach { return it.currentDate }
        }
        Handler().post { MyToast.getLongToast(ERROR_MSG) }
        return "type=$type, data=$data error!"
    }

    /**
     * 得到换日日期
     */
    fun getChangeTime(type: Int): Int {
        if (data.isNotEmpty()){
            data.filter { it.dateType == type }.forEach { return getHourByString(it.changeTime) }
        }
        Handler().post { MyToast.getLongToast(ERROR_MSG) }
        return 0
    }


    fun getNowStatus(type: Int): Int {
        if (data.isNotEmpty()) {
            data.filter { it.dateType == type }.forEach { return it.sceodResult }
        }
        Handler().post { MyToast.getLongToast(ERROR_MSG) }
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
        } else {
            Handler().post { MyToast.getLongToast(ERROR_MSG) }
            return false
        }
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
    fun judgmentCalender(date: String, msg: Message, handler: MyHandler.OnlyMyHandler, type: Int): Boolean {
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