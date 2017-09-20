package com.zhang.givejoe

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.webkit.*
import kotlinx.android.synthetic.main.activity_web.*



/**
 * Created by zhang
 * on 2017/9/14 0014 11:02.
 */
class WebActivity : AppCompatActivity() {

    var backCount = 0
    var url = ""
    var retry = ""
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_web)
        url = intent.getStringExtra("url")
        //检测是否有http，如果没有就加上
        if (!url.contains("http://") && !url.contains("https://")) {
            url = "http://" + url
        }
        val openAdm = intent.getBooleanExtra("openAdm", false)
        //配置userAgent
        val ua = my_web.settings.userAgentString + "ADM"
        my_web.settings.userAgentString = ua
        //支持javascript交互
        my_web.settings.javaScriptEnabled = true
        //支持javascript弹窗
        my_web.settings.javaScriptCanOpenWindowsAutomatically = true
        //html5数据存储支持
        my_web.settings.domStorageEnabled = true
        //取消滚动条
        my_web.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        //设置缓存
        my_web.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        //触摸焦点起作用
        my_web.requestFocus()
        //不允许缩放
        my_web.settings.setSupportZoom(false)
        //不显示缩放按钮
        my_web.settings.builtInZoomControls = false
        //任意比例缩放
        my_web.settings.useWideViewPort = true
        //解决自适应问题
        my_web.settings.loadWithOverviewMode = true
        //不使用缓存
        my_web.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        my_web.settings.setAppCacheEnabled(false)
        //设置渲染优先级
        my_web.settings.setRenderPriority(WebSettings.RenderPriority.HIGH)

        my_web.settings.loadsImagesAutomatically = true

        my_web.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        my_web.settings.defaultTextEncodingName = "UTF-8"
        my_web.settings.loadsImagesAutomatically = true

        my_web.settings.setSupportMultipleWindows(true)
        my_web.webViewClient = viewClient
        my_web.webChromeClient = chromeClient
        swipe.setDistanceToTriggerSync(200)
        swipe.setOnRefreshListener {
            my_web.loadUrl(my_web.url)
        }
        click()
        my_web.loadUrl(url)
    }

    private fun click() {
        my_web.setIWebViewScroll(object : cbWebView.IWebViewScroll {
            override fun onNotTop() {
                swipe.isEnabled=false
            }

            override fun onTop() {
                swipe.isEnabled = true
            }
        })
    }

    val viewClient = object : WebViewClient() {

        //打开网页时不调用系统浏览器， 而是在本WebView中显示,不同版本调用不同的
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            retry = url
            if (retry != this@WebActivity.url) {
                backCount = 0
            }
            return super.shouldOverrideUrlLoading(view, url)
        }



        //打开网页时不调用系统浏览器， 而是在本WebView中显示,不同版本调用不同的
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            retry = url
            if (retry != this@WebActivity.url) {
                backCount = 0
            }
            return false
        }
    }

    val chromeClient = object : WebChromeClient() {
        //获得网页的加载进度
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            if (newProgress == 100) {
                swipe.isRefreshing = false
            }
            super.onProgressChanged(view, newProgress)
        }

        override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
            val transport = resultMsg!!.obj as WebView.WebViewTransport
            resultMsg.sendToTarget()
            return true
        }
    }

    override fun onStart() {
        super.onStart()
        this.startLockTask()
    }

    override fun onBackPressed() {
        my_web.goBack()
        backCount++
        if (backCount == 10) {
            try {
                this.stopLockTask()
            }catch (e:Exception){}
            my_web.destroy()
            super.onBackPressed()
        }
    }
}