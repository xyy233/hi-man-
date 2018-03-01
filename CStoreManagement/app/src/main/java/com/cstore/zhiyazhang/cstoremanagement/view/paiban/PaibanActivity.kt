package com.cstore.zhiyazhang.cstoremanagement.view.paiban

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.PaibanBean
import com.cstore.zhiyazhang.cstoremanagement.bean.SortPaiban
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.presenter.paiban.PaibanAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.paiban.PaibanPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil.dateAddDay
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil.getDayByDate
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil.getHourByDate
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil.getHourPoor
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil.getWeekSundayByDate
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.activity_paiban.*
import kotlinx.android.synthetic.main.dialog_paiban.view.*
import kotlinx.android.synthetic.main.loading_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.util.*

/**
 * Created by zhiya.zhang
 * on 2018/2/8 14:39.
 */
class PaibanActivity(override val layoutId: Int = R.layout.activity_paiban) : MyActivity() {
    private val presenter = PaibanPresenter(this)
    private val dateList = ArrayList<String>()
    private lateinit var adapter: PaibanAdapter
    private lateinit var dialog: AlertDialog
    private lateinit var dialogView: View
    private var editData: PaibanBean? = null

    override fun initView() {
        my_toolbar.title = getString(R.string.paiban)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        paiban_recycler.layoutManager = MyLinearlayoutManager(this, LinearLayoutManager.VERTICAL, false)
        getWeek()
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dateList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        paiban_spinner.adapter = adapter
        initDialog()
    }

    private fun initDialog() {
        val builder = AlertDialog.Builder(this)
        dialogView = View.inflate(this, R.layout.dialog_paiban, null)
        builder.setView(dialogView)
        builder.setCancelable(true)
        dialog = builder.create()
        dialogView.dialog_cancel.setOnClickListener {
            dialog.cancel()
        }
        dialogView.drdy.inputType = InputType.TYPE_CLASS_NUMBER
        dialogView.drdy.keyListener = DigitsKeyListener.getInstance("1234567890")
        dialogView.drdy.setSelection(dialogView.drdy.text.length)
    }

    private fun getWeek() {
        //得到从上周开始到上四周的日期
        (1..4).mapTo(dateList) { MyTimeUtil.getWeekMondayDate(it * -1) }
        //循环得到从这周开始到第八周的日期
        (0..7).mapTo(dateList) { MyTimeUtil.getWeekMondayDate(it) }
        dateList.sort()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun initClick() {
        loading.setOnClickListener {
            showPrompt(getString(R.string.wait_loading))
        }
        loading_retry.setOnClickListener {
            loading_retry.visibility = View.GONE
            loading_text.visibility = View.VISIBLE
            loading_progress.visibility = View.VISIBLE
            presenter.getDataByDate()
        }
        paiban_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                presenter.getDataByDate()
            }

        }
    }

    override fun initData() {
        //presenter.getDataByDate()
    }

    override fun <T> showView(aData: T) {
        aData as ArrayList<SortPaiban>
        adapter = PaibanAdapter(aData, object : ItemClickListener {
            override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {
            }

            //data为排班bean，position为周几，从0开始,确认当前选中哪天为当前spinner日期+position日期
            override fun <T> onItemEdit(data: T, position: Int) {
                onclickDate(data as SortPaiban, position)
            }
        })
        paiban_recycler.adapter = adapter
    }

    @SuppressLint("SetTextI18n")
            /**
             * 弹出保存排版器
             * @param data 排班bean
             * @param day 从0开始,确认当前选中哪天为当前spinner日期+day日期
             */
    fun onclickDate(data: SortPaiban, day: Int) {
        var isEdit: Boolean = false
        //当前选择的日期
        val selectDate = dateAddDay(paiban_spinner.selectedItem.toString(), day)
        //夜班选择能跨一天
        val selectDate2 = dateAddDay(paiban_spinner.selectedItem.toString(), day + 1)
        val title = data.data[0].employeeName + "  " + selectDate + "  " +
                when (day) {
                    0 -> "周一"
                    1 -> "周二"
                    2 -> "周三"
                    3 -> "周四"
                    4 -> "周五"
                    5 -> "周六"
                    6 -> "周末"
                    else -> "未知"
                }
        dialogView.dialog_title.text = title

        val paibans = data.data.filter { it.systemDate != null && MyTimeUtil.deleteTime(it.systemDate) == selectDate }
        val selectDateList = ArrayList<String>()
        selectDateList.add(selectDate)
        selectDateList.add(selectDate2)
        //日期选择spinner初始化
        val beginAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, selectDateList)
        beginAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogView.begin_spinner.adapter = beginAdapter
        //开始日期不允许操作！只能是选择当天
        dialogView.begin_spinner.isEnabled = false
        val endAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, selectDateList)
        endAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogView.end_spinner.adapter = endAdapter

        /**
         * 如果是之前时间就不允许操作
         */
        if (paiban_spinner.selectedItemPosition < 4 || !isEnabled(selectDate)) {
            isEdit = false
            dialogView.end_spinner.isEnabled = false
            dialogView.drdy.isEnabled = false
            dialogView.dialog_deletge.visibility = View.GONE
            dialogView.dialog_save.visibility = View.GONE
        } else {
            isEdit = true
            dialogView.end_spinner.isEnabled = true
            dialogView.drdy.isEnabled = true
            dialogView.dialog_deletge.visibility = View.VISIBLE
            dialogView.dialog_save.visibility = View.VISIBLE
        }

        if (paibans.isNotEmpty()) {
            //有值
            val paiban = paibans[0]
            //设置spinner当前显示的值，如果选择的日期不等于开始、结束时间就代表日期为第二天，按照添加顺序第二天是第二条
            if (selectDate != MyTimeUtil.deleteTime(paiban.beginDateTime!!)) {
                dialogView.begin_spinner.setSelection(1)
            }
            if (selectDate != MyTimeUtil.deleteTime(paiban.endDateTime!!)) {
                dialogView.end_spinner.setSelection(1)
            }
            dialogView.begin_time.text = getTime(paiban.beginDateTime!!)
            dialogView.end_time.text = getTime(paiban.endDateTime!!)
            getWorkingHours(paiban.beginDateTime!!, paiban.endDateTime!!, paiban.danrenHr!!)
            dialogView.drdy.setText(paiban.danrenHr!!.toString())
            dialogView.begin_time.setOnClickListener {
                if (isEdit)
                    setTimeDialog(paiban, 0, dialogView.begin_time)
                else
                    showPrompt(getString(R.string.now_time_not_edit))
            }
            dialogView.end_time.setOnClickListener {
                if (isEdit)
                    setTimeDialog(paiban, 1, dialogView.end_time)
                else
                    showPrompt(getString(R.string.now_time_not_edit))
            }
        } else {
            //无值spinner为默认
            dialogView.begin_time.text = "00:00"
            dialogView.end_time.text = "00:00"
            dialogView.crb.text = "0"
            dialogView.begin_time.setOnClickListener {
                if (isEdit)
                    setTimeDialog(null, 0, dialogView.begin_time)
                else
                    showPrompt(getString(R.string.now_time_not_edit))
            }
            dialogView.end_time.setOnClickListener {
                if (isEdit)
                    setTimeDialog(null, 1, dialogView.end_time)
                else
                    showPrompt(getString(R.string.now_time_not_edit))
            }
        }
        dialogView.dialog_deletge.setOnClickListener {
            if (paibans.isEmpty()) {
                showPrompt(getString(R.string.noEditMsg))
                return@setOnClickListener
            }
            editData = paibans[0]
            startDialogLoading()
            presenter.editData(3)
        }
        dialogView.dialog_save.setOnClickListener {
            editData = getCreateData(selectDate, data.data[0].employeeName, data.data[0].employeeId)
            startDialogLoading()
            if (paibans.isEmpty()) {
                presenter.editData(1)
            } else {
                presenter.editData(2)
            }
        }
        dialog.show()
    }

    private fun startDialogLoading() {
        dialogView.dialog_progress.visibility = View.VISIBLE
        dialogView.dialog_cancel.isEnabled = false
        dialogView.dialog_deletge.isEnabled = false
        dialogView.dialog_save.isEnabled = false
    }

    /**
     * 得到创建用数据
     */
    private fun getCreateData(selectDate: String, name: String, id: String): PaibanBean {
        val uId = User.getUser().storeId
        val sysdate = selectDate
        val beginDate = dialogView.begin_spinner.selectedItem.toString() + " " + dialogView.begin_time.text.toString() + ":00"
        val endDate = dialogView.end_spinner.selectedItem.toString() + " " + dialogView.end_time.text.toString() + ":00"
        val drdy = dialogView.drdy.text.toString().toInt()
        return PaibanBean(uId, sysdate, id, name, beginDate, endDate, drdy, null)
    }

    /**
     * 确定是否可以编辑，如果选中时间小于当前时间就不允许
     */
    private fun isEnabled(selectDate: String): Boolean {
        val seDate = MyTimeUtil.getCalendarByString(selectDate)
        val nowDate = MyTimeUtil.getCalendarByString(MyTimeUtil.nowDate)
        return seDate.time > nowDate.time
    }

    /**
     * 设置常日班一般大夜显示
     * 夜班不考虑连跨两个夜班
     */
    private fun getWorkingHours(beginTime: String, endTime: String, danrenHr: Int) {
        //夜班时间
        val nightShift: Int
        //白班时间
        val dayShift: Int
        //得到总工时
        val allHour = getHourPoor(beginTime, endTime)
        if (allHour < 0) return
        //上班日
        val beginDay = getDayByDate(beginTime)
        //下班日
        val endDay = getDayByDate(endTime)
        //上班小时
        val beginHour = getHourByDate(beginTime)
        //下班小时
        val endHour = getHourByDate(endTime)
        //上班时间小于7必有夜班
        if (beginHour < 7) {
            //下班时间大于等于7代表有白班+夜班,夜班等于 7-上班时
            if (endHour > 7) {
                nightShift = 7 - beginHour
                dayShift = allHour - nightShift
            } else {
                //下班时间小于7不是还在今天就是跨天
                if (beginDay == endDay) {
                    //在今天
                    nightShift = allHour
                    dayShift = 0
                } else {
                    //跨天了
                    nightShift = 7
                    dayShift = allHour - nightShift
                }
            }
        } else {
            //下班时间跨天的话必有夜班，否则全白班
            if (beginDay == endDay) {
                //未跨天
                dayShift = allHour
                nightShift = 0
            } else {
                //跨天通过判断下班时间确认是否有白班
                if (endHour > 7) {
                    //下班时间大于7代表有上白班
                    nightShift = 8
                    dayShift = allHour - 8
                } else {
                    //无白班
                    nightShift = allHour
                    dayShift = 0
                }
            }
        }
        dialogView.crb.text = (dayShift - danrenHr).toString()
        dialogView.ybdy.text = nightShift.toString()
    }

    /**
     * 获得时间
     * @return HH:mm格式String
     */
    private fun getTime(dateTime: String): String {
        val c = MyTimeUtil.getCalendarByStringHMS(dateTime)
        val hour = if (c.get(Calendar.HOUR_OF_DAY) < 10) "0${c.get(Calendar.HOUR_OF_DAY)}" else c.get(Calendar.HOUR_OF_DAY).toString()
        val minute = if (c.get(Calendar.MINUTE) < 10) "0${c.get(Calendar.MINUTE)}" else c.get(Calendar.MINUTE).toString()
        return hour + ":" + minute
    }

    /**
     * 设置时间选择器
     * @param type 通过type确认设置什么时间 0=开始时间 1=结束时间
     */
    private fun setTimeDialog(pb: PaibanBean?, type: Int, textView: TextView) {
        if (type == 0) {
            if (pb != null) {
                val c = MyTimeUtil.getCalendarByStringHMS(pb.beginDateTime!!)
                showTimeDialog(c, textView)
            } else {
                showTimeDialog(null, textView)
            }
        } else {
            if (pb != null) {
                val c = MyTimeUtil.getCalendarByStringHMS(pb.endDateTime!!)
                showTimeDialog(c, textView)
            } else {
                showTimeDialog(null, textView)
            }
        }
    }

    /**
     * 显示时间选择器
     * @param c 为空就代表显示默认数据
     */
    private fun showTimeDialog(c: Calendar?, textView: TextView) {
        if (c != null) {
            TimePickerDialog(this@PaibanActivity, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val hour = if (hourOfDay < 10) "0$hourOfDay" else hourOfDay.toString()
                val mMinute = if (minute < 10) "0$minute" else minute.toString()
                val selectTime = hour + ":" + mMinute
                textView.text = selectTime

                val beginText = dialogView.begin_spinner.selectedItem.toString() + " " + dialogView.begin_time.text.toString() + ":00"
                val endText = dialogView.end_spinner.selectedItem.toString() + " " + dialogView.end_time.text.toString() + ":00"
                getWorkingHours(beginText, endText, dialogView.drdy.text.toString().toInt())

            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
        } else {
            TimePickerDialog(this@PaibanActivity, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val hour = if (hourOfDay < 10) "0$hourOfDay" else hourOfDay.toString()
                val mMinute = if (minute < 10) "0$minute" else minute.toString()
                val selectTime = hour + ":" + mMinute
                textView.text = selectTime

                val beginText = dialogView.begin_spinner.selectedItem.toString() + " " + dialogView.begin_time.text.toString() + ":00"
                val endText = dialogView.end_spinner.selectedItem.toString() + " " + dialogView.end_time.text.toString() + ":00"
                getWorkingHours(beginText, endText, dialogView.drdy.text.toString().toInt())

            }, 0, 0, true).show()
        }
    }

    override fun errorDealWith() {
        dialogView.dialog_progress.visibility = View.GONE
        dialogView.dialog_cancel.isEnabled = true
        dialogView.dialog_deletge.isEnabled = true
        dialogView.dialog_save.isEnabled = true
    }

    override fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loading.visibility = View.GONE
    }

    override fun getData1(): Any? {
        return this@PaibanActivity
    }

    //查询时间
    override fun getData2(): Any? {
        return paiban_spinner.selectedItem.toString()
    }

    override fun getData3(): Any? {
        return editData
    }

    override fun getData4(): Any? {
        return getWeekSundayByDate(getData2() as String)
    }

    override fun <T> updateDone(uData: T) {
        errorDealWith()
        dialog.cancel()
        editData = null
        presenter.getDataByDate()
    }


}