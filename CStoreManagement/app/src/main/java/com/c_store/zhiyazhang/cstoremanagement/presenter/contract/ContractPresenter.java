package com.c_store.zhiyazhang.cstoremanagement.presenter.contract;

import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.c_store.zhiyazhang.cstoremanagement.bean.ContractBean;
import com.c_store.zhiyazhang.cstoremanagement.bean.ContractResult;
import com.c_store.zhiyazhang.cstoremanagement.bean.UserBean;
import com.c_store.zhiyazhang.cstoremanagement.model.MyListener;
import com.c_store.zhiyazhang.cstoremanagement.model.contract.ContractInterface;
import com.c_store.zhiyazhang.cstoremanagement.model.contract.ContractModel;
import com.c_store.zhiyazhang.cstoremanagement.utils.ConnectionDetector;
import com.c_store.zhiyazhang.cstoremanagement.utils.onclick.RecyclerOnItemClickListener;
import com.c_store.zhiyazhang.cstoremanagement.view.interfaceview.ContractView;

/**
 * Created by zhiya.zhang
 * on 2017/5/10 15:12.
 */

public class ContractPresenter {
    private ContractInterface anInterface;
    private ContractView cView;
    private Handler mHandler = new Handler();
    private Context context;
    private ConnectionDetector cd = ConnectionDetector.getConnectionDetector();

    public ContractPresenter(ContractView contractView, Context context) {
        this.context = context;
        this.cView = contractView;
        this.anInterface = new ContractModel();
    }

    //正常获得数据
    public void getAllContract() {
        if (!cd.isOnline()){
            cView.hideLoading();
            return;
        }
        cView.showLoading();
        anInterface.getAllContract(cView.getSort(), UserBean.getUser(), cView.getContractType(), new MyListener() {
            @Override
            public void contractSuccess() {

            }

            @Override
            public void contractSuccess(final Object object) {
                ContractResult crs = (ContractResult) object;
                if (crs.getTotal() == 0) {
                    cView.showNoMessage();
                }
                mHandler.post(new ContractRunnable(crs, null));
            }

            @Override
            public void contractFailed(String errorMessage) {
                cView.showFailedError(errorMessage);
                cView.hideLoading();
            }
        });
    }

    //上拉加载获得数据
    public void pullGetAllContract(final ContractAdapter adapter) {
        if (!cd.isOnline()){
            cView.hideLoading();
            return;
        }

        anInterface.getAllContract(cView.getPage(), cView.getSort(), UserBean.getUser(), cView.getContractType(), new MyListener() {
            @Override
            public void contractSuccess() {

            }

            @Override
            public void contractSuccess(final Object object) {
                ContractResult crs = (ContractResult) object;
                if (crs.getTotal() == 0) {
                    cView.showNoMessage();
                }
                mHandler.post(new ContractRunnable(crs, adapter));
            }

            @Override
            public void contractFailed(String errorMessage) {
                cView.showFailedError(errorMessage);
            }
        });
    }

    //通过搜索获得数据的上拉加载
    public void searchAllContract(final ContractAdapter adapter) {
        if (!cd.isOnline()){
            cView.hideLoading();
            return;
        }

        anInterface.searchContract(cView.getPage(), cView.getSort(), cView.getSearchMessage(), UserBean.getUser(), new MyListener() {
            @Override
            public void contractSuccess() {

            }

            @Override
            public void contractSuccess(final Object object) {
                ContractResult crs = (ContractResult) object;
                if (crs.getTotal() == 0) {
                    cView.showNoMessage();
                }
                mHandler.post(new ContractRunnable(crs, adapter));
            }

            @Override
            public void contractFailed(String errorMessage) {
                cView.showFailedError(errorMessage);
            }
        });


    }

    //通过搜索获得数据
    public void searchAllContract() {
        if (!cd.isOnline()){
            cView.hideLoading();
            return;
        }

        cView.showLoading();

        anInterface.searchContract(cView.getSort(), cView.getSearchMessage(), UserBean.getUser(), new MyListener() {
            @Override
            public void contractSuccess() {

            }

            @Override
            public void contractSuccess(final Object object) {
                ContractResult crs = (ContractResult) object;
                if (crs.getTotal() == 0) {
                    cView.showNoMessage();
                }
                mHandler.post(new ContractRunnable(crs, null));
            }

            @Override
            public void contractFailed(String errorMessage) {
                cView.showFailedError(errorMessage);
            }
        });
    }

    //提交更新数据
    public void updateAllContract() {
        if (!cd.isOnline()){
            cView.hideLoading();
            return;
        }

        cView.showLoading();
        anInterface.updateAllContract(cView.getContractList(), UserBean.getUser(), new MyListener() {
            @Override
            public void contractSuccess() {
                cView.updateDone();
                cView.hideLoading();
            }

            @Override
            public void contractSuccess(Object object) {

            }

            @Override
            public void contractFailed(String errorMessage) {
                cView.showFailedError(errorMessage);
                cView.hideLoading();
            }
        });
    }


    private class ContractRunnable implements Runnable {
        ContractResult cr;
        ContractAdapter adapter;

        ContractRunnable(ContractResult cr, ContractAdapter adapter) {
            this.cr = cr;
            this.adapter = adapter;
        }

        @Override
        public void run() {
            if (adapter == null) {
                adapter = new ContractAdapter(cr, context);
                adapter.setIsEdit(true);
            } else {
                adapter.addItem(cr.getDetail());
                adapter.changeMoreStatus(ContractAdapter.PULLUP_LOAD_MORE);
                cView.setPage(cr.getPage());
                return;
            }
            adapter.setOnItemClickLitener(new RecyclerOnItemClickListener() {
                @Override
                public void onItemClick(View view, int positon) {
                    cView.toShortClick(view, (ContractBean) cr.getDetail().get(positon));
                }

                @Override
                public void onItemLongClick(View view, int positon) {
                }

                @Override
                public void onTouchAddListener(ContractBean cb, int positon, MotionEvent event) {
                    cView.touchAdd(cb, positon, event);
                }

                @Override
                public void onTouchLessListener(ContractBean cb, int positon, MotionEvent event) {
                    cView.touchLess(cb, positon, event);
                }
            });
            cView.showView(adapter);
            cView.hideLoading();
            cView.setPage(cr.getPage());
        }
    }
}
