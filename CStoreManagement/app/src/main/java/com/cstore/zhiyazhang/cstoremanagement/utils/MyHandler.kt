package com.cstore.zhiyazhang.cstoremanagement.utils

import android.os.Handler
import android.os.Message
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.zhiyazhang.mykotlinapplication.utils.MyApplication
import java.lang.ref.WeakReference

/**
 * Created by zhiya.zhang
 * on 2017/8/28 17:04.
 */
class MyHandler {
    companion object MyHandler:Handler(){
        val SUCCESS =0
        val ERROR1 =1
        val ERROR2 =2
        private var mActivity: WeakReference<MyActivity>?=null
        private var mListener:MyListener?=null
        fun writeActivity(activity: MyActivity):MyHandler{
            mActivity= WeakReference<MyActivity>(activity)
            return this
        }

        fun writeListener(myListener: MyListener):MyHandler{
            mListener=myListener
            return this
        }

        override fun handleMessage(msg: Message) {
            try {
                if (mActivity!!.get()==null){
                    mListener!!.listenerFailed(MyApplication.instance().applicationContext.getString(R.string.system_error))
                    return
                }
                when(msg.what){
                    SUCCESS -> {
                        mListener!!.listenerSuccess(msg.obj)
                    }
                    else-> {
                        mListener!!.listenerFailed(msg.obj as String)
                    }
                }
            }catch (e:Exception){
                mListener!!.listenerFailed(e.message!!)
                return
            } finally {
                //清除绑定的listener以免持有外部引用造成内存泄漏
                mActivity=null
                mListener=null
            }
        }
    }
}