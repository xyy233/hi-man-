package com.cstore.zhiyazhang.cstoremanagement.view.order.returnpurchase

import android.app.DatePickerDialog
import android.content.Intent
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ReturnedPurchaseBean
import com.cstore.zhiyazhang.cstoremanagement.bean.VendorBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.returnpurchase.ReturnPurchaseAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.returnpurchase.ReturnPurchasePresenter
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
import kotlin.collections.ArrayList

/**
 * Created by zhiya.zhang
 * on 2017/11/3 14:30.
 */
class ReturnPurchaseActivity(override val layoutId: Int = R.layout.activity_order) : MyActivity() {
    private val presenter = ReturnPurchasePresenter(this)
    private val calendar = Calendar.getInstance()!!
    //日期选择器
    private var datePickDialog: DatePickerDialog? = null
    private var adapter: ReturnPurchaseAdapter? = null

    override fun initView() {
        my_toolbar.title = getString(R.string.return_purchase)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        date_util.visibility = View.VISIBLE
        MyTimeUtil.setTextViewDate(date_util, CStoreCalendar.getCurrentDate(2))
        setSupportActionBar(my_toolbar)
        orderRecycler.layoutManager = MyLinearlayoutManager(this@ReturnPurchaseActivity, LinearLayout.VERTICAL, false)
        val dividerItemDecoration = MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        dividerItemDecoration.setDivider(R.drawable.divider_bg)
        orderRecycler.addItemDecoration(dividerItemDecoration)
        orderRecycler.itemAnimator = DefaultItemAnimator()

        datePickDialog = DatePickerDialog(this@ReturnPurchaseActivity, DatePickerDialog.OnDateSetListener { _, year, month, day ->
            run {
                val myCalendar = Calendar.getInstance()
                myCalendar.timeInMillis = System.currentTimeMillis()
                if (MyTimeUtil.nowHour >= CStoreCalendar.getChangeTime(2)) {
                    //换日了要加两天，因为用的是订货换日
                    myCalendar.set(Calendar.DATE, myCalendar.get(Calendar.DATE) + 2)
                }

                val m=if (month + 1<10)"0${month + 1}" else (month + 1).toString()
                val d= if (day < 10) "0$day" else day.toString()
                val selectDate = (year.toString() + m + d).toInt()
                val nowDate=MyTimeUtil.getYMDStringByDate3(myCalendar.time).toInt()
                if (selectDate > nowDate) {
                    showPrompt("不能选择未来日期")
                    return@run
                }
                val textYear = year.toString() + "年"
                val mm = m + "月"
                date_util.year.text = textYear
                date_util.month.text = mm
                date_util.day.text = d
                presenter.getReturnPurchaseList(MyTimeUtil.getTextViewDate(date_util))
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
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
            presenter.getReturnPurchaseList(MyTimeUtil.getTextViewDate(date_util))
        }
        date_util.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                datePickDialog!!.show()
                true
            } else false
        }
        orderLoading.setOnClickListener {
            showPrompt(getString(R.string.wait_loading))
        }
    }


    override fun initData() {
        presenter.getReturnPurchaseList(MyTimeUtil.getTextViewDate(date_util))
    }

    override fun showLoading() {
        orderLoading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        orderLoading.visibility = View.GONE
    }

    //已经获得完整的退货单了
    override fun <T> showView(aData: T) {
        aData as ArrayList<ReturnedPurchaseBean>
        if (adapter == null) {
            adapter = ReturnPurchaseAdapter(MyTimeUtil.getTextViewDate(date_util), aData, object : ItemClickListener {

                //预约单按钮
                override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {
                    val intent = Intent(this@ReturnPurchaseActivity, ReturnPurchaseItemActivity::class.java)
                    intent.putExtra("data", adapter!!.data[position])
                    intent.putExtra("date", MyTimeUtil.getTextViewDate(date_util))
                    startActivityForResult(intent, 0)
                }

                //添加按钮
                override fun onItemLongClick(view: RecyclerView.ViewHolder, position: Int) {
                    val i = Intent(this@ReturnPurchaseActivity, ReturnPurchaseCreateActivity::class.java)
                    i.putExtra("date", MyTimeUtil.getTextViewDate(date_util))
                    i.putExtra("type", 0)
                    val vendorData = ArrayList<VendorBean>()
                    adapter!!.data.forEach {
                        vendorData.add(VendorBean(it.vendorId, it.vendorName))
                    }
                    i.putExtra("vendor", vendorData)
                    startActivityForResult(i, 0)
                }
            })
            orderRecycler.adapter = adapter
        } else {
            adapter!!.editDate(MyTimeUtil.getTextViewDate(date_util))
            adapter!!.removeData()
            adapter!!.addItem(aData)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            0 -> {
                //从item页返回来的
                try {
                    val rb = data!!.getSerializableExtra("newData")
                    adapter!!.updateData(rb as ReturnedPurchaseBean)
                } catch (e: Exception) {
                    Log.e("预约退货", e.message.toString())
                    showPrompt(e.message.toString())
                }
            }
            1 -> {
                try {
                    val isNew = data!!.getBooleanExtra("isNew", false)
                    if (isNew) presenter.getReturnPurchaseList(MyTimeUtil.getTextViewDate(date_util))
                } catch (e: Exception) {
                    Log.e("预约退货", e.message.toString())
                    showPrompt(e.message.toString())
                }
            }
        }
    }
}