package com.c_store.zhiyazhang.cstoremanagement.model.signin;

import android.os.Handler;
import android.os.Message;

import com.c_store.zhiyazhang.cstoremanagement.R;
import com.c_store.zhiyazhang.cstoremanagement.bean.UserBean;
import com.c_store.zhiyazhang.cstoremanagement.model.MyListener;
import com.c_store.zhiyazhang.cstoremanagement.sql.MySql;
import com.c_store.zhiyazhang.cstoremanagement.utils.MyApplication;
import com.c_store.zhiyazhang.cstoremanagement.utils.socket.SocketUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by zhiya.zhang
 * on 2017/5/8 15:07.
 */

public class SignInModel implements SignInInterface {
    //此为http，现改为socket
   /* @Override
    public void login(String uid, String password, final MyListener myListener) {
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
                            myListener.contractFailed(myError());
                        }catch (Exception ignored){
                            myListener.contractFailed(e.getMessage());
                        }
                    }

                    @Override
                    public void onResponse(UserBean response, int id) {
                        myListener.contractSuccess(response);
                    }
                });
    }*/

    @Override
    public void login(String uid, final String password, final MyListener myListener) {
        String ip = MyApplication.getIP();
        if (ip.equals(MyApplication.getContext().getResources().getString(R.string.notFindIP))) {
            myListener.contractFailed(ip);
            return;
        }

        SocketUtil.getSocketUtil(ip).inquire(MySql.SignIn(uid), new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        try {
                            if (((String) msg.obj).equals("") || ((String) msg.obj).equals("[]")) {
                                myListener.contractFailed(MyApplication.getContext().getResources().getString(R.string.idError));
                                break;
                            }
                            List<UserBean> users = new Gson().fromJson((String) msg.obj, new TypeToken<List<UserBean>>() {
                            }.getType());
                            if (!users.get(0).getPassword().equals(password)) {
                                myListener.contractFailed(MyApplication.getContext().getResources().getString(R.string.pwdError));
                            } else {
                                myListener.contractSuccess(users.get(0));
                            }
                        } catch (Exception e) {
                            myListener.contractFailed((String) msg.obj);
                        }
                        break;
                    case 1:
                        myListener.contractFailed((String) msg.obj);
                        break;
                    case 2:
                        myListener.contractFailed((String) msg.obj);
                        break;
                    default:
                        break;
                }
            }
        });
    }
}
