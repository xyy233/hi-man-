package com.cstore.zhiyazhang.cstoremanagement.model.contracttype

import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.url.AppUrl
import com.cstore.zhiyazhang.cstoremanagement.url.AppUrl.CONNECTION_HEADER
import com.cstore.zhiyazhang.cstoremanagement.url.AppUrl.CONNECTION_SWITCH
import com.cstore.zhiyazhang.cstoremanagement.url.AppUrl.USER_HEADER
import com.zhiyazhang.mykotlinapplication.utils.MyStringCallBack
import com.zhy.http.okhttp.OkHttpUtils

/**
 * Created by zhiya.zhang
 * on 2017/6/12 15:54.
 */
class ContractTypeModel : ContractTypeInterface {

    override fun getAllContractType(user: User, isJustLook: Boolean, listener: MyListener) {
        OkHttpUtils
                .get()
                .url(AppUrl.CONTRACT_TYPE_URL)
                .addHeader(USER_HEADER, user.uId)
//                .addHeader(AppUrl.STORE_HEADER,"170514")
                .addHeader(AppUrl.STORE_HEADER, user.storeId)
                .addHeader(CONNECTION_HEADER, CONNECTION_SWITCH)
                .addHeader("is_just_look", isJustLook.toString())
                .build()
                .execute(object : MyStringCallBack(listener) {
                    override fun onResponse(response: String, id: Int) {
                       listener.listenerSuccess(response)
                    }
                })
    }
}

interface ContractTypeInterface {
    fun getAllContractType(user: User, isJustLook: Boolean, listener: MyListener)
}