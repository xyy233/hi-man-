package com.zhang.camerademo

import android.Manifest
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.aiyaapp.camera.sdk.AiyaEffects
import com.aiyaapp.camera.sdk.base.ActionObserver
import com.aiyaapp.camera.sdk.base.Event
import com.aiyaapp.camera.sdk.base.Log
import com.tencent.bugly.crashreport.CrashReport
import com.zhang.camerademo.camera.CameraActivity
import com.zhang.camerademo.mvc.SurfaceHolderActivity
import com.zhang.camerademo.mvc.TextureViewActivity
import com.zhang.camerademo.util.PermissionUtils

class MainActivity : AppCompatActivity() {
    private val mRunnable= Runnable {
        val observer= object : ActionObserver {
            override fun onAction(event: Event) {
                when {
                    event.eventType== Event.RESOURCE_FAILED -> {
                        Log.e("resource failed")
                        AiyaEffects.getInstance().unRegisterObserver(this)
                    }
                    event.eventType== Event.RESOURCE_READY -> Log.e("resource ready")
                    event.eventType== Event.INIT_FAILED -> {
                        Log.e("init failed")
                        Toast.makeText(this@MainActivity, "注册失败，请检查网络", Toast.LENGTH_SHORT)
                                .show()
                        AiyaEffects.getInstance().unRegisterObserver(this)
                    }
                    event.eventType== Event.INIT_SUCCESS -> {
                        Log.e("init success")
                        setContentView(R.layout.activity_main)
                        AiyaEffects.getInstance().unRegisterObserver(this)
                    }
                }
            }
        }
        AiyaEffects.getInstance().registerObserver(observer)
        AiyaEffects.getInstance().init(this@MainActivity, "4d9ef4226a05a1e0d1efface999707c1")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CrashReport.initCrashReport(this.application, "4d9ef4226a05a1e0d1efface999707c1", true)
        PermissionUtils.askPermission(this, arrayOf(Manifest.permission.CAMERA, Manifest
                .permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE), 10, mRunnable)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.mCamera -> startActivity(Intent(this, CameraActivity::class.java))
            R.id.mTexture -> startActivity(Intent(this, TextureViewActivity::class.java))
            R.id.mHolder -> startActivity(Intent(this, SurfaceHolderActivity::class.java))
        }
    }
}
