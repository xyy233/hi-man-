package com.cstore.zhiyazhang.cstoremanagement.presenter.distribution

import com.cstore.zhiyazhang.cstoremanagement.bean.*
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.distribution.DistributionInterface
import com.cstore.zhiyazhang.cstoremanagement.model.distribution.DistributionModel
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.google.gson.Gson

/**
 * Created by zhiya.zhang
 * on 2018/5/22 14:02.
 */
class DistributionPresenter(private val view: GenericView) {
    val model: DistributionInterface = DistributionModel()

    fun getDistribution() {
        if (!PresenterUtil.judgmentInternet(view)) return
        val listener = object : MyListener {
            override fun listenerSuccess(data: Any) {
                try {
                    val db = Gson().fromJson(data as String, DistributionResult::class.java)
                    view.showView(db)
                    view.hideLoading()
                } catch (e: Exception) {
                    view.showPrompt(e.message.toString())
                    view.errorDealWith()
                }
            }

            override fun listenerFailed(errorMessage: String) {
                view.showPrompt(errorMessage)
                view.errorDealWith()
            }
        }
        model.getDistribution(listener)
    }

    /**
     * 必须实现getData1、getData2,对应shelvesId,disTime
     */
    fun getDistributionItem() {
        if (!PresenterUtil.judgmentInternet(view)) return
        val listener = object : MyListener {
            override fun listenerSuccess(data: Any) {
                try {
                    val db = Gson().fromJson(data as String, DistributionItemResult::class.java)
                    val result = DistributionItemResult(db.code, ArrayList())
                    var bigId = ""
                    db.rows.forEach {
                        if (bigId != it.category) {
                            bigId = it.category
                            result.rows.add(DistributionItemBean("", 0, 0, "", it.bigName, it.category, "", true, ArrayList(), false))
                        }
                        it.isTitle = false
                        it.barContext
                        if (it.scanQty == null) it.scanQty = 0
                        result.rows.add(it)
                    }
                    view.showView(result)
                    view.hideLoading()
                } catch (e: Exception) {
                    view.showPrompt(e.message.toString())
                    view.errorDealWith()
                }
            }

            override fun listenerFailed(errorMessage: String) {
                view.showPrompt(errorMessage)
                view.errorDealWith()
            }
        }
        model.getDistributionItem(view.getData1() as String, view.getData2() as String, listener)
    }

    fun addDistributionItem(msg: String, barDate: String) {
        if (!PresenterUtil.judgmentInternet(view)) return
        val handler = MyHandler()
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                view.requestSuccess(data)
                view.hideLoading()
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                view.showPrompt(errorMessage)
                view.hideLoading()
                handler.cleanAll()
            }
        })
        model.addDistributionItem(msg, barDate, handler)
    }

    /**
     * getData3
     */
    fun saveDistribution() {
        if (!PresenterUtil.judgmentInternet(view)) return
        val data = view.getData3()
        if (data == "") {
            view.showPrompt("无保存数据")
            view.hideLoading()
            return
        }
        data as DistributionRequestResult
        val listener = object : MyListener {
            override fun listenerSuccess(data: Any) {
                try {
                    val result = Gson().fromJson(data as String, HttpBean::class.java)
                    if (result.code != 0) {
                        view.showPrompt(result.msg)
                        view.hideLoading()
                    } else {
                        view.updateDone(result)
                        view.hideLoading()
                    }
                } catch (e: Exception) {
                    view.showPrompt(data as String)
                    view.hideLoading()
                    return
                }
            }

            override fun listenerFailed(errorMessage: String) {
                view.showPrompt(errorMessage)
                view.errorDealWith(errorMessage)
            }
        }
        model.saveDistribution(view.getData1() as String, view.getData2() as String, data, listener)
    }
}