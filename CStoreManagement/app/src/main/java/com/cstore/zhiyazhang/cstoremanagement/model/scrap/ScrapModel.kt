package com.cstore.zhiyazhang.cstoremanagement.model.scrap

import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapContractBean
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.MyHandler.ERROR1
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.MyHandler.SUCCESS
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.ScrapView
import com.zhiyazhang.mykotlinapplication.utils.MyApplication

/**
 * Created by zhiya.zhang
 * on 2017/8/21 16:20.
 */
class ScrapModel(private val gView: GenericView, private val sView:ScrapView):ScrapInterface{

    override fun getAllScrap(handler: MyHandler.MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip,msg,handler))return@Runnable
            val data=SocketUtil.initSocket(ip,MySql.AllScrap(sView.getDate())).inquire()
            if (!SocketUtil.judgmentNull(data,msg,handler))return@Runnable

            val scraps=ArrayList<ScrapContractBean>()
            try {
                scraps.addAll(SocketUtil.getScrap(data))
            }catch (e:Exception){}
            if (scraps.isEmpty()){
                msg.obj = data
                msg.what = ERROR1
                handler.sendMessage(msg)
            }else{
                msg.obj=scraps
                msg.what=SUCCESS
                handler.sendMessage(msg)
            }
        }).start()
    }

    override fun searchScrap(message: String, handler: MyHandler.MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip,msg,handler))return@Runnable
            val data=SocketUtil.initSocket(ip,MySql.getScrapByMessage(message)).inquire()
            if (!SocketUtil.judgmentNull(data,msg,handler))return@Runnable

            val scraps=ArrayList<ScrapContractBean>()
            try {
                scraps.addAll(SocketUtil.getScrap(data))
            }catch (e:Exception){}
            if (scraps.isEmpty()){
                msg.obj = data
                msg.what = ERROR1
                handler.sendMessage(msg)
            }else{
                msg.obj=scraps
                msg.what=SUCCESS
                handler.sendMessage(msg)
            }
        }).start()
    }

    override fun submitScraps(data: ArrayList<ScrapContractBean>, reCode:Int, handler: MyHandler.MyHandler) {
        Thread(Runnable {
            val msg = Message()
            if (data.size<1){
                msg.obj=MyApplication.instance().applicationContext.resources.getString(R.string.noEditMsg)
                msg.what=ERROR1
                handler.sendMessage(msg)
                return@Runnable
            }
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip,msg,handler))return@Runnable
            val sql=getSubmitString(data, reCode)
            val sqlData=SocketUtil.initSocket(ip,sql).inquire()
            if (!SocketUtil.judgmentNull(sqlData,msg,handler))return@Runnable
            if (sqlData=="0"){
                msg.obj="0"
                msg.what=SUCCESS
                handler.sendMessage(msg)
            }else{
                msg.obj=sqlData
                msg.what= ERROR1
                handler.sendMessage(msg)
            }
        }).start()
    }

    /**
     * 从list中得到要用的sql语句
     */
    fun getSubmitString(data: ArrayList<ScrapContractBean>, reCode: Int):String{
        val result:StringBuilder=StringBuilder()
        result.append(MySql.affairHeader)
        val a=data.sortedByDescending { it.recordNumber }[0].recordNumber
        val b=reCode
        var i=0
        if (a>b)i=a else i=b
        data.forEach {
            //0==insert 1==update 2==delete 3==none
                when(it.action){
                    0->{
                        i++
                        it.recordNumber=i
                        result.append(MySql.insertScrap(it))
                    }
                    1->result.append(MySql.updateScrap(it))
                    2->result.append(MySql.deleteScrap(it))
                }
        }
        result.append(MySql.affairFoot)
        return result.toString()
    }
}

interface ScrapInterface{
    /**
     * 得到当前所有的报废信息
     */
    fun getAllScrap(handler:MyHandler.MyHandler)

    /**
     * 搜索报废品
     */
    fun searchScrap(message:String,handler:MyHandler.MyHandler)

    /**
     * 提交所有报废
     */
    fun submitScraps(data:ArrayList<ScrapContractBean>, reCode: Int, handler:MyHandler.MyHandler)
}