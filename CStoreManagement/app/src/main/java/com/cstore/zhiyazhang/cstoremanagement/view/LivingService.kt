package com.cstore.zhiyazhang.cstoremanagement.view

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.cstore.zhiyazhang.cstoremanagement.view.transfer.TransferService
import java.util.*

/**
 * Created by zhiya.zhang
 * on 2018/5/17 9:13.
 */
class LivingService : Service() {
    companion object {
        val TAG = "com.cstore.zhiyazhang.cstoremanagement.view.LivingService"

        /**
         * 查询存活service
         */
        fun isServiceWorked(context: Context, serviceName: String): Boolean {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            manager.getRunningServices(Int.MAX_VALUE).forEach {
                if (it.service.className.toString() == serviceName) {
                    return true
                }
            }
            return false
        }
    }

    private val tag = "LivingService"

    private val thread = Thread(Runnable {
        val timer = Timer()
        val task = object : TimerTask() {
            override fun run() {
                val b = isServiceWorked(this@LivingService, TransferService.TAG)
                if (!b) {
                    val service = Intent(this@LivingService, TransferService::class.java)
                    startService(service)
                }
            }
        }
        timer.schedule(task, 0, 1000 * 10)
    })

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        thread.start()
        return START_STICKY
    }


}