package com.c_store.zhiyazhang.cstoremanagement.model.signin;

/**
 * Created by zhiya.zhang
 * on 2017/5/8 15:03.
 */

public interface SignInInterface {
    public void login(String uid, String password, SignInListener signInListener);


}
