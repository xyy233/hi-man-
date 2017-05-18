package com.c_store.zhiyazhang.cstoremanagement.utils.callback;

import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhiya.zhang
 * on 2017/5/9 16:55.
 */

public abstract class MyStringCallBack extends Callback<String> {
    private String string;
    private int code;

    @Override
    public String parseNetworkResponse(Response response, int id) throws Exception {
        return response.body().string();
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

    @Override
    public void onError(Call call, Exception e, int id) {

    }

    @Override
    public void onResponse(String response, int id) {

    }

    protected String myError() {
        if (code != 200) {
            String message=string.substring(string.indexOf("HTTP Status 500 - ") + 18, string.indexOf("</h1><div class=\"line\">"));
            if(message.equals("")){
                message="服务器遇到内部错误，阻止其执行此请求,请重试。";
            }
            return message;
        } else {
            return "";
        }
    }
}
