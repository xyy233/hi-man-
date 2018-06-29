package com.cstore.zhiyazhang.cstorepay.util

/**
 * Created by zhiya.zhang
 * on 2018/6/21 15:47.
 */
/**
 * Created by zhiya.zhang
 * on 2017/5/10 15:02.
 */

interface MyListener {
    fun listenerSuccess(data: Any)
    fun listenerFailed(errorMessage: String){}
    fun listenerOther(data:Any){}
}
