package com.cstore.zhiyazhang.cstorepay

import android.Manifest
import android.content.Intent
import android.hardware.Camera
import android.net.Uri
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import com.cstore.zhiyazhang.cstorepay.util.MyActivity
import com.cstore.zhiyazhang.cstorepay.util.MyToast
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.EasyPermissions

class MainActivity(override val layoutId: Int = R.layout.activity_main) : MyActivity(), EasyPermissions.PermissionCallbacks {

    private val animation = TranslateAnimation(0f, 0f, 0f, -590f)
    private lateinit var showAction: Animation
    private lateinit var hideAction: Animation

    override fun initView() {
        showAction = AnimationUtils.loadAnimation(this, R.anim.anim_slide_in)
        hideAction = AnimationUtils.loadAnimation(this, R.anim.anim_slide_out)
        get_permissions.visibility = View.GONE
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
                val i = Intent(this@MainActivity, ScanningActivity::class.java)
                startActivity(i)
                finish()
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }

            override fun onAnimationStart(animation: Animation?) {
            }

        })
        getPermissions()
    }

    /**
     * 有权限就直接进行动画，完成就退出
     */
    private fun getPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (judgmentPermissions()) {
                image_box.startAnimation(animation)
            }
        } else {
            if (cameraIsCanUse()){
                image_box.startAnimation(animation)
            }else{
                MyToast.getLongToast("您未开启权限，请开启权限！")
                val intent = Intent()
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                intent.data = Uri.fromParts("package", this@MainActivity.packageName, null)
                this@MainActivity.startActivity(intent)
            }
        }
    }

    override fun initClick() {
        get_permissions.setOnClickListener {
            get_permissions.visibility = View.GONE
            get_permissions.startAnimation(hideAction)
            getPermissions()
        }
    }

    override fun initData() {
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
        val perms = arrayOf(Manifest.permission.CAMERA)
        if (!EasyPermissions.hasPermissions(this, *perms)) {
            EasyPermissions.requestPermissions(this, getString(R.string.open_permission), 1, *perms)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        showPrompt(getString(R.string.open_permission))
        get_permissions.visibility = View.VISIBLE
        get_permissions.startAnimation(showAction)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>?) {
        //获取成功
        image_box.startAnimation(animation)
    }
}
