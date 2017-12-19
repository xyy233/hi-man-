package com.cstore.zhiyazhang.cstoremanagement.presenter.contract

import android.os.Handler
import com.cstore.zhiyazhang.cstoremanagement.bean.ContractTypeResult
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.contracttype.ContractTypeInterface
import com.cstore.zhiyazhang.cstoremanagement.model.contracttype.ContractTypeModel
import com.cstore.zhiyazhang.cstoremanagement.sql.ContractTypeDao
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.ContractTypeView
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.google.gson.Gson

/**
 * Created by zhiya.zhang
 * on 2017/6/12 16:13.
 */
class ContractTypePresenter(val gView: GenericView, val tView: ContractTypeView, val ctd: ContractTypeDao) {
    val anInterface: ContractTypeInterface = ContractTypeModel()
    val mHandler = Handler()
    fun getAllContractType() {
        if (!PresenterUtil.judgmentInternet(gView)) return
        anInterface.getAllContractType(User.getUser(), tView.isJustLook, object : MyListener {

            override fun listenerSuccess(data: Any) {
                val contracts= Gson().fromJson(data as String, ContractTypeResult::class.java)
                mHandler.post(
                        Runnable {
                    kotlin.run {
                        val adapter: ContractTypeAdapter = ContractTypeAdapter(ctd.allData, tView.isJustLook, contracts.detail) {
                            gView.requestSuccess(it)
                        }
                        gView.showView(adapter)
                        if (adapter.ctbs.isEmpty())tView.showUsaTime(true)else tView.showUsaTime(false)
                        gView.hideLoading()
                    }
                })
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }

        })
    }
}