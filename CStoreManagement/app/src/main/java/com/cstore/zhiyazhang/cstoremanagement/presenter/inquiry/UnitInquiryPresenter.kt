package com.cstore.zhiyazhang.cstoremanagement.presenter.inquiry

import com.cstore.zhiyazhang.cstoremanagement.bean.UnitInquiryBean
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.inquiry.UnitInquiryModel
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView

/**
 * Created by zhiya.zhang
 * on 2018/1/30 11:41.
 */
class UnitInquiryPresenter(private val view: GenericView) {
    private val model = UnitInquiryModel()

    /**
     * 得到数据
     * getData1必须重写并传递 activity对象
     * getData2必须重写并传递 搜索关键字String
     * getData3必须重写并传递 对应搜索类型
     */
    fun getData() {
        if (!PresenterUtil.judgmentInternet(view)) return
        val handler = MyHandler()
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                view.showView(data)
                view.hideLoading()
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                view.errorDealWith(errorMessage)
                view.showPrompt(errorMessage)
                view.hideLoading()
                handler.cleanAll()
            }
        })
        model.getData(view.getData2() as String, view.getData3() as Int, handler)
    }

    fun saveInv() {
        val uib = view.getData4()
        if (uib == null) {
            view.showPrompt("获得数据出错saveInv")
            return
        }
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
                view.errorDealWith()
                view.hideLoading()
                handler.cleanAll()
            }
        })
        model.saveInv(uib as UnitInquiryBean, handler)
    }

    fun saveMinQty() {
        val uib = view.getData4()
        if (uib == null) {
            view.showPrompt("获得数据出错saveInv")
            return
        }
        if (!PresenterUtil.judgmentInternet(view)) return
        val handler = MyHandler()
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                view.requestSuccess2(data)
                view.hideLoading()
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                view.showPrompt(errorMessage)
                view.errorDealWith()
                view.hideLoading()
                handler.cleanAll()
            }
        })
        model.saveMinQty(uib as UnitInquiryBean, handler)
    }
}