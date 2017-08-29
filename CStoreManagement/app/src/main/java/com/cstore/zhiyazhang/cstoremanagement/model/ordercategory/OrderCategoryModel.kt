package com.cstore.zhiyazhang.cstoremanagement.model.ordercategory

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import com.zhiyazhang.mykotlinapplication.utils.MyApplication

/**
 * Created by zhiya.zhang
 * on 2017/7/25 13:43.
 */
class OrderCategoryModel : OrderCategoryInterface {
    override fun getAllShelf(myListener: MyListener) {
        val ip = MyApplication.getIP()
        if (ip == MyApplication.instance().getString(R.string.notFindIP)) {
            myListener.listenerFailed(ip)
            return
        }
        SocketUtil().writeIP(ip).inquire(MySql.getAllShelf, 10, @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> try {
                        when (msg.obj as String) {
                            "" -> myListener.listenerFailed(MyApplication.instance().applicationContext.resources.getString(R.string.noMessage))
                            "[]" -> myListener.listenerFailed(MyApplication.instance().applicationContext.resources.getString(R.string.noMessage))
                            else -> {
                                myListener.listenerSuccess(msg.obj as String)
                            }
                        }
                    } catch (e: Exception) {
                        myListener.listenerFailed(msg.obj as String)
                    }
                    else -> myListener.listenerFailed(msg.obj as String)
                }
            }
        })
    }

    override fun getNewItemId(myListener: MyListener) {
        val ip = MyApplication.getIP()
        if (ip == MyApplication.instance().getString(R.string.notFindIP)) {
            myListener.listenerFailed(ip)
            return
        }
        SocketUtil().writeIP(ip).inquire(MySql.getNewItemId, 10, @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> try {
                        when (msg.obj as String) {
                            "" -> myListener.listenerFailed(MyApplication.instance().applicationContext.resources.getString(R.string.noMessage))
                            "[]" -> myListener.listenerFailed(MyApplication.instance().applicationContext.resources.getString(R.string.noMessage))
                            else -> {
                                myListener.listenerSuccess(msg.obj as String)
                            }
                        }
                    } catch (e: Exception) {
                        myListener.listenerFailed(msg.obj as String)
                    }
                    else -> myListener.listenerFailed(msg.obj as String)
                }
            }
        })
    }

    override fun getSelf(myListener: MyListener) {
        val ip = MyApplication.getIP()
        if (ip == MyApplication.instance().getString(R.string.notFindIP)) {
            myListener.listenerFailed(ip)
            return
        }
        SocketUtil().writeIP(ip).inquire(MySql.getSelf, 10, @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> try {
                        when (msg.obj as String) {
                            "" -> myListener.listenerFailed(MyApplication.instance().applicationContext.resources.getString(R.string.noMessage))
                            "[]" -> myListener.listenerFailed(MyApplication.instance().applicationContext.resources.getString(R.string.noMessage))
                            else -> {
                                myListener.listenerSuccess(msg.obj as String)
                            }
                        }
                    } catch (e: Exception) {
                        myListener.listenerFailed(msg.obj as String)
                    }
                    else -> myListener.listenerFailed(msg.obj as String)
                }
            }
        })
    }

    override fun getAllCategory(user: User, myListener: MyListener) {
        val ip = MyApplication.getIP()
        if (ip == MyApplication.instance().getString(R.string.notFindIP)) {
            myListener.listenerFailed(ip)
            return
        }
        SocketUtil().writeIP(ip).inquire(MySql.getAllCategory(), 10, @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> try {
                        when (msg.obj as String) {
                            "" -> myListener.listenerFailed(MyApplication.instance().applicationContext.resources.getString(R.string.noMessage))
                            "[]" -> myListener.listenerFailed(MyApplication.instance().applicationContext.resources.getString(R.string.noMessage))
                            else -> {
                               myListener.listenerSuccess(msg.obj as String)
                            }
                        }
                    } catch (e: Exception) {
                        myListener.listenerFailed(msg.obj as String)
                    }
                    else -> myListener.listenerFailed(msg.obj as String)
                }
            }
        })
    }

    override fun getFresh(freshType: Int, myListener: MyListener) {
        val ip = MyApplication.getIP()
        if (ip == MyApplication.instance().getString(R.string.notFindIP)) {
            myListener.listenerFailed(ip)
            return
        }
        var sql = ""
        if (freshType == 1) sql = MySql.getFreshGroup1 else sql = MySql.getFreshGroup2
        SocketUtil().writeIP(ip).inquire(sql, 10, @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> {
                        try {
                            when (msg.obj as String) {
                                "" -> myListener.listenerFailed(MyApplication.instance().applicationContext.resources.getString(R.string.noMessage))
                                "[]" -> myListener.listenerFailed(MyApplication.instance().applicationContext.resources.getString(R.string.noMessage))
                                else -> {
                                    myListener.listenerSuccess(msg.obj as String)
                                }
                            }
                        } catch (e: Exception) {
                            myListener.listenerFailed(msg.obj as String)
                        }
                    }
                    else -> {
                        myListener.listenerFailed(msg.obj as String)
                    }
                }
            }
        })
    }

}

interface OrderCategoryInterface {
    fun getAllCategory(user: User, myListener: MyListener)
    fun getAllShelf(myListener: MyListener)
    fun getNewItemId(myListener: MyListener)
    fun getSelf(myListener: MyListener)
    fun getFresh(freshType: Int, myListener: MyListener)
}