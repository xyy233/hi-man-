package com.c_store.zhiyazhang.cstoremanagement.url;

/**
 * Created by zhiya.zhang
 * on 2017/5/8 14:02.
 */

public class AppUrl {
    private static final String APPIP = "http://watchstore.rt-store.com:8081/api";
    public static final String CONTRACT_TYPE_URL = APPIP + "/order/getGroups.do";
    public static final String CONTRACT_URL = APPIP + "/order/getItemsByGroupId.do";
    public static final String UPDATA_CONTRACT_URL = APPIP + "/order/updateOrdQty.do";
    public static final String UPDATA_CONTRACTS_URL = APPIP + "/order/updateOrdQtys.do";
    public static final String SEARCH_CONTRACT_URL = APPIP + "/order/getItemsByVague.do";
}
