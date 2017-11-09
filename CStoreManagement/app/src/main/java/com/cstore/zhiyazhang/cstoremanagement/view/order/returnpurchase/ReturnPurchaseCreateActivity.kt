package com.cstore.zhiyazhang.cstoremanagement.view.order.returnpurchase

import com.cstore.zhiyazhang.cstoremanagement.bean.ReturnPurchaseItemBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ReturnedPurchaseBean
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.ReturnView

/**
 * Created by zhiya.zhang
 * on 2017/11/3 14:41.
 */
class ReturnPurchaseCreateActivity(override val layoutId: Int) :MyActivity(),ReturnView,GenericView{
    override fun commodityError() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createError() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getRPB(): ReturnedPurchaseBean? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSelectVendor(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSelectType(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCreateData(): ArrayList<ReturnPurchaseItemBean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getVendor() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initView() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}