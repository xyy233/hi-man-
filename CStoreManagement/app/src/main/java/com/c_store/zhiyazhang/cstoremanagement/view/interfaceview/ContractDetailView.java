package com.c_store.zhiyazhang.cstoremanagement.view.interfaceview;

import com.c_store.zhiyazhang.cstoremanagement.bean.ContractBean;
import com.c_store.zhiyazhang.cstoremanagement.bean.UserBean;

/**
 * Created by zhiya.zhang
 * on 2017/5/11 16:26.
 */

public interface ContractDetailView {
    //得到用户信息
    UserBean getUser();

    //得到传递过来的商品信息
    ContractBean getContractBean();

    //得到当前的商品信息
    ContractBean getNowContractBean();

    //显示等待
    void showLoading();

    //取消等待
    void hideLoading();

    //显示成功
    void toActivity();

    //显示错误
    void showFailedError(String errorMessage);
}
