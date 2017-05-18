package com.c_store.zhiyazhang.cstoremanagement.model.contract;

import com.c_store.zhiyazhang.cstoremanagement.bean.ContractBean;
import com.c_store.zhiyazhang.cstoremanagement.bean.ContractTypeBean;
import com.c_store.zhiyazhang.cstoremanagement.bean.UserBean;
import com.c_store.zhiyazhang.cstoremanagement.model.MyListener;

import java.util.List;

/**
 * Created by zhiya.zhang
 * on 2017/5/10 15:00.
 */

public interface ContractInterface {
    public void getAllContract(String ordType, UserBean user, ContractTypeBean ctb, MyListener myListener);
    public void searchContract(String ordType, String searchMessage, UserBean user, MyListener myListener);
    public void getAllContract(int page, String ordType, UserBean user, ContractTypeBean ctb, MyListener myListener);
    public void searchContract(int page, String ordType, String searchMessage, UserBean user, MyListener myListener);
    public void updateAllContract(List<ContractBean> cbs, UserBean user,MyListener myListener);
}
