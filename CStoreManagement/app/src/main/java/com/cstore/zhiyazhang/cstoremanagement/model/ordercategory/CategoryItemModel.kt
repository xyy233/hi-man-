package com.cstore.zhiyazhang.cstoremanagement.model.ordercategory

import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.bean.CategoryItemBean
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.OnlyMyHandler.ERROR1
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.OnlyMyHandler.SUCCESS
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil

/**
 * Created by zhiya.zhang
 * on 2017/7/27 16:22.
 */
class CategoryItemModel : CategoryInterface {

    /**
     * 通过categoryId获得item
     */
    override fun getAllItemByCategory(categoryId: String, orderBy: String, handler: MyHandler.OnlyMyHandler) {
        Thread(Runnable {
            val msg=Message()
            val ip= MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip,msg,handler))return@Runnable

            val result=
                    if (categoryId=="-1")SocketUtil.initSocket(ip,MySql.getItemByEditCategory(orderBy)).inquire()
                    else SocketUtil.initSocket(ip,MySql.getItemByCategoryId(categoryId,orderBy)).inquire()

            if (!SocketUtil.judgmentNull(result,msg,handler))return@Runnable

            val items=ArrayList<CategoryItemBean>()
            try {
                items.addAll(SocketUtil.getCategoryItem(result))
            }catch (e:Exception){}
            if (items.isEmpty()){
                msg.obj=result
                msg.what=ERROR1
                handler.sendMessage(msg)
            }else{
                msg.obj=items
                msg.what=SUCCESS
                handler.sendMessage(msg)
            }
        }).start()
    }

    /**
     * 根据货架id获得商品
     */
    override fun getAllItemByShelf(shelfId: String, orderBy: String, handler: MyHandler.OnlyMyHandler) {
        Thread(Runnable {
            val msg=Message()
            val ip= MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip,msg,handler))return@Runnable
            val result=SocketUtil.initSocket(ip,MySql.getItemByShelfId(shelfId,orderBy)).inquire()
            if (!SocketUtil.judgmentNull(result,msg,handler))return@Runnable

            val items=ArrayList<CategoryItemBean>()
            try {
                items.addAll(SocketUtil.getCategoryItem(result))
            }catch (e:Exception){}
            if (items.isEmpty()){
                msg.obj=result
                msg.what=ERROR1
                handler.sendMessage(msg)
            }else{
                msg.obj=items
                msg.what=SUCCESS
                handler.sendMessage(msg)
            }
        }).start()
    }

    /**
     * 根据关键字获得单品
     */
    override fun getUnitItemByKeywords(keywords: String, handler: MyHandler.OnlyMyHandler) {
        Thread(Runnable {
        val msg=Message()
        val ip= MyApplication.getIP()
        if (!SocketUtil.judgmentIP(ip,msg,handler))return@Runnable
        val result=SocketUtil.initSocket(ip,MySql.unitOrder(keywords)).inquire()
        if (!SocketUtil.judgmentNull(result,msg,handler))return@Runnable

        val items=ArrayList<CategoryItemBean>()
        try {
            items.addAll(SocketUtil.getCategoryItem(result))
        }catch (e:Exception){}
        if (items.isEmpty()){
            msg.obj=result
            msg.what=ERROR1
            handler.sendMessage(msg)
        }else{
            msg.obj=items
            msg.what=SUCCESS
            handler.sendMessage(msg)
        }
    }).start()
    }

    /**
     *根据自用品种类id获得商品
     */
    override fun getAllItemBySelfId(selfId: String, orderBy: String, handler: MyHandler.OnlyMyHandler) {
        Thread(Runnable {
            val msg=Message()
            val ip= MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip,msg,handler))return@Runnable
            val result=SocketUtil.initSocket(ip,MySql.getSelfBySelfId(selfId, orderBy)).inquire()
            if (!SocketUtil.judgmentNull(result,msg,handler))return@Runnable

            val items=ArrayList<CategoryItemBean>()
            try {
                items.addAll(SocketUtil.getCategoryItem(result))
            }catch (e:Exception){}
            if (items.isEmpty()){
                msg.obj=result
                msg.what=ERROR1
                handler.sendMessage(msg)
            }else{
                msg.obj=items
                msg.what=SUCCESS
                handler.sendMessage(msg)
            }
        }).start()
    }

    /**
     * 根据新品档期获得商品
     */
    override fun getNewItemById(nopId: String, orderBy: String, handler: MyHandler.OnlyMyHandler) {
        Thread(Runnable {
            val msg=Message()
            val ip= MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip,msg,handler))return@Runnable
            val result=SocketUtil.initSocket(ip,if (nopId == "0") MySql.getPromotion(orderBy) else MySql.getNewItemById(nopId, orderBy)).inquire()
            if (!SocketUtil.judgmentNull(result,msg,handler))return@Runnable

            val items=ArrayList<CategoryItemBean>()
            try {
                items.addAll(SocketUtil.getCategoryItem(result))
            }catch (e:Exception){}
            if (items.isEmpty()){
                msg.obj=result
                msg.what=ERROR1
                handler.sendMessage(msg)
            }else{
                msg.obj=items
                msg.what=SUCCESS
                handler.sendMessage(msg)
            }
        }).start()
    }

    /**
     * 获得鲜食
     */
    override fun getAllFreshItem(categoryId: String, midId: String, orderBy: String, handler: MyHandler.OnlyMyHandler) {
        Thread(Runnable {
            val msg=Message()
            val ip= MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip,msg,handler))return@Runnable
            val result=SocketUtil.initSocket(ip,MySql.getFreashItem(categoryId,midId,orderBy)).inquire()
            if (!SocketUtil.judgmentNull(result,msg,handler))return@Runnable

            val items=ArrayList<CategoryItemBean>()
            try {
                items.addAll(SocketUtil.getCategoryItem(result))
            }catch (e:Exception){}
            if (items.isEmpty()){
                msg.obj=result
                msg.what=ERROR1
                handler.sendMessage(msg)
            }else{
                msg.obj=items
                msg.what=SUCCESS
                handler.sendMessage(msg)
            }
        }).start()
    }

    /**
     * 更新item
     */
    override fun updateAllCategory(categoryList: ArrayList<CategoryItemBean>, handler: MyHandler.OnlyMyHandler) {
        Thread(Runnable {
            //把数据变成sql语句
            val sql: StringBuilder = StringBuilder()
            sql.append(MySql.affairHeader)
            categoryList.forEach {
                sql.append(MySql.updateOrdItem(it.itemId, it.orderQTY))
            }
            sql.append(MySql.affairFoot)
            val msg=Message()
            val ip= MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip,msg,handler))return@Runnable
            if (!CStoreCalendar.judgmentCalender(CStoreCalendar.getCurrentDate(2), msg, handler, 2)) return@Runnable
            val result=SocketUtil.initSocket(ip,sql.toString()).inquire()
            if (!SocketUtil.judgmentNull(result,msg,handler))return@Runnable

            if (result=="0"){
                msg.obj="0"
                msg.what=SUCCESS
                handler.sendMessage(msg)
            }else{
                msg.obj=result
                msg.what=ERROR1
                handler.sendMessage(msg)
            }
        }).start()
    }
}

interface CategoryInterface {
    /**
     * 通过categoryId获得item
     */
    fun getAllItemByCategory(categoryId: String, orderBy: String, handler: MyHandler.OnlyMyHandler)

    /**
     * 根据货架id获得商品
     */
    fun getAllItemByShelf(shelfId: String, orderBy: String, handler: MyHandler.OnlyMyHandler)

    /**
     * 根据关键字获得单品
     */
    fun getUnitItemByKeywords(keywords: String, handler: MyHandler.OnlyMyHandler)

    /**
     *根据自用品种类id获得商品
     */
    fun getAllItemBySelfId(selfId: String, orderBy: String, handler: MyHandler.OnlyMyHandler)

    /**
     * 根据新品档期获得商品或直接获得促销品
     */
    fun getNewItemById(nopId: String, orderBy: String, handler: MyHandler.OnlyMyHandler)

    /**
     * 获得鲜食
     */
    fun getAllFreshItem(categoryId: String, midId: String, orderBy: String, handler: MyHandler.OnlyMyHandler)

    /**
     * 更新item
     */
    fun updateAllCategory(categoryList: ArrayList<CategoryItemBean>, handler: MyHandler.OnlyMyHandler)
}