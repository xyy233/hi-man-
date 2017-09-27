package com.zhang.givejoe

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var backCount=0
    val url="donation.qinheyuzhou.org"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //全屏化
        this.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)
        web_edit.setText(url)
        go_web.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this.stopLockTask()
            }
            val i=Intent(this@MainActivity,WebActivity::class.java)
            i.putExtra("url", web_edit.text.toString())
            startActivity(i)
        }
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.startLockTask()
        }
    }

    override fun onResume() {
        super.onResume()
    }
    override fun onBackPressed() {
        backCount++
        if (backCount==20){
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    this.stopLockTask()
                }
            }catch (e:Exception){}
            super.onBackPressed()
        }
    }
}
