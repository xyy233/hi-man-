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

    /**
     * 通过categoryId获得item
     */
    override fun getAllItemByCategory(categoryId: String, orderBy: String, myListener: MyListener) {
        val ip = MyApplication.getIP()
        if (ip == MyApplication.instance().getString(R.string.notFindIP)) {
            myListener.listenerFailed(ip)
            return
        }
        SocketUtil.getSocketUtil(ip).inquire(MySql.getItemByCategoryId(categoryId, orderBy), 20, @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> try {
                        if (msg.obj as String == "" || msg.obj as String == "[]") {
                            myListener.listenerFailed(MyApplication.instance().applicationContext.resources.getString(R.string.noMessage))
                        } else {
                            myListener.listenerSuccess(Gson().fromJson<ArrayList<CategoryItemBean>>(msg.obj as String, object : TypeToken<ArrayList<CategoryItemBean>>() {}.type))
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
     * 根据货架id获得商品
     */
    override fun getAllItemByShelf(shelfId: String, orderBy: String, myListener: MyListener) {
        val ip = MyApplication.getIP()
        if (ip == MyApplication.instance().getString(R.string.notFindIP)) {
            myListener.listenerFailed(ip)
            return
        }
        SocketUtil.getSocketUtil(ip).inquire(MySql.getItemByShelfId(shelfId, orderBy), 20, @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> try {
                        if (msg.obj as String == "" || msg.obj as String == "[]") {
                            myListener.listenerFailed(MyApplication.instance().applicationContext.resources.getString(R.string.noMessage))
                        } else {
                            myListener.listenerSuccess(Gson().fromJson<ArrayList<CategoryItemBean>>(msg.obj as String, object : TypeToken<ArrayList<CategoryItemBean>>() {}.type))
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
     * 根据关键字获得单品
     */
    override fun getUnitItemByKeywords(keywords: String, myListener: MyListener) {
        val ip = MyApplication.getIP()
        if (ip == MyApplication.instance().getString(R.string.notFindIP)) {
            myListener.listenerFailed(ip)
            return
        }
        SocketUtil.getSocketUtil(ip).inquire(MySql.unitOrder(keywords), 20, @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> try {
                        if (msg.obj as String == "" || msg.obj as String == "[]") {
                            myListener.listenerFailed(MyApplication.instance().applicationContext.resources.getString(R.string.noMessage))
                        } else {
                            myListener.listenerSuccess(Gson().fromJson<ArrayList<CategoryItemBean>>(msg.obj as String, object : TypeToken<ArrayList<CategoryItemBean>>() {}.type))
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
     *根据自用品种类id获得商品
     */
    override fun getAllItemBySelfId(selfId: String, orderBy: String, myListener: MyListener) {
        val ip = MyApplication.getIP()
        if (ip == MyApplication.instance().getString(R.string.notFindIP)) {
            myListener.listenerFailed(ip)
            return
        }
        SocketUtil.getSocketUtil(ip).inquire(MySql.getSelfBySelfId(selfId, orderBy), 20, @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> try {
                        if (msg.obj as String == "" || msg.obj as String == "[]") {
                            myListener.listenerFailed(MyApplication.instance().applicationContext.resources.getString(R.string.noMessage))
                        } else {
                            myListener.listenerSuccess(Gson().fromJson<ArrayList<CategoryItemBean>>(msg.obj as String, object : TypeToken<ArrayList<CategoryItemBean>>() {}.type))
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
     * 根据新品档期获得商品
     */
    override fun getNewItemById(nopId: String, orderBy: String, myListener: MyListener) {
        val ip = MyApplication.getIP()
        if (ip == MyApplication.instance().getString(R.string.notFindIP)) {
            myListener.listenerFailed(ip)
            return
        }
        var sql: String? = null
        if (nopId == "0") sql = MySql.getPromotion(orderBy) else sql = MySql.getNewItemById(nopId, orderBy)
        SocketUtil.getSocketUtil(ip).inquire(sql, 20, @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> try {
                        if (msg.obj as String == "" || msg.obj as String == "[]") {
                            myListener.listenerFailed(MyApplication.instance().applicationContext.resources.getString(R.string.noMessage))
                        } else {
                            myListener.listenerSuccess(Gson().fromJson<ArrayList<CategoryItemBean>>(msg.obj as String, object : TypeToken<ArrayList<CategoryItemBean>>() {}.type))
                        }
                    } catch (e: Exception) {
                        myListener.listenerFailed(msg.obj as String)
                    }
                    else -> myListener.listenerFailed(msg.obj as String)
                }
            }
        })
    }

    override fun getAllFreshItem(categoryId: String, midId: String, orderBy: String, myListener: MyListener) {
        val ip = MyApplication.getIP()
        if (ip == MyApplication.instance().getString(R.string.notFindIP)) {
            myListener.listenerFailed(ip)
            return
        }
        SocketUtil.getSocketUtil(ip).inquire(MySql.getFreashItem(categoryId,midId,orderBy),10, @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> try {
                        if (msg.obj as String == "" || msg.obj as String == "[]") {
                            myListener.listenerFailed(MyApplication.instance().applicationContext.resources.getString(R.string.noMessage))
                        } else {
                            myListener.listenerSuccess(Gson().fromJson<ArrayList<CategoryItemBean>>(msg.obj as String, object : TypeToken<ArrayList<CategoryItemBean>>() {}.type))
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
     * 更新item
     */
    override fun updateAllCategory(categoryList: ArrayList<CategoryItemBean>, myListener: MyListener) {
        //把数据变成sql语句
        val sql: StringBuilder = StringBuilder()
        sql.append(MySql.affairHeader)
        categoryList.forEach {
            sql.append(MySql.updateOrdItem(it.itemId, it.orderQTY))
        }
        sql.append(MySql.affairFoot)

        val ip = MyApplication.getIP()
        if (ip == MyApplication.instance().getString(R.string.notFindIP)) {
            myListener.listenerFailed(ip)
            return
        }

        SocketUtil.getSocketUtil(ip).inquire(sql.toString().toLowerCase(), 20, @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> myListener.listenerSuccess()
                    else -> myListener.listenerFailed(msg.obj as String)
                }
            }
        })
    }
}

interface CategoryInterface {
    /**
     * 通过categoryId获得item
     */
    fun getAllItemByCategory(categoryId: String, orderBy: String, myListener: MyListener)

    /**
     * 根据货架id获得商品
     */
    fun getAllItemByShelf(shelfId: String, orderBy: String, myListener: MyListener)

    /**
     * 根据关键字获得单品
     */
    fun getUnitItemByKeywords(keywords: String, myListener: MyListener)

    /**
     *根据自用品种类id获得商品
     */
    fun getAllItemBySelfId(selfId: String, orderBy: String, myListener: MyListener)

    /**
     * 根据新品档期获得商品或直接获得促销品
     */
    fun getNewItemById(nopId: String, orderBy: String, myListener: MyListener)

    /**
     * 获得鲜食
     */
    fun getAllFreshItem(categoryId: String, midId: String, orderBy: String, myListener: MyListener)

    /**
     * 更新item
     */
    fun updateAllCategory(categoryList: ArrayList<CategoryItemBean>, myListener: MyListener)
}