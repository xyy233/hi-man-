package com.cstore.zhiyazhang.cstoremanagement.model.shelves

import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.url.AppUrl
import com.cstore.zhiyazhang.cstoremanagement.utils.MyStringCallBack
import com.zhy.http.okhttp.OkHttpUtils

/**
 * Created by zhiya.zhang
 * on 2018/5/20 10:33.
 */
class ShelvesModel : ShelvesInterface {
    override fun getShelves(myListener: MyListener) {
        OkHttpUtils
                .get()
                .url(AppUrl.GET_SHELVES)
                //测试
                .addHeader(AppUrl.STOREHEADER, User.getUser().storeId)
//                .addHeader(AppUrl.STOREHEADER, "130902")
                .build()
                .execute(object : MyStringCallBack(myListener) {
                    override fun onResponse(response: String, id: Int) {
                        myListener.listenerSuccess(response)
                    }
                })
    }

    override fun getShelvesItem(data: String, myListener: MyListener) {
        OkHttpUtils
                .get()
                .url(AppUrl.GET_SHELVES_ITEM)
                //测试
                .addHeader(AppUrl.STOREHEADER, User.getUser().storeId)
//                .addHeader(AppUrl.STOREHEADER, "130902")
                .addHeader(AppUrl.SHELVES_ID, data)
                .build()
                .execute(object : MyStringCallBack(myListener) {
                    override fun onResponse(response: String, id: Int) {
                        myListener.listenerSuccess(response)
                    }
                })
    }

}

interface ShelvesInterface {
    /**
     * 获得冰箱
     */
    fun getShelves(myListener: MyListener)

    /**
     * 获得冰箱内商品
     */
    fun getShelvesItem(data: String, myListener: MyListener)
}