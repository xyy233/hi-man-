package com.cstore.zhiyazhang.cstoremanagement.model.mobile

import com.cstore.zhiyazhang.cstoremanagement.bean.MobileDriverBean
import com.cstore.zhiyazhang.cstoremanagement.bean.MobileErrorBean
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.utils.MJBMD5Util
import com.cstore.zhiyazhang.cstoremanagement.utils.MyStringCallBack
import com.google.gson.Gson
import com.zhy.http.okhttp.OkHttpUtils

/**
 * Created by zhiya.zhang
 * on 2018/6/29 14:04.
 */
object MobileUtil {
    private val headAPPID = "app_id"
    private val token = "token"
    //appId
    private val appId = "b8d9ed34-3cd1-4056-a1c9-80f70a8c890a"
    //校验秘钥
    val driverNameByEncryptId = "f0a775d5-a0e6-49a3-af5f-a20a76639f35"
    //补货数量查询秘钥
    val purchaseCountId = "faabd509-fa03-49ca-a1bd-9a07723ed5c5"
    //退货数量查询秘钥
    val returnCountId = "8374191d-b8e8-4b65-a3dd-bd57e080bb71"
    //司机补货秘钥
    val purchaseId = "4c92a015-43aa-46df-ad5b-58f7c02054a2"
    //提交装配数据秘钥
    val shelfId = "51e2538d-7c53-44c2-b8b4-a81c3b86f834"
    //退货提交秘钥
    val returnId = "f34ac5e5-8d6d-40e4-b22f-4e6376a74614"
    //校验地址，需加上司机编号，get方式
    private val driverNameByEncryptUrl = "https://dev-api.mobilemart.cn/driver-center-api/driver/driverNameByEncrypt?driverCode="
    //补货数量查询地址,get
    val purchaseCountUrl = "https://dev-api.mobilemart.cn/scm-web/scm/supply/driver/third/list?driverCode="
    //退货数量查询地址,get
    val returnCountUrl = "http://dev-api.mobilemart.cn/scm-web/scm/third/party/refund/driver/shelf/info?driverCode="
    //司机补货地址,post
    val purchaseUrl = "https://dev-api.mobilemart.cn/scm-web/third/unitStream/stationSupply/action"
    //提交装配数据地址.post
    val shelfUrl = "https://dev-api.mobilemart.cn/scm-web/shelfAssemble/third"
    //退货提交地址,post
    val returnUrl = "http://dev-api.mobilemart.cn/scm-web/scm/third/party/refund/driver/shelf/refund"

    fun judgmentDriver(code: String, listener: MyListener) {
        val map = HashMap<String, String>()
        map.put("driverCode", code)
        val tokenMsg = MJBMD5Util.sign(map, appId, driverNameByEncryptId)
        OkHttpUtils
                .get()
                .url(driverNameByEncryptUrl + code)
                .addHeader(headAPPID, appId)
                .addHeader(token, tokenMsg)
                .build()
                .execute(object : MyStringCallBack(listener) {
                    override fun onResponse(response: String?, id: Int) {
                        try {
                            val result = Gson().fromJson(response!!, MobileDriverBean::class.java)
                            if (result.success && result.errMsg == "SUCCESS") {
                                listener.listenerSuccess(result)
                            } else {
                                listener.listenerFailed(result.errMsg)
                            }
                        } catch (e: Exception) {
                            val result = Gson().fromJson(response!!, MobileErrorBean::class.java)
                            listener.listenerFailed(result.errMsg)
                        }
                    }
                })
    }
}