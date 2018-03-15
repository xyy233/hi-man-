package com.cstore.zhiyazhang.cstoremanagement.view.attendance

import android.app.DatePickerDialog
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
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
import java.util.*

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
    private lateinit var beginDialog: DatePickerDialog
    private lateinit var endDialog: DatePickerDialog

    override fun initView() {
        my_toolbar.title = getString(R.string.attendance_record)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
//        begin_year.text = nowYear.toString()
//        begin_month.text = (nowMonth - 1).toString()
        end_year.text = nowYear.toString()
        end_month.text = if (nowMonth < 10) {
            "0$nowMonth"
        } else {
            nowMonth.toString()
        }
        //这个方法里的month是计算机的month，所以要在正常month上-1
        afterChangeEndDate(nowYear, nowMonth-1, end_day.text.toString().toInt())
        initDatePickerDialog()
        initEdit()
        initDetailBox()
    }

    private val beginDialogListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        val m = if (month + 1 < 10) {
            "0${month + 1}"
        } else {
            (month + 1).toString()
        }
        val d = if (dayOfMonth < 10) {
            "0$dayOfMonth"
        } else {
            dayOfMonth.toString()
        }
        val beginDate = "$year$m$d".toInt()
        val endDate = "${end_year.text}${end_month.text}${end_day.text}".toInt()
        if (beginDate > endDate) {
            showPrompt("结束日期不能大于开始日期")
            return@OnDateSetListener
        }
        begin_year.text = year.toString()
        begin_month.text = m
        begin_day.text = d
        beginDialog.cancel()
    }

    private val endDialogListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        val m = if (month + 1 < 10) {
            "0${month + 1}"
        } else {
            (month + 1).toString()
        }
        val d = if (dayOfMonth < 10) {
            "0$dayOfMonth"
        } else {
            dayOfMonth.toString()
        }
        end_year.text = year.toString()
        end_month.text = m
        end_day.text = d
        afterChangeEndDate(year, month, dayOfMonth)
        endDialog.cancel()
    }

    @Suppress("DEPRECATION")
    private fun initDatePickerDialog() {
        val calendar = Calendar.getInstance()
        beginDialog = object : DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT,
                beginDialogListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)) {
            override fun onDateChanged(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                super.onDateChanged(view, year, month, dayOfMonth)
                setTitle("设置开始日期")
            }

            override fun onStop() {}
        }
        endDialog = object : DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT,
                endDialogListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)) {
            override fun onDateChanged(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                super.onDateChanged(view, year, month, dayOfMonth)
                setTitle("设置结束日期")
            }

            override fun onStop() {}
        }
        val nowDate = Date(System.currentTimeMillis())
        //只能查到前六个月的
        val minDate = MyTimeUtil.getAddDate("month", nowDate, -5)
        val maxDate = MyTimeUtil.getMaxDateByNowMonth(nowDate)
        beginDialog.datePicker.minDate = minDate.time
        beginDialog.datePicker.maxDate = maxDate.time
        endDialog.datePicker.minDate = minDate.time
        endDialog.datePicker.maxDate = maxDate.time
    }

    /**
     * 修改结束日期后要做的事
     * 1.修改开始日期为上一个月
     * 2.修改工时
     */
    private fun afterChangeEndDate(year: Int, month: Int, day: Int) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, day)
        cal.add(Calendar.MONTH, -1)
        val cY = cal.get(Calendar.YEAR)
        val cM = cal.get(Calendar.MONTH)
        begin_year.text = cY.toString()
        begin_month.text = if (cM + 1 < 10) {
            "0${cM + 1}"
        } else {
            (cM + 1).toString()
        }
        //修改工时
        changeWorkHoursEdit()
    }

    private fun changeWorkHoursEdit() {
        if (workHoursList.isNotEmpty()) {
            val yM = end_year.text.toString() + MyTimeUtil.isAddZero(end_month.text.toString().toInt())
            workHoursList.filter { it.ym == yM }.forEach {
                work_hours.setText(it.hours.toString())
            }
        }
    }

    /**
     * 初始化EditText
     */
    private fun initEdit() {
        work_hours.inputType = InputType.TYPE_CLASS_NUMBER
        work_hours.keyListener = DigitsKeyListener.getInstance("1234567890.")
        work_hours.setSelection(work_hours.text.length)
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
        /*end_month_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    begin_month.text = endMonthRes[1]
                } else if (position == 1) {
                    val month = MyTimeUtil.getLastMonthByMonth(endMonthRes[1])
                    begin_month.text = month
                }
                changeWorkHoursEdit()
            }
        }*/
        begin_date.setOnClickListener {
            beginDialog.datePicker.init(begin_year.text.toString().toInt(), begin_month.text.toString().toInt() - 1, begin_day.text.toString().toInt(), beginDialog)
            beginDialog.show()
        }
        end_date.setOnClickListener {
            endDialog.datePicker.init(end_year.text.toString().toInt(), end_month.text.toString().toInt() - 1, end_day.text.toString().toInt(), endDialog)
            endDialog.show()
        }
        recording_search.setOnClickListener {
            if (end_day.text.toString().isEmpty() || begin_day.text.toString().isEmpty() || work_hours.text.toString().isEmpty()) {
                showPrompt(getString(R.string.please_edit_value))
            } else {
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(work_hours.windowToken, 0)
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
        val month = MyTimeUtil.isAddZero(end_month.text.toString().toInt())
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
        val month = MyTimeUtil.isAddZero(end_month.text.toString().toInt())
        val day = MyTimeUtil.isAddZero(end_day.text.toString().toInt())
        return "${begin_year.text}-$month-$day"
    }

    //返回工时，如果有工时数据就用工时数据，否则就用用户输入的
    override fun getData5(): Any? {
        val editWorkHours = work_hours.text.toString()
        return if (editWorkHours.isNotEmpty()) {
            editWorkHours
        } else {
            null
        }
    }

    override fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loading.visibility = View.GONE
    }
}