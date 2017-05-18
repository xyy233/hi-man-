package com.c_store.zhiyazhang.cstoremanagement.model.contractType;

/**
 * Created by zhiya.zhang
 * on 2017/5/9 16:46.
 */

public interface ContractTypeListener {
    void contractSuccess(Object object);
    void contractFailed(String errorMessage);
}
