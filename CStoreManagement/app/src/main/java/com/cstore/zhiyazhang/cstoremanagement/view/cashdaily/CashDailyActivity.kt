package com.cstore.zhiyazhang.cstoremanagement.view.cashdaily

import android.app.DatePickerDialog
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.CashDailyBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.cashdaily.CashDailyPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import kotlinx.android.synthetic.main.activity_cashdaily.*
import kotlinx.android.synthetic.main.dialog_cashdaily.view.*
import kotlinx.android.synthetic.main.loading_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.util.*

/**
 * Created by zhiya.zhang
 * on 2017/9/4 11:29.
 */
class CashDailyActivity(override val layoutId: Int=R.layout.activity_cashdaily) : MyActivity(), GenericView{

    private val tabIndicators = ArrayList<String>()
    private val presenter=CashDailyPresenter(this,this,this)

    var dialog:AlertDialog?=null

    override fun initView() {
        my_toolbar.title=getString(R.string.cash_daily)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        toolbar_time.visibility=View.VISIBLE
        toolbar_time.text=MyTimeUtil.nowDate
        setSupportActionBar(my_toolbar)
        //设置title
        resources.getStringArray(R.array.cashDailyTags).forEach { tabIndicators.add(it) }
        //设置tab和viewPager联动
        cash_daily_tab.setupWithViewPager(cash_daily_viewpager)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->onBackPressed()
        }
        return true
    }

    override fun initClick() {
        loading.setOnClickListener {
            showPrompt(getString(R.string.with_loading))
        }
        toolbar_time.setOnTouchListener{_,event->
            if (event.action==MotionEvent.ACTION_DOWN){
                showDatePickDlg()
                true
            }else false
        }
        loading_retry.setOnClickListener {
            loading_retry.visibility=View.GONE
            loading_text.visibility=View.VISIBLE
            loading_progress.visibility=View.VISIBLE
            presenter.getAllCashDaily(toolbar_time.text.toString())
        }
    }

    override fun initData() {
        presenter.getAllCashDaily(toolbar_time.text.toString())
    }

    var dialogView:View?= null
    fun updateData(view:View, cd:CashDailyBean){
        if (dialog==null){
            val builder=AlertDialog.Builder(this@CashDailyActivity)
            dialogView = View.inflate(this,R.layout.dialog_cashdaily,null)!!
            builder.setView(dialogView)
            builder.setCancelable(true)
            dialog=builder.create()
            //设置输入类型
            dialogView!!.dialog_edit.inputType=InputType.TYPE_CLASS_NUMBER
            dialogView!!.dialog_edit.keyListener=DigitsKeyListener.getInstance("1234567890.")

            dialogView!!.dialog_save.setOnClickListener {
                //去掉空格
                val value=dialogView!!.dialog_edit.text.toString().replace(" ","")

                if (value=="")showPrompt(getString(R.string.please_edit_value))//不能为空
                else if (value==cd.cdValue){
                    //和之前一样就直接发个消息说完成
                    showPrompt(getString(R.string.saveDone))
                    dialog!!.cancel()
                }
                else {
                    presenter.updateCashDaily(view,cd,value)
                }
            }
            dialogView!!.dialog_cancel.setOnClickListener {
                dialog!!.cancel()
            }
        }
        dialog!!.show()
    }

    /**
     * 得到选中日期
     */
    private fun showDatePickDlg() {
        val calendar = Calendar.getInstance()
        val datePickDialog: DatePickerDialog = DatePickerDialog(this@CashDailyActivity, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            run {
                var mm = ""
                if (monthOfYear + 1 < 10) mm = "0${monthOfYear + 1}"//如果小于十月就代表是个位数要手动加上0
                else mm = (monthOfYear + 1).toString()
                var dd = ""
                if (dayOfMonth < 10) dd = "0$dayOfMonth"//如果小于十日就代表是个位数要手动加上0
                else dd = dayOfMonth.toString()
                val time = "$year-$mm-$dd"
                toolbar_time.text = time
                presenter.getAllCashDaily(time)
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePickDialog.show()
    }

    //在这里用做更新失败
    override fun <T> requestSuccess(rData: T) {
    }

    override fun <T> updateDone(uData: T) {
        dialog!!.cancel()
    }

    override fun showLoading() {
        if (loading_progress.visibility==View.GONE)loading_progress.visibility=View.VISIBLE
        if (loading_text.visibility==View.GONE)loading_text.visibility=View.VISIBLE
        if (loading_retry.visibility==View.VISIBLE)loading_retry.visibility=View.GONE
        loading.visibility=View.VISIBLE
    }

    override fun hideLoading() {
        MyHandler.removeCallbacksAndMessages(null)
        loading.visibility=View.GONE
    }

    var adapter:CashDailyPagerAdapter?=null
    override fun <T> showView(aData: T) {
        if (adapter==null){
            //设置viewPager的adapter，把fragment放入viewpager
            adapter=CashDailyPagerAdapter(supportFragmentManager, aData as ArrayList<CashDailyFragment>,tabIndicators)
            cash_daily_viewpager.adapter=adapter
        }else{
            (cash_daily_viewpager.adapter as CashDailyPagerAdapter).setFragments(aData as ArrayList<CashDailyFragment>)
            cash_daily_viewpager.currentItem = 0
        }
    }

    override fun errorDealWith() {
        loading_progress.visibility=View.GONE
        loading_text.visibility=View.GONE
        loading_retry.visibility=View.VISIBLE
        MyHandler.removeCallbacksAndMessages(null)
    }
}