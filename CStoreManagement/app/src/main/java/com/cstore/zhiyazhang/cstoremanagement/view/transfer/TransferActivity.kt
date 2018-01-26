package com.cstore.zhiyazhang.cstoremanagement.view.transfer

import android.app.DatePickerDialog
import android.content.Intent
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.TrsBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.transfer.TransferAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.transfer.TransferPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyDividerItemDecoration
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.layout_date.*
import kotlinx.android.synthetic.main.layout_date.view.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.util.*

/**
 * Created by zhiya.zhang
 * on 2018/1/19 15:24.
 */
class TransferActivity(override val layoutId: Int = R.layout.activity_order) : MyActivity() {

    private val presenter = TransferPresenter(this)
    private lateinit var datePickDialog: DatePickerDialog
    private lateinit var adapter: TransferAdapter

    override fun initView() {
        my_toolbar.title = getString(R.string.transfer)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        date_util.visibility = View.VISIBLE
        MyTimeUtil.setTextViewDate(date_util, CStoreCalendar.getCurrentDate(0))
        setSupportActionBar(my_toolbar)
        orderRecycler.layoutManager = MyLinearlayoutManager(this@TransferActivity, LinearLayout.VERTICAL, false)
        val dividerItemDecoration = MyDividerItemDecoration(this@TransferActivity, LinearLayout.VERTICAL)
        dividerItemDecoration.setDivider(R.drawable.divider_bg)
        orderRecycler.addItemDecoration(dividerItemDecoration)
        orderRecycler.itemAnimator = DefaultItemAnimator()
        initDate()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    private fun initDate() {
        val calendar = Calendar.getInstance()!!
        datePickDialog = DatePickerDialog(this@TransferActivity, DatePickerDialog.OnDateSetListener { _, year, month, day ->
            run {
                val calendar1 = Calendar.getInstance()
                calendar1.timeInMillis = System.currentTimeMillis()
                if (MyTimeUtil.nowHour >= CStoreCalendar.getChangeTime(0)) calendar1.set(Calendar.DATE, calendar1.get(Calendar.DATE) + 1)

                val m = if (month + 1 < 10) "0${month + 1}" else (month + 1).toString()
                val d = if (day < 10) "0$day" else day.toString()
                val selectDate = (year.toString() + m + d).toInt()
                val nowDate = MyTimeUtil.getYMDStringByDate3(calendar1.time).toInt()
                if (selectDate > nowDate) {
                    showPrompt("不能选择未来日期")
                    return@run
                }
                val textYear = year.toString() + "年"
                val mm = m + "月"
                date_util.year.text = textYear
                date_util.month.text = mm
                date_util.day.text = d
                presenter.getAllTrs(MyTimeUtil.getTextViewDate(date_util))
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
    }

    override fun initClick() {
        orderretry.setOnClickListener {
            orderpro.visibility = View.VISIBLE
            orderprotext.visibility = View.VISIBLE
            orderretry.visibility = View.GONE
            presenter.getAllTrs(MyTimeUtil.getTextViewDate(date_util))
        }
        date_util.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                datePickDialog.show()
                true
            } else false
        }
        orderLoading.setOnClickListener { showPrompt(getString(R.string.wait_loading)) }
    }

    override fun initData() {
    }

    override fun onStart() {
        super.onStart()
        //为保证获得最新数据放在start中，不使用activityResult
        presenter.getAllTrs(MyTimeUtil.getTextViewDate(date_util))
    }

    override fun showLoading() {
        orderLoading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        orderLoading.visibility = View.GONE
    }

    override fun <T> showView(aData: T) {
        aData as ArrayList<TrsBean>
        adapter = TransferAdapter(MyTimeUtil.getTextViewDate(date_util), aData, object : ItemClickListener {
            override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {
                //添加
                //拿到所有调出门市的数据
                val stores = ArrayList<String>()
                aData.forEach {
                    stores.add(it.trsStoreId)
                }
                val i = Intent(this@TransferActivity, TransferItemActivity::class.java)
                i.putExtra("date", MyTimeUtil.getTextViewDate(date_util))
                i.putExtra("stores", stores)
                startActivity(i)
            }

            override fun <T> onItemEdit(data: T, position: Int) {
                //查看
                if (data is TrsBean) {
                    val i = Intent(this@TransferActivity, TransferItemActivity::class.java)
                    i.putExtra("date", MyTimeUtil.getTextViewDate(date_util))
                    i.putExtra("data", data)
                    startActivity(i)
                }
            }
        })
        orderRecycler.adapter = adapter
    }

}