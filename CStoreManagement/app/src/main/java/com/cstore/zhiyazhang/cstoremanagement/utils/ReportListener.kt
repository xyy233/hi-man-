package com.cstore.zhiyazhang.cstoremanagement.utils

import android.os.Environment
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.url.AppUrl
import com.cstore.zhiyazhang.cstoremanagement.utils.MyJavaFun.clearErrorTxtMessage
import com.cstore.zhiyazhang.cstoremanagement.utils.MyJavaFun.getErrorTxtMessage
import com.zhiyazhang.mykotlinapplication.utils.MyStringCallBack
import com.zhy.http.okhttp.OkHttpUtils

/**
 * Created by zhiya.zhang
 * on 2017/7/20 8:47.
 * 上报日志
 */
object ReportListener {

    /**
     * 上传操作信息
     */
    fun report(errorMessage: String, errorDate: String) {
        if (!ConnectionDetector.getConnectionDetector().isOnline) return
        OkHttpUtils
                .postString()
                .url(AppUrl.UPLOAD_ERROR)
                .content("店号：${User.getUser().storeId},\r\n版本号：${MyApplication.getVersion()!!},\r\n动作信息：$errorMessage,\r\n时间：${MyTimeUtil.nowTimeString}，\r\n数据：$errorDate")
                .addHeader("fileName", "${User.getUser().storeId}/${MyTimeUtil.nowTimeString}.txt")
                .build()
                .execute(object : MyStringCallBack(object : MyListener {

                    override fun listenerSuccess(data: Any) {
                    }

                    override fun listenerFailed(errorMessage: String) {
                    }
                }) {
                    override fun onResponse(p0: String?, p1: Int) {
                    }

                })
    }

    /**
     * 上传错误信息
     */
    fun reportError() {
        if (!ConnectionDetector.getConnectionDetector().isOnline) return
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val dataMsg = getErrorTxtMessage()
            if (dataMsg.isNotEmpty()) {
                OkHttpUtils
                        .postString()
                        .url(AppUrl.UPLOAD_ERROR)
                        .content("店号：${User.getUser().storeId},\r\n版本号：${MyApplication.getVersion()!!},\r\n动作信息：应用崩溃,\r\n时间：${MyTimeUtil.nowTimeString}，\r\n数据：$dataMsg")
                        .addHeader("fileName", "Error/${User.getUser().storeId + MyTimeUtil.nowTimeString}.txt")
                        .build()
                        .execute(object : MyStringCallBack(object : MyListener {

                            override fun listenerSuccess(data: Any) {
                            }

                            override fun listenerFailed(errorMessage: String) {

                            }
                        }) {
                            override fun onResponse(p0: String?, p1: Int) {
                                //成功要清空内容
                                clearErrorTxtMessage()
                            }
                        })
            }
        }
        /*val crashFile = getCrashFile()
        if (crashFile.exists()) {
            val data = MyJavaFun.getTxtFileMessage(crashFile)
            if (data.isEmpty()) return
            OkHttpUtils
                    .postString()
                    .url(AppUrl.UPLOAD_ERROR)
                    .content("店号：${User.getUser().storeId},\r\n版本号：${MyApplication.getVersion()!!},\r\n动作信息：应用崩溃,\r\n时间：${MyTimeUtil.nowTimeString}，\r\n数据：$data")
                    .addHeader("fileName", "Error/${User.getUser().storeId + MyTimeUtil.nowTimeString}.txt")
                    .build()
                    .execute(object : MyStringCallBack(object : MyListener {

                        override fun listenerSuccess(data: Any) {
                        }

                        override fun listenerFailed(errorMessage: String) {

                        }
                    }) {
                        override fun onResponse(p0: String?, p1: Int) {
                            //成功要删除
                            crashFile.deleteRecursively()
                        }

                    })
        }*/
    }

    /**
     * 登录就上传
     */
    fun reportEnter(storeId: String) {
        if (!ConnectionDetector.getConnectionDetector().isOnline) return
        OkHttpUtils
                .postString()
                .url(AppUrl.UPLOAD_ERROR)
                .content("版本号：${MyApplication.getVersion()!!}")
                .addHeader("fileName", "$storeId/login.txt")
                .build()
                .execute(object : MyStringCallBack(object : MyListener {

                    override fun listenerSuccess(data: Any) {
                    }

                    override fun listenerFailed(errorMessage: String) {
                    }
                }) {
                    override fun onResponse(p0: String?, p1: Int) {
                    }

                })
    }
}