package com.cstore.zhiyazhang.cstoremanagement.utils

import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.zhy.http.okhttp.callback.Callback
import okhttp3.Call
import okhttp3.Response

/**
 * Created by zhiya.zhang
 * on 2017/6/2 16:45.
 */
abstract class MyStringCallBack(val listener: MyListener) : Callback<String>() {
    var string: String? = null
    var code: Int? = null

    @Throws(Exception::class)
    override fun parseNetworkResponse(response: Response?, id: Int): String? {
        return response?.body()?.string()
    }

    override fun onError(call: Call, ex: java.lang.Exception, id: Int) {
        try {
            string = ex.message
            listener.listenerFailed(myError())
        } catch (ignored: Exception) {
            try {
                if (string!!.contains("Failed to connect to")) {
                    listener.listenerFailed("连接异常,请检查网络")
                } else {
                    listener.listenerFailed(string!!)
                }
            } catch (e: Exception) {
                listener.listenerFailed("请上报此错误信息：${e.message}")
            }
        }
    }

    private fun myError(): String {
        return if (code != 200) {
            var message = string!!.substring(string!!.indexOf("HTTP Status") + 25, string!!.indexOf("</h1><div class=\"line\">"))
            if (message == "") {
                message = "服务器遇到内部错误，阻止其执行此请求,请重试。"
            }
            message
        } else {
            ""
        }
    }
}