package com.c_store.zhiyazhang.cstoremanagement.presenter.contract;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.c_store.zhiyazhang.cstoremanagement.bean.ContractBean;
import com.c_store.zhiyazhang.cstoremanagement.bean.ContractTypeResult;
import com.c_store.zhiyazhang.cstoremanagement.model.contractType.ContractTypeInterface;
import com.c_store.zhiyazhang.cstoremanagement.model.contractType.ContractTypeListener;
import com.c_store.zhiyazhang.cstoremanagement.model.contractType.ContractTypeModel;
import com.c_store.zhiyazhang.cstoremanagement.utils.onclick.RecyclerOnItemClickListener;
import com.c_store.zhiyazhang.cstoremanagement.view.interfaceview.ContractTypeView;

/**
 * Created by zhiya.zhang
 * on 2017/5/9 17:11.
 */

public class ContractTypePresenter {
    private ContractTypeInterface anInterface;
    private ContractTypeView typeView;
    private Handler mHandler=new Handler();
    public ContractTypePresenter(ContractTypeView contractTypeView){
        this.typeView=contractTypeView;
        this.anInterface=new ContractTypeModel();
    }

    public void getAllContractType(){
        anInterface.getAllContractType(typeView.getUser(), new ContractTypeListener() {
            @Override
            public void contractSuccess(final Object object) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ContractTypeAdapter adapter=new ContractTypeAdapter(((ContractTypeResult) object).getDetail());
                        adapter.setOnItemClickLitener(new RecyclerOnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int positon) {
                                typeView.toShortClick(view, ((ContractTypeResult) object).getDetail().get(positon));
                            }

                            @Override
                            public void onItemLongClick(View view, int positon) {
                                typeView.toLongClick(view);
                            }

                            @Override
                            public void onTouchAddListener(ContractBean cb, int positon, MotionEvent event) {

                            }

                            @Override
                            public void onTouchLessListener(ContractBean cb, int positon, MotionEvent event) {

                            }
                        });
                        typeView.showView(adapter);
                        typeView.hideLoading();
                    }
                });
            }

            @Override
            public void contractFailed(String errorMessage) {
                typeView.showFailedError(errorMessage);
                typeView.hideLoading();
            }
        });
    }
}
