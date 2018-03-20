package com.cstore.zhiyazhang.cstoremanagement.view

import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import kotlinx.android.synthetic.main.activity_test.*
import java.util.*

/**
 * Created by zhiya.zhang
 * on 2018/3/20 11:25.
 */
class TestActivity(override val layoutId: Int = R.layout.activity_test) : MyActivity() {
    override fun initView() {
        val c = Calendar.getInstance(Locale.CHINA)
        val d = Date()
        c.time = d
        datePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)) { view, year, monthOfYear, dayOfMonth ->

        }
    }

    override fun initClick() {
    }

    override fun initData() {
    }

}