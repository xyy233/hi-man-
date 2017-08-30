package com.cstore.zhiyazhang.cstoremanagement.utils

import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.url.AppUrl
import com.zhiyazhang.mykotlinapplication.utils.MyStringCallBack
import com.zhy.http.okhttp.OkHttpUtils

/**
 * Created by zhiya.zhang
 * on 2017/7/20 8:47.
 * 上报日志
 */
object ReportListener {
    fun report(storeId: String, version: String, errorMessage: String, errorDate: String) {
        if (!ConnectionDetector.getConnectionDetector().isOnline) return
        OkHttpUtils
                .postString()
                .url(AppUrl.UPLOAD_ERROR)
                .content("店号：$storeId,\r\n版本号：$version,\r\n动作信息：$errorMessage,\r\n时间：${MyTimeUtil.nowTimeString}，\r\n数据：$errorDate")
                .addHeader("fileName", "$storeId/${MyTimeUtil.nowTimeString}.txt")
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

    fun reportEnter(storeId: String){
        if (!ConnectionDetector.getConnectionDetector().isOnline) return
        OkHttpUtils
                .postString()
                .url(AppUrl.UPLOAD_ERROR)
                .content("")
                .addHeader("fileName","$storeId/login.txt")
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