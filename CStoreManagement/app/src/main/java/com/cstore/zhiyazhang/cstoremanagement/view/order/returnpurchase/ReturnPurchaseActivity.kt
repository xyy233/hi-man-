package com.cstore.zhiyazhang.cstoremanagement.view.order.returnpurchase

import android.app.DatePickerDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.presenter.returnpurchase.ReturnPurchasePresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyDividerItemDecoration
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.layout_date.*
import kotlinx.android.synthetic.main.layout_date.view.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.util.*

/**
 * Created by zhiya.zhang
 * on 2017/11/3 14:30.
 */
class ReturnPurchaseActivity(override val layoutId: Int = R.layout.activity_order) : MyActivity(), GenericView {
    private val presenter = ReturnPurchasePresenter(this)
    private val calendar = Calendar.getInstance()!!
    //日期选择器
    private val datePickDialog = DatePickerDialog(this@ReturnPurchaseActivity, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        run {
            val myCalendar = Calendar.getInstance()
            myCalendar.timeInMillis = System.currentTimeMillis()
            if (MyTimeUtil.nowHour >= CStoreCalendar.getChangeTime(2)) {
                //换日了要加两天，因为用的是订货换日
                myCalendar.set(Calendar.DATE, myCalendar.get(Calendar.DATE) + 2)
            }

            val selectDate=(year.toString()+monthOfYear.toString()+dayOfMonth.toString()).toInt()
            val nowDate=(myCalendar.get(Calendar.YEAR).toString()+myCalendar.get(Calendar.MONTH).toString()+myCalendar.get(Calendar.DAY_OF_MONTH).toString()).toInt()
            if (selectDate>nowDate){
                showPrompt("不能选择未来日期")
                return@run
            }
            val textYear = year.toString() + "年"
            val mm = if (monthOfYear + 1 < 10) "0${monthOfYear + 1}月"//如果小于十月就代表是个位数要手动加上0
            else (monthOfYear + 1).toString() + "月"
            val dd = if (dayOfMonth < 10) "0$dayOfMonth"//如果小于十日就代表是个位数要手动加上0
            else dayOfMonth.toString()
            date_util.year.text = textYear
            date_util.month.text = mm
            date_util.day.text = dd
            presenter.getReturnPurchaseList(MyTimeUtil.getTextViewDate(date_util))
            orderRecycler.adapter = null
        }
    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

    override fun initView() {
        my_toolbar.title=getString(R.string.return_purchase)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        date_util.visibility= View.VISIBLE
        MyTimeUtil.setTextViewDate(date_util,CStoreCalendar.getCurrentDate(2))
        setSupportActionBar(my_toolbar)
        orderRecycler.layoutManager=MyLinearlayoutManager(this@ReturnPurchaseActivity,LinearLayout.VERTICAL,false)
        val dividerItemDecoration= MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL)
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
            orderpro.visibility=View.VISIBLE
            orderprotext.visibility=View.VISIBLE
            orderretry.visibility=View.GONE
            presenter.getReturnPurchaseList(MyTimeUtil.getTextViewDate(date_util))
        }
        date_util.setOnTouchListener { _, event ->
            if (event.action== MotionEvent.ACTION_DOWN){
                datePickDialog.show()
                true
            }else false
        }
        orderLoading.setOnClickListener {
            showPrompt(getString(R.string.wait_loading))
        }
    }

    //获得数据在start那里，因为可能保存后退回进入，不用管直接重新获得
    override fun initData() {}

    override fun onStart() {
        super.onStart()
        presenter.getReturnPurchaseList(MyTimeUtil.getTextViewDate(date_util))
    }

    override fun showLoading() {
        orderLoading.visibility=View.VISIBLE
    }

    override fun hideLoading() {
        orderLoading.visibility=View.GONE
    }
}