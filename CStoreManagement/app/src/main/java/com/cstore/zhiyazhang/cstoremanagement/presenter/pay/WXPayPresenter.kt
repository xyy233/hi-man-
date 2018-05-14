package com.cstore.zhiyazhang.cstoremanagement.presenter.pay

import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.pay.WXPayModel
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil
import com.cstore.zhiyazhang.cstoremanagement.view.pay.PayCollectActivity

/**
 * Created by zhiya.zhang
 * on 2017/11/16 9:19.
 */
class WXPayPresenter(private val activity: PayCollectActivity) {
    private val model = WXPayModel(activity)

    fun wechatCollectMoney(key: String, money: Double) {
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
        model.wechatCollectMoney(activity, key, money, handler)
    }

    fun wechatRefund(data: Map<String, String>) {
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
                activity.refundError(errorMessage, data)
                activity.hideLoading()
                handler.cleanAll()
            }
        })
        model.wechatRefund(activity, data, handler)
    }

    fun wechatRefund(outTranNo: String) {
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
        Thread(Runnable {
            val data = model.doOrderQuery(outTranNo, handler)
            //不等于空代表是正常的
            if (data.isNotEmpty()) {
                model.wechatRefund(data, handler)
            }
        }).start()
    }
}