package com.cstore.zhiyazhang.cstoremanagement.model.contractType

import com.cstore.zhiyazhang.cstoremanagement.bean.ContractTypeResult
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.url.AppUrl
import com.google.gson.Gson
import com.zhiyazhang.mykotlinapplication.utils.MyStringCallBack
import com.zhy.http.okhttp.OkHttpUtils

/**
 * Created by zhiya.zhang
 * on 2017/6/12 15:54.
 */
class ContractTypeModel : ContractTypeInterface {

    override fun getAllContractType(user: User, listener: MyListener) {
        OkHttpUtils
                .get()
                .url(AppUrl.CONTRACT_TYPE_URL)
                .addHeader("Authorization", user.uId)
                .addHeader("store_id", "091209")
                .build()
                .execute(object : MyStringCallBack(listener) {
                    override fun onResponse(response: String, id: Int) {
                        listener.contractSuccess(Gson().fromJson(response, ContractTypeResult::class.java))
                    }
                })
    }
}

interface ContractTypeInterface {
    fun getAllContractType(user: User, listener: MyListener)
}