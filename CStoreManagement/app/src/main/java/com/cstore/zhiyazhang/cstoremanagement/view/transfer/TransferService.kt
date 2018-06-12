package com.cstore.zhiyazhang.cstoremanagement.view.transfer

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.presenter.transfer.TransferServicePresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.GlobalException
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.view.LivingService
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import java.util.*


/**
 * Created by zhiya.zhang
 * on 2018/5/9 15:02.
 * 专门用来给卫星调拨用的前台服务
 */
class TransferService : Service(), GenericView {
    companion object {
        val TAG = "com.cstore.zhiyazhang.cstoremanagement.view.transfer.TransferService"
    }

    private val tag="TransferService"
    private val channelId = "channel_first"
    private val channelName = "通知渠道"
    private val firstNotificationId = 528//前台服务常驻通知
    private val secondNotificationId = 529//有新通知才显示的
    private lateinit var manager: NotificationManager
    private val presenter = TransferServicePresenter(this)
    private lateinit var notification: Notification
    private var wakeLock: PowerManager.WakeLock? = null
    private var timer: Timer? = null
    private var timeTask: TimerTask? = null
    private var nowTaskType = -1
    private val thread = Thread(Runnable {
        val timer = Timer()
        val task = object : TimerTask() {
            override fun run() {
                val b = LivingService.isServiceWorked(this@TransferService, LivingService.TAG)
                if (!b) {
                    val service = Intent(this@TransferService, LivingService::class.java)
                    startService(service)
                }
            }
        }
        timer.schedule(task, 0, 1000 * 10)
    })
    private val handler = TransHandler()
    private val listener = object : MyListener {
        override fun listenerSuccess(data: Any) {
            val nowHour = MyTimeUtil.nowHour
            timer?.cancel()
            timer = null
            timer = Timer()
            timeTask?.cancel()
            timeTask = null
            //重新定义TimerTask
            timeTask = object : TimerTask() {
                override fun run() {
                    val h = MyTimeUtil.nowHour
                    presenter.getJudgment()
                    var j = false
                    if (h == 10 || h == 14) {
                        if (nowTaskType != 0) {
                            nowTaskType = 0
                            j = true
                        }
                    } else if (h in 8..17) {
                        if (nowTaskType != 1) {
                            nowTaskType = 1
                            j = true
                        }
                    } else {
                        if (nowTaskType != 2) {
                            nowTaskType = 2
                            j = true
                        }
                    }
                    if (j) {
                        handler.sendMessage(Message())
                    }
                }
            }

            nowTaskType = if (nowHour == 10 || nowHour == 14) {
                //小时在10或14就每30s查询一次
                timer!!.schedule(timeTask!!, 0, 1000 * 30)
                0
            } else if (nowHour in 8..17) {
                //小时小于18大于7就1分钟查询一次
                timer!!.schedule(timeTask!!, 0, 1000 * 60)
                1
            } else {
                //每30m查询一次
                timer!!.schedule(timeTask!!, 0, 1000 * 60 * 30)
                2
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        handler.writeListener(listener)
        initNotificationManager()
        notification = initNoNotification()
        acquireWakeLock()
        handler.sendMessage(Message())
        //全局错误信息收集
        Thread.setDefaultUncaughtExceptionHandler(GlobalException.instance)
    }

    override fun onDestroy() {
        //不移除之前通知
        stopForeground(true)
        releaseWakeLock()
        handler.cleanAll()
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(firstNotificationId, notification)
        thread.start()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    /**
     * 有新调拨需要处理,更新notification
     */
    override fun <T> requestSuccess(rData: T) {
        val text = "有新调拨等待处理！"
        val hangIntent = Intent()
        hangIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        hangIntent.setClass(this, TransferZActivity::class.java)
        val contentIntent = PendingIntent.getActivity(this, 0, hangIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelId(1)
            Notification.Builder(applicationContext, channelId)
                    .setContentTitle(text)
                    .setContentText(text)
                    .setSmallIcon(R.mipmap.ic_logo)
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_logo))
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .setTicker("您有新的调拨产生")
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(contentIntent)
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .setFullScreenIntent(contentIntent, true)
                    .build()
        } else {
            val sound = Uri.parse("android.resource://" + packageName + "/" + R.raw.trs_mp3)
            val vibrate = longArrayOf(0, 1000, 200, 1000, 200, 1000, 200, 1000, 200, 1000, 200)
            NotificationCompat.Builder(applicationContext, channelId)
                    .setSound(sound)
                    .setVibrate(vibrate)
                    .setContentTitle(text)
                    .setContentText(text)
                    .setSmallIcon(R.mipmap.ic_logo)
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_logo))
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .setTicker("您有新的调拨产生")
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(contentIntent)
                    .setVisibility(Notification.VISIBILITY_PRIVATE)
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .setFullScreenIntent(contentIntent, true)
                    .build()
        }
        manager.notify(secondNotificationId, notification)
    }

    /**
     * 初始化notificationManager,必须要先执行的
     */
    private fun initNotificationManager() {
        manager = this.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    /**
     * 初始化notification
     */
    private fun initNoNotification(): Notification {
        val title = "喜士多卫星店调拨服务运行中"
        val text = "调拨服务运行中，请勿划掉程序"
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelId(0)
            Notification.Builder(applicationContext, channelId)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setSmallIcon(R.mipmap.ic_logo)
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_logo))
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setWhen(0)
                    .build()
        } else {
            NotificationCompat.Builder(applicationContext, channelId)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setSmallIcon(R.mipmap.ic_logo)
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_logo))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setWhen(0)
                    .build()
        }
    }

    /**
     * 8.0必须构建通知渠道
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannelId(type: Int) {
        val groupPrimary = "group_first"
        val ncp = NotificationChannelGroup(groupPrimary, channelName)
        manager.createNotificationChannelGroup(ncp)
        val channel = NotificationChannel(channelId, channelName, if (type == 0) NotificationManager.IMPORTANCE_DEFAULT else NotificationManager.IMPORTANCE_HIGH)
        channel.lightColor = Color.GREEN
        channel.group = groupPrimary
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        if (type == 1) channel.setSound(Uri.parse("android.resource://" + packageName + "/" + R.raw.trs_mp3), Notification.AUDIO_ATTRIBUTES_DEFAULT)
        if (type == 1) channel.vibrationPattern = longArrayOf(0, 1000, 200, 1000, 200, 1000, 200, 1000, 200, 1000, 200)
        manager.createNotificationChannel(channel)
    }

    @SuppressLint("WakelockTimeout")
    private fun acquireWakeLock() {
        if (wakeLock == null) {
            val pm = this.getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE, "PostLocationService")
            if (wakeLock != null) {
                wakeLock!!.acquire()
            }
        }
    }

    private fun releaseWakeLock() {
        if (wakeLock != null) {
            wakeLock!!.release()
            wakeLock = null
        }
    }

    class TransHandler : Handler() {
        private var mListener: MyListener? = null
        fun writeListener(myListener: MyListener): TransHandler {
            mListener = myListener
            return this
        }

        fun cleanAll() {
            mListener = null
            this.removeCallbacksAndMessages(null)
        }

        override fun handleMessage(msg: Message) {
            if (mListener != null) {
                mListener!!.listenerSuccess(1)
            }
        }

    }

    override fun getData2(): Any? {
        return this
    }

}