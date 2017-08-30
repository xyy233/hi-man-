package com.cstore.zhiyazhang.cstoremanagement.model.ordercategory

import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.bean.*
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.MyHandler.ERROR1
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.MyHandler.SUCCESS
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import com.zhiyazhang.mykotlinapplication.utils.MyApplication

/**
 * Created by zhiya.zhang
 * on 2017/7/25 13:43.
 * msg每次都要重置，ip每次都要检查
 */
class OrderCategoryModel() : OrderCategoryInterface {

    override fun getAllCategory(handler: MyHandler.MyHandler) {
        Thread(Runnable {
            val msg= Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip,msg,handler))return@Runnable
            val result=SocketUtil.initSocket(ip,MySql.getAllCategory()).inquire()
            if (!SocketUtil.judgmentNull(result,msg,handler))return@Runnable

            val category=ArrayList<OrderCategoryBean>()
            try {
                category.addAll(SocketUtil.getCategory(result))
            }catch (e:Exception){}
            if (category.isEmpty()){
                msg.obj=result
                msg.what=ERROR1
                handler.sendMessage(msg)
            }else{
                msg.obj=category
                msg.what= SUCCESS
                handler.sendMessage(msg)
            }
        }).start()
    }

    override fun getAllShelf(handler: MyHandler.MyHandler) {
        Thread(Runnable {
            val msg= Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip,msg,handler))return@Runnable
            val result=SocketUtil.initSocket(ip,MySql.getAllShelf).inquire()
            if (!SocketUtil.judgmentNull(result,msg,handler))return@Runnable

            val shelf=ArrayList<ShelfBean>()
            try {
                shelf.addAll(SocketUtil.getShelf(result))
            }catch (e:Exception){}
            if (shelf.isEmpty()){
                msg.obj=result
                msg.what=ERROR1
                handler.sendMessage(msg)
            }else{
                msg.obj=shelf
                msg.what= SUCCESS
                handler.sendMessage(msg)
            }
        }).start()
    }

    override fun getNewItemId(handler: MyHandler.MyHandler) {
        Thread(Runnable {
            val msg= Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip,msg,handler))return@Runnable
            val result=SocketUtil.initSocket(ip,MySql.getNewItemId).inquire()
            if (!SocketUtil.judgmentNull(result,msg,handler))return@Runnable

            val nop=ArrayList<NOPBean>()
            try {
                nop.addAll(SocketUtil.getNOP(result))
            }catch (e:Exception){}
            if (nop.isEmpty()){
                msg.obj=result
                msg.what=ERROR1
                handler.sendMessage(msg)
            }else{
                msg.obj=nop
                msg.what= SUCCESS
                handler.sendMessage(msg)
            }
        }).start()
    }

    override fun getSelf(handler: MyHandler.MyHandler) {
        Thread(Runnable {
            val msg= Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip,msg,handler))return@Runnable
            val result=SocketUtil.initSocket(ip,MySql.getSelf).inquire()
            if (!SocketUtil.judgmentNull(result,msg,handler))return@Runnable

            val self=ArrayList<SelfBean>()
            try {
                self.addAll(SocketUtil.getSelf(result))
            }catch (e:Exception){}
            if (self.isEmpty()){
                msg.obj=result
                msg.what=ERROR1
                handler.sendMessage(msg)
            }else{
                msg.obj=self
                msg.what= SUCCESS
                handler.sendMessage(msg)
            }
        }).start()
    }

    override fun getFresh(freshType: Int, handler: MyHandler.MyHandler) {
        Thread(Runnable {
            val msg= Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip,msg,handler))return@Runnable
            val result=SocketUtil.initSocket(ip,if (freshType==1)MySql.getFreshGroup1 else MySql.getFreshGroup2).inquire()
            if (!SocketUtil.judgmentNull(result,msg,handler))return@Runnable

            val fresh=ArrayList<FreshGroup>()
            try {
                fresh.addAll(SocketUtil.getFresh(result))
            }catch (e:Exception){}
            if (fresh.isEmpty()){
                msg.obj=result
                msg.what=ERROR1
                handler.sendMessage(msg)
            }else{
                msg.obj=fresh
                msg.what= SUCCESS
                handler.sendMessage(msg)
            }
        }).start()
    }
}

interface OrderCategoryInterface {
    fun getAllCategory(handler:MyHandler.MyHandler)
    fun getAllShelf(handler:MyHandler.MyHandler)
    fun getNewItemId(handler:MyHandler.MyHandler)
    fun getSelf(handler:MyHandler.MyHandler)
    fun getFresh(freshType: Int, handler:MyHandler.MyHandler)
}