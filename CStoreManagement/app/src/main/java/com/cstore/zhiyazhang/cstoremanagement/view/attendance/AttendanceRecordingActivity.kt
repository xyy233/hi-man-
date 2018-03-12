package com.cstore.zhiyazhang.cstoremanagement.view.attendance

import android.support.v7.widget.LinearLayoutManager
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.presenter.attendance.AttendanceRecordingAdapter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import kotlinx.android.synthetic.main.activity_attendance_recording.*

/**
 * Created by zhiya.zhang
 * on 2018/3/9 17:50.
 */
class AttendanceRecordingActivity(override val layoutId: Int = R.layout.activity_attendance_recording) : MyActivity() {

    override fun initView() {
        my_toolbar.title = "测试"
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        val data = ArrayList<String>()
        (0..12).mapTo(data) { it.toString() }
        record_recycler.layoutManager = LinearLayoutManager(this)
        record_recycler.adapter = AttendanceRecordingAdapter(data)
    }

    override fun initClick() {

    }

    override fun initData() {

    }

}