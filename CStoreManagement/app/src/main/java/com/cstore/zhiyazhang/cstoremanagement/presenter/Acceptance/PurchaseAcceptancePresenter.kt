package com.cstore.zhiyazhang.cstoremanagement.presenter.acceptance

import android.content.Context
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.AcceptanceBean
import com.cstore.zhiyazhang.cstoremanagement.bean.AcceptanceItemBean
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
class PurchaseAcceptancePresenter(private val gView: GenericView, private val context: Context, private val activity: MyActivity) {
    private val model = AcceptanceModel()

    /**
     * 得到所有验收单
     */
    fun getAcceptanceList(date: String) {
        if (!PresenterUtil.judgmentInternet(gView)) return
        model.getAcceptanceList(date, MyHandler.writeActivity(activity).writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                gView.requestSuccess(data)
                gView.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(context.getString(R.string.socketError)+errorMessage)
                gView.hideLoading()
            }
        }))
    }

    /**
     * 更新验收单
     */
    fun updateAcceptance(date:String, ab:AcceptanceBean){
        if (!PresenterUtil.judgmentInternet(gView)) return
        model.updateAcceptance(date, ab, MyHandler.writeActivity(activity).writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                gView.showPrompt(MyApplication.instance().getString(R.string.saveDone))
                gView.updateDone(data)
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(context.getString(R.string.socketError)+errorMessage)
                gView.errorDealWith()
            }
        }))
    }

    /**
     * 得到所有配送商
     */
    fun getVendor(){
        if (!PresenterUtil.judgmentInternet(gView)) return
        model.getVendor(MyHandler.writeActivity(activity).writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                gView.showView(data)
                gView.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(context.getString(R.string.socketError)+errorMessage)
                gView.errorDealWith()
            }
        }))
    }

    fun getCommodity(commodityId:String, vendorId:String){
        if (!PresenterUtil.judgmentInternet(gView)) return
        model.getCommodity(commodityId,vendorId, MyHandler.writeActivity(activity).writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                gView.requestSuccess(data)
                gView.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                if (errorMessage=="0"){
                    gView.showPrompt(context.getString(R.string.socketError))
                }
                gView.showPrompt(errorMessage+","+context.getString(R.string.getCommodity_error))
                gView.hideLoading()
            }
        }))
    }

    fun createAcceptance(date:String, ab: AcceptanceBean?,aib:AcceptanceItemBean){
        if (!PresenterUtil.judgmentInternet(gView)) return
        model.createAcceptance(date,ab,aib, MyHandler.writeActivity(activity).writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                gView.updateDone(data)
                gView.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(context.getString(R.string.socketError)+errorMessage)
                gView.hideLoading()
            }
        }))

    }


}