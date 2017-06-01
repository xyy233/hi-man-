package com.c_store.zhiyazhang.cstoremanagement.view.interfaceview;

import android.view.View;

import com.c_store.zhiyazhang.cstoremanagement.bean.ContractTypeBean;
import com.c_store.zhiyazhang.cstoremanagement.bean.UserBean;
import com.c_store.zhiyazhang.cstoremanagement.presenter.contract.ContractTypeAdapter;

/**
 * Created by zhiya.zhang
 * on 2017/5/9 16:41.
 */

public interface ContractTypeView {
    void toShortClick(View view, ContractTypeBean ctb);

    void toLongClick(View view);

    void showLoading();

    void hideLoading();

    void showView(ContractTypeAdapter adapter);

    void showFailedError(String errorMessage);
}
