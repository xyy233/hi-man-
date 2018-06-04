package com.cstore.zhiyazhang.cstoremanagement.view.update

import android.Manifest
import android.content.Intent
import android.hardware.Camera
import android.net.Uri
import android.os.Environment
import android.support.v7.app.AlertDialog
import android.view.ContextThemeWrapper
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.UpdateBean
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.url.AppUrl
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyStringCallBack
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import com.cstore.zhiyazhang.cstoremanagement.view.SignInActivity
import com.google.gson.Gson
import com.zhy.http.okhttp.OkHttpUtils
import kotlinx.android.synthetic.main.activity_loading.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File


/**
 * Created by zhiya.zhang
 * on 2018/6/4 10:40.
 */
class LoadingActivity(override val layoutId: Int = R.layout.activity_loading) : MyActivity(), EasyPermissions.PermissionCallbacks {


    private val animation = TranslateAnimation(0f, 0f, 0f, -590f)

    private val myListener = object : MyListener {

        override fun listenerSuccess(data: Any) {
            val updates = Gson().fromJson(data as String, UpdateBean::class.java)
            if (MyApplication.getVersion()!!.indexOf("Alpha") == -1) {
                //线上版本
                //如果版本号不同就去下载
                if ((updates as UpdateBean).versionNumber > MyApplication.getVersionNum()) {
                    val versionUrl = updates.downloadUrl
                    val versionName = "CStoreManagement.apk"
                    val i = Intent(this@LoadingActivity, DownloadActivity::class.java)
                    i.putExtra(DownloadService.BUNDLE_KEY_DOWNLOAD_URL, versionUrl)
                    i.putExtra(DownloadService.BUNDLE_KEY_DOWNLOAD_NAME, versionName)
                    startActivity(i)
                    finish()
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                } else {
                    image_box.startAnimation(animation)
                }
            } else {
                //预览版

                //如果版本名不同就去下载
                if ((updates as UpdateBean).alphaVerNumber > MyApplication.getVersionNum()) {
                    val versionUrl = updates.downloadUrl
                    val versionName = "CStoreManagementAlpha.apk"
                    val i = Intent(this@LoadingActivity, DownloadActivity::class.java)
                    i.putExtra(DownloadService.BUNDLE_KEY_DOWNLOAD_URL, versionUrl)
                    i.putExtra(DownloadService.BUNDLE_KEY_DOWNLOAD_NAME, versionName)
                    startActivity(i)
                    finish()
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                } else {
                    image_box.startAnimation(animation)
                }
            }
        }

        override fun listenerFailed(errorMessage: String) {
            AlertDialog.Builder(ContextThemeWrapper(this@LoadingActivity, R.style.AlertDialogCustom))
                    .setTitle("提示")
                    .setMessage("网络异常，请重试或联系系统部\n$errorMessage")
                    .setPositiveButton("重试") { _, _ ->
                        judgementUpdate()
                    }
                    .setNegativeButton("退出", { _, _ ->
                        finish()
                    })
                    .show()
        }
    }

    override fun initView() {
        animation.duration = 2000
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                val layoutX = image_box.left
                val layoutY = image_box.top + -590
                val tempWidth = image_box.width
                val tempHeight = image_box.height
                image_box.clearAnimation()
                image_box.layout(layoutX, layoutY, layoutX + tempWidth, layoutY + tempHeight)
                startActivity(Intent(this@LoadingActivity, SignInActivity::class.java))
                finish()
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }

            override fun onAnimationStart(animation: Animation?) {
            }

        })
    }

    override fun initClick() {
    }

    override fun initData() {
        getPermissions()
    }

    private fun judgementUpdate() {
        OkHttpUtils
                .get()
                .url(AppUrl.UPDATE_APP)
                .addHeader(AppUrl.CONNECTION_HEADER, AppUrl.CONNECTION_SWITCH)
                .build()
                .execute(object : MyStringCallBack(myListener) {
                    override fun onResponse(response: String, id: Int) {
                        myListener.listenerSuccess(response)
                    }
                })
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

    //删除预览版安装包
    private fun deleteAlphaDownload() {
        val versionName = "CStoreManagementAlpha.apk"
        val downloadPath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath}${File.separator}$versionName"
        val f = File(downloadPath)
        if (f.exists()) {
            f.delete()
        }
    }


    private fun getPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (judgmentPermissions()) {
                deleteAlphaDownload()
                deleteDownload()
                judgementUpdate()
            }
        } else {
            if (!cameraIsCanUse()) {
                MyToast.getLongToast("您未开启权限，请开启权限！")
                val intent = Intent()
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                intent.data = Uri.fromParts("package", this@LoadingActivity.packageName, null)
                this@LoadingActivity.startActivity(intent)
            }else{
                deleteAlphaDownload()
                deleteDownload()
                judgementUpdate()
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
        val perms = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        if (!EasyPermissions.hasPermissions(this, *perms)) {
            EasyPermissions.requestPermissions(this, getString(R.string.openCamera), 1, *perms)
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
        deleteAlphaDownload()
        deleteDownload()
        judgementUpdate()
    }
}