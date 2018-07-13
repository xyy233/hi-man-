package com.cstore.zhiyazhang.cstoremanagement.utils

import android.os.Environment
import com.cstore.zhiyazhang.cstoremanagement.bean.AppVersionData
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.url.AppUrl
import com.cstore.zhiyazhang.cstoremanagement.utils.MyJavaFun.clearErrorTxtMessage
import com.cstore.zhiyazhang.cstoremanagement.utils.MyJavaFun.getErrorTxtMessage
import com.google.gson.Gson
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
    fun report(errorMessage: String, errorData: String) {
        if (!ConnectionDetector.getConnectionDetector().isOnline) return
        OkHttpUtils
                .postString()
                .url(AppUrl.UPLOAD_ERROR)
                .content("店号：${User.getUser().storeId},\r\n版本号：${MyApplication.getVersion()!!},\r\n动作信息：$errorMessage,\r\n时间：${MyTimeUtil.nowTimeString}，\r\n数据：$errorData")
                .addHeader("fileName", "${User.getUser().storeId}/${MyTimeUtil.nowTimeString3}.txt")
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
                        .addHeader("fileName", "Error/${User.getUser().storeId + MyTimeUtil.nowTimeString3}.txt")
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
    }

    /**
     * 上传错误信息
     */
    fun reportError(errorMessage: String, errorData: String) {
        if (!ConnectionDetector.getConnectionDetector().isOnline) return
        OkHttpUtils
                .postString()
                .url(AppUrl.UPLOAD_ERROR)
                .content("店号：${User.getUser().storeId},\r\n版本号：${MyApplication.getVersion()!!},\r\n动作信息：$errorMessage,\r\n时间：${MyTimeUtil.nowTimeString}，\r\n数据：$errorData")
                .addHeader("fileName", "Error/${User.getUser().storeId + MyTimeUtil.nowTimeString3}.txt")
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
     * 登录就上传
     */
    fun reportEnter(storeId: String) {
        if (!ConnectionDetector.getConnectionDetector().isOnline) return
        val phoneId = MyApplication.getOnlyid()
        val verNum = MyApplication.getVersionNum()
        val androidVer = MyApplication.getAndroidVersion()
        val phoneName = MyApplication.getPhoneName()
        val avd = AppVersionData(phoneId, storeId, verNum.toString(), phoneName, androidVer)
        val data = Gson().toJson(avd)
        OkHttpUtils
                .postString()
                .url(AppUrl.APP_VERSION_UPLOAD)
                .content(data)
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