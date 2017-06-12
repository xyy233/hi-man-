package com.cstore.zhiyazhang.cstoremanagement.presenter.contract

import android.os.Handler
import com.cstore.zhiyazhang.cstoremanagement.bean.ContractTypeResult
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.contractType.ContractTypeInterface
import com.cstore.zhiyazhang.cstoremanagement.model.contractType.ContractTypeModel
import com.cstore.zhiyazhang.cstoremanagement.utils.ConnectionDetector
import com.cstore.zhiyazhang.cstoremanagement.view.interface_view.ContractTypeView

/**
 * Created by zhiya.zhang
 * on 2017/6/12 16:13.
 */
class ContractTypePresenter(val typeView: ContractTypeView) {
    val anInterface: ContractTypeInterface = ContractTypeModel()
    val mHandler = Handler()
    fun getAllContractType() {
        if (!ConnectionDetector.getConnectionDetector().isOnline) {
            typeView.hideLoading()
            return
        }
        anInterface.getAllContractType(User.getUser(), object : MyListener {
            //在这无用
            override fun contractSuccess() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun contractSuccess(`object`: Any) {
                mHandler.post(Runnable {
                    kotlin.run {
                        val adapter: ContractTypeAdapter = ContractTypeAdapter((`object` as ContractTypeResult).detail) {
                            typeView.toActivity(it)
                        }
                        typeView.showView(adapter)
                        typeView.hideLoading()
                    }
                })
            }

            override fun contractFailed(errorMessage: String) {
                typeView.showFailedError(errorMessage)
                typeView.hideLoading()
            }

        })
    }
}