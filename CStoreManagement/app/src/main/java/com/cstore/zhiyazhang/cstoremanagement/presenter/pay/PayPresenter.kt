package com.cstore.zhiyazhang.cstoremanagement.presenter.pay

import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.pay.PayModel
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil
import com.cstore.zhiyazhang.cstoremanagement.view.pay.PayActivity

/**
 * Created by zhiya.zhang
 * on 2017/11/8 14:27.
 */
class PayPresenter(private val activity: PayActivity) {
    private val model = PayModel()

    fun getCommodity(barCode: String) {
        if (!PresenterUtil.judgmentInternet(activity)) return
        val handler = MyHandler().writeActivity(activity)
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                activity.showView(data)
                activity.hideLoading()
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                activity.errorDealWith()
                activity.hideLoading()
                activity.showPrompt(errorMessage)
                handler.cleanAll()
            }
        })
        model.getCommodity(barCode, handler)
    }

    fun searchCommodity(key: String) {
        if (!PresenterUtil.judgmentInternet(activity)) return
        val handler = MyHandler().writeActivity(activity)
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                activity.showView(data)
                activity.hideLoading()
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                activity.errorDealWith()
                activity.hideLoading()
                activity.showPrompt(errorMessage)
                handler.cleanAll()
            }
        })
        model.searchCommodity(key, handler)
    }

    fun updateCommodity(barCode: String, count:Int){
        if (!PresenterUtil.judgmentInternet(activity)) return
        val handler = MyHandler().writeActivity(activity)
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                activity.showView(data)
                activity.hideLoading()
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                activity.errorDealWith()
                activity.hideLoading()
                activity.showPrompt(errorMessage)
                handler.cleanAll()
            }
        })
        model.updateCommodity(barCode,count,handler)
    }

    /**
     * 清空数据
     */
    fun deleteData() {
        if (!PresenterUtil.judgmentInternet(activity)) return
        val handler = MyHandler().writeActivity(activity)
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                activity.requestSuccess(data)
                activity.hideLoading()
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                activity.errorDealWith(errorMessage)
                activity.hideLoading()
                activity.showPrompt(errorMessage)
                handler.cleanAll()
            }
        })
        model.deleteData(handler)
    }
}