package com.cstore.zhiyazhang.cstoremanagement.presenter.ordercategory

import com.cstore.zhiyazhang.cstoremanagement.bean.*
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.ordercategory.OrderCategoryInterface
import com.cstore.zhiyazhang.cstoremanagement.model.ordercategory.OrderCategoryModel
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.OnlyMyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView

/**
 * Created by zhiya.zhang
 * on 2017/7/25 14:24.
 */
class OrderCategoryPresenter(private val gView: GenericView, private val activity:MyActivity) {

    private val anInterface: OrderCategoryInterface = OrderCategoryModel()

    fun getAllCategory() {
        if (!PresenterUtil.judgmentInternet(gView))return
        anInterface.getAllCategory(OnlyMyHandler.writeActivity(activity).writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                val adapter= OrderCategoryAdapter("category",data as ArrayList<OrderCategoryBean>){
                    gView.requestSuccess(it)
                }
                gView.showView(adapter)
                gView.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }
        }))
    }

    fun getShelf(){
        if (!PresenterUtil.judgmentInternet(gView))return
        anInterface.getAllShelf(OnlyMyHandler.writeActivity(activity).writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                val adapter=OrderCategoryAdapter("shelf",data as ArrayList<ShelfBean>){
                    gView.requestSuccess(it)
                }
                gView.showView(adapter)
                gView.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }
        }))
    }

    fun getSelf() {
        if (!PresenterUtil.judgmentInternet(gView))return
        anInterface.getSelf(OnlyMyHandler.writeActivity(activity).writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                val adapter=OrderCategoryAdapter("self",data as ArrayList<SelfBean>){
                    gView.requestSuccess(it)
                }
                gView.showView(adapter)
                gView.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }
        }))
    }

    fun getNOP() {
        if (!PresenterUtil.judgmentInternet(gView))return
        anInterface.getNewItemId(OnlyMyHandler.writeActivity(activity).writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                val adapter=OrderCategoryAdapter("nop",data as ArrayList<NOPBean>){
                    gView.requestSuccess(it)
                }
                gView.showView(adapter)
                gView.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }
        }))
    }

    /**
     * 1==fresh1 2==fresh2
     */
    fun getFresh(freshType: Int) {
        if (!PresenterUtil.judgmentInternet(gView))return
        anInterface.getFresh(freshType,OnlyMyHandler.writeActivity(activity).writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                val adapter=OrderCategoryAdapter("fresh",data as ArrayList<FreshGroup>){
                    gView.requestSuccess(it)
                }
                gView.showView(adapter)
                gView.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }
        }))
    }
}