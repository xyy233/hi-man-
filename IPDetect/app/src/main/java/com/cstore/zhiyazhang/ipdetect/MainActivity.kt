package com.cstore.zhiyazhang.ipdetect

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ip1.text = getIP()
    }

    fun getIP(): String {
        var result: String = ""
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                val name = intf.displayName
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        val ip = inetAddress.hostAddress
                        val ips = ip.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        result = ips[0] + "." + ips[1] + "." + ips[2] + ".100"
                        if (name.indexOf("wlan") != -1 && result != "") {
                            return result
                        }
                    }
                }
            }
        } catch (ex: SocketException) {
            result = "不能获取"
        }
        return result
    }
}
