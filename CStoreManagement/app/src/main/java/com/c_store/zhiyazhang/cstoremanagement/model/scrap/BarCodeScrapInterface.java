package com.c_store.zhiyazhang.cstoremanagement.model.scrap;

import android.content.Context;

import com.c_store.zhiyazhang.cstoremanagement.bean.ScrapContractBean;
import com.c_store.zhiyazhang.cstoremanagement.bean.UserBean;
import com.c_store.zhiyazhang.cstoremanagement.model.MyListener;

import java.util.ArrayList;

/**
 * Created by zhiya.zhang
 * on 2017/5/24 16:26.
 */

public interface BarCodeScrapInterface {
    public void getScrap(UserBean user, String barcode, MyListener myListener);
    public void editSqlite(Context context, ArrayList<ScrapContractBean> scb);
}
