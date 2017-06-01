package com.c_store.zhiyazhang.cstoremanagement.presenter.scrap;

import android.os.Handler;

import com.c_store.zhiyazhang.cstoremanagement.bean.ScrapContractBean;
import com.c_store.zhiyazhang.cstoremanagement.bean.UserBean;
import com.c_store.zhiyazhang.cstoremanagement.model.MyListener;
import com.c_store.zhiyazhang.cstoremanagement.model.scrap.BarCodeScrapInterface;
import com.c_store.zhiyazhang.cstoremanagement.model.scrap.BarCodeScrapModel;
import com.c_store.zhiyazhang.cstoremanagement.view.interfaceview.BarCodeScrapView;

/**
 * Created by zhiya.zhang
 * on 2017/5/24 16:25.
 */

public class BarCodeScrapPresenter {
    private BarCodeScrapInterface anInterface;
    private BarCodeScrapView cView;
    Handler mHandler = new Handler();

    public BarCodeScrapPresenter(BarCodeScrapView view) {
        this.cView = view;
        this.anInterface = new BarCodeScrapModel();
    }

    public void getScrap(String barcode) {
        cView.showLoading();
        anInterface.getScrap(UserBean.getUser(), barcode, new MyListener() {
            @Override
            public void contractSuccess() {
            }

            @Override
            public void contractSuccess(Object object) {
                cView.hideLoading();
                cView.showView((ScrapContractBean) object);
            }

            @Override
            public void contractFailed(String errorMessage) {
                cView.hideLoading();
                cView.showFailedError(errorMessage);
            }
        });
    }

}
