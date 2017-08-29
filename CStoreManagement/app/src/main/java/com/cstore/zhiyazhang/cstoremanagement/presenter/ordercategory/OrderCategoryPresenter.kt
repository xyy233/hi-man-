package com.cstore.zhiyazhang.cstoremanagement.presenter.ordercategory

import android.os.Handler
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.*
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.ordercategory.OrderCategoryInterface
import com.cstore.zhiyazhang.cstoremanagement.model.ordercategory.OrderCategoryModel
import com.cstore.zhiyazhang.cstoremanagement.utils.ConnectionDetector
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zhiyazhang.mykotlinapplication.utils.MyApplication

/**
 * Created by zhiya.zhang
 * on 2017/7/25 14:24.
 */
class OrderCategoryPresenter(private val gView: GenericView) {
    private val anInterface: OrderCategoryInterface = OrderCategoryModel()
    private val mHandler = Handler()
    fun getAllCategory() {
        if (!ConnectionDetector.getConnectionDetector().isOnline) {
            gView.hideLoading()
            gView.showPrompt(MyApplication.instance().applicationContext.getString(R.string.noInternet))
            return
        }
        anInterface.getAllCategory(User.getUser(), object : MyListener {
            override fun listenerSuccess(data: String) {
                val categories= Gson().fromJson<ArrayList<OrderCategoryBean>>(data, object : TypeToken<ArrayList<OrderCategoryBean>>() {}.type)
                mHandler.post(
                        {
                            kotlin.run {
                                val adapter=OrderCategoryAdapter(ArrayList<OrderCategoryBean>(), "category",categories){
                                    gView.requestSuccess(it)
                                }
                                gView.showView(adapter)
                                gView.hideLoading()
                            }
                        }
                )
            }

            override fun listenerFailed(errorMessage: String) {
                mHandler.post {
                    gView.showPrompt(errorMessage)
                    gView.errorDealWith()
                    gView.hideLoading()
                }
            }
        })
    }

    fun getShelf(){
        if (!ConnectionDetector.getConnectionDetector().isOnline) {
            gView.hideLoading()
            gView.showPrompt(MyApplication.instance().applicationContext.getString(R.string.noInternet))
            return
        }
        anInterface.getAllShelf(object : MyListener {
            override fun listenerSuccess(data: String) {
                val shelf=Gson().fromJson<ArrayList<ShelfBean>>(data, object : TypeToken<ArrayList<ShelfBean>>() {}.type)
                mHandler.post(
                        {
                            kotlin.run {
                                val adapter=OrderCategoryAdapter(ArrayList<ShelfBean>(), "shelf",shelf){
                                    gView.requestSuccess(it)
                                }
                                gView.showView(adapter)
                                gView.hideLoading()
                            }
                        }
                )
            }

            override fun listenerFailed(errorMessage: String) {
                mHandler.post {
                    gView.showPrompt(errorMessage)
                    gView.errorDealWith()
                    gView.hideLoading()
                }
            }
        })
    }

    fun getSelf() {
        if (!ConnectionDetector.getConnectionDetector().isOnline) {
            gView.hideLoading()
            gView.showPrompt(MyApplication.instance().applicationContext.getString(R.string.noInternet))
            return
        }
        anInterface.getSelf(object : MyListener {

            override fun listenerSuccess(data: String) {
                val self=Gson().fromJson<ArrayList<SelfBean>>(data, object : TypeToken<ArrayList<SelfBean>>() {}.type)
                mHandler.post(
                        {
                            kotlin.run {
                                val adapter=OrderCategoryAdapter(ArrayList<SelfBean>(), "self",self){
                                    gView.requestSuccess(it)
                                }
                                gView.showView(adapter)
                                gView.hideLoading()
                            }
                        }
                )
            }

            override fun listenerFailed(errorMessage: String) {
                mHandler.post {
                    gView.showPrompt(errorMessage)
                    gView.errorDealWith()
                    gView.hideLoading()
                }
            }
        })
    }

    fun getNOP() {
        if (!ConnectionDetector.getConnectionDetector().isOnline) {
            gView.hideLoading()
            gView.showPrompt(MyApplication.instance().applicationContext.getString(R.string.noInternet))
            return
        }
        anInterface.getNewItemId(object : MyListener {

            override fun listenerSuccess(`data`: String) {
                val nops=Gson().fromJson<ArrayList<NOPBean>>(data, object : TypeToken<ArrayList<NOPBean>>() {}.type)
                mHandler.post(
                        {
                            kotlin.run {
                                val adapter=OrderCategoryAdapter(ArrayList<NOPBean>(), "nop",nops){
                                    gView.requestSuccess(it)
                                }
                                gView.showView(adapter)
                                gView.hideLoading()
                            }
                        }
                )
            }

            override fun listenerFailed(errorMessage: String) {
                mHandler.post {
                    gView.showPrompt(errorMessage)
                    gView.errorDealWith()
                    gView.hideLoading()
                }
            }
        })
    }

    /**
     * 1==fresh1 2==fresh2
     */
    fun getFresh(freshType: Int) {
        if (!ConnectionDetector.getConnectionDetector().isOnline) {
            gView.hideLoading()
            gView.showPrompt(MyApplication.instance().applicationContext.getString(R.string.noInternet))
            return
        }
        anInterface.getFresh(freshType, object : MyListener {

            override fun listenerSuccess(data: String) {
                val freshs=Gson().fromJson<ArrayList<FreshGroup>>(data, object : TypeToken<ArrayList<FreshGroup>>() {}.type)
                mHandler.post(
                        {
                            kotlin.run {
                                val adapter=OrderCategoryAdapter(ArrayList<FreshGroup>(), "fresh",freshs){
                                    gView.requestSuccess(it)
                                }
                                gView.showView(adapter)
                                gView.hideLoading()
                            }
                        }
                )
            }

            override fun listenerFailed(errorMessage: String) {
                mHandler.post {
                    gView.showPrompt(errorMessage)
                    gView.errorDealWith()
                    gView.hideLoading()
                }
            }
        })
    }
}