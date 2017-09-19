package com.cstore.zhiyazhang.cstoremanagement.utils

import android.os.Looper
import android.os.Message
import android.util.Log
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.MyHandler.ERROR1
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2017/9/12 14:21.
 * 0营业换日  1会计换日  2订货换日  3验收换日  4情报换日  5订单上传换日  6打卡换日
 */
object CStoreCalendar{

    private var data:ArrayList<CStoreCalendarBean>?=null//换日数据
    private var date:String?=null//上一次执行获得换日的日期
    val SUCCESS_MSG="success"
    private val ERROR_MSG="获得换日表失败,请停止操作并退出重进应用或联系系统部"

    /**
     * 没开线程，是要在执行线程的时候执行
     */
    fun setCStoreCalendar():String{
        var result=""
        var i=0
        while (result==""||result=="[]"||result=="0"){
            result= SocketUtil.initSocket(MyApplication.getIP(), MySql.cstoreCalendar).inquire()
            i++
            if (i==2)break
        }
        if (i==2){
            Log.e("CStoreCalendar","获得换日表失败")
            Looper.prepare()
            Looper.loop()
            return ERROR_MSG
        }
        data=SocketUtil.getCstoreCalendar(result)
        date=MyTimeUtil.nowDate
        return SUCCESS_MSG
    }

    fun getCurrentDate(type:Int):String{
        try {
            //有的话这里就结束了
            data?.filter { it.dateType==type }?.forEach { return it.currentDate }
        }catch (e:Exception){
            Log.e("CStoreCalendar",e.message)
        }
        MyToast.getLongToast(ERROR_MSG)
        return "type=$type error!"
    }

    fun getChangeTime(type:Int):Int{
        try {
            data?.filter{it.dateType==type}?.forEach { return getHourByString(it.changeTime) }
        }catch (e:Exception){
            Log.e("CStoreCalendar",e.message)
        }
        MyToast.getLongToast(ERROR_MSG)
        return 0
    }

    fun getNowStatus(type:Int):Int{
        try {
            data?.filter{it.dateType==type}?.forEach { return it.sceodResult }
        }catch (e:Exception){
            Log.e("CStoreCalendar",e.message)
        }
        MyToast.getLongToast(ERROR_MSG)
        return 0
    }

    /**
     * 把string时间截出时间来
     */
    private fun getHourByString(date:String):Int{
        return date.substring(0,2).toInt()
    }

    /**
     * 判断是否能执行创建或修改操作
     */
    fun judgmentCalender(date:String, msg: Message, handler: MyHandler.MyHandler, type:Int):Boolean{
        if (CStoreCalendar.setCStoreCalendar()!= SUCCESS_MSG){
            msg.obj= ERROR_MSG
            msg.what= ERROR1
            handler.sendMessage(msg)
            return false
        }
        //时间不对或或换日状态异常就报错
        if (CStoreCalendar.getNowStatus(type)!=0||CStoreCalendar.getCurrentDate(type)!=date){
            var errorMsg=""
            if (CStoreCalendar.getNowStatus(type)==1){
                errorMsg="正在换日中，请等待换日完成！"
            }else if (CStoreCalendar.getNowStatus(type)==2){
                errorMsg="换日失败，请停止操作并联系系统部！"
            }else{
                errorMsg="当前日期不能进行操作"
            }
            msg.obj=errorMsg
            msg.what= ERROR1
            handler.sendMessage(msg)
            return false
        }
        return true
    }
}

data class CStoreCalendarBean(
        @SerializedName("storeid")
        val storeId:String,//店号
        @SerializedName("datetype")
        val dateType:Int,//换日类型
        @SerializedName("currentdate")
        val currentDate:String,//当前换日日期
        @SerializedName("changetime")
        val changeTime:String,//换日时间
        @SerializedName("sceodresult")
        val sceodResult:Int//换日是否成功 0:成功 1:正在换日 2:换日失败
):Serializable