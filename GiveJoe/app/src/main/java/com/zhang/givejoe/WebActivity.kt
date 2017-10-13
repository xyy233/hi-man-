package com.zhang.givejoe

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.webkit.WebSettings
import android.webkit.WebView
import kotlinx.android.synthetic.main.activity_web.*
import org.xwalk.core.XWalkPreferences
import org.xwalk.core.XWalkView
import org.xwalk.core.internal.XWalkViewBridge




/**
 * Created by zhang
 * on 2017/9/14 0014 11:02.
 */
class WebActivity : AppCompatActivity() {

    private val TAG="WebActivity"
    private var url = ""
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_web)
        url = intent.getStringExtra("url")
        //检测是否有http，如果没有就加上
        if (!url.contains("http://") && !url.contains("https://")) {
            url = "http://" + url
        }
        intent.getBooleanExtra("openAdm", false)
        //配置userAgent
        val ua = my_web.settings.userAgentString + "ADM"
        my_web.settings.userAgentString = ua
        //setCacheMode()
        XWalkPreferences.setValue("enable-javascript", true)

        if (Build.VERSION.SDK_INT>=21){
            my_web.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }

        //取消滚动条
        my_web.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        //触摸焦点起作用
        my_web.requestFocus()
        //不允许缩放
        my_web.settings.setSupportZoom(false)
        //不显示缩放按钮
        my_web.settings.builtInZoomControls = false

        click()
        my_web.load(url,null)
    }

    /**
     * 设置缓存
     */
    private fun setCacheMode() {
        try {
            val _getBridge = XWalkView::class.java.getDeclaredMethod("getBridge")
            _getBridge.isAccessible = true
            var xWalkViewBridge: XWalkViewBridge? = null
            xWalkViewBridge = _getBridge.invoke(my_web) as XWalkViewBridge
            xWalkViewBridge.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        }catch (e:Exception){}
    }

    override fun onPause() {
        super.onPause()
        my_web.pauseTimers()
        my_web.onHide()
    }

    override fun onResume() {
        super.onResume()
        my_web.resumeTimers()
        my_web.onShow()
    }

    override fun onDestroy() {
        super.onDestroy()
        my_web.onDestroy()
    }

    @SuppressLint("ShowToast")
    private fun click() {
        refresh.setOnClickListener {
            my_web.load(my_web.url,null)
        }
        back.setOnClickListener {
            onBackPressed()
            //operateSystemNavigation(true)
        }
    }
/*
    *//**
     * 隐藏或者显示系统下面的导航栏 true为显示。false为隐藏
     *
     * @param flag
     *//*
    private fun operateSystemNavigation(flag: Boolean) {
        *//**
         * droid_5159_hide_systemBar 导航栏显示与隐藏的KEY status 0 隐藏 ，1显示
         *//*
        //		getWindow().getDecorView().setSystemUiVisibility(
        //				View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        //						| View.SYSTEM_UI_FLAG_FULLSCREEN);
        val status = Settings.System.getInt(contentResolver,
                "droid_5159_hide_systemBar", 1)
        if (flag) {
            if (status != 0) {
                // 如果是隐藏 ,即该软件是全屏状态，不做处理；保持系统当前的bar状态
                return
            }
            Settings.System.putInt(contentResolver,
                    "droid_5159_hide_systemBar", 1)
        } else {
            if (status == 0) {
                // 如果是隐藏 ,即该软件是全屏状态，不做处理；保持系统当前的bar状态
                return
            }
            Settings.System.putInt(contentResolver,
                    "droid_5159_hide_systemBar", 0)
        }

        val intent = Intent("rk.android.systemBar.SHOW")
        intent.putExtra("display", flag)
        this.sendBroadcast(intent)
    }*/
}