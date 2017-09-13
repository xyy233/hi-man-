package com.cstore.zhiyazhang.cstoremanagement.view.acceptance

import android.app.DatePickerDialog
import android.content.Intent
import android.support.v7.widget.DefaultItemAnimator
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.AcceptanceBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.acceptance.PurchaseAcceptanceAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.acceptance.PurchaseAcceptancePresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.DividerItemDecoration
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.layout_date.*
import kotlinx.android.synthetic.main.layout_date.view.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.util.*

/**
 * Created by zhiya.zhang
 * on 2017/9/11 16:42.
 * order页布局简单刚好用于此处
 */
class PurchaseAcceptanceActivity(override val layoutId: Int = R.layout.activity_order) : MyActivity(), GenericView {
    private val presenter = PurchaseAcceptancePresenter(this, this, this)

    override fun initView() {
        my_toolbar.title = getString(R.string.purchase_acceptance)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        date_util.visibility = View.VISIBLE
        //不用管换不换日直接从换日表拿时间
        MyTimeUtil.setTextViewDate(date_util, CStoreCalendar.getCurrentDate(3))
        setSupportActionBar(my_toolbar)
        orderRecycler.layoutManager = MyLinearlayoutManager(this@PurchaseAcceptanceActivity, LinearLayout.VERTICAL, false)
        val dividerItemDecoration=DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST)
        dividerItemDecoration.setDivider(R.drawable.divider_bg)
        orderRecycler.addItemDecoration(dividerItemDecoration)
        orderRecycler.itemAnimator= DefaultItemAnimator()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun initClick() {
        orderretry.setOnClickListener {
            presenter.getAcceptanceList(MyTimeUtil.getTextViewDate(date_util))
        }
        date_util.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                showDatePickDlg()
                true
            } else false
        }
    }

    //获得数据在start那里，因为可能保存后退回进入，不用管直接重新获得
    override fun initData() {}

    override fun onStart() {
        super.onStart()
        presenter.getAcceptanceList(MyTimeUtil.getTextViewDate(date_util))
    }

    override fun <T> requestSuccess(rData: T) {
        rData as ArrayList<AcceptanceBean>
        val adapter = PurchaseAcceptanceAdapter(MyTimeUtil.getTextViewDate(date_util),rData, object : ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val i = Intent(this@PurchaseAcceptanceActivity, PurchaseAcceptanceItemActivity::class.java)
                i.putExtra("date", MyTimeUtil.getTextViewDate(date_util))
                i.putExtra("data", rData[position])
                startActivity(i)
            }

            override fun onItemLongClick(view: View, position: Int) {
                showPrompt("创建一个新的")
            }
        })
        orderRecycler.adapter = adapter
    }

    override fun showLoading() {
        orderLoading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        super.hideLoading()
        orderLoading.visibility = View.GONE
    }

    override fun <T> showView(aData: T) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun errorDealWith() {
        orderpro.visibility = View.GONE
        orderprotext.visibility = View.GONE
        orderretry.visibility = View.VISIBLE
    }

    /**
     * 得到选中日期
     */
    private fun showDatePickDlg() {
        val calendar = Calendar.getInstance()
        val datePickDialog: DatePickerDialog = DatePickerDialog(this@PurchaseAcceptanceActivity, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            run {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = System.currentTimeMillis()
                if (MyTimeUtil.nowHour >= CStoreCalendar.getChangeTime(3)) {
                    //换日了要加一天
                    calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1)
                }
                if (year > calendar.get(Calendar.YEAR) || monthOfYear > calendar.get(Calendar.MONTH) || dayOfMonth > calendar.get(Calendar.DAY_OF_MONTH)) {
                    showPrompt("不能选择未来日期")
                    return@run
                }
                val textYear = year.toString() + "年"
                var mm = ""
                if (monthOfYear + 1 < 10) mm = "0${monthOfYear + 1}月"//如果小于十月就代表是个位数要手动加上0
                else mm = (monthOfYear + 1).toString() + "月"
                var dd = ""
                if (dayOfMonth < 10) dd = "0$dayOfMonth"//如果小于十日就代表是个位数要手动加上0
                else dd = dayOfMonth.toString()
                date_util.year.text = textYear
                date_util.month.text = mm
                date_util.day.text = dd
                presenter.getAcceptanceList(MyTimeUtil.getTextViewDate(date_util))
                orderRecycler.adapter = null
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePickDialog.show()
    }
}