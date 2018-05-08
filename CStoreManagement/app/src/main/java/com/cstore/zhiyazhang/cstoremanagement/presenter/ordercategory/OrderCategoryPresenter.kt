package com.cstore.zhiyazhang.cstoremanagement.presenter.ordercategory

import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.ordercategory.OrderCategoryInterface
import com.cstore.zhiyazhang.cstoremanagement.model.ordercategory.OrderCategoryModel
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView

/**
 * Created by zhiya.zhang
 * on 2017/7/25 14:24.
 */
class OrderCategoryPresenter(private val gView: GenericView, private val activity: MyActivity) {

    private val anInterface: OrderCategoryInterface = OrderCategoryModel()

    fun getAllCategory() {
        if (!PresenterUtil.judgmentInternet(gView)) return
        val handler = MyHandler().writeActivity(activity)
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                gView.showView(data)
                gView.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }
        })
        anInterface.getAllCategory(handler)
    }

    fun getShelf() {
        if (!PresenterUtil.judgmentInternet(gView)) return
        val handler = MyHandler().writeActivity(activity)
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                gView.showView(data)
                gView.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }
        })
        anInterface.getAllShelf(handler)
    }

    fun getSelf() {
        if (!PresenterUtil.judgmentInternet(gView)) return
        val handler = MyHandler().writeActivity(activity)
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                gView.showView(data)
                gView.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }
        })
        anInterface.getSelf(handler)
    }

    fun getNOP() {
        if (!PresenterUtil.judgmentInternet(gView)) return
        val handler = MyHandler().writeActivity(activity)
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                gView.showView(data)
                gView.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }
        })
        anInterface.getNewItemId(handler)
    }

    /**
     * 1==fresh1 2==fresh2
     */
    fun getFresh(freshType: Int) {
        if (!PresenterUtil.judgmentInternet(gView)) return
        val handler = MyHandler().writeActivity(activity)
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                gView.showView(data)
                gView.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }
        })
        anInterface.getFresh(freshType, handler)
    }
}