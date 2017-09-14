package com.zhang.givejoe

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager
import android.webkit.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_web.*

/**
 * Created by zhang on 2017/9/14 0014.
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
        if (!url.contains("http://") && !url.contains("https://")) {
            url = "http://" + url
        }
        //配置userAgent
        val ua = my_web.settings.userAgentString + "ADM"
        my_web.settings.userAgentString = ua
        //支持javascript交互
        my_web.settings.javaScriptEnabled = true
        //取消滚动条
        my_web.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        //触摸焦点起作用
        my_web.requestFocus()
        my_web.settings.javaScriptCanOpenWindowsAutomatically = true
        my_web.webViewClient = viewClient
        my_web.webChromeClient = chromeClient
        swipe.setDistanceToTriggerSync(200)
        swipe.setOnRefreshListener {
            my_web.loadUrl(my_web.url)
        }
        my_web.loadUrl(url)
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
    }

    override fun onStart() {
        super.onStart()
        this.startLockTask()
    }

    override fun onBackPressed() {
        my_web.goBack()
        backCount++
        if (backCount == 10) {
            this.stopLockTask()
            finish()
        }
    }
}