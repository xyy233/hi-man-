package com.cstore.zhiyazhang.cstoremanagement.view.interfaceview

import com.cstore.zhiyazhang.cstoremanagement.bean.ReturnPurchaseItemBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ReturnedPurchaseBean

/**
 * Created by zhiya.zhang
 * on 2017/11/3 14:28.
 */
interface ReturnView{

    /**
     * 得到ReturnedPurchaseBean，如果有就代表是在之前上添加，没有就是完全新建，model会处理好一切
     */
    fun getRPB():ReturnedPurchaseBean?

    /**
     * 得到选择的配送商
     */
    fun getSelectVendor():String

    /**
     * 得到选择的短期或长期选择
     *  0=短期商品  1=长期商品
     */
    fun getSelectType():Int

    /**
     * 得到要创建的数据
     */
    fun getCreateData():ArrayList<ReturnPurchaseItemBean>
}