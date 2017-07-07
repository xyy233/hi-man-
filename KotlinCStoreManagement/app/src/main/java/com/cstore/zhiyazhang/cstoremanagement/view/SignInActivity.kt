package com.cstore.zhiyazhang.cstoremanagement.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Camera
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.presenter.signin.SignInPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.SignInView
import com.zhiyazhang.mykotlinapplication.utils.MyActivity
import com.zhiyazhang.mykotlinapplication.utils.MyApplication
import kotlinx.android.synthetic.main.activity_signin.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File


/**
 * Created by zhiya.zhang
 * on 2017/6/7 15:23.
 */
class SignInActivity(override val layoutId: Int = R.layout.activity_signin) : MyActivity(), SignInView, GenericView, EasyPermissions.PermissionCallbacks {

    var preferences: SharedPreferences? = null
    val mSigninPresenter: SignInPresenter = SignInPresenter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        //获得权限
        getPermissions()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        //尝试获得之前的用户
        preferences = getSharedPreferences("idpwd", Context.MODE_PRIVATE)
        /*test.setOnClickListener({
            val intent = Intent(this@SignInActivity, HomeActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@SignInActivity, test, "login").toBundle())
        })*/
//        test2.setOnClickListener { MyApplication.instance().startService(Intent(MyApplication.instance().applicationContext, UpdateService::class.java)) }
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
        app_version.text = "v: ${MyApplication.getVersion()}"
/*        // 获取WiFi服务
        val wifiInfo = (application.getSystemService(WIFI_SERVICE) as WifiManager).connectionInfo
        val ipAddress = wifiInfo.ipAddress
        val ip = (ipAddress and 0xFF).toString() + "." +
                (ipAddress shr 8 and 0xFF) + "." +
                (ipAddress shr 16 and 0xFF) + "." +
                (ipAddress shr 24 and 0xFF)*/

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
        } catch(e: Exception) {
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

    override fun <T> requestSuccess(objects: T) {
        when (objects) {
            is User -> {
                showPrompt(objects.name + "您好,登陆成功")
                val intent = Intent(this@SignInActivity, HomeActivity::class.java)
                intent.putExtra("user", objects)
                startActivity(intent)
                //finish()
            }
            else -> showPrompt(getString(R.string.system_error))
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


    private fun getPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (judgmentPermissions()) {
                deleteDownload()
            }
        } else {
            if (!cameraIsCanUse()) {
                MyToast.getLongToast("您未开启相机权限，请开启相机权限！")
                val intent = Intent()
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                intent.data = Uri.fromParts("package", this@SignInActivity.packageName, null)
                this@SignInActivity.startActivity(intent)
            }
            deleteDownload()
        }
    }

    /**
     * 返回true 表示可以使用  返回false表示不可以使用
     */
    fun cameraIsCanUse(): Boolean {
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

    //获得相机权限
    @pub.devrel.easypermissions.AfterPermissionGranted(1)
    fun judgmentPermissions(): Boolean {
        val perms = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (!EasyPermissions.hasPermissions(this, *perms)) {
            EasyPermissions.requestPermissions(this, getString(R.string.openAu), 1, *perms)
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
        deleteDownload()
    }

    //检查删除安装包
    private fun deleteDownload() {
        val versionName = "CStoreManagement.apk"
        val downloadPath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath}${File.separator}$versionName"
        val f = File(downloadPath)
        if (f.exists()) {
            f.delete()
        }
    }
}