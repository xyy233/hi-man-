package com.cstore.zhiyazhang.cstoremanagement.presenter.signin

import android.content.Context
import android.os.Handler
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.signin.SignInInterface
import com.cstore.zhiyazhang.cstoremanagement.model.signin.SignInModel
import com.cstore.zhiyazhang.cstoremanagement.utils.ConnectionDetector
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.SignInView

/**
 * Created by zhiya.zhang
 * on 2017/5/8 15:02.
 */

class SignInPresenter(private val signInView: SignInView, val genericView: GenericView, val context: Context, val myActivity: MyActivity) {
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

        signInInterface.login(signInView,genericView, MyHandler.writeActivity(myActivity).writeListener(object : MyListener {

            override fun listenerSuccess(data: Any) {
                data as User
                genericView.requestSuccess(data)
                genericView.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                genericView.showPrompt(errorMessage)
                genericView.hideLoading()
            }

        }))
    }
}
