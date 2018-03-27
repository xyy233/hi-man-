package com.cstore.zhiyazhang.cstoremanagement.view

import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import kotlinx.android.synthetic.main.activity_test.*

/**
 * Created by zhiya.zhang
 * on 2018/3/20 11:25.
 */
class TestActivity(override val layoutId: Int = R.layout.activity_test) : MyActivity() {
    override fun initView() {
        scroll.post {
            scroll.smoothScrollTo(0,linear.measuredHeight)
        }
    }

    override fun initClick() {
    }

    override fun initData() {
    }

}