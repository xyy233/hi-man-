package com.cstore.zhiyazhang.cstoremanagement.view.checkin

import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.presenter.personnel.CheckInDYPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity

/**
 * Created by zhiya.zhang
 * on 2018/3/29 11:37.
 */
class CheckInDYActivity(override val layoutId: Int = R.layout.activity_order) : MyActivity() {
    private val presenter = CheckInDYPresenter(this)
    override fun initView() {
        presenter.getPhotoByDate()
    }

    override fun initClick() {
    }

    override fun initData() {
    }

    override fun getData1(): Any? {
        return this
    }

    override fun getData2(): Any? {
        return "20180411"
    }

}