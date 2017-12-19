package com.cstore.zhiyazhang.cstoremanagement.model.adjustment

import android.os.Message
import android.util.Log
import com.cstore.zhiyazhang.cstoremanagement.bean.AdjustmentBean
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.cstore.zhiyazhang.cstoremanagement.utils.GsonUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil

/**
 * Created by zhiya.zhang
 * on 2017/9/26 14:28.
 */
class AdjustmentModel:AdjustmentInterface{
    private val TAG="AdjustmentModel"
    override fun getAllAdjustmentList(date: String, handler: MyHandler.OnlyMyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val result=SocketUtil.initSocket(ip,MySql.getAdjustmentList(date)).inquire()
            val abs=ArrayList<AdjustmentBean>()
            if (result=="[]"){
                msg.obj=abs
                msg.what= MyHandler.SUCCESS
                handler.sendMessage(msg)
                return@Runnable
            }
            try {
                abs.addAll(GsonUtil.getAdjustment(result))
            }catch (e:Exception){
                Log.e(TAG,e.message)
            }
            if (abs.isEmpty()){
                msg.obj=result
                msg.what= MyHandler.ERROR
                handler.sendMessage(msg)
            }else{
                msg.obj=abs
                msg.what=MyHandler.SUCCESS
                handler.sendMessage(msg)
            }
        }).start()
    }

    override fun searchAdjustment(searchMsg: String, handler: MyHandler.OnlyMyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val result=SocketUtil.initSocket(ip,MySql.searchAdjustment(CStoreCalendar.getCurrentDate(0),searchMsg)).inquire()
            if (!SocketUtil.judgmentNull(result,msg,handler))return@Runnable
            val abs=ArrayList<AdjustmentBean>()
            try {
                abs.addAll(GsonUtil.getAdjustment(result))
            }catch (e:Exception){
                Log.e(TAG,e.message)
            }
            if (abs.isEmpty()){
                msg.obj=result
                msg.what=MyHandler.ERROR
                handler.sendMessage(msg)
            }else{
                msg.obj=abs
                msg.what=MyHandler.SUCCESS
                handler.sendMessage(msg)
            }
        }).start()
    }

    override fun createAdjustment(data: ArrayList<AdjustmentBean>, handler: MyHandler.OnlyMyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            //得到换日时间
            val date = CStoreCalendar.getCurrentDate(0)
            //得到最大id
            val recodeNumber=getMaxRecodeNumber(date, ip, msg, handler)
            //-1代表出错，内部获得的时候已经处理过外部只要检查报错就结束
            if (recodeNumber==-1)return@Runnable
            val sql=getCreateSql(date, recodeNumber, data)
            val result=SocketUtil.initSocket(ip,sql).inquire()
            if (result=="0"){
                msg.obj=result
                msg.what=MyHandler.SUCCESS
                handler.sendMessage(msg)
            }else{
                msg.obj=result
                msg.what=MyHandler.ERROR
                handler.sendMessage(msg)
            }
        }).start()
    }

    /**
     * 得到最大的id用于创建
     */
    private fun getMaxRecodeNumber(date:String, ip: String, msg: Message, handler: MyHandler.OnlyMyHandler): Int {
        val result=SocketUtil.initSocket(ip,MySql.getAdjustmentMaxId(date)).inquire()
        val id: Int
        try {
            val ids=GsonUtil.getUtilBean(result)
            if (ids[0].value==null)return 0
            id=ids[0].value!!.toInt()
        }catch (e:Exception){
            msg.obj=result
            msg.what=MyHandler.ERROR
            handler.sendMessage(msg)
            return -1
        }
        return id
    }

    /**
     * 得到sql语句
     */
    private fun getCreateSql(date:String, recodeNumber:Int, data: ArrayList<AdjustmentBean>): String {
        var i=recodeNumber
        val result=StringBuilder()
        result.append(MySql.affairHeader)
        data.forEach {
            i++
            result.append(MySql.createAdjustment(it, date, i))
        }
        result.append(MySql.affairFoot)
        return result.toString()
    }

}

interface AdjustmentInterface{
    /**
     * 得到某天所有的库调信息
     */
    fun getAllAdjustmentList(date:String, handler:MyHandler.OnlyMyHandler)

    /**
     * 搜索库调商品
     */
    fun searchAdjustment(searchMsg:String, handler:MyHandler.OnlyMyHandler)

    /**
     * 创建库调
     */
    fun createAdjustment(data:ArrayList<AdjustmentBean>,handler: MyHandler.OnlyMyHandler)
}