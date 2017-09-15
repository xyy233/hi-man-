package com.cstore.zhiyazhang.cstoremanagement.model.cashdaily

import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.bean.CashDailyBean
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.MyHandler.ERROR1
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.MyHandler.SUCCESS
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import com.cstore.zhiyazhang.cstoremanagement.view.cashdaily.CashDailyFragment

/**
 * Created by zhiya.zhang
 * on 2017/9/5 9:25.
 */
class CashDailyModel:CashDailyInterface{
    override fun getAllCashDaily(date: String, handler: MyHandler.MyHandler) {
        Thread(Runnable {
            val msg=Message()
            val ip= MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip,msg,handler))return@Runnable
            val result=SocketUtil.initSocket(ip,MySql.cashDaily()+MySql.getAllCashDaily(date)).inquire()
            if (!SocketUtil.judgmentNull(result,msg,handler))return@Runnable

            val cds=ArrayList<CashDailyBean>()
            try {
                cds.addAll(SocketUtil.getCashDaily(result))
            }catch (e:Exception){}
            if (cds.isEmpty()){
                msg.obj=result
                msg.what=ERROR1
                handler.sendMessage(msg)
            }else{
                val fragments=ArrayList<CashDailyFragment>()
                fragments.add(CashDailyFragment.newInstance(9,(cds.filter { it.cdType=="10"&&it.cdId!="1097"&&it.cdId!="1098"&&it.cdId!="1100"&&it.cdId!="1099" } as ArrayList<CashDailyBean>),date))
                fragments.add(CashDailyFragment.newInstance(0,(cds.filter { it.cdType=="0" } as ArrayList<CashDailyBean>),date))
                fragments.add(CashDailyFragment.newInstance(1,(cds.filter { it.cdType=="1" } as ArrayList<CashDailyBean>),date))
                fragments.add(CashDailyFragment.newInstance(2,(cds.filter { it.cdType=="2" } as ArrayList<CashDailyBean>),date))
                fragments.add(CashDailyFragment.newInstance(3,(cds.filter { it.cdType=="3" } as ArrayList<CashDailyBean>),date))
                fragments.add(CashDailyFragment.newInstance(4,(cds.filter { it.cdType=="5" } as ArrayList<CashDailyBean>),date))
                fragments.add(CashDailyFragment.newInstance(5,(cds.filter { it.cdType=="6" } as ArrayList<CashDailyBean>),date))
                fragments.add(CashDailyFragment.newInstance(6,(cds.filter { it.cdType=="7" } as ArrayList<CashDailyBean>),date))
                fragments.add(CashDailyFragment.newInstance(7,(cds.filter { it.cdType=="8" } as ArrayList<CashDailyBean>),date))
                fragments.add(CashDailyFragment.newInstance(8,(cds.filter { it.cdType=="9" } as ArrayList<CashDailyBean>),date))
                fragments.add(CashDailyFragment.newInstance(10,(cds.filter { it.cdId=="1097"||it.cdId=="1098"||it.cdId=="1100"||it.cdId=="1099" } as ArrayList<CashDailyBean>),date))
                msg.obj=fragments
                msg.what=SUCCESS
                Thread.sleep(1000)
                handler.sendMessage(msg)
            }
        }).start()
    }

    override fun updateCashDaily(date: String, value: String, cd: CashDailyBean, handler: MyHandler.MyHandler) {
        Thread(Runnable {
            val msg=Message()
            val ip= MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip,msg,handler))return@Runnable
            if (!CStoreCalendar.judgmentCalender(date, msg, handler, 1)) return@Runnable
            val sql=getSql(cd,value)
            val result=SocketUtil.initSocket(ip,sql).inquire()
            if (result=="1"){
                Thread.sleep(1000)
                msg.obj=result
                msg.what= SUCCESS
                handler.sendMessage(msg)
            }else{
                msg.obj=result
                msg.what= ERROR1
                handler.sendMessage(msg)
            }
        }).start()
    }

    private fun getSql(cd: CashDailyBean, value: String): String {
        if (cd.cdId=="1100")return MySql.updateCashDaily2(cd.cdId,value) else return MySql.updateCashDaily(cd.cdId,value)
    }

}

interface CashDailyInterface{
    fun getAllCashDaily(date:String, handler:MyHandler.MyHandler)

    fun updateCashDaily(date:String, value:String, cd:CashDailyBean,handler:MyHandler.MyHandler)
}