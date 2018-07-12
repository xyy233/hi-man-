package com.cstore.zhiyazhang.cstoremanagement.presenter.scrap

import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapContractBean
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.scrap.ScrapHotModel
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil.judgmentInternet
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView

/**
 * Created by zhiya.zhang
 * on 2017/9/1 12:16.
 */
class ScrapHotPresenter(private val gView: GenericView, private val activity: MyActivity) {
    private val anInterface = ScrapHotModel()

    /**
     * 得到报废热食中类
     */
    fun getScrapHotMid() {
        if (!judgmentInternet(gView)) return
        val handler = MyHandler()
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                try {
                    gView.requestSuccess(data)
                    gView.hideLoading()
                } catch (e: Exception) {
                    listenerFailed(activity.getString(R.string.socketError))
                }
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
                handler.cleanAll()
            }
        })
        anInterface.getMidCategory(handler)
    }

    fun getAllScraphot(midId: String) {
        if (!judgmentInternet(gView)) return
        val handler = MyHandler()
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                try {
                    gView.requestSuccess(data)
                    gView.hideLoading()
                } catch (e: Exception) {
                    listenerFailed(activity.getString(R.string.socketError))
                }
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
                handler.cleanAll()
            }
        })
        anInterface.getHotItem(midId, handler)
    }

    fun submitScrap(data: ArrayList<ScrapContractBean>) {
        if (!judgmentInternet(gView)) return
        val handler = MyHandler()
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                try {
                    gView.requestSuccess(data)
                    gView.hideLoading()
                } catch (e: Exception) {
                    listenerFailed(activity.getString(R.string.socketError))
                }
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.hideLoading()
                handler.cleanAll()
            }
        })
        anInterface.submitScrap(data, handler)
    }
}