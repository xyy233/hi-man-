package com.cstore.zhiyazhang.cstoremanagement.utils

import android.os.Handler
import android.os.Message
import android.util.Log
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import java.lang.ref.WeakReference

/**
 * Created by zhiya.zhang
 * on 2017/8/28 17:04.
 */
class MyHandler {
    companion object MyHandler : Handler() {
        val SUCCESS = 0
        val ERROR1 = 1
        private var isRun:Boolean=false

        private var mActivity: WeakReference<MyActivity>? = null
        private var mListener: MyListener? = null

        fun writeActivity(activity: MyActivity): MyHandler {
            mActivity = WeakReference(activity)
            isRun=true
            return this
        }

        fun writeListener(myListener: MyListener): MyHandler {
            mListener = myListener
            isRun=true
            return this
        }

        fun cleanAL() {
            if (!isRun){//还在运行就不允许清空
                mActivity = null
                mListener = null
            }
        }

        override fun handleMessage(msg: Message) {
            try {
                if (mActivity != null && mListener != null) {
                    if (mActivity!!.get() == null) {
                        mListener!!.listenerFailed(MyApplication.instance().applicationContext.getString(R.string.system_error))
                        isRun=false
                        return
                    }
                    isRun = when (msg.what) {
                        SUCCESS -> {
                            mListener!!.listenerSuccess(msg.obj)
                            false
                        }
                        else -> {
                            mListener!!.listenerFailed(msg.obj as String)
                            false
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("MyHandler",e.message)
                //mListener!!.listenerFailed(e.message!!)
                isRun=false
                return
            }
        }
    }
}