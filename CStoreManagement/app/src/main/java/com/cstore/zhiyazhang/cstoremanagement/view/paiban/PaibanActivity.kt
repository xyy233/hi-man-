package com.cstore.zhiyazhang.cstoremanagement.view.paiban

import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.SortPaiban
import com.cstore.zhiyazhang.cstoremanagement.presenter.paiban.PaibanAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.paiban.PaibanPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil.dateAddDay
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil.getWeekSundayByDate
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.activity_paiban.*
import kotlinx.android.synthetic.main.dialog_paiban.view.*
import kotlinx.android.synthetic.main.loading_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*

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

    /**
     * 弹出保存排版器
     * @param data 排班bean
     * @param day 从0开始,确认当前选中哪天为当前spinner日期+day日期
     */
    fun onclickDate(data: SortPaiban, day: Int) {
        //当前选择的日期
        val selectDate = dateAddDay(paiban_spinner.selectedItem.toString(), day)
        //夜班选择能跨一天
        val selectDate2 = dateAddDay(paiban_spinner.selectedItem.toString(), day + 1)
        val title = data.data[0].employeeName + "  " + selectDate
        dialogView.dialog_title.text = title

        val paibans = data.data.filter { it.systemDate == selectDate }
        val selectDateList = ArrayList<String>()
        selectDateList.add(selectDate)
        selectDateList.add(selectDate2)
        //日期选择spinner初始化
        val beginAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, selectDateList)
        beginAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogView.begin_spinner.adapter = beginAdapter
        val endAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, selectDateList)
        endAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogView.end_spinner.adapter = endAdapter

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
        } else {
            //无值
        }
        dialog.show()
    }

    override fun errorDealWith() {
        loading_retry.visibility = View.VISIBLE
        loading_text.visibility = View.GONE
        loading_progress.visibility = View.GONE
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

    //修改数据
    override fun getData3(): Any? {
        return super.getData3()
    }

    override fun getData4(): Any? {
        return getWeekSundayByDate(getData2() as String)
    }

}