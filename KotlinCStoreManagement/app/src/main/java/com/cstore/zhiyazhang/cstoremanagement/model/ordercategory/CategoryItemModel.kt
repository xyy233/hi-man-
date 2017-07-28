package com.cstore.zhiyazhang.cstoremanagement.model.ordercategory

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.CategoryItemBean
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zhiyazhang.mykotlinapplication.utils.MyApplication

/**
 * Created by zhiya.zhang
 * on 2017/7/27 16:22.
 */
class CategoryItemModel : CategoryInterface {
    override fun getAllItem(categoryId: String, orderBy: String, listener: MyListener) {
        val ip = MyApplication.getIP()
        if (ip == MyApplication.instance().getString(R.string.notFindIP)) {
            listener.contractFailed(ip)
            return
        }
        SocketUtil.getSocketUtil(ip).inquire(MySql.getItemByCategoryId(categoryId, orderBy), 120000, @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> try {
                        if (msg.obj as String == "" || msg.obj as String == "[]") {
                            listener.contractFailed(MyApplication.instance().applicationContext.resources.getString(R.string.idError))
                        } else {
                            listener.contractSuccess(Gson().fromJson<ArrayList<CategoryItemBean>>(msg.obj as String, object : TypeToken<ArrayList<CategoryItemBean>>() {}.type))
                        }
                    } catch (e: Exception) {
                        listener.contractFailed(msg.obj as String)
                    }
                    else -> listener.contractFailed(msg.obj as String)
                }
            }
        })
    }

}

interface CategoryInterface {
    fun getAllItem(categoryId: String, orderBy: String, listener: MyListener)
}