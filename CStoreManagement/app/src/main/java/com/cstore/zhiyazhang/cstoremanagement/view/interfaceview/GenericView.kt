package com.cstore.zhiyazhang.cstoremanagement.view.interfaceview

import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast

/**
 * Created by zhiya.zhang
 * on 2017/6/13 16:16.
 * 通用接口,
 */
interface GenericView {
    /**
     * 请求成功，传递泛型对象进来自己处理
     */
    fun <T> requestSuccess(rData: T){}

    /**
     * 输出提示信息
     */
    fun showPrompt(errorMsg: String) {
        MyToast.getLongToast(errorMsg)
    }

    /**
     * 显示loading信息
     */
    fun showLoading()

    /**
     * 隐藏loading信息
     */
    fun hideLoading(){
        //注销handler里的数据
        MyHandler.removeCallbacksAndMessages(null)
    }

    /**
     * presenter内adapter设置成功递给view的泛型adapter
     *
     * 现在作用和requestsuccess一样
     */
    fun <T> showView(aData: T){}

    fun errorDealWith(){}

    fun <T> updateDone(uData:T){}
}