package com.cstore.zhiyazhang.cstoremanagement.presenter.returnpurchase

import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ReturnedPurchaseBean
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.returnpurchase.ReturnPurchaseModel
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil
import com.cstore.zhiyazhang.cstoremanagement.view.order.returnpurchase.ReturnPurchaseActivity
import com.cstore.zhiyazhang.cstoremanagement.view.order.returnpurchase.ReturnPurchaseCreateActivity
import com.cstore.zhiyazhang.cstoremanagement.view.order.returnpurchase.ReturnPurchaseItemActivity

/**
 * Created by zhiya.zhang
 * on 2017/11/3 10:32.
 * 2017年11月3日 14:35:27 开始同类型view不在全适配activity，只为单独activity适配
 */

/**
 * 退货首页
 */
class ReturnPurchasePresenter(private val activity: ReturnPurchaseActivity) {
    val model = ReturnPurchaseModel()

    /**
     * 获得退货原因
     */
    fun getReasonList(){
        if (!PresenterUtil.judgmentInternet(activity)) return
        val handler = MyHandler().writeActivity(activity)
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                activity.requestSuccess(data)
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
    }

    /**
     * 得到所有的退货单，之后要得到原因因此不hideLoading
     */
    fun getReturnPurchaseList(date: String) {
        if (!PresenterUtil.judgmentInternet(activity)) return
        val handler = MyHandler().writeActivity(activity)
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                activity.showView(data)
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                activity.errorDealWith()
                activity.hideLoading()
                activity.showPrompt(errorMessage)
                handler.cleanAll()
            }
        })
        model.getReturnPurchaseList(date, handler)
    }
}

/**
 * 退货item页
 */
class ReturnPurchaseItemPresenter(private val activity: ReturnPurchaseItemActivity) {
    val model = ReturnPurchaseModel()

    /**
     * 更新退货
     */
    fun updateReturnPurchase(date: String, rpb: ReturnedPurchaseBean) {
        if (!PresenterUtil.judgmentInternet(activity)) return
        val handler = MyHandler().writeActivity(activity)
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                activity.updateDone(data)
                activity.hideLoading()
                activity.showPrompt(activity.getString(R.string.saveDone))
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                activity.errorDealWith()
                activity.hideLoading()
                activity.showPrompt(errorMessage)
                handler.cleanAll()
            }
        })
        model.updateReturnPurchase(date, rpb, handler)
    }
}

class ReturnPurchaseCreatePresenter(private val activity: ReturnPurchaseCreateActivity) {
    val model = ReturnPurchaseModel()

    /**
     * 得到配送商
     */
    fun getVendor() {
        if (!PresenterUtil.judgmentInternet(activity)) return
        val handler = MyHandler().writeActivity(activity)
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                activity.requestSuccess(data)
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
        model.getVendor(handler)
    }

    /**
     * 得到选择的配送单下的商品
     */
    fun getCommodity() {
        if (!PresenterUtil.judgmentInternet(activity)) return
        val handler = MyHandler().writeActivity(activity)
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                activity.showView(data)
                activity.hideLoading()
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                activity.commodityError()
                activity.hideLoading()
                activity.showPrompt(errorMessage)
                handler.cleanAll()
            }
        })
        model.getCommodity(
                activity.getRPB(),
                activity.getSelectVendor(),
                activity.getSelectType(),
                handler
        )
    }

    /**
     * 创建退货单
     */
    fun createReturnPurchase(date: String) {
        if (!PresenterUtil.judgmentInternet(activity)) return
        val handler = MyHandler().writeActivity(activity)
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                activity.updateDone(data)
                activity.hideLoading()
                activity.showPrompt(activity.getString(R.string.saveDone))
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                activity.showPrompt(errorMessage)
                activity.createError()
                activity.hideLoading()
                handler.cleanAll()
            }
        })
        model.createReturnPurchase(date, activity.getCreateData(), handler)
    }
}