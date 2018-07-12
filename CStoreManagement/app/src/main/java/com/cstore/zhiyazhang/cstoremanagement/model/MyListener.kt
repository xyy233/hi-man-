package com.cstore.zhiyazhang.cstoremanagement.model

/**
 * Created by zhiya.zhang
 * on 2017/5/10 15:02.
 */

interface MyListener {
    fun listenerSuccess(data: Any)
    fun listenerFailed(errorMessage: String){}
    fun listenerOther(data:Any){}
}
