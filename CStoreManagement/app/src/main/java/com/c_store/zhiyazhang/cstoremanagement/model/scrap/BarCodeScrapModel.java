package com.c_store.zhiyazhang.cstoremanagement.model.scrap;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.c_store.zhiyazhang.cstoremanagement.R;
import com.c_store.zhiyazhang.cstoremanagement.bean.ScrapContractBean;
import com.c_store.zhiyazhang.cstoremanagement.bean.UserBean;
import com.c_store.zhiyazhang.cstoremanagement.model.MyListener;
import com.c_store.zhiyazhang.cstoremanagement.sql.MySql;
import com.c_store.zhiyazhang.cstoremanagement.utils.MyApplication;
import com.c_store.zhiyazhang.cstoremanagement.utils.socket.SocketUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhiya.zhang
 * on 2017/5/24 16:26.
 */

public class BarCodeScrapModel implements BarCodeScrapInterface {

    @Override
    public void getScrap(UserBean user, String barcode, final MyListener myListener) {
        String ip = MyApplication.getIP();
        if (ip.equals(MyApplication.getContext().getResources().getString(R.string.notFindIP))) {
            myListener.contractFailed(ip);
            return;
        }
        SocketUtil.getSocketUtil(ip).inquire(MySql.getScrapByBarcode(barcode), new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        if (((String) msg.obj).equals("") || ((String) msg.obj).equals("[]")) {
                            myListener.contractFailed(MyApplication.getContext().getResources().getString(R.string.noScrap));
                            break;
                        }
                        List<ScrapContractBean> scbs = new Gson().fromJson((String) msg.obj, new TypeToken<List<ScrapContractBean>>() {}.getType());
                        myListener.contractSuccess(scbs.get(0));
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

    @Override
    public void editSqlite(Context context, ArrayList<ScrapContractBean> scb) {

    }
}
