package com.cstore.zhiyazhang.cstoremanagement.presenter.cashdaily

import android.content.Context
import android.util.Log
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
class CashDailyPresenter(private val gView: GenericView, private val context: Context, private val activity: MyActivity) {
    private val mInterface: CashDailyInterface = CashDailyModel()
    fun getAllCashDaily(date: String) {
        if (!PresenterUtil.judgmentInternet(gView)) return
        val handler = MyHandler()
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                gView.showView(data)
                gView.hideLoading()
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                handler.cleanAll()
            }
        })
        mInterface.getAllCashDaily(date, handler)
    }

    fun updateCashDaily(date: String, view: View, cd: CashDailyBean, value: String) {
        if (!PresenterUtil.judgmentInternet(gView)) return
        val handler = MyHandler()
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                if (cd.cdId == "1097") {
                    try {
                        val weather = context.resources.getStringArray(R.array.weather)[value.toInt() - 1]
                        (view as TextView).text = weather
                        cd.cdValue = weather
                    } catch (e: Exception) {
                        Log.e("CashDaily", e.message)
                    }
                } else {
                    (view as TextView).text = value
                    cd.cdValue = value
                }
                gView.showPrompt(MyApplication.instance().getString(R.string.saveDone))
                gView.updateDone(data)
                gView.hideLoading()
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
                handler.cleanAll()
            }
        })
        mInterface.updateCashDaily(date, value, cd, handler)
    }
}