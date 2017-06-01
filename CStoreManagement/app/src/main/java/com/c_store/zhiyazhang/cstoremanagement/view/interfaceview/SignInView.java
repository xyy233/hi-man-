package com.c_store.zhiyazhang.cstoremanagement.view.interfaceview;

import com.c_store.zhiyazhang.cstoremanagement.bean.UserBean;

/**
 * Created by zhiya.zhang
 * on 2017/5/8 14:59.
 */

public interface SignInView {
    String getUID();

    String getPassword();

    void showLoading();

    void hideLoading();

    void toActivity(UserBean user);

    void showFailedError(String errorMessage);

    void saveUser(UserBean user);
}
