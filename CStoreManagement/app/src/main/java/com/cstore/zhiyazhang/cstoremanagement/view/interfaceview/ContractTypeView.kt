package com.cstore.zhiyazhang.cstoremanagement.view.interfaceview

/**
 * Created by zhiya.zhang
 * on 2017/6/27 11:47.
 */
interface ContractTypeView {
    val isJustLook:Boolean

    /**
     * 显示使用时间数据
     */
    fun showUsaTime(isShow:Boolean)

    val whereIsIt:String
}