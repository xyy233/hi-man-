package com.cstore.zhiyazhang.cstoremanagement.presenter.ordercategory

import android.os.Handler
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.*
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.ordercategory.OrderCategoryInterface
import com.cstore.zhiyazhang.cstoremanagement.model.ordercategory.OrderCategoryModel
import com.cstore.zhiyazhang.cstoremanagement.utils.ConnectionDetector
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.ContractTypeView
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.zhiyazhang.mykotlinapplication.utils.MyApplication

/**
 * Created by zhiya.zhang
 * on 2017/7/25 14:24.
 */
class OrderCategoryPresenter(private val gView: GenericView, private val tView:ContractTypeView) {
    private val anInterfacve: OrderCategoryInterface = OrderCategoryModel()
    private val mHandler = Handler()
    fun getAllCategory() {
        if (!ConnectionDetector.getConnectionDetector().isOnline) {
            gView.hideLoading()
            gView.showPrompt(MyApplication.instance().applicationContext.getString(R.string.noInternet))
            return
        }
        anInterfacve.getAllCategory(User.getUser(), object : MyListener {
            override fun listenerSuccess() {
            }

            override fun listenerSuccess(`object`: Any) {
                mHandler.post(
                        Runnable {
                            kotlin.run {
                                val adapter=OrderCategoryAdapter(ArrayList<OrderCategoryBean>(), "category",`object` as ArrayList<OrderCategoryBean>){
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
        anInterfacve.getAllShelf(object : MyListener {
            override fun listenerSuccess() {
            }

            override fun listenerSuccess(`object`: Any) {
                mHandler.post(
                        Runnable {
                            kotlin.run {
                                val adapter=OrderCategoryAdapter(ArrayList<ShelfBean>(), "shelf",`object` as ArrayList<ShelfBean>){
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
        anInterfacve.getSelf(object : MyListener {
            override fun listenerSuccess() {
            }

            override fun listenerSuccess(`object`: Any) {
                mHandler.post(
                        Runnable {
                            kotlin.run {
                                val adapter=OrderCategoryAdapter(ArrayList<SelfBean>(), "self",`object` as ArrayList<SelfBean>){
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
        anInterfacve.getNewItemId(object : MyListener {
            override fun listenerSuccess() {
            }

            override fun listenerSuccess(`object`: Any) {
                mHandler.post(
                        Runnable {
                            kotlin.run {
                                val adapter=OrderCategoryAdapter(ArrayList<NOPBean>(), "nop",`object` as ArrayList<NOPBean>){
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
        anInterfacve.getFresh(freshType, object : MyListener {
            override fun listenerSuccess() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun listenerSuccess(`object`: Any) {
                mHandler.post(
                        Runnable {
                            kotlin.run {
                                val adapter=OrderCategoryAdapter(ArrayList<FreshGroup>(), "fresh",`object` as ArrayList<FreshGroup>){
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