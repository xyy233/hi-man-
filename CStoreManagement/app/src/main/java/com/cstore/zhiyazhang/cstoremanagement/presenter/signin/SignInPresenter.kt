package com.cstore.zhiyazhang.cstoremanagement.presenter.signin

import android.content.Context
import android.os.Handler
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.signin.SignInInterface
import com.cstore.zhiyazhang.cstoremanagement.model.signin.SignInModel
import com.cstore.zhiyazhang.cstoremanagement.sql.ContractTypeDao
import com.cstore.zhiyazhang.cstoremanagement.utils.ConnectionDetector
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.SignInView

/**
 * Created by zhiya.zhang
 * on 2017/5/8 15:02.
 */

class SignInPresenter(private val signInView: SignInView, val genericView: GenericView, val context: Context) {
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
                    //判断和之前的用户店号是否一样，不一样要删除本地数据库
                    if (User.getUser().storeId != `object`.storeId) {
                        val cd= ContractTypeDao(context)
                        cd.editSQL(null,"deleteTable")
                    }
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
