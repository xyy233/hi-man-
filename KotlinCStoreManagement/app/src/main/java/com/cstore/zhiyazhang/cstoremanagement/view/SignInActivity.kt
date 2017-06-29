package com.cstore.zhiyazhang.cstoremanagement.view

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.presenter.signin.SignInPresenter
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.SignInView
import com.zhiyazhang.mykotlinapplication.utils.MyActivity
import kotlinx.android.synthetic.main.activity_signin.*

/**
 * Created by zhiya.zhang
 * on 2017/6/7 15:23.
 */
class SignInActivity(override val layoutId: Int = R.layout.activity_signin) : MyActivity(), SignInView, GenericView {

    var preferences: SharedPreferences? = null
    val mSigninPresenter: SignInPresenter = SignInPresenter(this,this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        //尝试获得之前的用户
        preferences = getSharedPreferences("idpwd", Context.MODE_PRIVATE)
        test.setOnClickListener({
            val intent = Intent(this@SignInActivity, HomeActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@SignInActivity, test, "login").toBundle())
        })
        test2.setOnClickListener { startActivity(Intent(this@SignInActivity,ImageActivity::class.java)) }
        //如果获得了就直接输入否则为“”
        user_id.setText(preferences?.getString("id", ""))
        user_password.setText(preferences?.getString("pwd", ""))
        //根据获得用户信息变更checkBox样式
        if (user_id.text.toString() != "") {
            save_id.isChecked = true
            if (user_password.text.toString() != "") save_pwd.isChecked = true
        }
        //如果保存id checkBox为true，保存密码为可点击，否则不可点击并为false
        save_id.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) save_pwd.isEnabled = true else {
                save_pwd.isChecked = false
                save_pwd.isEnabled = false
            }
        }
        //如果保存密码为true，保存帐号必定为true
        save_pwd.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) save_id.isChecked = true
        }
        login.setOnClickListener({
            if (uid == "")
                showPrompt(getString(R.string.please_edit_account))
            else {
                mSigninPresenter.login()
                //将输入法隐藏
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                        .hideSoftInputFromWindow(user_password.windowToken, 0)
            }
        })
    }

    override val uid: String
        get() = user_id.text.toString()

    override val password: String
        get() = user_password.text.toString()

    override fun showLoading() {
        progress.visibility = View.VISIBLE
        login.isEnabled = false
    }

    override fun hideLoading() {
        progress.visibility = View.GONE
        login.isEnabled = true
    }

    override fun <T> requestSuccess(objects: T) {
        when (objects) {
            is User ->{
                showPrompt(objects.name + "您好,登陆成功")
                val intent = Intent(this@SignInActivity, HomeActivity::class.java)
                intent.putExtra("user", objects)
                startActivity (intent)
                finish ()
            }
            else->showPrompt(getString(R.string.system_error))
        }
    }

    override fun <T> showView(adapter: T) {
    }
    override fun errorDealWith() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveUser(user: User) {
        val editor = preferences?.edit()
        if (save_id.isChecked) {
            editor!!.putString("id", user_id.text.toString())
            if (save_pwd.isChecked) {
                editor.putString("pwd", user_password.text.toString())
            } else {
                editor.remove("pwd")
            }
        } else {
            editor!!.remove("id")
            editor.remove("pwd")
        }
        editor.apply()
        val userShared = getSharedPreferences("user", Context.MODE_PRIVATE)
        val ue = userShared.edit()
        ue.putString("storeId", user.storeId)
        ue.putString("uid", user.uId)
        ue.putString("uName", user.name)
        ue.putString("telphone", user.telphone)
        ue.putString("storeName", user.storeName)
        ue.putString("address", user.address)
        ue.apply()
    }
}