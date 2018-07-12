package com.cstore.zhiyazhang.cstoremanagement.view.update

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.*
import android.support.v4.content.FileProvider
import android.webkit.MimeTypeMap
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * 用DownloadManager来实现版本更新
 *
 * @author Phoenix
 * @date 2016-8-24 14:06
 */
class DownloadService : Service() {

    private var activity: Activity? = null
    private var binder: DownloadBinder? = null
    private var downloadManager: DownloadManager? = null
    private var downloadObserver: DownloadChangeObserver? = null
    private var downLoadBroadcast: BroadcastReceiver? = null
    private var scheduledExecutorService: ScheduledExecutorService? = null

    //下载任务ID
    private var downloadId: Long = 0
    private var downloadUrl: String? = null
    private var downloadName: String? = null
    private var onProgressListener: OnProgressListener? = null

    var downLoadHandler: Handler? = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (onProgressListener != null && HANDLE_DOWNLOAD == msg.what) {
                //被除数可以为0，除数必须大于0
                if (msg.arg1 >= 0 && msg.arg2 > 0) {
                    onProgressListener!!.onProgress(msg.arg1 / msg.arg2.toFloat())
                }
            }
        }
    }

    private val progressRunnable = Runnable { updateProgress() }

    override fun onCreate() {
        super.onCreate()
        binder = DownloadBinder()
    }

    override fun onBind(intent: Intent): IBinder? {
        downloadUrl = intent.getStringExtra(BUNDLE_KEY_DOWNLOAD_URL)
        downloadName = intent.getStringExtra(BUNDLE_KEY_DOWNLOAD_NAME)
        downloadApk(downloadUrl, downloadName)
        return binder
    }

    /**
     * 下载最新APK
     */
    private fun downloadApk(url: String?, name: String?) {
        downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadObserver = DownloadChangeObserver()

        registerContentObserver()

        val request = DownloadManager.Request(Uri.parse(url))
        /**设置漫游状态下是否可以下载 */
        request.setAllowedOverRoaming(true)
        //设置文件类型，可以在下载结束后自动打开该文件
        val mimeTypeMap = MimeTypeMap.getSingleton()
        val mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url))
        request.setMimeType(mimeString)
        /**设置通知栏是否可见 */
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        /**如果我们希望下载的文件可以被系统的Downloads应用扫描到并管理，
         * 我们需要调用Request对象的setVisibleInDownloadsUi方法，传递参数true. */
        request.setVisibleInDownloadsUi(true)
        /**设置文件保存路径 */
        request.setDestinationInExternalPublicDir("/download/", name)
        /**将下载请求放入队列， return下载任务的ID */
        downloadId = downloadManager!!.enqueue(request)

        registerBroadcast()
    }

    /**
     * 注册广播
     */
    private fun registerBroadcast() {
        /**注册service 广播 1.任务完成时 2.进行中的任务被点击 */
        val intentFilter = IntentFilter()
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED)
        registerReceiver(DownLoadBroadcast(), intentFilter)
    }

    /**
     * 注销广播
     */
    private fun unregisterBroadcast() {
        if (downLoadBroadcast != null) {
            unregisterReceiver(downLoadBroadcast)
            downLoadBroadcast = null
        }
    }

    /**
     * 注册ContentObserver
     */
    private fun registerContentObserver() {
        /** observer download change  */
        if (downloadObserver != null) {
            contentResolver.registerContentObserver(Uri.parse("content://downloads/my_downloads"), false, downloadObserver!!)
        }
    }

    /**
     * 注销ContentObserver
     */
    private fun unregisterContentObserver() {
        if (downloadObserver != null) {
            contentResolver.unregisterContentObserver(downloadObserver!!)
        }
    }

    /**
     * 关闭定时器，线程等操作
     */
    private fun close() {
        if (scheduledExecutorService != null && !scheduledExecutorService!!.isShutdown) {
            scheduledExecutorService!!.shutdown()
        }

        if (downLoadHandler != null) {
            downLoadHandler!!.removeCallbacksAndMessages(null)
        }
    }

    /**
     * 发送Handler消息更新进度和状态
     */
    private fun updateProgress() {
        val bytesAndStatus = getBytesAndStatus(downloadId)
        downLoadHandler!!.sendMessage(downLoadHandler!!.obtainMessage(HANDLE_DOWNLOAD, bytesAndStatus[0], bytesAndStatus[1], bytesAndStatus[2]))
    }

    /**
     * 通过query查询下载状态，包括已下载数据大小，总大小，下载状态
     *
     * @param downloadId
     * @return
     */
    private fun getBytesAndStatus(downloadId: Long): IntArray {
        val bytesAndStatus = intArrayOf(-1, -1, 0)
        val query = DownloadManager.Query().setFilterById(downloadId)
        var cursor: Cursor? = null
        try {
            cursor = downloadManager!!.query(query)
            if (cursor != null && cursor.moveToFirst()) {
                //已经下载文件大小
                bytesAndStatus[0] = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                //下载文件的总大小
                bytesAndStatus[1] = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                //下载状态
                bytesAndStatus[2] = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            }
        } finally {
            cursor?.close()
        }
        return bytesAndStatus
    }

    /**
     * 绑定此DownloadService的Activity实例
     *
     * @param activity
     */
    fun setTargetActivity(activity: Activity) {
        this.activity = activity
    }

    /**
     * 接受下载完成广播
     */
    private inner class DownLoadBroadcast : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            when (intent.action) {
                DownloadManager.ACTION_DOWNLOAD_COMPLETE -> if (downloadId == downId && downId != (-1).toLong() && downloadManager != null) {
//                    val downIdUri = downloadManager!!.getUriForDownloadedFile(downloadId)
                    val downloadPath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath}${File.separator}$downloadName"
                    val file = File(downloadPath)
                    if (!file.exists()) return
                    val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        FileProvider.getUriForFile(MyApplication.instance().applicationContext, "cstore.zhiyazhang.cstoremanagement.fileprovider", file)
                    } else {
                        Uri.parse("file://" + file.toString())
                    }

                    close()

                    if (uri != null) {
                        try {
                            installAPK(uri)
                        } catch (e: Exception) {
                            MyToast.getLongToast("打开安装文件失败，请自行进入Download文件下安装软件，错误代码：${e.message.toString()}")
                        }
                    }
                    if (onProgressListener != null) {
                        onProgressListener!!.onProgress(UNBIND_SERVICE)
                    }
                }

                else -> {
                }
            }
        }
    }

    private fun installAPK(apkPath: Uri) {
        val i = Intent()
        i.action = Intent.ACTION_VIEW
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            i.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        } else {
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        i.setDataAndType(apkPath, "application/vnd.android.package-archive")
        MyApplication.instance().applicationContext.startActivity(i)
    }

    /**
     * 监听下载进度
     */
    private inner class DownloadChangeObserver : ContentObserver(downLoadHandler) {
        init {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        }

        /**
         * 当所监听的Uri发生改变时，就会回调此方法
         *
         * @param selfChange 此值意义不大, 一般情况下该回调值false
         */
        override fun onChange(selfChange: Boolean) {
            scheduledExecutorService!!.scheduleAtFixedRate(progressRunnable, 0, 1, TimeUnit.SECONDS)
        }
    }

    inner class DownloadBinder : Binder() {
        /**
         * 返回当前服务的实例
         *
         * @return
         */
        val service: DownloadService
            get() = this@DownloadService

    }

    interface OnProgressListener {
        /**
         * 下载进度
         *
         * @param fraction 已下载/总大小
         */
        fun onProgress(fraction: Float)
    }

    /**
     * 对外开发的方法
     *
     * @param onProgressListener
     */
    fun setOnProgressListener(onProgressListener: OnProgressListener) {
        this.onProgressListener = onProgressListener
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterBroadcast()
        unregisterContentObserver()
    }

    companion object {
        private val TAG = "DownloadService"

        val HANDLE_DOWNLOAD = 0x001
        val BUNDLE_KEY_DOWNLOAD_URL = "download_url"
        val BUNDLE_KEY_DOWNLOAD_NAME = "download_name"
        val UNBIND_SERVICE = 2.0f
    }
}
