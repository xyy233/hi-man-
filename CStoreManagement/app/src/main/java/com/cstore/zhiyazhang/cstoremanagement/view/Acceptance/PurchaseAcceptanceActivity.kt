package com.cstore.zhiyazhang.cstoremanagement.view.acceptance

import android.app.DatePickerDialog
import android.content.Intent
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.AcceptanceBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ReturnAcceptanceBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.acceptance.PurchaseAcceptanceAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.acceptance.PurchaseAcceptancePresenter
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
 * on 2017/9/11 16:42.
 * order页布局简单刚好用于此处
 */
class PurchaseAcceptanceActivity(override val layoutId: Int = R.layout.activity_order) : MyActivity() {
    private val presenter = PurchaseAcceptancePresenter(this, this)
    private var type = 1

    override fun initView() {
        type = intent.getIntExtra("type", 1)
        my_toolbar.title = if (type == 1) getString(R.string.purchase_acceptance) else getString(R.string.return_purchase_acceptance)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        date_util.visibility = View.VISIBLE
        //不用管换不换日直接从换日表拿时间
        MyTimeUtil.setTextViewDate(date_util, CStoreCalendar.getCurrentDate(3))
        setSupportActionBar(my_toolbar)
        orderRecycler.layoutManager = MyLinearlayoutManager(this@PurchaseAcceptanceActivity, LinearLayout.VERTICAL, false)
        val dividerItemDecoration = MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        dividerItemDecoration.setDivider(R.drawable.divider_bg)
        orderRecycler.addItemDecoration(dividerItemDecoration)
        orderRecycler.itemAnimator = DefaultItemAnimator()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun initClick() {
        orderretry.setOnClickListener {
            orderpro.visibility = View.VISIBLE
            orderprotext.visibility = View.VISIBLE
            orderretry.visibility = View.GONE
            if (type == 1) presenter.getAcceptanceList(MyTimeUtil.getTextViewDate(date_util))
            else presenter.getReturnAcceptanceList(MyTimeUtil.getTextViewDate(date_util))
        }
        date_util.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                showDatePickDlg()
                true
            } else false
        }
        orderLoading.setOnClickListener {
            showPrompt(getString(R.string.wait_loading))
        }
    }

    //获得数据在start那里，因为可能保存后退回进入，不用管直接重新获得
    override fun initData() {}

    override fun onStart() {
        super.onStart()
        if (type == 1) presenter.getAcceptanceList(MyTimeUtil.getTextViewDate(date_util))
        else presenter.getReturnAcceptanceList(MyTimeUtil.getTextViewDate(date_util))
    }

    override fun <T> requestSuccess(rData: T) {

        val adapter = PurchaseAcceptanceAdapter(type, MyTimeUtil.getTextViewDate(date_util),
                if (type == 1) {
                    rData as ArrayList<AcceptanceBean>
                } else {
                    rData as ArrayList<ReturnAcceptanceBean>
                }, object : ItemClickListener {
            override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {
                val i = Intent(this@PurchaseAcceptanceActivity, PurchaseAcceptanceItemActivity::class.java)
                i.putExtra("date", MyTimeUtil.getTextViewDate(date_util))
                i.putExtra("type", type)
                i.putExtra("data",
                        if (type == 1) {
                            rData as ArrayList<AcceptanceBean>
                            rData[position]
                        } else {
                            rData as ArrayList<ReturnAcceptanceBean>
                            rData[position]
                        })
                startActivity(i)
            }

            override fun onItemLongClick(view: RecyclerView.ViewHolder, position: Int) {
                val i = Intent(this@PurchaseAcceptanceActivity, PurchaseAcceptanceCreate::class.java)
                i.putExtra("type", type)
                i.putExtra("date", MyTimeUtil.getTextViewDate(date_util))
                i.putExtra("isAction", 1)
                startActivity(i)
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
        val datePickDialog = DatePickerDialog(this@PurchaseAcceptanceActivity, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            run {
                val myCalendar = Calendar.getInstance()
                myCalendar.timeInMillis = System.currentTimeMillis()
                if (MyTimeUtil.nowHour >= CStoreCalendar.getChangeTime(3)) {
                    //换日了要加一天
                    myCalendar.set(Calendar.DATE, myCalendar.get(Calendar.DATE) + 1)
                }
                val m = if (monthOfYear + 1 < 10) "0${monthOfYear + 1}" else (monthOfYear + 1).toString()
                val d = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                val selectDate = (year.toString() + m + d).toInt()
                val nowDate = MyTimeUtil.getYMDStringByDate3(myCalendar.time).toInt()
                if (selectDate > nowDate) {
                    showPrompt("不能选择未来日期")
                    return@run
                }
                val textYear = year.toString() + "年"
                val mm = m + "月"
                date_util.year.text = textYear
                date_util.month.text = mm
                date_util.day.text = d
                if (type == 1) presenter.getAcceptanceList(MyTimeUtil.getTextViewDate(date_util))
                else presenter.getReturnAcceptanceList(MyTimeUtil.getTextViewDate(date_util))
                orderRecycler.adapter = null
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePickDialog.show()
    }
}