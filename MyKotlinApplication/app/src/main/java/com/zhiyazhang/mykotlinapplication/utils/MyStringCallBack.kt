package com.zhiyazhang.mykotlinapplication.utils

import com.zhy.http.okhttp.callback.Callback
import okhttp3.Response
import java.io.IOException

/**
 * Created by zhiya.zhang
 * on 2017/6/2 16:45.
 */
abstract class MyStringCallBack : Callback<String>() {
    var string: String? = null
    var code: Int? = null

    @Throws(Exception::class)
    override fun parseNetworkResponse(response: Response?, id: Int): String? {
        return response?.body()?.string()
    }

    override fun validateReponse(response: Response?, id: Int): Boolean {
        code = response?.code()
        if (code != 200) {
            try {
                string = response?.body()?.string()
            } catch (e: IOException) {
                return true
            }
        }
        return true
    }

    protected fun myError(): String {
        if (code != 200) {
            var message = string!!.substring(string!!.indexOf("HTTP Status 500 - ") + 18, string!!.indexOf("</h1><div class=\"line\">"))
            if (message == "") {
                message = "服务器遇到内部错误，阻止其执行此请求,请重试。"
            }
            return message
        } else {
            return ""
        }
    }
}