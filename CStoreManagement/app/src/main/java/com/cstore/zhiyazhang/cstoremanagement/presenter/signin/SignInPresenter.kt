package com.cstore.zhiyazhang.cstoremanagement.presenter.signin

import android.content.Context
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.signin.SignInInterface
import com.cstore.zhiyazhang.cstoremanagement.model.signin.SignInModel
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.SignInView

/**
 * Created by zhiya.zhang
 * on 2017/5/8 15:02.
 */

class SignInPresenter(private val signInView: SignInView, private val genericView: GenericView, private val context: Context, private val myActivity: MyActivity) {
    private val signInInterface: SignInInterface = SignInModel()

    fun login() {
        if (!PresenterUtil.judgmentInternet(genericView))return
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
