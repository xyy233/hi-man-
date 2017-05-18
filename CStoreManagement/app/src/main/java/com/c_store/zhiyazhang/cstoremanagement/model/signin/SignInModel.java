package com.c_store.zhiyazhang.cstoremanagement.model.signin;

import com.c_store.zhiyazhang.cstoremanagement.bean.UserBean;
import com.c_store.zhiyazhang.cstoremanagement.utils.url.AppUrl;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by zhiya.zhang
 * on 2017/5/8 15:07.
 */

public class SignInModel implements SignInInterface {
    @Override
    public void login(String uid, String password, final SignInListener signInListener) {
        UserBean user=new UserBean();
        user.setUid(uid);
        user.setPassword(password);
        OkHttpUtils
                .postString()
                .url(AppUrl.LOGIN_URL)
                .content(new Gson().toJson(user))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new UserCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        //输出错误信息
                        try {
                            signInListener.loginFailed(myError());
                        }catch (Exception ignored){
                            signInListener.loginFailed(e.getMessage());
                        }
                    }

                    @Override
                    public void onResponse(UserBean response, int id) {
                        signInListener.loginSuccess(response);
                    }
                });
    }
}
