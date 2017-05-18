package com.c_store.zhiyazhang.cstoremanagement.model.signin;

import com.c_store.zhiyazhang.cstoremanagement.bean.UserBean;

/**
 * Created by zhiya.zhang
 * on 2017/5/8 15:04.
 */

public interface SignInListener {
    void loginSuccess(UserBean user);
    void loginFailed(String errorMessage);
}
