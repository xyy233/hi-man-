package com.c_store.zhiyazhang.cstoremanagement.url;

/**
 * Created by zhiya.zhang
 * on 2017/5/8 14:02.
 */

public class AppUrl {
    private static final String APPIP = "http://watchstore.rt-store.com:8081/appserver/";
    public static final String LOGIN_URL = APPIP + "login.getJson";
    public static final String CONTRACT_TYPE_URL = APPIP + "getGroups.order";
    public static final String CONTRACT_URL = APPIP + "getItemsByGroupId.order";
    public static final String UPDATA_CONTRACT_URL = APPIP + "updateOrdQty.order";
    public static final String UPDATA_CONTRACTS_URL = APPIP + "updateOrdQtys.order";
    public static final String SEARCH_CONTRACT_URL = APPIP + "getItemsByVague.order";
}
