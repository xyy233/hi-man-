package com.c_store.zhiyazhang.cstoremanagement.model.contractDetail;

import com.c_store.zhiyazhang.cstoremanagement.bean.ContractBean;
import com.c_store.zhiyazhang.cstoremanagement.bean.UserBean;
import com.c_store.zhiyazhang.cstoremanagement.model.MyListener;
import com.c_store.zhiyazhang.cstoremanagement.utils.callback.MyStringCallBack;
import com.c_store.zhiyazhang.cstoremanagement.utils.url.AppUrl;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by zhiya.zhang
 * on 2017/5/12 11:03.
 */

public class ContractDetailModel implements ContractDetailInterface {

    @Override
    public void updateCB(UserBean user, ContractBean cb, final MyListener listener) {
        OkHttpUtils
                .postString()
                .url(AppUrl.UPDATA_CONTRACT_URL)
                .content(new Gson().toJson(cb))
                .addHeader("Authorization", user.getUid())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new MyStringCallBack() {
                    @Override
                    public void onResponse(String response, int id) {
                        super.onResponse(response, id);
                        listener.contractSuccess();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        try {
                            listener.contractFailed(myError());
                        }catch (Exception ignored){
                            listener.contractFailed(e.getMessage());
                        }
                    }
                });
    }
}
