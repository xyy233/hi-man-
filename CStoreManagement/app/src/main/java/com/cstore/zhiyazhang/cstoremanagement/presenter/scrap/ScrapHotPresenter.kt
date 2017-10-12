package com.cstore.zhiyazhang.cstoremanagement.presenter.scrap

import android.os.Handler
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapContractBean
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.scrap.ScrapHotModel
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.OnlyMyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil.judgmentInternet
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView

/**
 * Created by zhiya.zhang
 * on 2017/9/1 12:16.
 */
class ScrapHotPresenter(private val gView: GenericView, private val activity: MyActivity) {
    private val anInterface = ScrapHotModel()
    private val handler = Handler()

    /**
     * 得到报废热食中类
     */
    fun getScrapHotMid() {
        if (!judgmentInternet(gView)) return
        anInterface.getMidCategory(OnlyMyHandler.writeActivity(activity).writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                handler.post {
                    gView.requestSuccess(data)
                    gView.hideLoading()
                }
            }

            override fun listenerFailed(errorMessage: String) {
                handler.post {
                    gView.showPrompt(errorMessage)
                    gView.errorDealWith()
                    gView.hideLoading()
                }
            }
        }))
    }

    fun getAllScraphot(midId: String) {
        if (!judgmentInternet(gView)) return
        anInterface.getHotItem(midId, OnlyMyHandler.writeActivity(activity).writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                handler.post {
                    gView.requestSuccess(data)
                    gView.hideLoading()
                }
            }

            override fun listenerFailed(errorMessage: String) {
                handler.post {
                    gView.showPrompt(errorMessage)
                    gView.errorDealWith()
                    gView.hideLoading()
                }
            }
        }))
    }

    fun submitScrap(data: ArrayList<ScrapContractBean>) {
        if (!judgmentInternet(gView)) return
        anInterface.submitScrap(data, OnlyMyHandler.writeActivity(activity).writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                handler.post {
                    gView.requestSuccess(data)
                    gView.hideLoading()
                }
            }

            override fun listenerFailed(errorMessage: String) {
                handler.post {
                    gView.showPrompt(errorMessage)
                    gView.hideLoading()
                }
            }
        }))
    }
}