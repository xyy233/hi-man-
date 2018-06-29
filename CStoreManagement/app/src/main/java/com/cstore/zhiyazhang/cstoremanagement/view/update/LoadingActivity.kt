package com.cstore.zhiyazhang.cstoremanagement.view.update

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Environment
import android.os.IBinder
import android.support.v7.app.AlertDialog
import android.view.ContextThemeWrapper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.UpdateBean
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.url.AppUrl
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyStringCallBack
import com.cstore.zhiyazhang.cstoremanagement.utils.ReportListener
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

    private var isBindService: Boolean = false
    private val conn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as DownloadService.DownloadBinder
            val downloadService = binder.service

            //接口回调，下载进度
            downloadService.setOnProgressListener(object : DownloadService.OnProgressListener {
                override fun onProgress(fraction: Float) {
                    npb!!.progress = (fraction * 100).toInt()

                    //判断是否真的下载完成进行安装了，以及是否注册绑定过服务
                    if (fraction == DownloadService.UNBIND_SERVICE && isBindService) {
                        unBind()
                        isBindService = false
                        finish()
                    }
                }
            })
        }

        override fun onServiceDisconnected(name: ComponentName) {

        }
    }

    private fun unBind() {
        unbindService(conn)
    }

    private var isUpdate = false

    private val animation = TranslateAnimation(0f, 0f, 0f, -590f)

    private val updateAnimation = TranslateAnimation(0f, 0f, 0f, -100f)

    private var downloadUrl: String? = null

    private val myListener = object : MyListener {

        override fun listenerSuccess(data: Any) {
            try {
                val updates = Gson().fromJson(data as String, UpdateBean::class.java)
                //如果版本号不同就去下载
                if ((updates as UpdateBean).versionNumber > MyApplication.getVersionNum()) {
                    isUpdate = true
                    downloadUrl = updates.downloadUrl
                    image_box.startAnimation(updateAnimation)
                    pg_box.visibility = View.VISIBLE
                    pg_box.startAnimation(AnimationUtils.loadAnimation(this@LoadingActivity, R.anim.anim_slide_in))
                } else {
                    image_box.startAnimation(animation)
                }
            } catch (e: Exception) {
                listenerFailed(data as String)
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
        updateAnimation.duration = 1000
        updateAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                val layoutX = image_box.left
                val layoutY = image_box.top + -100
                val tempWidth = image_box.width
                val tempHeight = image_box.height
                image_box.clearAnimation()
                image_box.layout(layoutX, layoutY, layoutX + tempWidth, layoutY + tempHeight)
                val versionUrl = downloadUrl
                val versionName = "CStoreManagement.apk"
                val i = Intent(this@LoadingActivity, DownloadService::class.java)
                i.putExtra(DownloadService.BUNDLE_KEY_DOWNLOAD_URL, versionUrl)
                i.putExtra(DownloadService.BUNDLE_KEY_DOWNLOAD_NAME, versionName)
                isBindService = bindService(i, conn, Context.BIND_AUTO_CREATE)
            }

            override fun onAnimationStart(animation: Animation?) {
            }

        })
    }

    override fun initClick() {
    }

    override fun initData() {
        ReportListener.reportError()
    }

    override fun onStart() {
        super.onStart()
        getPermissions()
    }

    override fun onBackPressed() {
        if (isUpdate) {
            showPrompt(getString(R.string.wait_loading))
        } else {
            super.onBackPressed()
        }
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
            deleteAlphaDownload()
            deleteDownload()
            judgementUpdate()
        }
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
        deleteAlphaDownload()
        deleteDownload()
        judgementUpdate()
    }
}