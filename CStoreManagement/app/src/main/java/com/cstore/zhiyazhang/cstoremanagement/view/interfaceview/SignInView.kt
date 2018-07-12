package com.cstore.zhiyazhang.cstoremanagement.view.interfaceview

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
     * 保存用户信息
     */
    fun saveUser(user: User)
}
