package com.cstore.zhiyazhang.cstoremanagement.presenter.adjustment

import android.content.Context
import com.cstore.zhiyazhang.cstoremanagement.bean.AdjustmentBean
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.adjustment.AdjustmentModel
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView

/**
 * Created by zhiya.zhang
 * on 2017/9/26 15:19.
 */
class AdjustmentPresenter(private val gView: GenericView, private val context: Context, private val activity: MyActivity){
    private val model=AdjustmentModel()

    /**
     * 根据日期得到所有货调
     */
    fun getAdjustmentList(date:String){
        if (!PresenterUtil.judgmentInternet(gView)) return
        model.getAllAdjustmentList(date, MyHandler.writeActivity(activity).writeListener(object : MyListener {
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

    fun searchAdjustment(searchMsg:String){
        if (!PresenterUtil.judgmentInternet(gView)) return
        model.searchAdjustment(searchMsg, MyHandler.writeActivity(activity).writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                gView.requestSuccess(data)
                gView.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.hideLoading()
            }
        }))
    }

    fun createAdjustment(data:ArrayList<AdjustmentBean>){
        if (!PresenterUtil.judgmentInternet(gView)) return
        model.createAdjustment(data, MyHandler.writeActivity(activity).writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
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