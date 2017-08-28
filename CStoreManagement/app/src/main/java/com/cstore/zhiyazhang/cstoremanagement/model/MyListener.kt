package com.cstore.zhiyazhang.cstoremanagement.model

/**
 * Created by zhiya.zhang
 * on 2017/5/10 15:02.
 */

interface MyListener {
    fun listenerSuccess()
    fun listenerSuccess(`object`: Any)
    fun listenerFailed(errorMessage: String)
}
