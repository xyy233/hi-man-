/*
package com.cstore.zhiyazhang.cstoremanagement.presenter.scrap

import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.scrap.BarCodeScrapModel
import com.cstore.zhiyazhang.cstoremanagement.utils.ConnectionDetector
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.zhiyazhang.mykotlinapplication.utils.MyApplication

*/
/**
 * Created by zhiya.zhang
 * on 2017/8/11 17:36.
 *//*

class BarCodeScrapPresenter(val gView:GenericView){

    val anInterface=BarCodeScrapModel()

    fun getScrap(barCode:String){
        gView.showLoading()
        if (!ConnectionDetector.getConnectionDetector().isOnline) {
            gView.showPrompt(MyApplication.instance().applicationContext.getString(R.string.noInternet))
            gView.hideLoading()
            return
        }
        anInterface.getScrap(User.getUser(),barCode, object : MyListener {
            override fun listenerSuccess() {}

            override fun listenerSuccess(`object`: Any) {
                gView.hideLoading()
                gView.requestSuccess(`object`)
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.hideLoading()
            }
        })
    }
}*/
