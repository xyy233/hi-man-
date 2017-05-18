package com.c_store.zhiyazhang.cstoremanagement.model.contractType;

import com.c_store.zhiyazhang.cstoremanagement.bean.ContractTypeResult;
import com.c_store.zhiyazhang.cstoremanagement.bean.UserBean;
import com.c_store.zhiyazhang.cstoremanagement.utils.callback.MyStringCallBack;
import com.c_store.zhiyazhang.cstoremanagement.utils.url.AppUrl;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import okhttp3.Call;

/**
 * Created by zhiya.zhang
 * on 2017/5/9 16:50.
 */

public class ContractTypeModel implements ContractTypeInterface {
    @Override
    public void getAllContractType(UserBean user, final ContractTypeListener contractTypeListener) {
        OkHttpUtils
                .get()
                .url(AppUrl.CONTRACT_TYPE_URL)
                .addHeader("Authorization", user.getUid())
                .build()
                .execute(new MyStringCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        try {
                            contractTypeListener.contractFailed(myError());
                        } catch (Exception ignored) {
                            if (e.getMessage().contains("failed to connect to")) {
                                contractTypeListener.contractFailed("连接超时");
                            } else {
                                contractTypeListener.contractFailed(e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        super.onResponse(response, id);
                        contractTypeListener.contractSuccess(new Gson().fromJson(response, ContractTypeResult.class));
                    }
                });
    }
}
