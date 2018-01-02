package com.cstore.zhiyazhang.cstoremanagement.model.contract

import android.util.Log
import com.cstore.zhiyazhang.cstoremanagement.bean.ContractBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ContractTypeBean
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.url.AppUrl
import com.google.gson.Gson
import com.zhiyazhang.mykotlinapplication.utils.MyStringCallBack
import com.zhy.http.okhttp.OkHttpUtils
import okhttp3.MediaType

/**
 * Created by zhiya.zhang
 * on 2017/6/14 15:59.
 */
class ContractModel : ContractInterface {
    override fun getAllContract(page: Int, ordType: String, user: User, ctb: ContractTypeBean, isJustLook: Boolean, myListener: MyListener) {
        OkHttpUtils
                .postString()
                .url(AppUrl.CONTRACT_URL)
                .content("{\"type_id\":\"" + ctb.typeId + "\",\"orderby\":\"" + ordType + "\",\"page\":" + (page + 1) + "}")
                .addHeader(AppUrl.USER_HEADER, user.uId)
//                .addHeader(AppUrl.STORE_HEADER,"150808")
                .addHeader(AppUrl.STORE_HEADER, user.storeId)
                .addHeader(AppUrl.IS_JUST_LOOK, isJustLook.toString())
                .addHeader(AppUrl.CONNECTION_HEADER, AppUrl.CONNECTION_SWITCH)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(object : MyStringCallBack(myListener) {
                    override fun onResponse(response: String, id: Int) {
                        myListener.listenerSuccess(response)
                    }
                })
    }

    override fun getEditAllContract(page: Int, user: User, myListener: MyListener) {
        OkHttpUtils
                .postString()
                .url(AppUrl.ALL_EDITT_CONTRACT)
                .content("{\"page\":" + (page + 1) + "}")
                .addHeader(AppUrl.USER_HEADER, user.uId)
//                .addHeader(AppUrl.STORE_HEADER,"150808")
                .addHeader(AppUrl.STORE_HEADER, user.storeId)
                .addHeader(AppUrl.CONNECTION_HEADER, AppUrl.CONNECTION_SWITCH)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(object : MyStringCallBack(myListener) {
                    override fun onResponse(response: String, id: Int) {
                        myListener.listenerSuccess(response)
                    }
                })
    }

    override fun searchContract(page: Int, ordType: String, searchMessage: String, user: User, myListener: MyListener) {
        OkHttpUtils
                .postString()
                .url(AppUrl.SEARCH_CONTRACT_URL)
                .content("{\"vague\":\"" + searchMessage + "\",\"orderby\":\"" + ordType + "\",\"page\":" + (page + 1) + "}")
                .addHeader(AppUrl.USER_HEADER, user.uId)
//                .addHeader(AppUrl.STORE_HEADER,"150808")
                .addHeader(AppUrl.STORE_HEADER, user.storeId)
                .addHeader(AppUrl.CONNECTION_HEADER, AppUrl.CONNECTION_SWITCH)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(object : MyStringCallBack(myListener) {
                    override fun onResponse(response: String, id: Int) {
                        myListener.listenerSuccess(response)
                    }
                })
    }

    override fun updateAllContract(cbs: List<ContractBean>, user: User, myListener: MyListener) {
        //防提交重复信息，相同品号的就删除,有可能导致anr，耗时操作，如出现就开线程去做
        val nowList = cbs as ArrayList<ContractBean>//使用一个新的list
        try {
            if (nowList.size>1){
                for (i in 0 until nowList.size) {//遍历list
                    (nowList.size - 1 downTo i + 1)//遍历内部遍历一遍
                            .filter { nowList[i].cId == nowList[it].cId }//查到品号相等的
                            .forEach { nowList.removeAt(it) }//遍历删掉
                }
            }
        }catch (e:Exception){
            Log.e("防重复报错",e.message)
        }
        OkHttpUtils
                .postString()
                .url(AppUrl.UPDATA_CONTRACTS_URL)
                .content(Gson().toJson(nowList))
                .addHeader(AppUrl.USER_HEADER, user.uId)
//                .addHeader(AppUrl.STORE_HEADER,"150808")
                .addHeader(AppUrl.STORE_HEADER, user.storeId)
                .addHeader(AppUrl.CONNECTION_HEADER, AppUrl.CONNECTION_SWITCH)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(object : MyStringCallBack(myListener) {
                    override fun onResponse(response: String, id: Int) {
                        myListener.listenerSuccess(response)
                    }
                })
    }
}


interface ContractInterface {
    /**
     * 根据类获得全部商品的分页
     */
    fun getAllContract(page: Int, ordType: String, user: User, ctb: ContractTypeBean, isJustLook: Boolean, myListener: MyListener)

    /**
     * 得到所有已修改过的商品
     */
    fun getEditAllContract(page: Int, user: User, myListener: MyListener)

    /**
     * 搜索得到全部商品的分页
     */
    fun searchContract(page: Int, ordType: String, searchMessage: String, user: User, myListener: MyListener)

    /**
     * 编辑更新所有修改的商品
     */
    fun updateAllContract(cbs: List<ContractBean>, user: User, myListener: MyListener)
}