package com.cstore.zhiyazhang.cstoremanagement.view.attendance

import android.support.v7.widget.LinearLayoutManager
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.AttendanceRecordingBean
import com.cstore.zhiyazhang.cstoremanagement.bean.WorkHoursBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.attendance.AttendanceRecordingLeftAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.attendance.AttendanceRecordingPresenter
import com.cstore.zhiyazhang.cstoremanagement.presenter.attendance.AttendanceRecordingRightAdapter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import kotlinx.android.synthetic.main.activity_attendance_recording.*
import kotlinx.android.synthetic.main.loading_layout.view.*

/**
 * Created by zhiya.zhang
 * on 2018/3/9 17:50.
 */
class AttendanceRecordingActivity(override val layoutId: Int = R.layout.activity_attendance_recording) : MyActivity() {

    private val presenter = AttendanceRecordingPresenter(this)
    private val workHoursList = ArrayList<WorkHoursBean>()
    private val nowYear = MyTimeUtil.nowYear
    private val nowMonth = MyTimeUtil.nowMonth
    private val leftAdapter = AttendanceRecordingLeftAdapter(ArrayList())
    private val rightAdapter = AttendanceRecordingRightAdapter(ArrayList())
    private val endMonthRes = ArrayList<String>()

    override fun initView() {
        my_toolbar.title = getString(R.string.attendance_record)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        begin_year.text = nowYear.toString()
        begin_month.text = (nowMonth - 1).toString()
        end_year.text = nowYear.toString()
        initMonthSpinner()
        initEdit()
        initDetailBox()
    }

    /**
     * 初始化EditText
     */
    private fun initEdit() {
        begin_day.inputType = InputType.TYPE_CLASS_NUMBER
        begin_day.keyListener = DigitsKeyListener.getInstance("1234567890")
        begin_day.setSelection(begin_day.text.length)
        end_day.inputType = InputType.TYPE_CLASS_NUMBER
        end_day.keyListener = DigitsKeyListener.getInstance("1234567890")
        end_day.setSelection(end_day.text.length)
        work_hours.inputType = InputType.TYPE_CLASS_NUMBER
        work_hours.keyListener = DigitsKeyListener.getInstance("1234567890.")
        work_hours.setSelection(work_hours.text.length)
    }

    /**
     * 初始化月份选择器
     */
    private fun initMonthSpinner() {
        endMonthRes.add(nowMonth.toString())
        endMonthRes.add(MyTimeUtil.getLastMonthByMonth(nowMonth.toString()))
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, endMonthRes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        end_month_spinner.adapter = adapter
    }

    /**
     * 初始化详细数据区内容
     */
    private fun initDetailBox() {
        layoutInflater.inflate(R.layout.attendance_recording_table_right_title, right_title)
        content_horsv.setScrollView(title_horsv)
        title_horsv.setScrollView(content_horsv)
        left_list.layoutManager = MyLinearlayoutManager(this, LinearLayoutManager.VERTICAL, false)
        left_list.adapter = leftAdapter
        left_list.isNestedScrollingEnabled = false
        right_list.layoutManager = MyLinearlayoutManager(this, LinearLayoutManager.VERTICAL, false)
        right_list.adapter = rightAdapter
        right_list.isNestedScrollingEnabled = false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun initClick() {
        end_month_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position==0){
                    begin_month.text=endMonthRes[1]
                }else if (position==1){
                    val month=MyTimeUtil.getLastMonthByMonth(endMonthRes[1])
                    begin_month.text=month
                }
                changeWorkHoursEdit()
            }
        }
        recording_search.setOnClickListener {
            changeWorkHoursEdit()
            if (end_day.text.toString().isEmpty() || begin_day.text.toString().isEmpty() || work_hours.text.toString().isEmpty()) {
                showPrompt(getString(R.string.please_edit_value))
            } else {
                presenter.getRecordingData()
            }
        }
        loading.setOnClickListener {
            showPrompt(getString(R.string.wait_loading))
        }
    }

    override fun initData() {
        presenter.getWorkHours()
    }

    private fun changeWorkHoursEdit() {
        if (workHoursList.isNotEmpty()) {
            val yM = end_year.text.toString() + MyTimeUtil.isAddZero(end_month_spinner.selectedItem.toString().toInt())
            workHoursList.filter { it.ym == yM }.forEach {
                work_hours.setText(it.hours.toString())
            }
        }
    }

    override fun <T> requestSuccess(rData: T) {
        workHoursList.addAll(rData as ArrayList<WorkHoursBean>)
        changeWorkHoursEdit()
    }

    override fun <T> errorDealWith(eData: T) {
        loading.loading_progress.visibility = View.GONE
        loading.loading_text.visibility = View.GONE
        loading.loading_retry.visibility = View.VISIBLE
        eData as Int
        when (eData) {
            0 -> {
                //得到工时时错误
                loading.loading_retry.setOnClickListener {
                    loading.loading_progress.visibility = View.VISIBLE
                    loading.loading_text.visibility = View.VISIBLE
                    loading.loading_retry.visibility = View.GONE
                    presenter.getWorkHours()
                }
            }
            1 -> {
                //得到考勤数据时错误
                loading.loading_retry.setOnClickListener {
                    loading.loading_progress.visibility = View.VISIBLE
                    loading.loading_text.visibility = View.VISIBLE
                    loading.loading_retry.visibility = View.GONE
                    presenter.getRecordingData()
                }
            }
        }
    }

    override fun <T> showView(aData: T) {
        aData as ArrayList<AttendanceRecordingBean>
        aData.sortBy { it.uId }
        leftAdapter.setData(aData)
        rightAdapter.setData(aData)
    }

    //返回myActivity对象
    override fun getData1(): Any? {
        return this
    }

    //返回结束时间的yyyyMM格式日期
    override fun getData2(): Any? {
        val month = MyTimeUtil.isAddZero(end_month_spinner.selectedItem.toString().toInt())
        return end_year.text.toString() + month
    }

    //返回开始日期 yyyy-MM-dd
    override fun getData3(): Any? {
        val month = MyTimeUtil.isAddZero(begin_month.text.toString().toInt())
        val day = MyTimeUtil.isAddZero(begin_day.text.toString().toInt())
        return "${begin_year.text}-$month-$day"
    }

    //返回结束日期 yyyy-MM-dd
    override fun getData4(): Any? {
        val month = MyTimeUtil.isAddZero(end_month_spinner.selectedItem.toString().toInt())
        val day = MyTimeUtil.isAddZero(end_day.text.toString().toInt())
        return "${begin_year.text}-$month-$day"
    }

    //返回工时，如果有工时数据就用工时数据，否则就用用户输入的
    override fun getData5(): Any? {
        val workHours = workHoursList.filter { it.ym == getData2() }
        return if (workHours.isNotEmpty()) {
            work_hours.setText(workHours[0].hours.toString())
            workHours[0].hours
        } else {
            val editWorkHours = work_hours.text.toString()
            if (editWorkHours.isNotEmpty()) {
                editWorkHours
            } else {
                null
            }
        }
    }

    override fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loading.visibility = View.GONE
    }
}