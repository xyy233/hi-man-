package com.cstore.zhiyazhang.cstoremanagement.presenter.mobile

import com.cstore.zhiyazhang.cstoremanagement.bean.MobileDetailBean
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.mobile.MobileInterface
import com.cstore.zhiyazhang.cstoremanagement.model.mobile.MobileModel
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView

/**
 * Created by zhiya.zhang
 * on 2018/6/28 15:10.
 */
class MobilePresenter(private val view: GenericView) {
    val model: MobileInterface = MobileModel()

    /**
     * 得到所有历史
     */
    fun getMobileData() {
        if (!PresenterUtil.judgmentInternet(view)) return
        val handler = MyHandler()
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                view.showView(data)
                view.hideLoading()
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                view.showPrompt(errorMessage)
                view.hideLoading()
                handler.cleanAll()
            }
        })
        model.getMobileData(handler)
    }

    /**
     * 扫码得到单个
     */
    fun getMobileItem(msg: String) {
        if (!PresenterUtil.judgmentInternet(view)) return
        val handler = MyHandler()
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                view.showView(data)
                view.hideLoading()
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                view.showPrompt(errorMessage)
                view.hideLoading()
                handler.cleanAll()
            }
        })
        model.getMobileItem(msg, handler)
    }

    /**
     * 得到货架
     */
    fun getMobileGondola() {
        if (!PresenterUtil.judgmentInternet(view)) return
        val handler = MyHandler()
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                view.showView(data)
                view.hideLoading()
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                view.showPrompt(errorMessage)
                view.hideLoading()
                handler.cleanAll()
            }
        })
        model.getMobileGondola(handler)
    }

    /**
     * 创建
     */
    fun getMobilInsert(data: MobileDetailBean) {
        if (!PresenterUtil.judgmentInternet(view)) return
        val handler = MyHandler()
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                view.updateDone(data)
                view.hideLoading()
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                view.showPrompt(errorMessage)
                view.hideLoading()
                view.errorDealWith(errorMessage)
                handler.cleanAll()
            }
        })
        model.getMobilInsert(data, "", handler)
    }
}