package com.c_store.zhiyazhang.cstoremanagement.model;

/**
 * Created by zhiya.zhang
 * on 2017/5/10 15:02.
 */

public interface MyListener {
    void contractSuccess();
    void contractSuccess(Object object);
    void contractFailed(String errorMessage);
}
