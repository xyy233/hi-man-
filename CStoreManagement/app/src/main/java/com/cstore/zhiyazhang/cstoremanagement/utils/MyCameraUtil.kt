package com.cstore.zhiyazhang.cstoremanagement.utils

import android.Manifest
import android.content.Intent
import android.hardware.Camera
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import com.cstore.zhiyazhang.cstoremanagement.R
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.lang.ref.WeakReference

/**
 * Created by zhiya.zhang
 * on 2018/1/31 15:43.
 */
object MyCameraUtil : EasyPermissions.PermissionCallbacks {

    private var mActivity: WeakReference<AppCompatActivity>? = null

    fun getPermissions(activity: AppCompatActivity): Boolean {
        mActivity = WeakReference(activity)
        return if (android.os.Build.VERSION.SDK_INT >= 23) {
            judgmentPermissions(activity)
        } else {
            if (!cameraIsCanUse()) {
                MyToast.getLongToast("您未开启相机权限，请开启相机权限！")
                val intent = Intent()
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                intent.data = Uri.fromParts("package", activity.packageName, null)
                activity.startActivity(intent)
                false
            } else {
                true
            }
        }
    }

    //获得相机权限
    @pub.devrel.easypermissions.AfterPermissionGranted(1)
    private fun judgmentPermissions(activity: AppCompatActivity): Boolean {
        val perms = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (!EasyPermissions.hasPermissions(activity, *perms)) {
            EasyPermissions.requestPermissions(activity, activity.getString(R.string.open_permission), 1, *perms)
            return false
        }
        return true
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

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>?) {
        if (mActivity != null) {
            val activity = mActivity!!.get()
            if (activity!=null){
                if (EasyPermissions.somePermissionPermanentlyDenied(activity, perms!!)) {
                    AppSettingsDialog.Builder(activity).build().show()
                }
            }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>?) {
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

}