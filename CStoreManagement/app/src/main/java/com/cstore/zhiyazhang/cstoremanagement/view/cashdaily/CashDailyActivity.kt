package com.cstore.zhiyazhang.cstoremanagement.view.cashdaily

import android.app.DatePickerDialog
import android.content.Context
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.Gravity
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.CashDailyBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.cashdaily.CashDailyPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import kotlinx.android.synthetic.main.activity_cashdaily.*
import kotlinx.android.synthetic.main.dialog_cashdaily.*
import kotlinx.android.synthetic.main.dialog_cashdaily.view.*
import kotlinx.android.synthetic.main.layout_date.*
import kotlinx.android.synthetic.main.layout_date.view.*
import kotlinx.android.synthetic.main.loading_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.util.*

/**
 * Created by zhiya.zhang
 * on 2017/9/4 11:29.
 */
class CashDailyActivity(override val layoutId: Int=R.layout.activity_cashdaily) : MyActivity(){

    private val tabIndicators = ArrayList<String>()
    private val presenter=CashDailyPresenter(this,this,this)

    var dialog:AlertDialog?=null

    override fun initView() {
        my_toolbar.title=getString(R.string.cash_daily)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        date_util.visibility=View.VISIBLE
        //用换日表的时间
        MyTimeUtil.setTextViewDate(date_util,CStoreCalendar.getCurrentDate(1))
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
            showPrompt(getString(R.string.wait_loading))
        }
        date_util.setOnTouchListener{_,event->
            if (event.action==MotionEvent.ACTION_DOWN){
                showDatePickDlg()
                true
            }else false
        }
        loading_retry.setOnClickListener {
            loading_retry.visibility=View.GONE
            loading_text.visibility=View.VISIBLE
            loading_progress.visibility=View.VISIBLE
            presenter.getAllCashDaily(MyTimeUtil.getTextViewDate(date_util))
        }
    }

    override fun initData() {
        presenter.getAllCashDaily(MyTimeUtil.getTextViewDate(date_util))
    }

    var dialogView:View?= null
    fun updateData(view: View, cd:CashDailyBean){
        if (dialog==null){
            val builder=AlertDialog.Builder(this@CashDailyActivity)
            dialogView = View.inflate(this,R.layout.dialog_cashdaily,null)!!
            builder.setView(dialogView)
            builder.setCancelable(true)
            dialog=builder.create()
            dialogView!!.dialog_cancel.setOnClickListener {
                dialog!!.cancel()
            }
        }
        if (CStoreCalendar.getCurrentDate(1)!=MyTimeUtil.getTextViewDate(date_util)){
            //1100事件要可点击查看
            dialogView!!.dialog_save.visibility=View.GONE
            dialogView!!.dialog_title.text=cd.cdName
            dialogView!!.dialog_edit.setText(cd.cdValue)
            dialogView!!.dialog_spinner.visibility=View.GONE
            dialogView!!.dialog_edit.visibility=View.VISIBLE
            dialogView!!.dialog_edit.isEnabled=false
            dialogView!!.dialog_edit.inputType=InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE
//            dialogView!!.dialog_edit.gravity=Gravity.TOP
            dialogView!!.dialog_edit.setSingleLine(false)
            dialogView!!.dialog_edit.setHorizontallyScrolling(false)
        }else{
            dialogView!!.dialog_edit.isEnabled=true
            dialogView!!.dialog_save.visibility=View.VISIBLE
            dialogView!!.dialog_save.setOnClickListener {
                if (cd.cdId=="1097"){
                    dialogView!!.dialog_progress.visibility=View.VISIBLE
                    //天气,在spinner内是从0开始，显示及存储是从1开始，所以+1
                    presenter.updateCashDaily(MyTimeUtil.getTextViewDate(date_util), view,cd,(dialogView!!.dialog_spinner.selectedItemPosition+1).toString())
                }else{//其他

                    //去掉空格
                    val value=dialogView!!.dialog_edit.text.toString().replace(" ","")
                    when (value) {
                        "" -> showPrompt(getString(R.string.please_edit_value))//不能为空
                        cd.cdValue -> {
                            //和之前一样就直接发个消息说完成
                            showPrompt(getString(R.string.saveDone))
                            dialog!!.cancel()
                        }
                        else -> {
                            dialogView!!.dialog_progress.visibility=View.VISIBLE
                            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(dialogView!!.dialog_edit.windowToken, 0)
                            presenter.updateCashDaily(MyTimeUtil.getTextViewDate(date_util),view,cd,value)
                        }
                    }
                }
            }
            dialogView!!.dialog_title.text=cd.cdName
            if (cd.cdId=="1097"){
                dialogView!!.dialog_spinner.visibility=View.VISIBLE
                dialogView!!.dialog_edit.visibility=View.GONE
            }else{
                dialogView!!.dialog_edit.setText(cd.cdValue)
                dialogView!!.dialog_spinner.visibility=View.GONE
                dialogView!!.dialog_edit.visibility=View.VISIBLE
                //设置输入类型
                if (cd.cdId=="1100"){
                    dialogView!!.dialog_edit.inputType=InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE
                    dialogView!!.dialog_edit.gravity= Gravity.TOP
                    dialogView!!.dialog_edit.setSingleLine(false)
                    dialogView!!.dialog_edit.setHorizontallyScrolling(false)
                }else{
                    dialogView!!.dialog_edit.inputType=InputType.TYPE_CLASS_NUMBER
                    dialogView!!.dialog_edit.keyListener=DigitsKeyListener.getInstance("1234567890.")
                }
                dialogView!!.dialog_edit.setSelection(dialogView!!.dialog_edit.text.length)
            }
        }
        dialog!!.show()
    }

    /**
     * 得到天气选择的下拉列表
     */
    fun getWeatherAdapter(): ArrayAdapter<String> {
        val weatherAdapter=ArrayAdapter(this,R.layout.custom_spinner_text_item,resources.getStringArray(R.array.weather))
        weatherAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        return weatherAdapter
    }

    /**
     * 得到选中日期
     */
    private fun showDatePickDlg() {
        val calendar = Calendar.getInstance()
        val datePickDialog: DatePickerDialog = DatePickerDialog(this@CashDailyActivity, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            run {
                val calendar1=Calendar.getInstance()
                calendar1.timeInMillis=System.currentTimeMillis()
                if (MyTimeUtil.nowHour >= CStoreCalendar.getChangeTime(1)) {
                    //换日了要加一天
                    calendar1.set(Calendar.DATE, calendar1.get(Calendar.DATE) + 1)
                }

                val selectDate=(year.toString()+monthOfYear.toString()+dayOfMonth.toString()).toInt()
                val nowDate=(calendar1.get(Calendar.YEAR).toString()+calendar1.get(Calendar.MONTH).toString()+calendar1.get(Calendar.DAY_OF_MONTH).toString()).toInt()
                if (selectDate>nowDate){
                    showPrompt("不能选择未来日期")
                    return@run
                }
                val textYear=year.toString()+"年"
                val mm = if (monthOfYear + 1 < 10) "0${monthOfYear + 1}月"//如果小于十月就代表是个位数要手动加上0
                else (monthOfYear + 1).toString()+"月"
                val dd = if (dayOfMonth < 10) "0$dayOfMonth"//如果小于十日就代表是个位数要手动加上0
                else dayOfMonth.toString()
                date_util.year.text=textYear
                date_util.month.text=mm
                date_util.day.text=dd
                presenter.getAllCashDaily(MyTimeUtil.getTextViewDate(date_util))
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePickDialog.show()
    }

    //在这里用做更新失败
    override fun <T> requestSuccess(rData: T) {
    }

    override fun <T> updateDone(uData: T) {
        dialogView!!.dialog_progress.visibility=View.GONE
        dialog!!.cancel()
        dialogView!!.dialog_edit.setText("")
    }

    override fun showLoading() {
        if (loading_progress.visibility==View.GONE)loading_progress.visibility=View.VISIBLE
        if (loading_text.visibility==View.GONE)loading_text.visibility=View.VISIBLE
        if (loading_retry.visibility==View.VISIBLE)loading_retry.visibility=View.GONE
        loading.visibility=View.VISIBLE
    }

    override fun hideLoading() {
        MyHandler.OnlyMyHandler.removeCallbacksAndMessages(null)
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
        if (dialog!=null&&dialog!!.dialog_progress.visibility==View.VISIBLE){
            dialog!!.dialog_progress.visibility=View.GONE
        }else{
            loading_retry.visibility=View.VISIBLE
        }
        MyHandler.OnlyMyHandler.removeCallbacksAndMessages(null)
    }
}