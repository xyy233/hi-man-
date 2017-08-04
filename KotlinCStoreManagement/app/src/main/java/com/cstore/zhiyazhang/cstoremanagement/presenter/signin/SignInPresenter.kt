package com.cstore.zhiyazhang.cstoremanagement.presenter.signin

import android.os.Handler
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.signin.SignInInterface
import com.cstore.zhiyazhang.cstoremanagement.model.signin.SignInModel
import com.cstore.zhiyazhang.cstoremanagement.utils.ConnectionDetector
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.SignInView

/**
 * Created by zhiya.zhang
 * on 2017/5/8 15:02.
 */

class SignInPresenter(private val signInView: SignInView, val genericView: GenericView) {
    private val signInInterface: SignInInterface
    private val mHandler = Handler()

    init {
        this.signInInterface = SignInModel()
    }

    fun login() {
        if (!ConnectionDetector.getConnectionDetector().isOnline) {
            genericView.hideLoading()
            return
        }
        genericView.showLoading()
        signInInterface.login(signInView.uid, signInView.password, object : MyListener {
            override fun listenerSuccess() {

            }

            override fun listenerSuccess(`object`: Any) {
                mHandler.post {
                    genericView.requestSuccess(`object` as User)
                    signInView.saveUser(`object`)
                    genericView.hideLoading()
                }
            }

            override fun listenerFailed(errorMessage: String) {
                mHandler.post {
                    genericView.showPrompt(errorMessage)
                    genericView.hideLoading()
                }
            }
        })
    }
}
