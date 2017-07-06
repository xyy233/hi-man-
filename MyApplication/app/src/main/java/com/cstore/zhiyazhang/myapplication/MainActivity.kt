package com.cstore.zhiyazhang.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import android.text.format.Formatter.formatIpAddress
import android.net.wifi.WifiInfo
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       // val xx=getIP2()
        val aa=getIP()
        //aa1.text=xx
        aa2.text=aa
    }

    fun getIP2(): String {
        val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = wifiManager.connectionInfo
        ip = Formatter.formatIpAddress(info.ipAddress)
        return ip
    }

}
