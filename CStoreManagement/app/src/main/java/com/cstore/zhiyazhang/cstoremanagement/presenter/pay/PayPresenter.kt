package com.cstore.zhiyazhang.cstoremanagement.presenter.pay

import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.pay.PayModel
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil

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
                activity.hideLoading()
                activity.showPrompt(activity.getString(R.string.noMessage))
                handler.cleanAll()
            }
        })
        model.getCommodity(barCode, handler)
    }
}