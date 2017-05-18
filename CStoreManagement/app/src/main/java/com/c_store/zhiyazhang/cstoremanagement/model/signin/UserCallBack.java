package com.c_store.zhiyazhang.cstoremanagement.model.signin;

import com.c_store.zhiyazhang.cstoremanagement.bean.UserBean;
import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by zhiya.zhang
 * on 2017/5/8 15:11.
 */

abstract class UserCallBack extends Callback<UserBean> {
    private String string;
    private int code;

    @Override
    public UserBean parseNetworkResponse(Response response, int id) throws Exception {
        string = response.body().string();
        return new Gson().fromJson(string, UserBean.class);
    }

    @Override
    public boolean validateReponse(Response response, int id) {
        code = response.code();
        if (code != 200) {
            try {
                string = response.body().string();
            } catch (IOException e) {
                return true;
            }
        }
        return true;
    }

    String myError() {
        if (code != 200) {
            return string.substring(string.indexOf("HTTP Status 500 - ") + 18, string.indexOf("</h1><div class=\"line\">"));
        } else {
            return "";
        }
    }
}
