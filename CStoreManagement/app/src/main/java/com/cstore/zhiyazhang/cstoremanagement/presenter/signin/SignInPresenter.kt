package com.cstore.zhiyazhang.cstoremanagement.presenter.signin

import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.signin.SignInModel
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.OnlyMyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil
import com.cstore.zhiyazhang.cstoremanagement.view.SignInActivity

/**
 * Created by zhiya.zhang
 * on 2017/5/8 15:02.
 */

class SignInPresenter(private val view: SignInActivity) {
    private val model = SignInModel()

    fun login() {
        if (!PresenterUtil.judgmentInternet(view)) return

        val handler = OnlyMyHandler.writeActivity(view)
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                data as User
                view.saveUser(data)
                view.requestSuccess(data)
                view.hideLoading()
            }

            override fun listenerFailed(errorMessage: String) {
                view.showPrompt(errorMessage)
                view.hideLoading()
            }
        })

        model.login(view.uid, view.password, handler)
    }
}
