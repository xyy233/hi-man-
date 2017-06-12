package com.cstore.zhiyazhang.cstoremanagement.view.interface_view

import com.cstore.zhiyazhang.cstoremanagement.bean.User

/**
 * Created by zhiya.zhang
 * on 2017/5/8 14:59.
 */

interface SignInView {
    /**
     * 得到输入的id
     */
    val uid: String

    /**
     * 得到输入的密码
     */
    val password: String

    /**
     * 显示loading
     */
    fun showLoading()

    /**
     * 隐藏loading
     */
    fun hideLoading()

    /**
     * 登录成功跳转activity
     */
    fun toActivity(user: User)

    /**
     * 显示错误信息
     */
    fun showFailedError(errorMessage: String)

    /**
     * 保存用户信息
     */
    fun saveUser(user: User)
}
