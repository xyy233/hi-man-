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
        lateinit private var mActivity: WeakReference<MyActivity>
        lateinit private var mListener:MyListener
        fun writeActivity(activity: MyActivity):MyHandler{
            mActivity= WeakReference<MyActivity>(activity)
            return this
        }

        fun writeListener(myListener: MyListener):MyHandler{
            mListener=myListener
            return this
        }

        override fun handleMessage(msg: Message) {
            if (mActivity.get()==null)return
            when(msg.what){
                SUCCESS -> {
                    if (msg.obj as String == "" || msg.obj as String == "[]") {
                        mListener.listenerFailed(MyApplication.instance().applicationContext.resources.getString(R.string.idError))
                    }
                    mListener.listenerSuccess(msg.obj as String)
                }
                else-> mListener.listenerFailed(msg.obj as String)
            }
        }
    }
}