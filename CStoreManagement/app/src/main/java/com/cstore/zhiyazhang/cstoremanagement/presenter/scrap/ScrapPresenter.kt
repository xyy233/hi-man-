package com.cstore.zhiyazhang.cstoremanagement.presenter.scrap

import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapContractBean
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.scrap.ScrapModel
import com.cstore.zhiyazhang.cstoremanagement.utils.ConnectionDetector
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.ScrapView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zhiyazhang.mykotlinapplication.utils.MyApplication

/**
 * Created by zhiya.zhang
 * on 2017/8/22 13:35.
 */
class ScrapPresenter(private val gView:GenericView,private val sView:ScrapView){
    private val anInterface=ScrapModel()

    /**
     * 根据日期得到已报废的商品
     */
    fun getAllScrap(){
        gView.showLoading()
        if (!ConnectionDetector.getConnectionDetector().isOnline) {
            gView.showPrompt(MyApplication.instance().applicationContext.getString(R.string.noInternet))
            gView.hideLoading()
            return
        }
        anInterface.getAllScrap(sView.getDate(), object : MyListener {
            override fun listenerSuccess(data: String) {
                gView.requestSuccess(Gson().fromJson<ArrayList<ScrapContractBean>>(data, object : TypeToken<ArrayList<ScrapContractBean>>() {}.type))
                gView.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.hideLoading()
                gView.showPrompt(errorMessage)
            }
        })
    }

    /**
     * 根据关键字搜索报废品
     */
    fun searchScrap(message:String){
        gView.showLoading()
        if (!ConnectionDetector.getConnectionDetector().isOnline) {
            gView.showPrompt(MyApplication.instance().applicationContext.getString(R.string.noInternet))
            gView.hideLoading()
            return
        }
        anInterface.searchScrap(message, object : MyListener {
            override fun listenerSuccess(data: String) {
                gView.requestSuccess(Gson().fromJson<ArrayList<ScrapContractBean>>(data, object : TypeToken<ArrayList<ScrapContractBean>>() {}.type))
                gView.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.hideLoading()
                gView.showPrompt(errorMessage)
            }
        })
    }

    fun submitScraps(data:ArrayList<ScrapContractBean>, reCode:Int){
        gView.showLoading()
        if (!ConnectionDetector.getConnectionDetector().isOnline) {
            gView.showPrompt(MyApplication.instance().applicationContext.getString(R.string.noInternet))
            gView.hideLoading()
            return
        }
        anInterface.submitScraps(data, reCode, object : MyListener {

            override fun listenerSuccess(data: String) {
                sView.updateDone()
                gView.showPrompt(MyApplication.instance().applicationContext.getString(R.string.done))
                gView.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.hideLoading()
                gView.showPrompt(errorMessage)
            }
        })
    }
}