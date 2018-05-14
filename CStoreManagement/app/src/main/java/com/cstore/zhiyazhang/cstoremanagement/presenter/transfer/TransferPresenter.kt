package com.cstore.zhiyazhang.cstoremanagement.presenter.transfer

import com.cstore.zhiyazhang.cstoremanagement.bean.TrsItemBean
import com.cstore.zhiyazhang.cstoremanagement.bean.TrsfItemBean
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.transfer.TransferModel
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil

/**
 * Created by zhiya.zhang
 * on 2018/1/19 15:29.
 */
class TransferPresenter(private val activity: MyActivity) {
    val model = TransferModel()

    fun getAllTrs(date: String) {
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
        model.getAllTrs(date, handler)
    }

    fun searchCommodity(data: String) {
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
        model.searchCommodity(data, handler)
    }

    fun editTrs(data: ArrayList<TrsItemBean>) {
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
        model.editTrs(data, handler)
    }

    fun getAllTrsf() {
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
        model.getAllTrsf(handler)
    }

    fun createTrsf(data: ArrayList<TrsfItemBean>) {
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
        model.createTrsf(data, handler)
    }

    fun searchStore(data: String) {
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
        model.searchStore(data, handler)
    }
}