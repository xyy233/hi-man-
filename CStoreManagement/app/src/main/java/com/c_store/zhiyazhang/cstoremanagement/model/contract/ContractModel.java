package com.c_store.zhiyazhang.cstoremanagement.model.contract;

import com.c_store.zhiyazhang.cstoremanagement.bean.ContractBean;
import com.c_store.zhiyazhang.cstoremanagement.bean.ContractResult;
import com.c_store.zhiyazhang.cstoremanagement.bean.ContractTypeBean;
import com.c_store.zhiyazhang.cstoremanagement.bean.UserBean;
import com.c_store.zhiyazhang.cstoremanagement.model.MyListener;
import com.c_store.zhiyazhang.cstoremanagement.utils.callback.MyStringCallBack;
import com.c_store.zhiyazhang.cstoremanagement.utils.url.AppUrl;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;

import static com.zhy.http.okhttp.OkHttpUtils.postString;

/**
 * Created by zhiya.zhang
 * on 2017/5/10 15:00.
 */

public class ContractModel implements ContractInterface {

    @Override
    public void getAllContract(String ordType, UserBean user, ContractTypeBean ctb, final MyListener myListener) {
        postString()
                .url(AppUrl.CONTRACT_URL)
                .content("{\"type_id\":\"" + ctb.getTypeId() + "\",\"orderby\":\"" + ordType + "\",\"page\":1}")
                .addHeader("Authorization", user.getUid())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new MyStringCallBack() {
                    @Override
                    public void onResponse(String response, int id) {
                        super.onResponse(response, id);
                        myListener.contractSuccess(new Gson().fromJson(response, ContractResult.class));
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        try {
                            myListener.contractFailed(myError());
                        } catch (Exception ignored) {
                            myListener.contractFailed(e.getMessage());
                        }
                    }
                });
    }

    @Override
    public void searchContract(String ordType, String searchMessage, UserBean user, final MyListener myListener) {
        postString()
                .url(AppUrl.SEARCH_CONTRACT_URL)
                .content("{\"vague\":\"" + searchMessage + "\",\"orderby\":\"" + ordType + "\",\"page\":1}")
                .addHeader("Authorization", user.getUid())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new MyStringCallBack() {
                    @Override
                    public void onResponse(String response, int id) {
                        super.onResponse(response, id);
                        myListener.contractSuccess(new Gson().fromJson(response, ContractResult.class));
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        try {
                            myListener.contractFailed(myError());
                        } catch (Exception ignored) {
                            myListener.contractFailed(e.getMessage());
                        }
                    }
                });
    }

    @Override
    public void getAllContract(int page, String ordType, UserBean user, ContractTypeBean ctb, final MyListener myListener) {
        postString()
                .url(AppUrl.CONTRACT_URL)
                .content("{\"type_id\":\"" + ctb.getTypeId() + "\",\"orderby\":\"" + ordType + "\",\"page\":" + (page + 1) + "}")
                .addHeader("Authorization", user.getUid())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new MyStringCallBack() {
                    @Override
                    public void onResponse(String response, int id) {
                        super.onResponse(response, id);
                        myListener.contractSuccess(new Gson().fromJson(response, ContractResult.class));
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        try {
                            myListener.contractFailed(myError());
                        } catch (Exception ignored) {
                            myListener.contractFailed(e.getMessage());
                        }
                    }
                });
    }

    @Override
    public void searchContract(int page, String ordType, String searchMessage, UserBean user, final MyListener myListener) {
        postString()
                .url(AppUrl.SEARCH_CONTRACT_URL)
                .content("{\"vague\":\"" + searchMessage + "\",\"orderby\":\"" + ordType + "\",\"page\":" + (page + 1) + "}")
                .addHeader("Authorization", user.getUid())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new MyStringCallBack() {
                    @Override
                    public void onResponse(String response, int id) {
                        super.onResponse(response, id);
                        myListener.contractSuccess(new Gson().fromJson(response, ContractResult.class));
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        try {
                            myListener.contractFailed(myError());
                        } catch (Exception ignored) {
                            myListener.contractFailed(e.getMessage());
                        }
                    }
                });
    }

    @Override
    public void updateAllContract(List<ContractBean> cbs, UserBean user, final MyListener myListener) {
        OkHttpUtils
                .postString()
                .url(AppUrl.UPDATA_CONTRACTS_URL)
                .content(new Gson().toJson(cbs))
                .addHeader("Authorization", user.getUid())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new MyStringCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        try {
                            myListener.contractFailed(myError());
                        } catch (Exception ignored) {
                            myListener.contractFailed(e.getMessage());
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        super.onResponse(response, id);
                        myListener.contractSuccess();
                    }
                });
    }


}
