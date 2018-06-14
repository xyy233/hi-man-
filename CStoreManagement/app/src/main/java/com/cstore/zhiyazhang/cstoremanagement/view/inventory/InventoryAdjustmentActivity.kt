package com.cstore.zhiyazhang.cstoremanagement.view.inventory

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Camera
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.text.InputFilter
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.bumptech.glide.Glide
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.AdjustmentBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.adjustment.AdjustmentPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.*
import com.cstore.zhiyazhang.cstoremanagement.view.order.contract.ContractSearchActivity
import kotlinx.android.synthetic.main.activity_adjustment.*
import kotlinx.android.synthetic.main.dialog_cashdaily.view.*
import kotlinx.android.synthetic.main.layout_date.*
import kotlinx.android.synthetic.main.layout_date.view.*
import kotlinx.android.synthetic.main.layout_tutorial.*
import kotlinx.android.synthetic.main.loading_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by zhiya.zhang
 * on 2017/9/25 17:16.
 */
class InventoryAdjustmentActivity(override val layoutId: Int = R.layout.activity_adjustment) : MyActivity(), EasyPermissions.PermissionCallbacks {
    private val presenter = AdjustmentPresenter(this, this, this)
    private var dialog: AlertDialog? = null
    private var dialogView: View? = null
    private val tabIndicators = ArrayList<String>()
    private val fragments = ArrayList<InventoryAdjustmentFragment>()
    private var adapter: InventoryAdjustmentPagerAdapter? = null
    private var isShowTutorial = false
    private var exitTutorial: Long = 0
    private var sp: SharedPreferences? = null

    override fun initView() {
        sp = getSharedPreferences("tutorial", Context.MODE_PRIVATE)
        isShowTutorial = sp!!.getBoolean("inventoryAdjustment", true)
        my_toolbar.title = getString(R.string.inventory_adjustment)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        date_util.visibility = View.VISIBLE
        MyTimeUtil.setTextViewDate(date_util, CStoreCalendar.getCurrentDate(0))
        setSupportActionBar(my_toolbar)
        adjustment_tab.setupWithViewPager(adjustment_viewpager)
        adjustment_viewpager.setPageTransformer(true, ZoomOutPageTransformer())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun onBackPressed() {
        if (fragments.size == 2) {
            val nowData = fragments[1].adapter!!.data.filter { it.isChange } as ArrayList<AdjustmentBean>
            //只要确保有数据就行，不用检查时间，因为提取的就是换日时间
            if (nowData.isNotEmpty()) {
                //有需要保存的数据
                AlertDialog.Builder(ContextThemeWrapper(this,R.style.AlertDialogCustom))
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
            if (event.action == MotionEvent.ACTION_DOWN) {
                showDatePickDlg()
                true
            } else false
        }
        loading_retry.setOnClickListener {
            loading_retry.visibility = View.GONE
            loading_text.visibility = View.VISIBLE
            loading_progress.visibility = View.VISIBLE
            presenter.getAdjustmentList(MyTimeUtil.getTextViewDate(date_util))
        }
        tutorial_body.setOnClickListener {
            if (System.currentTimeMillis() - exitTutorial > 2000) {
                showPrompt(getString(R.string.double_close_tutorial))
                exitTutorial = System.currentTimeMillis()
            } else {
                tutorial_body.visibility = View.GONE
                val editor = sp!!.edit()
                editor.putBoolean("inventoryAdjustment", false)
                editor.apply()
            }
        }
    }

    override fun initData() {
        if (CStoreCalendar.getNowStatus(0) != 0) {
            showLoading()
            loading_progress.visibility = View.GONE
            loading_text.text = getString(R.string.zero_error)
            showPrompt(getString(R.string.zero_error))
        } else {
            presenter.getAdjustmentList(MyTimeUtil.getTextViewDate(date_util))
        }
    }

    private fun showDatePickDlg() {
        val calendar = Calendar.getInstance()
        val datePickDialog = DatePickerDialog(this@InventoryAdjustmentActivity, DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val calendar1 = Calendar.getInstance()
            calendar1.timeInMillis = System.currentTimeMillis()
            if (MyTimeUtil.nowHour >= CStoreCalendar.getChangeTime(0)) calendar1.set(Calendar.DATE, calendar1.get(Calendar.DATE) + 1)

            val m=if (month+1<10)"0${month + 1}" else (month + 1).toString()
            val d= if (day < 10) "0$day" else day.toString()
            val selectDate = (year.toString() + m + d).toInt()
            val nowDate=MyTimeUtil.getYMDStringByDate3(calendar1.time).toInt()
            if (selectDate > nowDate) {
                showPrompt("不能选择未来日期")
                return@OnDateSetListener
            }
            val textYear = year.toString() + "年"
            val mm = m + "月"
            date_util.year.text = textYear
            date_util.month.text = mm
            date_util.day.text = d
            presenter.getAdjustmentList(MyTimeUtil.getTextViewDate(date_util))
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePickDialog.show()
    }

    //根据日期得到所有货调
    override fun <T> showView(aData: T) {
        aData as ArrayList<AdjustmentBean>
        val nowDate = MyTimeUtil.getTextViewDate(date_util)
        fragments.clear()
        tabIndicators.clear()
        fragments.add(InventoryAdjustmentFragment.newInstance(1, aData, nowDate))
        tabIndicators.add("查询")
        if (nowDate == CStoreCalendar.getCurrentDate(0)) {
            fragments.add(InventoryAdjustmentFragment.newInstance(2, ArrayList(), nowDate))
            tabIndicators.add("新增")
        }
        if (adapter == null) {
            adapter = InventoryAdjustmentPagerAdapter(supportFragmentManager, fragments, tabIndicators)
            adjustment_viewpager.adapter = adapter
        } else {
            adapter!!.refresh()
            adjustment_viewpager.currentItem = 0
        }
    }

    //根据日期得到所有货调出错
    override fun errorDealWith() {
        loading_progress.visibility = View.GONE
        loading_text.visibility = View.GONE
        loading_retry.visibility = View.VISIBLE
    }

    //搜索得到的数据
    override fun <T> requestSuccess(rData: T) {
        val searchFragment = fragments[1]
        searchFragment.adapter?.addData(rData as ArrayList<AdjustmentBean>)
        searchFragment.clearEdit()
    }

    //保存成功
    override fun <T> updateDone(uData: T) {
        val addList = fragments[1].adapter!!.data.filter { it.isChange } as ArrayList<AdjustmentBean>
        addList.forEach { it.isChange = false }
        fragments[0].adapter!!.addData(addList.clone() as ArrayList<AdjustmentBean>)
        fragments[1].adapter!!.data.clear()
        fragments[1].adapter!!.notifyDataSetChanged()
        fragments[1].saveBtn!!.visibility=View.GONE
        adjustment_viewpager.currentItem = 0
        showPrompt(getString(R.string.saveDone))
    }

    override fun showLoading() {
        if (loading_progress.visibility == View.GONE) loading_progress.visibility = View.VISIBLE
        if (loading_text.visibility == View.GONE) loading_text.visibility = View.VISIBLE
        if (loading_retry.visibility == View.VISIBLE) loading_retry.visibility = View.GONE
        loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        super.hideLoading()
        loading.visibility = View.GONE
    }

    /**
     * 弹出dialog修改数据
     */
    fun updateDate(ab: AdjustmentBean) {
        if (dialog == null) {
            createDialog()
        }
        //能点击就代表当前是允许操作的，就不用在此判断了
        dialogView!!.dialog_edit.setText(ab.actStockQTY.toString())
        dialogView!!.dialog_edit.inputType = InputType.TYPE_CLASS_NUMBER
        dialogView!!.dialog_edit.keyListener = DigitsKeyListener.getInstance("1234567890")
        dialogView!!.dialog_title.text = ab.itemName
        dialogView!!.dialog_edit.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(3))
        dialogView!!.dialog_save.setOnClickListener {
            val value = dialogView!!.dialog_edit.text.toString()
            if (value == "") {
                showPrompt(getString(R.string.isNotNull))
                return@setOnClickListener
            }
            ab.currStockQTY = value.toInt()
            ab.adjQTY = ab.currStockQTY - ab.actStockQTY
            ab.isChange = true
            fragments[1].saveBtn!!.visibility = View.VISIBLE
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(dialogView!!.dialog_edit.windowToken, 0)
            dialog!!.cancel()
            fragments[1].adapter?.notifyDataSetChanged()
        }
        dialogView!!.dialog_edit.setSelection(dialogView!!.dialog_edit.text.length)
        dialog!!.show()
    }

    private fun createDialog() {
        val builder = AlertDialog.Builder(this@InventoryAdjustmentActivity)
        dialogView = View.inflate(this, R.layout.dialog_cashdaily, null)!!
        builder.setView(dialogView)
        builder.setCancelable(true)
        dialog = builder.create()
        dialogView!!.dialog_save.text = getString(R.string.sure)
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
        val nowData = data.filter { it.isChange } as ArrayList<AdjustmentBean>
        if (nowData.isNotEmpty()) {
            presenter.createAdjustment(nowData)
        } else {
            showPrompt(getString(R.string.noSaveMessage))
        }
    }

    /**
     * 显示教程
     */
    fun showTutorial(type: Int) {
        if (isShowTutorial && type == 2) {
            Glide.with(this@InventoryAdjustmentActivity).load(R.drawable.adjustment_tutorial).crossFade().into(tutorial_img)
            isShowTutorial = false
            tutorial_body.visibility = View.VISIBLE
        }
    }

    /**
     * 打开镜头扫描
     */
    fun openQRCode() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (judgmentCarmer()) goQRCode()
        } else {
            if (cameraIsCanUse()) goQRCode() else {
                showPrompt(getString(R.string.open_permission))
                val intent = Intent()
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                intent.data = Uri.fromParts("package", this@InventoryAdjustmentActivity.packageName, null)
                this@InventoryAdjustmentActivity.startActivity(intent)
            }
        }
    }

    private fun goQRCode() {
        val i = Intent(this@InventoryAdjustmentActivity, ContractSearchActivity::class.java)
        i.putExtra("whereIsIt", "result")
        startActivityForResult(i, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            presenter.searchAdjustment(data.getStringExtra("message"))
        }
    }

    /**
     * 获得相机权限
     */
    @pub.devrel.easypermissions.AfterPermissionGranted(1)
    private fun judgmentCarmer(): Boolean {
        val perms = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (!EasyPermissions.hasPermissions(this, *perms)) {
            EasyPermissions.requestPermissions(this, "请打开权限以操作扫描更新", 1, *perms)
            return false
        }
        return true
    }

    /**
     * 返回true 表示可以使用  返回false表示不可以使用
     */
    private fun cameraIsCanUse(): Boolean {
        var isCanUse = true
        var mCamera: Camera? = null
        try {
            mCamera = Camera.open()
            val mParameters = mCamera!!.parameters //针对魅族手机
            mCamera.parameters = mParameters
        } catch (e: Exception) {
            isCanUse = false
        }

        if (mCamera != null) {
            try {
                mCamera.release()
            } catch (e: Exception) {
                e.printStackTrace()
                return isCanUse
            }

        }
        return isCanUse
    }

    //请求权限结果
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    //获取权限失败
    override fun onPermissionsDenied(requestCode: Int, list: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    //获取权限成功
    override fun onPermissionsGranted(requestCode: Int, list: List<String>) {
        goQRCode()
    }
}