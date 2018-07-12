package com.cstore.zhiyazhang.cstoremanagement.utils

import android.app.IntentService
import android.os.Handler
import android.os.Message
import android.util.Log
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import java.lang.ref.WeakReference

/**
 * Created by zhiya.zhang
 * on 2017/12/18 15:50.
 */
class ServiceHandler : Handler() {
    private var mService: WeakReference<IntentService>? = null
    private var mListener: MyListener? = null

    fun writeService(service: IntentService): ServiceHandler {
        mService = WeakReference(service)
        return this
    }

    fun writeListener(myListener: MyListener): ServiceHandler {
        mListener = myListener
        return this
    }

    fun cleanAll() {
        mService = null
        mListener = null
        this.removeCallbacksAndMessages(null)
    }

    override fun handleMessage(msg: Message) {
        try {
            if (mService != null && mListener != null) {
                if (mService!!.get() == null) {
                    mListener!!.listenerFailed(MyApplication.instance().applicationContext.getString(R.string.system_error))
                    return
                }
                when (msg.what) {
                    MyHandler.SUCCESS -> {
                        mListener!!.listenerSuccess(msg.obj)
                    }
                    MyHandler.ERROR -> {
                        mListener!!.listenerFailed(msg.obj as String)
                    }
                    MyHandler.OTHER -> {
                        mListener!!.listenerOther(msg.obj)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MyHandler", e.message)
            return
        }
    }
}