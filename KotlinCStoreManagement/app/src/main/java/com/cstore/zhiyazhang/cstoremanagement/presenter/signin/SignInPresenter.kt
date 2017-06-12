package com.cstore.zhiyazhang.cstoremanagement.presenter.signin

import android.os.Handler

import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.signin.SignInInterface
import com.cstore.zhiyazhang.cstoremanagement.model.signin.SignInModel
import com.cstore.zhiyazhang.cstoremanagement.utils.ConnectionDetector
import com.cstore.zhiyazhang.cstoremanagement.view.interface_view.SignInView

/**
 * Created by zhiya.zhang
 * on 2017/5/8 15:02.
 */

class SignInPresenter(private val signInView: SignInView) {
    private val signInInterface: SignInInterface
    private val mHandler = Handler()

    init {
        this.signInInterface = SignInModel()
    }

    fun login() {
        if (!ConnectionDetector.getConnectionDetector().isOnline) {
            signInView.hideLoading()
            return
        }
        signInView.showLoading()
        signInInterface.login(signInView.uid, signInView.password, object : MyListener {
            override fun contractSuccess() {

            }

            override fun contractSuccess(`object`: Any) {
                mHandler.post {
                    signInView.toActivity(`object` as User)
                    signInView.saveUser(`object`)
                    signInView.hideLoading()
                }
            }

            override fun contractFailed(errorMessage: String) {
                mHandler.post {
                    signInView.showFailedError(errorMessage)
                    signInView.hideLoading()
                }
            }
        })
    }
}
