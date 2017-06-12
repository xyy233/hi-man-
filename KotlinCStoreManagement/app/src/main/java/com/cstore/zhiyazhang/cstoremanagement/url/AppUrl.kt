package com.cstore.zhiyazhang.cstoremanagement.url

/**
 * Created by zhiya.zhang
 * on 2017/5/8 14:02.
 */

object AppUrl {
    private val APPIP = "http://watchstore.rt-store.com:8081/api"
    val CONTRACT_TYPE_URL = APPIP + "/order/getGroups.do"
    val CONTRACT_URL = APPIP + "/order/getItemsByGroupId.do"
    val UPDATA_CONTRACT_URL = APPIP + "/order/updateOrdQty.do"
    val UPDATA_CONTRACTS_URL = APPIP + "/order/updateOrdQtys.do"
    val SEARCH_CONTRACT_URL = APPIP + "/order/getItemsByVague.do"
}
