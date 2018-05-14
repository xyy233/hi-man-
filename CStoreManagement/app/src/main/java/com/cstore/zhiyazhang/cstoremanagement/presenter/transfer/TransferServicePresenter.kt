package com.cstore.zhiyazhang.cstoremanagement.presenter.transfer

import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.TransResult
import com.cstore.zhiyazhang.cstoremanagement.bean.TransServiceBean
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.transfer.TransferServiceInterface
import com.cstore.zhiyazhang.cstoremanagement.model.transfer.TransferServiceModel
import com.cstore.zhiyazhang.cstoremanagement.utils.*
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.google.gson.Gson

/**
 * Created by zhiya.zhang
 * on 2018/5/10 10:09.
 */
class TransferServicePresenter(private val view: GenericView) {
    private val model: TransferServiceInterface = TransferServiceModel()

    fun getJudgment() {
        if (!ConnectionDetector.getConnectionDetector().isOnline) return
        val user = User.getUser()
        if (user.storeId == "") return
        val listener = object : MyListener {
            override fun listenerSuccess(data: Any) {
                try {
                    val trs = Gson().fromJson(data as String, TransResult::class.java)
                    //如果没有新的就不做处理了
                    if (trs.count == 0) {
                        return
                    }
                    view.requestSuccess(trs)
                } catch (e: Exception) {
                    ReportListener.reportError("TransferService", e.message.toString())
                }
            }

            override fun listenerFailed(errorMessage: String) {
                ReportListener.reportError("TransferService", errorMessage)
            }
        }
        model.getJudgment(user, listener)
    }

    fun getAllTrs() {
        if (!PresenterUtil.judgmentInternet(view)) return
        val listener = object : MyListener {
            override fun listenerSuccess(data: Any) {
                val trs = Gson().fromJson(data as String, TransResult::class.java)
                trs.rows.sortBy { it.trsType }
                view.showView(trs)
                view.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                view.showPrompt(errorMessage)
                view.errorDealWith()
            }
        }
        model.getAllTrs(User.getUser(), listener)
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
                if (data != "SUCCESS") view.showPrompt(data)
                else view.showPrompt(MyApplication.instance().applicationContext.getString(R.string.done))
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
        model.doneTrs(data, handler)
    }
}