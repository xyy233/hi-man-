package com.cstore.zhiyazhang.cstoremanagement.view.transfer

import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.presenter.transfer.TransferPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity

/**
 * Created by zhiya.zhang
 * on 2018/1/19 15:24.
 */
class TransferActivity(override val layoutId: Int = R.layout.activity_order) : MyActivity() {

    private val presenter = TransferPresenter(this)

    override fun showLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideLoading() {

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