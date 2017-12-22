package com.cstore.zhiyazhang.cstoremanagement.presenter.pay

import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.pay.CashPayModel
import com.cstore.zhiyazhang.cstoremanagement.sql.CashPayDao
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil
import com.cstore.zhiyazhang.cstoremanagement.view.pay.PayCollectActivity

/**
 * Created by zhiya.zhang
 * on 2017/12/20 12:05.
 */
class CashPayPresenter(private val activity: PayCollectActivity) {
    private val model = CashPayModel()

    fun cashCollect(money: Double) {
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
                handler.cleanAll()
            }
        })
        model.cashCollect(CashPayDao(activity), activity, money, handler)
    }
}