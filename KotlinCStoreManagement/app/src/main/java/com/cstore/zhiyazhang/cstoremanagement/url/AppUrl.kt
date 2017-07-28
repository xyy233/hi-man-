package com.cstore.zhiyazhang.cstoremanagement.url

/**
 * Created by zhiya.zhang
 * on 2017/5/8 14:02.
 */

object AppUrl {

    val USER_HEADER = "Authorization"
    val STORE_HEADER = "store_id"
    val CONNECTION_HEADER = "Connection"
    val CONNECTION_SWITCH = "close"
    val IS_JUST_LOOK = "is_just_look"

    var appIp: String = "http://watchstore.rt-store.com:8086/app/"
    val CONTRACT_TYPE_URL = appIp + "order/getGroups.do"
    val CONTRACT_URL = appIp + "order/getItemsByGroupId.do"
    val ALL_EDITT_CONTRACT = appIp + "order/getUpdatedItems.do"
    val UPDATA_CONTRACT_URL = appIp + "order/updateOrdQty.do"
    val UPDATA_CONTRACTS_URL = appIp + "order/updateOrdQtys.do"
    val SEARCH_CONTRACT_URL = appIp + "order/getItemsByVague.do"
    val UPDATE_APP = appIp + "order/updateAPP.do"
    val UPLOAD_ERROR = appIp + "asset/uploadError.do"
}
