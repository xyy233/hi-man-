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
 *
 * 2017/10/11修改后启用,增加外部handler
 * 类本身就是一个handler，和内部的静态handler区别在于，内部handler是全局唯一，相当于static final，
 * MyHandler相当于只是一个对象，用来new一个handler使用，因此不像内部handler会自动处理防止泄漏
 * 内部handler用于socket，外部handler用于其他
 */
class MyHandler:Handler() {
    companion object OnlyMyHandler : Handler() {
        val SUCCESS = 0
        val ERROR1 = 1
        private var isRun:Boolean=false

        private var mActivity: WeakReference<MyActivity>? = null
        private var mListener: MyListener? = null

        fun writeActivity(activity: MyActivity): OnlyMyHandler {
            mActivity = WeakReference(activity)
            isRun=true
            return this
        }

        fun writeListener(myListener: MyListener): OnlyMyHandler {
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

    private var isRun:Boolean=false
    private var mActivity: WeakReference<MyActivity>? = null
    private var mListener: MyListener? = null

    fun writeActivity(activity: MyActivity): com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler {
        mActivity = WeakReference(activity)
        isRun=true
        return this
    }

    fun writeListener(myListener: MyListener): com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler {
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
            isRun=false
            return
        }
    }
}