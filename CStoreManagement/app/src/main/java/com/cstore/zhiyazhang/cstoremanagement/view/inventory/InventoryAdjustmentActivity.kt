package com.cstore.zhiyazhang.cstoremanagement.view.inventory

import android.app.DatePickerDialog
import android.content.Context
import android.support.v7.app.AlertDialog
import android.text.InputFilter
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.AdjustmentBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.adjustment.AdjustmentPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import kotlinx.android.synthetic.main.activity_adjustment.*
import kotlinx.android.synthetic.main.dialog_cashdaily.view.*
import kotlinx.android.synthetic.main.layout_date.*
import kotlinx.android.synthetic.main.layout_date.view.*
import kotlinx.android.synthetic.main.loading_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by zhiya.zhang
 * on 2017/9/25 17:16.
 */
class InventoryAdjustmentActivity(override val layoutId: Int=R.layout.activity_adjustment) :MyActivity(),GenericView{
    private val presenter=AdjustmentPresenter(this,this,this)
    private var dialog:AlertDialog?=null
    private var dialogView:View?=null
    private val tabIndicators=ArrayList<String>()
    private val fragments=ArrayList<InventoryAdjustmentFragment>()
    private var adapter:InventoryAdjustmentPagerAdapter?=null

    override fun initView() {
        my_toolbar.title=getString(R.string.inventory_adjustment)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        date_util.visibility=View.VISIBLE
        MyTimeUtil.setTextViewDate(date_util,CStoreCalendar.getCurrentDate(0))
        setSupportActionBar(my_toolbar)
        tabIndicators.add("查询")
        tabIndicators.add("新增")
        adjustment_tab.setupWithViewPager(adjustment_viewpager)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->onBackPressed()
        }
        return true
    }

    override fun onBackPressed() {
        if (fragments.size==2){
            val nowData=fragments[1].adapter!!.data.filter { it.isChange } as ArrayList<AdjustmentBean>
            //只要确保有数据就行，不用检查时间，因为提取的就是换日时间
            if (nowData.isNotEmpty()){
                //有需要保存的数据
                AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("您有未提交的修改，是否放弃？")
                        .setPositiveButton("提交修改", { _, _ ->
                            presenter.createAdjustment(nowData)
                        })
                        .setNegativeButton("放弃") { _, _ ->
                            super.onBackPressed()
                        }
                        .show()
                return
            }
        }
        super.onBackPressed()
    }

    override fun initClick() {
        loading.setOnClickListener {
            showPrompt(getString(R.string.wait_loading))
        }
        date_util.setOnTouchListener { _, event ->
            if (event.action==MotionEvent.ACTION_DOWN){
                showDatePickDlg()
                true
            }else false
        }
        loading_retry.setOnClickListener {
            loading_retry.visibility=View.GONE
            loading_text.visibility=View.VISIBLE
            loading_progress.visibility=View.VISIBLE
            presenter.getAdjustmentList(MyTimeUtil.getTextViewDate(date_util))
        }
    }

    override fun initData() {
        presenter.getAdjustmentList(MyTimeUtil.getTextViewDate(date_util))
    }

    private fun showDatePickDlg(){
        val calendar= Calendar.getInstance()
        val datePickDialog=DatePickerDialog(this@InventoryAdjustmentActivity,DatePickerDialog.OnDateSetListener { _, year, month, day ->
            run {
                val calendar1=Calendar.getInstance()
                calendar1.timeInMillis=System.currentTimeMillis()
                if (MyTimeUtil.nowHour >= CStoreCalendar.getChangeTime(0)) calendar1.set(Calendar.DATE, calendar1.get(Calendar.DATE) + 1)
                if (year>calendar1.get(Calendar.YEAR)||month>calendar1.get(Calendar.MONTH)||day>calendar1.get(Calendar.DAY_OF_MONTH)){
                    showPrompt("不能选择未来日期")
                    return@run
                }
                val textYear=year.toString()+"年"
                var mm = ""
                mm = if (month + 1 < 10) "0${month + 1}月"//如果小于十月就代表是个位数要手动加上0
                else (month + 1).toString()+"月"
                var dd = ""
                dd = if (day < 10) "0$day"//如果小于十日就代表是个位数要手动加上0
                else day.toString()
                date_util.year.text=textYear
                date_util.month.text=mm
                date_util.day.text=dd
                presenter.getAdjustmentList(MyTimeUtil.getTextViewDate(date_util))
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))
        datePickDialog.show()
    }

    //根据日期得到所有货调
    override fun <T> showView(aData: T) {
            aData as ArrayList<AdjustmentBean>
            val nowDate=MyTimeUtil.getTextViewDate(date_util)
            fragments.add(InventoryAdjustmentFragment.newInstance(1,aData,nowDate))
            if (nowDate==CStoreCalendar.getCurrentDate(0)){
                fragments.add(InventoryAdjustmentFragment.newInstance(2,ArrayList<AdjustmentBean>(),nowDate))
            }
            if (adapter==null){
                adapter= InventoryAdjustmentPagerAdapter(supportFragmentManager,fragments,tabIndicators)
                adjustment_viewpager.adapter=adapter
            }else{
                (adjustment_viewpager.adapter as InventoryAdjustmentPagerAdapter).setFragments(fragments)
                adjustment_viewpager.currentItem=0
            }
    }

    //根据日期得到所有货调出错
    override fun errorDealWith() {
        loading_progress.visibility=View.GONE
        loading_text.visibility=View.GONE
        loading_retry.visibility=View.VISIBLE
        MyHandler.removeCallbacksAndMessages(null)
    }

    //搜索得到的数据
    override fun <T> requestSuccess(rData: T) {
        val searchFragment = fragments[1]
        searchFragment.adapter?.addData(rData as ArrayList<AdjustmentBean>)
    }

    //保存成功
    override fun <T> updateDone(uData: T) {
        showPrompt(getString(R.string.saveDone))
    }

    override fun showLoading() {
        if (loading_progress.visibility==View.GONE)loading_progress.visibility=View.VISIBLE
        if (loading_text.visibility==View.GONE)loading_text.visibility=View.VISIBLE
        if (loading_retry.visibility==View.VISIBLE)loading_retry.visibility=View.GONE
        loading.visibility=View.VISIBLE
    }

    override fun hideLoading() {
        super.hideLoading()
    }

    /**
     * 弹出dialog修改数据
     */
    fun updateDate(view: View, ab: AdjustmentBean) {
        if (dialog==null){
            createDialog()
        }
        //能点击就代表当前是允许操作的，就不用在此判断了
        dialogView!!.dialog_edit.setText("")
        dialogView!!.dialog_edit.inputType= InputType.TYPE_CLASS_NUMBER
        dialogView!!.dialog_edit.keyListener= DigitsKeyListener.getInstance("1234567890")
        dialogView!!.dialog_title.text=ab.itemName
        dialogView!!.dialog_edit.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(3))
        dialogView!!.dialog_save.setOnClickListener {
            val value=dialogView!!.dialog_edit.text.toString()
            if (value==""){
                showPrompt(getString(R.string.isNotNull))
                return@setOnClickListener
            }
            ab.actStockQTY=value.toInt()
            ab.adjQTY=ab.currStockQTY-ab.actStockQTY
            ab.isChange=true
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(dialogView!!.dialog_edit.windowToken, 0)
            dialog!!.cancel()
            fragments[1].adapter?.notifyDataSetChanged()
        }
        dialog!!.show()
    }

    private fun createDialog() {
        val builder=AlertDialog.Builder(this@InventoryAdjustmentActivity)
        dialogView = View.inflate(this,R.layout.dialog_cashdaily,null)!!
        builder.setView(dialogView)
        builder.setCancelable(true)
        dialog=builder.create()
        dialogView!!.dialog_cancel.setOnClickListener {
            dialog!!.cancel()
        }
    }

    /**
     * 搜索库调品
     */
    fun searchAdjustment(searchMsg: String) {
        presenter.searchAdjustment(searchMsg)
    }

    /**
     * 保存数据
     */
    fun saveData(data: ArrayList<AdjustmentBean>) {
        val nowData=data.filter { it.isChange } as ArrayList<AdjustmentBean>
        if (nowData.isNotEmpty()){
            presenter.createAdjustment(nowData)
        }else{
            showPrompt(getString(R.string.noSaveMessage))
        }
    }

    /**
     * 打开镜头扫描
     */
    fun openQRCode() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}