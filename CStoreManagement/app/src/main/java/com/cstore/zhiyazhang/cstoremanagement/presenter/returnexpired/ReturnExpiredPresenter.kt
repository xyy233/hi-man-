package com.cstore.zhiyazhang.cstoremanagement.presenter.returnexpired

import com.cstore.zhiyazhang.cstoremanagement.bean.ReturnExpiredBean
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.returnexpired.ReturnExpiredModel
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil
import com.cstore.zhiyazhang.cstoremanagement.view.order.returnexpired.ReturnExpiredActivity

/**
 * Created by zhiya.zhang
 * on 2018/1/3 11:24.
 */
class ReturnExpiredPresenter(private val activity: ReturnExpiredActivity) {
    private val model = ReturnExpiredModel()

    fun getAll() {
        if (!PresenterUtil.judgmentInternet(activity)) return
        val handler = MyHandler()
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                activity.showView(data)
                activity.hideLoading()
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                activity.showPrompt(errorMessage)
                activity.hideLoading()
                handler.cleanAll()
            }
        })
        model.getAll(handler)
    }

    fun searchExpiredCommodity(data: String) {
        if (!PresenterUtil.judgmentInternet(activity)) return
        val handler = MyHandler()
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                activity.requestSuccess(data)
                activity.hideLoading()
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                activity.showPrompt(errorMessage)
                activity.hideLoading()
                handler.cleanAll()
            }
        })
        model.searchExpiredCommodity(data, handler)
    }

    fun getDateAll(date1: String, date2: String, checkIndex: Int) {
        if (!PresenterUtil.judgmentInternet(activity)) return
        val handler = MyHandler()
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                activity.requestSuccess2(data)
                activity.hideLoading()
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                activity.showPrompt(errorMessage)
                activity.hideLoading()
                handler.cleanAll()
            }
        })
        model.getDateExpiredAll(date1, date2, checkIndex, handler)
    }

    fun saveData(changeData: ArrayList<ReturnExpiredBean>) {
        if (!PresenterUtil.judgmentInternet(activity)) return
        val handler = MyHandler()
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                activity.updateDone(data)
                activity.hideLoading()
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                activity.showPrompt(errorMessage)
                activity.hideLoading()
                handler.cleanAll()
            }
        })
        model.saveData(changeData, handler)
    }
}