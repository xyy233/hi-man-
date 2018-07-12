package com.cstore.zhiyazhang.cstoremanagement.presenter.pay

import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.pay.ALIPayModel
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil
import com.cstore.zhiyazhang.cstoremanagement.view.pay.PayCollectActivity

/**
 * Created by zhiya.zhang
 * on 2018/1/11 10:36.
 */
class ALIPayPresenter(private val activity: PayCollectActivity) {

    private val model = ALIPayModel(activity)

    fun aliCollectMoney(key: String, money: Double) {
        if (!PresenterUtil.judgmentInternet(activity)) return
        val handler = MyHandler()
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
        model.aliCollectMoney(activity, key, money, handler)
    }

    fun aliRefund(outTranNo: String) {

        if (!PresenterUtil.judgmentInternet(activity)) return
        val handler = MyHandler()
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                //退款成功
                activity.refundSuccess(data)
                activity.hideLoading()
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                //退款失败
                activity.refundError(errorMessage, outTranNo)
                activity.hideLoading()
                handler.cleanAll()
            }
        })
        model.aliRefund(activity, outTranNo, handler)
    }

}