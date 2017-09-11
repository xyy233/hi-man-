package com.cstore.zhiyazhang.cstoremanagement.view.Acceptance

import android.app.DatePickerDialog
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.AcceptanceBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.Acceptance.PurchaseAcceptanceAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.Acceptance.PurchaseAcceptancePresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
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
        MyTimeUtil.setTextViewDate(date_util, MyTimeUtil.nowDate)
        setSupportActionBar(my_toolbar)
        orderRecycler.layoutManager = MyLinearlayoutManager(this@PurchaseAcceptanceActivity, LinearLayout.VERTICAL, false)
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
        date_util.setOnTouchListener{_,event->
            if (event.action== MotionEvent.ACTION_DOWN){
                showDatePickDlg()
                true
            }else false
        }
    }

    override fun initData() {
        presenter.getAcceptanceList(MyTimeUtil.getTextViewDate(date_util))
    }

    override fun <T> requestSuccess(rData: T) {
        rData as ArrayList<AcceptanceBean>
        val adapter=PurchaseAcceptanceAdapter(rData, object : ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                showPrompt(position.toString())
            }

            override fun onItemLongClick(view: View, position: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
        orderRecycler.adapter=adapter
    }

    override fun showLoading() {
        orderpro.visibility = View.VISIBLE
        orderprotext.visibility = View.VISIBLE
        orderLoading.visibility = View.VISIBLE
        orderretry.visibility = View.GONE
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
                val calendar= Calendar.getInstance()
                calendar.timeInMillis=System.currentTimeMillis()
                if (year>calendar.get(Calendar.YEAR)||monthOfYear>calendar.get(Calendar.MONTH)||dayOfMonth>calendar.get(Calendar.DAY_OF_MONTH)){
                    showPrompt("不能选择未来日期")
                    return@run
                }
                val textYear=year.toString()+"年"
                var mm = ""
                if (monthOfYear + 1 < 10) mm = "0${monthOfYear + 1}月"//如果小于十月就代表是个位数要手动加上0
                else mm = (monthOfYear + 1).toString()+"月"
                var dd = ""
                if (dayOfMonth < 10) dd = "0$dayOfMonth"//如果小于十日就代表是个位数要手动加上0
                else dd = dayOfMonth.toString()
                date_util.year.text=textYear
                date_util.month.text=mm
                date_util.day.text=dd
                presenter.getAcceptanceList(MyTimeUtil.getTextViewDate(date_util))
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePickDialog.show()
    }
}