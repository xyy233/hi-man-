package com.cstore.zhiyazhang.cstoremanagement.presenter.cashdaily

import android.content.Context
import android.view.View
import android.widget.TextView
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.CashDailyBean
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.cashdaily.CashDailyInterface
import com.cstore.zhiyazhang.cstoremanagement.model.cashdaily.CashDailyModel
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView

/**
 * Created by zhiya.zhang
 * on 2017/9/5 10:24.
 */
class CashDailyPresenter(private val gView:GenericView,private val context: Context,private val activity: MyActivity){
    private val mInterface:CashDailyInterface=CashDailyModel()
    fun getAllCashDaily(date:String){
        if(!PresenterUtil.judgmentInternet(gView))return
        mInterface.getAllCashDaily(date,MyHandler.writeActivity(activity).writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                gView.showView(data)
                gView.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
            }
        }))
    }

    fun updateCashDaily(view:View, cd:CashDailyBean, value:String) {
        if(!PresenterUtil.judgmentInternet(gView))return
        mInterface.updateCashDaily(value,cd, MyHandler.writeActivity(activity).writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                (view as TextView).text=value
                cd.cdValue=value
                gView.showPrompt(MyApplication.instance().getString(R.string.saveDone))
                gView.updateDone(data)
                gView.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.hideLoading()
            }
        }))
    }
}