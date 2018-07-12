package com.cstore.zhiyazhang.cstoremanagement.view.attendance

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

    override fun initView() {
        my_toolbar.title = getString(R.string.attendance_record)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)

        val c = Calendar.getInstance(Locale.CHINA)
        begin_date.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 26, beginDialogListener)
        end_date.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 25, endDialogListener)

        val maxDate = MyTimeUtil.getMaxDateByNowMonth(Date(System.currentTimeMillis()))
        begin_date.maxDate = maxDate.time
        end_date.maxDate = maxDate.time

        //这个方法里的month是计算机的month，所以要在正常month上-1
        afterChangeEndDate(nowYear, nowMonth - 1, "26".toInt())
        initEdit()
        initDetailBox()
    }

    private val beginDialogListener = DatePicker.OnDateChangedListener { _, _, _, _ ->
    }

    /**
     * 根据type返回view对应的数据
     * @param type y = year m = month d = day
     */
    private fun getDateText(view: DatePicker, type: String): String {
        when (type) {
            "y" -> {
                return view.year.toString()
            }
            "m" -> {
                val m = view.month
                return if (m + 1 < 10) {
                    "0${m + 1}"
                } else {
                    (m + 1).toString()
                }
            }
            "d" -> {
                val d = view.dayOfMonth
                return if (d < 10) {
                    "0$d"
                } else {
                    d.toString()
                }
            }
            else -> {
                return ""
            }
        }
    }

    private val endDialogListener = DatePicker.OnDateChangedListener { _, year, month, dayOfMonth ->
        afterChangeEndDate(year, month, dayOfMonth)
    }

    /**
     * 修改结束日期后要做的事
     * 1.修改开始日期为上一个月
     * 2.修改工时
     */
    private fun afterChangeEndDate(year: Int, month: Int, day: Int) {
        val cal = Calendar.getInstance(Locale.CHINA)
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, day)
        val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        cal.add(Calendar.MONTH, -1)
        val cY = cal.get(Calendar.YEAR)
        var cM = cal.get(Calendar.MONTH)
        val cD = when (day) {
            maxDay -> {
                cM = month
                1
            }
            25 -> 26
            else -> begin_date.dayOfMonth
        }
        begin_date.updateDate(cY, cM, cD)
        //修改工时
        changeWorkHoursEdit()
    }

    private fun changeWorkHoursEdit() {
        if (workHoursList.isNotEmpty()) {
            val yM = getDateText(end_date, "y") + MyTimeUtil.isAddZero(getDateText(end_date, "m").toInt())
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
        recording_search.setOnClickListener {
            val endDate = "${getDateText(end_date, "y")}${getDateText(end_date, "m")}${getDateText(end_date, "d")}".toInt()
            val beginDate = "${getDateText(begin_date, "y")}${getDateText(begin_date, "m")}${getDateText(begin_date, "d")}".toInt()
            if (endDate < beginDate) {
                showPrompt("开始日期不能大于结束日期")
                return@setOnClickListener
            }
            if (work_hours.text.toString().isEmpty()) {
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

        return getDateText(end_date, "y") + getDateText(end_date, "m")
    }

    //返回开始日期 yyyy-MM-dd
    override fun getData3(): Any? {
        return "${getDateText(begin_date, "y")}-${getDateText(begin_date, "m")}-${getDateText(begin_date, "d")}"
    }

    //返回结束日期 yyyy-MM-dd
    override fun getData4(): Any? {
        return "${getDateText(end_date, "y")}-${getDateText(end_date, "m")}-${getDateText(end_date, "d")}"
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