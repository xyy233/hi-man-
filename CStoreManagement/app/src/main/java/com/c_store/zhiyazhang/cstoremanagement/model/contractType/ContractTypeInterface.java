package com.c_store.zhiyazhang.cstoremanagement.model.contractType;

import com.c_store.zhiyazhang.cstoremanagement.bean.UserBean;

/**
 * Created by zhiya.zhang
 * on 2017/5/9 16:45.
 */

public interface ContractTypeInterface {
    public void getAllContractType(UserBean user, ContractTypeListener contractTypeListener);
}
