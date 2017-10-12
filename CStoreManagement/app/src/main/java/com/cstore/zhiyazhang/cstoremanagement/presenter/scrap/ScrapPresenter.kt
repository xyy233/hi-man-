package com.cstore.zhiyazhang.cstoremanagement.presenter.scrap

import android.os.Handler
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapContractBean
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.scrap.ScrapModel
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.OnlyMyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil.judgmentInternet
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.ScrapView
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication

/**
 * Created by zhiya.zhang
 * on 2017/8/22 13:35.
 */
class ScrapPresenter(private val gView: GenericView, private val sView: ScrapView, private val activity: MyActivity) {
    private val anInterface = ScrapModel(sView)
    private val handler = Handler()

    /**
     * 根据日期得到已报废的商品
     */
    fun getAllScrap() {
        if (!judgmentInternet(gView)) return
        anInterface.getAllScrap(OnlyMyHandler.writeActivity(activity).writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                handler.post { gView.requestSuccess(data as ArrayList<ScrapContractBean>) }
            }

            override fun listenerFailed(errorMessage: String) {
                handler.post {
                    gView.showPrompt(errorMessage)
                    gView.hideLoading()
                }
            }
        }))
    }

    /**
     * 根据关键字搜索报废品
     */
    fun searchScrap(message: String) {
        if (!judgmentInternet(gView)) return
        anInterface.searchScrap(message, OnlyMyHandler.writeActivity(activity).writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                handler.post { gView.requestSuccess(data as ArrayList<ScrapContractBean>) }
            }

            override fun listenerFailed(errorMessage: String) {
                handler.post {
                    gView.showPrompt(errorMessage)
                    gView.hideLoading()
                }
            }
        }))
    }

    fun submitScraps(data: ArrayList<ScrapContractBean>, reCode: Int) {
        if (!judgmentInternet(gView)) return
        anInterface.submitScraps(data, reCode, OnlyMyHandler.writeActivity(activity).writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                handler.post {
                    sView.updateDone()
                    gView.showPrompt(MyApplication.instance().applicationContext.getString(R.string.done))
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