package com.zhang.givejoe

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var backCount=0
    private val url="donation.qinheyuzhou.org"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //全屏化
        this.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)
        web_edit.setText(url)
        go_web.setOnClickListener {
            val i=Intent(this@MainActivity,WebActivity::class.java)
            i.putExtra("url", web_edit.text.toString())
            startActivity(i)
        }
        go_back.setOnClickListener {
            onBackPressed()
        }
    }
}
