package com.c_store.zhiyazhang.cstoremanagement.view.interfaceview;

import com.c_store.zhiyazhang.cstoremanagement.bean.ScrapContractBean;

/**
 * Created by zhiya.zhang
 * on 2017/5/24 10:12.
 */

public interface BarCodeScrapView {

    //添加数据到adapter中
    void showView(ScrapContractBean scb);

    //显示等待
    void showLoading();

    //取消等待
    void hideLoading();

    //显示错误
    void showFailedError(String errorMessage);
}
