package com.c_store.zhiyazhang.cstoremanagement.model.contractDetail;

import com.c_store.zhiyazhang.cstoremanagement.bean.ContractBean;
import com.c_store.zhiyazhang.cstoremanagement.bean.UserBean;
import com.c_store.zhiyazhang.cstoremanagement.model.MyListener;

/**
 * Created by zhiya.zhang
 * on 2017/5/12 11:02.
 */

public interface ContractDetailInterface {
    public void updateCB(UserBean user, ContractBean cb, MyListener listener);
}
