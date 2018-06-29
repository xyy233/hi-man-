package com.cstore.zhiyazhang.cstorepay.util

/**
 * Created by zhiya.zhang
 * on 2017/6/13 16:16.
 * 通用接口,
 */
interface GenericView {
    /**
     * 请求成功，传递泛型对象进来自己处理
     */
    fun <T> requestSuccess(rData: T) {}

    /**
     * 输出提示信息
     */
    fun showPrompt(errorMsg: String) {
        MyToast.getLongToast(errorMsg)
    }

    /**
     * 显示loading信息
     */
    fun showLoading() {}

    /**
     * 隐藏loading信息
     */
    fun hideLoading() {
        //注销handler里的数据
    }

    /**
     * presenter内adapter设置成功递给view的泛型adapter
     *
     * 现在作用和requestsuccess一样
     */
    fun <T> showView(aData: T) {}

    fun errorDealWith() {}

    fun <T> errorDealWith(eData: T) {}

    fun <T> updateDone(uData: T) {}

    /**
     * 缺个丢参方法，凑合下
     */
    fun <T> requestSuccess2(rData: T) {}

    /**
     * 得到activity数据专用
     * 2018年1月30日 11:51:32
     */
    fun getData1(): Any? {
        return ""
    }

    /**
     * 得到activity数据专用
     * 2018年1月30日 11:51:32
     */
    fun getData2(): Any? {
        return ""
    }

    /**
     * 得到activity数据专用
     * 2018年1月30日 11:51:32
     */
    fun getData3(): Any? {
        return ""
    }

    /**
     * 得到activity数据专用
     * 2018年1月30日 11:51:32
     */
    fun getData4():Any?{
        return ""
    }

    /**
     * 得到activity数据专用
     * 2018年1月30日 11:51:32
     */
    fun getData5():Any?{
        return ""
    }
}