package com.zhang.givejoe

import android.content.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var backCount=0
    val url="donation.qinheyuzhou.org"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)
        web_url.setText(url)
        go_web.setOnClickListener {
            this.stopLockTask()
            val i=Intent(this@MainActivity,WebActivity::class.java)
            i.putExtra("url",web_url.text.toString())
            startActivity(i)
        }
    }

    override fun onStart() {
        super.onStart()
        this.startLockTask()
    }

    override fun onResume() {
        super.onResume()
    }
    override fun onBackPressed() {
        backCount++
        if (backCount==20){
            this.stopLockTask()
            super.onBackPressed()
        }
    }
}
