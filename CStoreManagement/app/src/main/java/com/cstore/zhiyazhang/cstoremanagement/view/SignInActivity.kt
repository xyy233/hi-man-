package com.cstore.zhiyazhang.cstoremanagement.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Camera
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.presenter.signin.SignInPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import com.cstore.zhiyazhang.cstoremanagement.utils.ReportListener
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.SignInView
import kotlinx.android.synthetic.main.activity_signin.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


/**
 * Created by zhiya.zhang
 * on 2017/6/7 15:23.
 */
class SignInActivity(override val layoutId: Int = R.layout.activity_signin) : MyActivity(), SignInView, EasyPermissions.PermissionCallbacks {


    private var preferences: SharedPreferences? = null
    private val mSigninPresenter: SignInPresenter = SignInPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //获得权限
        getPermissions()
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        //尝试获得之前的用户
        preferences = getSharedPreferences("idpwd", Context.MODE_PRIVATE)
        //如果获得了就直接输入否则为""
        user_id.setText(preferences?.getString("id", ""))
        user_password.setText(preferences?.getString("pwd", ""))
        //根据获得用户信息变更checkBox样式
        if (user_id.text.toString() != "") {
            save_id.isChecked = true
            if (user_password.text.toString() != "") save_pwd.isChecked = true
        }
        detail_box.visibility = View.VISIBLE
        detail_box.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_slide_in))
    }

    override fun initClick() {
        test.setOnClickListener({
            val intent = Intent(this@SignInActivity, HomeActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@SignInActivity, test, "login").toBundle())
        })
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

    @SuppressLint("SetTextI18n")
    override fun initData() {
        app_version.text = "v: ${MyApplication.getVersion()}"
    }

    override fun onStart() {
        super.onStart()
        //获得ip地址
        localhost_ip.text = MyApplication.getIP()
        try {
            //获得wifi名字
            var wifiName = (application.getSystemService(WIFI_SERVICE) as WifiManager).connectionInfo?.ssid
            if (wifiName == null || wifiName == "") wifiName = getString(R.string.mobile_network)
            @SuppressLint("WifiManagerLeak")
            wifi_hints.text = "${getString(R.string.now_wifi)}$wifiName\n${getString(R.string.wifi_hints)}"
        } catch (e: Exception) {
            wifi_hints.text = getString(R.string.not_wifi_name)
        }
    }

    override val uid: String
        get() = user_id.text.toString()

    override val password: String
        get() = user_password.text.toString()

    override fun showLoading() {
        wifi_hints.visibility = View.GONE
        progress.visibility = View.VISIBLE
        login.isEnabled = false
    }

    override fun hideLoading() {
        progress.visibility = View.GONE
        wifi_hints.visibility = View.VISIBLE
        login.isEnabled = true
    }

    override fun <T> requestSuccess(rData: T) {
        when (rData) {
            is User -> {
                showPrompt(rData.name + "您好,登陆成功")
                ReportListener.reportEnter(rData.storeId)
                val out = intent.getIntExtra("out", 0)
                if (out == 1) {
                    //中卫调拨的
                    val i = Intent()
                    setResult(0, i)
                    onBackPressed()
                } else {
                    val intent = Intent(this@SignInActivity, HomeActivity::class.java)
                    intent.putExtra("user", rData)
                    startActivity(intent)
                    finish()
                }
            }
            else -> showPrompt(getString(R.string.system_error))
        }
    }

    override fun <T> showView(aData: T) {
    }

    override fun errorDealWith() {
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
        User.saveUser(user)
    }


    private fun getPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (judgmentPermissions()) {
            }
        } else {
            if (!cameraIsCanUse()) {
                MyToast.getLongToast("您未开启权限，请开启权限！")
                val intent = Intent()
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                intent.data = Uri.fromParts("package", this@SignInActivity.packageName, null)
                this@SignInActivity.startActivity(intent)
            }
        }
    }

    /**
     * 返回true 表示可以使用  返回false表示不可以使用
     */
    private fun cameraIsCanUse(): Boolean {
        var isCanUse = true
        var mCamera: Camera? = null
        try {
            mCamera = Camera.open()
            val mParameters = mCamera!!.parameters //针对魅族手机
            mCamera.parameters = mParameters
        } catch (e: Exception) {
            isCanUse = false
        }

        if (mCamera != null) {
            try {
                mCamera.release()
            } catch (e: Exception) {
                e.printStackTrace()
                return isCanUse
            }

        }
        return isCanUse
    }

    //获得权限
    @pub.devrel.easypermissions.AfterPermissionGranted(1)
    private fun judgmentPermissions(): Boolean {
        val perms = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE)
        if (!EasyPermissions.hasPermissions(this, *perms)) {
            EasyPermissions.requestPermissions(this, getString(R.string.open_permission), 1, *perms)
            return false
        }
        return true
    }


    //请求权限结果
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    //获取权限失败
    override fun onPermissionsDenied(requestCode: Int, list: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    //获取权限成功
    override fun onPermissionsGranted(requestCode: Int, list: List<String>) {
    }
}