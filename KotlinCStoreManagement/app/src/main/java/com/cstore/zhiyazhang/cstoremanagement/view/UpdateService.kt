package com.cstore.zhiyazhang.cstoremanagement.view

import android.app.DownloadManager
import android.app.IntentService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import com.cstore.zhiyazhang.cstoremanagement.bean.UpdateBean
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.url.AppUrl
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import com.google.gson.Gson
import com.zhiyazhang.mykotlinapplication.utils.MyApplication
import com.zhiyazhang.mykotlinapplication.utils.MyStringCallBack
import com.zhy.http.okhttp.OkHttpUtils
import java.io.File


/**
 * Created by zhiya.zhang
 * on 2017/7/3 8:44.
 */
class UpdateService(value: String = "UpdateService") : IntentService(value) {

    private var mTaskId: Long? = null
    private var downloadManager: DownloadManager? = null
    private val TAG = "UpdateService"
    private var versionUrl: String? = null
    private val versionName: String = "CStoreManagement.apk"
    private var downloadPath: String? = null
    private val context = MyApplication.instance().applicationContext!!
    override fun onHandleIntent(intent: Intent) {
        try {
            judgmentVersion()
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }
    }

    private val myListener = object : MyListener {
        override fun contractSuccess() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun contractSuccess(`object`: Any) {
            downloadPath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath}${File.separator}$versionName"

            //如果版本名不同就去下载
            if ((`object` as UpdateBean).version != MyApplication.getVersion()) {
                versionUrl = `object`.downloadUrl
                downloadAPK()
            }
        }

        override fun contractFailed(errorMessage: String) {
            Log.e(TAG, errorMessage)
        }

    }

    /**
     * 从服务器获得当前版本名和url地址
     */
    private fun judgmentVersion() {
        OkHttpUtils
                .get()
                .url(AppUrl.UPDATE_APP)
                .addHeader(AppUrl.CONNECTION_HEADER, AppUrl.CONNECTION_SWITCH)
                .build()
                .execute(object : MyStringCallBack(myListener) {
                    override fun onResponse(response: String, id: Int) {
                        myListener.contractSuccess(Gson().fromJson(response, UpdateBean::class.java))
                    }
                })
    }

    /**
     * 使用系统下载器
     */
    private fun downloadAPK() {
        //创建下载任务
        val request: DownloadManager.Request = DownloadManager.Request(Uri.parse(versionUrl))
        request.setAllowedOverRoaming(true)

        //设置文件类型，可以在下载结束后自动打开该文件
        val mimeTypeMap = MimeTypeMap.getSingleton()
        val mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(versionUrl))
        request.setMimeType(mimeString)

        //在通知栏显示
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        request.setVisibleInDownloadsUi(true)

        //download文件夹
        request.setDestinationInExternalPublicDir("/download/", versionName)

        //把下载请求加入下载队列
        downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        //加入下载队列后会给该任务返回一个long型的id,通过该id可以取消任务，重启任务等,现在用不到先注释
        mTaskId = downloadManager!!.enqueue(request)

        //注册广播接受，监听下载状态
        context.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    //广播接受者，接收下载状态
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            checkDownloadStatus()//检查下载状态
        }
    }

    //检查下载状态
    private fun checkDownloadStatus() {
        val query = DownloadManager.Query()
        //筛选下载任务，传入任务ID，可变参数
        query.setFilterById(mTaskId!!)
        val c = downloadManager!!.query(query)
        if (c.moveToFirst()) {
            val status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
            when (status) {
                DownloadManager.STATUS_PAUSED -> Log.e(TAG, ">>>下载暂停")
                DownloadManager.STATUS_PENDING -> Log.e(TAG, ">>>下载延迟")
                DownloadManager.STATUS_RUNNING -> Log.e(TAG, ">>>正在下载")
                DownloadManager.STATUS_SUCCESSFUL -> {
                    Log.e(TAG, ">>>下载完成")
                    installAPK(File(downloadPath))
                }
                DownloadManager.STATUS_FAILED -> {
                    Log.e(TAG, ">>>下载失败")
                    MyToast.getShortToast(">>>下载失败")
                }
            }
        }
    }

    //下载到本地后执行安装
    private fun installAPK(file: File) {
        if (!file.exists()) return
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = Uri.parse("file://" + file.toString())
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        //在服务中开启activity必须设置flag,后面解释
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
}
