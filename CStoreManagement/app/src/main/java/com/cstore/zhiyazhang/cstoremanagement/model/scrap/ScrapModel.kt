package com.cstore.zhiyazhang.cstoremanagement.model.scrap

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapContractBean
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import com.zhiyazhang.mykotlinapplication.utils.MyApplication

/**
 * Created by zhiya.zhang
 * on 2017/8/21 16:20.
 */
class ScrapModel:ScrapInterface{

    override fun getAllScrap(date:String, myListener: MyListener) {
        val ip = MyApplication.getIP()
        if (ip == MyApplication.instance().getString(R.string.notFindIP)) {
            myListener.listenerFailed(ip)
            return
        }
        SocketUtil().writeIP(ip).inquire(MySql.AllScrap(date),10, @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> try {
                        if (msg.obj as String != "" && msg.obj as String != "[]") {
                            myListener.listenerSuccess(msg.obj as String)
                        }else myListener.listenerFailed(MyApplication.instance().applicationContext.resources.getString(R.string.noMessage))
                    } catch (e: Exception) {
                        myListener.listenerFailed(msg.obj as String)
                    }
                    else -> myListener.listenerFailed(msg.obj as String)
                }
            }
        })
    }

    override fun searchScrap(message: String, myListener: MyListener) {
        val ip = MyApplication.getIP()
        if (ip == MyApplication.instance().getString(R.string.notFindIP)) {
            myListener.listenerFailed(ip)
            return
        }
        SocketUtil().writeIP(ip).inquire(MySql.getScrapByMessage(message),10, @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> try {
                        if (msg.obj as String != "" && msg.obj as String != "[]") {
                           myListener.listenerSuccess(msg.obj as String)
                        }else{
                            myListener.listenerFailed(MyApplication.instance().applicationContext.resources.getString(R.string.noMessage))
                        }
                    } catch (e: Exception) {
                        myListener.listenerFailed(msg.obj as String)
                    }
                    else -> myListener.listenerFailed(msg.obj as String)
                }
            }
        })
    }

    override fun submitScraps(data: ArrayList<ScrapContractBean>, reCode:Int, myListener: MyListener) {
        val ip = MyApplication.getIP()
        if (ip == MyApplication.instance().getString(R.string.notFindIP)) {
            myListener.listenerFailed(ip)
            return
        }
        if (data.size<1){
            myListener.listenerFailed(MyApplication.instance().applicationContext.resources.getString(R.string.noEditMsg))
            return
        }
        val sql=getSubmitString(data, reCode)
        SocketUtil().writeIP(ip).inquire(sql,10, @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> try {
                        if (msg.obj as String != "" && msg.obj as String != "[]") {
                            myListener.listenerSuccess("")
                        }else{
                            myListener.listenerFailed(MyApplication.instance().applicationContext.resources.getString(R.string.socketError))
                        }
                    } catch (e: Exception) {
                        myListener.listenerFailed(msg.obj as String)
                    }
                    else -> myListener.listenerFailed(msg.obj as String)
                }
            }
        })
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
    fun getAllScrap(date:String, myListener: MyListener)

    /**
     * 搜索报废品
     */
    fun searchScrap(message:String,myListener: MyListener)

    /**
     * 提交所有报废
     */
    fun submitScraps(data:ArrayList<ScrapContractBean>, reCode: Int, myListener: MyListener)
}