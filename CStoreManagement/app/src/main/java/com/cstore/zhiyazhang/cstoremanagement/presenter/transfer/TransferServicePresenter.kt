package com.cstore.zhiyazhang.cstoremanagement.presenter.transfer

import android.util.Log
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.TransResult
import com.cstore.zhiyazhang.cstoremanagement.bean.TransServiceBean
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.transfer.TransferServiceInterface
import com.cstore.zhiyazhang.cstoremanagement.model.transfer.TransferServiceModel
import com.cstore.zhiyazhang.cstoremanagement.sql.TranDao
import com.cstore.zhiyazhang.cstoremanagement.utils.ConnectionDetector
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.google.gson.Gson

/**
 * Created by zhiya.zhang
 * on 2018/5/10 10:09.
 */
class TransferServicePresenter(private val view: GenericView) {
    private val model: TransferServiceInterface = TransferServiceModel()
    private val db = TranDao.instance()

    fun getJudgment() {
        if (!ConnectionDetector.getConnectionDetector().isOnlineNoToast) return
        val user = User.getUser()
        if (user.storeId == "") return
        val listener = object : MyListener {
            override fun listenerSuccess(data: Any) {
                try {
                    val trs = Gson().fromJson(data as String, TransResult::class.java)
                    Log.e("TransferService", "当前数据：" + data.toString())
                    //如果没有新的就不做处理了
                    if (trs.count == 0) {
                        return
                    } else {
                        view.requestSuccess(trs)
                    }
                } catch (e: Exception) {
                }
            }

            override fun listenerFailed(errorMessage: String) {
            }
        }
        model.getJudgment(user, listener)
    }

    fun getAllTrs() {
        if (!PresenterUtil.judgmentInternet(view)) return
        val listener = object : MyListener {
            override fun listenerSuccess(data: Any) {
                try {
                    val trs = getTrsByJudgment(data as String)
                    trs.rows.sortBy { it.trsType }
                    if (trs.rows.size == 0) {
                        view.showView(trs)
                        view.hideLoading()
                        return
                    }
                    //获得库存
                    val handler = MyHandler()
                    handler.writeListener(object : MyListener {
                        override fun listenerSuccess(data: Any) {
                            view.showView(data)
                            view.hideLoading()
                            handler.cleanAll()
                        }

                        override fun listenerFailed(errorMessage: String) {
                            view.showPrompt(errorMessage)
                            view.errorDealWith()
                            handler.cleanAll()
                        }
                    })
                    model.getZWInv(trs, handler)
                } catch (e: Exception) {
                    view.showPrompt(e.message.toString())
                    view.errorDealWith()
                }
            }

            override fun listenerFailed(errorMessage: String) {
                view.showPrompt(errorMessage)
                view.errorDealWith()
            }
        }
        model.getAllTrs(User.getUser(), listener)
    }

    /**
     * 得到数据，通过比对sqlite中数据
     */
    private fun getTrsByJudgment(data: String): TransResult {
        //请求得到的数据
        val httpResult = Gson().fromJson(data, TransResult::class.java)
        //得到数据库数据
        val d = db.getAllData()
        val sqlResult = TransResult(0, d.size, d)
        //重复数据不管网络的，只获取本地的，本地为准,
        val repeatData = ArrayList<TransServiceBean>()
        for (sb in sqlResult.rows) {
            httpResult.rows.filterTo(repeatData) { sb.storeId == it.storeId && sb.trsStoreId == it.trsStoreId && sb.disTime == it.disTime && sb.trsType == it.trsType }
        }
        //删掉重复数据
        httpResult.rows.removeAll(repeatData)
        //新数据就新建
        if (httpResult.rows.size > 0) {
            db.insertSQL(httpResult.rows)
            sqlResult.rows.addAll(httpResult.rows)
        }
        sqlResult.count = sqlResult.rows.size
        return sqlResult
    }

    fun doneTrs() {
        val data = view.getData1()
        if (data !is TransServiceBean) {
            view.showPrompt("获取数据类型错误！doneTrs:getData1 Error")
            view.errorDealWith()
            return
        }
        if (!PresenterUtil.judgmentInternet(view)) return
        val handler = MyHandler()
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                data as String
                if (data != "SUCCESS") {
                    view.showPrompt(data)
                } else {
                    view.showPrompt(MyApplication.instance().applicationContext.getString(R.string.done))
                }
                view.updateDone(data)
                view.hideLoading()
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                view.errorDealWith()
                view.showPrompt(errorMessage)
                handler.cleanAll()
            }
        })
        model.doneTrs(db, data, handler)
    }
}