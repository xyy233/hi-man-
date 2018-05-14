package com.cstore.zhiyazhang.cstoremanagement.presenter.acceptance

import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.AcceptanceBean
import com.cstore.zhiyazhang.cstoremanagement.bean.AcceptanceItemBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ReturnAcceptanceBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ReturnAcceptanceItemBean
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.acceptance.AcceptanceModel
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView

/**
 * Created by zhiya.zhang
 * on 2017/9/11 16:51.
 */
class PurchaseAcceptancePresenter(private val gView: GenericView, private val activity: MyActivity) {
    private val model = AcceptanceModel()

    /**
     * 得到所有进货验收单
     */
    fun getAcceptanceList(date: String) {
        if (!PresenterUtil.judgmentInternet(gView)) return
        val handler = MyHandler()
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                gView.requestSuccess(data)
                gView.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(activity.getString(R.string.socketError) + errorMessage)
                gView.errorDealWith()
            }
        })
        model.getAcceptanceList(date, handler)
    }

    /**
     * 得到所有退货验收单
     */
    fun getReturnAcceptanceList(date: String) {
        if (!PresenterUtil.judgmentInternet(gView)) return
        val handler = MyHandler()
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                gView.requestSuccess(data)
                gView.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(activity.getString(R.string.socketError) + errorMessage)
                gView.hideLoading()
            }
        })
        model.getReturnAcceptanceList(date, handler)
    }

    /**
     * 更新验收单
     */
    fun updateAcceptance(date: String, ab: AcceptanceBean) {
        if (!PresenterUtil.judgmentInternet(gView)) return
        val handler = MyHandler()
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                gView.showPrompt(activity.getString(R.string.saveDone))
                gView.updateDone(data)
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(activity.getString(R.string.socketError) + errorMessage)
                gView.errorDealWith()
            }
        })
        model.updateAcceptance(date, ab, handler)
    }

    /**
     * 更新退货验收单
     */
    fun updateReturnAcceptance(date: String, rab: ReturnAcceptanceBean) {
        if (!PresenterUtil.judgmentInternet(gView)) return
        val handler = MyHandler()
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                gView.showPrompt(MyApplication.instance().getString(R.string.saveDone))
                gView.updateDone(data)
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(activity.getString(R.string.socketError) + errorMessage)
                gView.errorDealWith()
            }
        })
        model.updateReturnAcceptance(date, rab, handler)
    }

    /**
     * 得到所有配送商
     */
    fun getVendor() {
        if (!PresenterUtil.judgmentInternet(gView)) return
        val handler = MyHandler()
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                gView.showView(data)
                gView.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(activity.getString(R.string.socketError) + errorMessage)
                gView.errorDealWith()
            }
        })
        model.getVendor(handler)
    }

    /**
     * 得到输入的商品
     */
    fun getCommodity(ab: AcceptanceBean?, vendorId: String) {
        if (!PresenterUtil.judgmentInternet(gView)) return
        val handler = MyHandler()
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                gView.requestSuccess(data)
                gView.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(activity.getString(R.string.socketError) + "," + errorMessage)
                gView.hideLoading()
            }
        })
        model.getCommodity(ab, vendorId, handler)
    }

    fun getReturnCommodity(rab: ReturnAcceptanceBean?, vendorId: String) {
        if (!PresenterUtil.judgmentInternet(gView)) return
        val handler = MyHandler()
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                gView.requestSuccess(data)
                gView.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(activity.getString(R.string.socketError) + "," + errorMessage)
                gView.hideLoading()
            }
        })
        model.getReturnCommodity(rab, vendorId, handler)
    }

    /**
     * 创建进货验收单
     */
    fun createAcceptance(date: String, ab: AcceptanceBean?, aib: ArrayList<AcceptanceItemBean>) {
        if (!PresenterUtil.judgmentInternet(gView)) return
        val handler = MyHandler()
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                gView.updateDone(data)
                gView.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(activity.getString(R.string.socketError) + errorMessage)
                gView.hideLoading()
            }
        })
        model.createAcceptance(date, ab, aib, handler)
    }

    /**
     * 创建退货验收单
     */
    fun createReturnAcceptance(date: String, rab: ReturnAcceptanceBean?, raib: ArrayList<ReturnAcceptanceItemBean>) {
        if (!PresenterUtil.judgmentInternet(gView)) return
        val handler = MyHandler()
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                gView.updateDone(data)
                gView.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(activity.getString(R.string.socketError) + errorMessage)
                gView.hideLoading()
            }
        })
        model.createReturnAcceptance(date, rab, raib, handler)
    }


}