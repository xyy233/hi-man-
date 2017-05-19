package com.c_store.zhiyazhang.cstoremanagement.presenter.signin;

import android.os.Handler;

import com.c_store.zhiyazhang.cstoremanagement.bean.UserBean;
import com.c_store.zhiyazhang.cstoremanagement.model.signin.SignInInterface;
import com.c_store.zhiyazhang.cstoremanagement.model.signin.SignInListener;
import com.c_store.zhiyazhang.cstoremanagement.model.signin.SignInModel;
import com.c_store.zhiyazhang.cstoremanagement.utils.ConnectionDetector;
import com.c_store.zhiyazhang.cstoremanagement.view.interfaceview.SignInView;

/**
 * Created by zhiya.zhang
 * on 2017/5/8 15:02.
 */

public class SignInPresenter {
    private SignInInterface signInInterface;
    private SignInView signInView;
    private Handler mHandler=new Handler();
    private ConnectionDetector cd = ConnectionDetector.getConnectionDetector();
    public SignInPresenter(SignInView signInView){
        this.signInView=signInView;
        this.signInInterface=new SignInModel();
    }

    public void login(){
        if (!cd.isOnline()){
            signInView.hideLoading();
            return;
        }
        signInView.showLoading();
        signInInterface.login(signInView.getUID(), signInView.getPassword(), new SignInListener() {
            @Override
            public void loginSuccess(final UserBean user) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        signInView.toActivity(user);
                        signInView.saveUser();
                        signInView.hideLoading();
                    }
                });
            }

            @Override
            public void loginFailed(final String errorMessage) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        signInView.showFailedError(errorMessage);
                        signInView.hideLoading();
                    }
                });
            }
        });
    }
}
