package com.cstore.zhiyazhang.cstoremanagement.view.scrap

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.hardware.Camera
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapContractBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.scrap.ScrapAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.scrap.ScrapPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.AddLessClickListener
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.ScrapView
import com.cstore.zhiyazhang.cstoremanagement.view.order.contract.ContractSearchActivity
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemTouchHelperCallback
import com.zhiyazhang.mykotlinapplication.utils.recycler.onMoveAndSwipedListener
import kotlinx.android.synthetic.main.activity_scrap.*
import kotlinx.android.synthetic.main.dialog_cashdaily.view.*
import kotlinx.android.synthetic.main.layout_date.*
import kotlinx.android.synthetic.main.layout_date.view.*
import kotlinx.android.synthetic.main.layout_search_line3.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by zhiya.zhang
 * on 2017/8/21 15:44.
 */
class ScrapActivity(override val layoutId: Int = R.layout.activity_scrap) : MyActivity(), ScrapView, EasyPermissions.PermissionCallbacks {

    private val presenter = ScrapPresenter(this, this, this)
    private val editData = ArrayList<ScrapContractBean>()
    private val adapter: ScrapAdapter = ScrapAdapter(ArrayList<ScrapContractBean>(), object : AddLessClickListener {
        override fun <T> onItemClick(view: RecyclerView.ViewHolder, beanData: T, position: Int, type: Int) {
            view as ScrapAdapter.ViewHolder
            beanData as ScrapContractBean
            if (type == 1)
                editCount(beanData.mrkCount + 1, view, position)
            else
                editCount(beanData.mrkCount - 1, view, position)
        }

        override fun <T> onItemRemove(data: T, position: Int) {
            //在此为左滑右滑删除
            data as ScrapContractBean
            when (data.action) {
            //如果是创建就代表是新的，直接删掉就可以
                0 -> {
                    editData.remove(data)
                }
            //如果是更新代表是之前的
                1 -> {
                    var i = 0
                    editData.filter { it.scrapId == data.scrapId }.forEach {
                        i++
                        it.action = 2
                    }
                    if (i == 0) {
                        data.action = 2
                        editData.add(data)
                    }
                }
            //删除和无代表不用操作
            }
        }
    })

    private val layoutManager = MyLinearlayoutManager(this, LinearLayoutManager.VERTICAL, false)
    private lateinit var dialogView: View
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        super.onCreate(savedInstanceState)
    }

    private fun initDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this@ScrapActivity)
        builder.setView(dialogView)
        builder.setCancelable(true)
        dialogView.dialog_cancel.setOnClickListener { dialog.cancel() }
        return builder.create()
    }

    override fun initView() {
        my_toolbar.title = getString(R.string.scrap)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        date_util.visibility = View.VISIBLE
        MyTimeUtil.setTextViewDate(date_util, MyTimeUtil.nowDate)
        setSupportActionBar(my_toolbar)
        header_text1_v.text = getString(R.string.idorname)
        header_text2_v.text = getString(R.string.unit_price)
        header_text3_v.text = getString(R.string.mrk_count)
        header_text4_v.text = getString(R.string.all_price)
        scrap_recycler.layoutManager = layoutManager
        scrap_recycler.adapter = adapter
        val callback = ItemTouchHelperCallback(adapter as onMoveAndSwipedListener, MyTimeUtil.nowDate == MyTimeUtil.getTextViewDate(date_util))
        val mItemTOuchHelper = ItemTouchHelper(callback)
        mItemTOuchHelper.attachToRecyclerView(scrap_recycler)
        dialogView = View.inflate(this@ScrapActivity, R.layout.dialog_cashdaily, null)
        dialog = initDialog()
        showDone(true)
    }

    override fun initData() {
        presenter.getAllScrap()
    }


    override fun initClick() {
        loading.setOnClickListener {
            showPrompt(getString(R.string.wait_loading))
        }

        go_hot_scrap.setOnClickListener {
            judgmentGoHot()
        }

        date_util.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                judgmentDate()
                true
            } else false
        }
        date_util.setOnFocusChangeListener { _, b ->
            if (b) {
                judgmentDate()
            }
        }
        search_qrcode.setOnClickListener {
            if (MyTimeUtil.getTextViewDate(date_util) != MyTimeUtil.nowDate) {
                goTodayPrompt()
            } else {
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    if (judgmentCarmer()) goQRCode()
                } else {
                    if (cameraIsCanUse()) goQRCode() else {
                        showPrompt(getString(R.string.openCamera))
                        val intent = Intent()
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                        intent.data = Uri.fromParts("package", this@ScrapActivity.packageName, null)
                        this@ScrapActivity.startActivity(intent)
                    }
                }
            }
        }
        search_btn.setOnClickListener {
            if (MyTimeUtil.getTextViewDate(date_util) != MyTimeUtil.nowDate) {
                goTodayPrompt()
            } else {
                if (search_edit.text.toString() != "")
                    presenter.searchScrap(search_edit.text.toString())
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(search_edit.windowToken, 0)
                search_edit.setText("")
            }
        }
        search_edit.setOnEditorActionListener { _, actionId, _ ->
            if (MyTimeUtil.getTextViewDate(date_util) != MyTimeUtil.nowDate) {
                goTodayPrompt()
                true
            } else {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    presenter.searchScrap(search_edit.text.toString())
                    val imm = getSystemService(
                            Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(search_edit.windowToken, 0)
                    search_edit.setText("")
                    true
                } else false
            }
        }
        scrap_done.setOnClickListener {
            if (MyTimeUtil.nowHour > 23) {
                showPrompt(getString(R.string.mrk_time))
            } else {
                var i = 0
                if (adapter.data.size > 0) {
                    i = adapter.data.sortedByDescending { it.recordNumber }[0].recordNumber
                }
                if (editData.size > 0) {
                    presenter.submitScraps(editData, i)
                } else {
                    showPrompt(getString(R.string.noSaveMessage))
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> judgmentFinish()
        }
        return true
    }

    override fun onBackPressed() {
        judgmentFinish()
    }

    /**
     * 如果时间不在今天就弹错误
     */
    private fun goTodayPrompt() {
        AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle("提示")
                .setMessage("进行报废操作要切换回今天，是否切换？")
                .setPositiveButton("切换到今天", { _, _ ->
                    MyTimeUtil.setTextViewDate(date_util, MyTimeUtil.nowDate)
                    adapter.data.clear()
                    editData.clear()
                    presenter.getAllScrap()
                })
                .setNegativeButton("放弃") { _, _ ->
                }
                .show()
    }

    /**
     * 去解析页面
     */
    private fun goQRCode() {
        val i = Intent(this@ScrapActivity, ContractSearchActivity::class.java)
        i.putExtra("whereIsIt", "result")
        startActivityForResult(i, 0)
    }

    /**
     * 判断是否有未提交的信息，弹提示框或选择日期
     */
    private fun judgmentDate() {
        if (editData.size != 0) {

            if (MyTimeUtil.nowHour > 23) {
                showDatePickDlg()
            } else {
                AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                        .setTitle("提示")
                        .setMessage("您有未提交的修改，是否放弃？")
                        .setPositiveButton("提交修改", { _, _ ->
                            var i = 0
                            if (adapter.data.size != 0) {
                                i = adapter.data.sortedByDescending { it.recordNumber }[0].recordNumber
                            }
                            presenter.submitScraps(editData, i)
                        })
                        .setNegativeButton("放弃") { _, _ ->
                            showDatePickDlg()
                        }
                        .show()
            }
        } else showDatePickDlg()
    }

    /**
     * 判断是否有未提交的信息，根据选择决定finish
     */
    private fun judgmentFinish() {
        if (editData.size != 0) {
            if (MyTimeUtil.nowHour > 23) {
                super.onBackPressed()
            } else {
                AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                        .setTitle("提示")
                        .setMessage("您有未提交的修改，是否放弃？")
                        .setPositiveButton("提交修改", { _, _ ->
                            var i = 0
                            if (adapter.data.size != 0) {
                                i = adapter.data.sortedByDescending { it.recordNumber }[0].recordNumber
                            }
                            presenter.submitScraps(editData, i)
                        })
                        .setNegativeButton("放弃") { _, _ ->
                            super.onBackPressed()
                            finish()
                        }
                        .show()
            }

        } else {
            super.onBackPressed()
            finish()
        }
    }

    /**
     * 判断写法有问题，可以不用写这么多，哪天有时间改
     */
    private fun judgmentGoHot() {
        if (editData.size != 0) {
            AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                    .setTitle("提示")
                    .setMessage("您有未提交的修改，是否放弃？")
                    .setPositiveButton("提交修改", { _, _ ->
                        var i = 0
                        if (adapter.data.size != 0) {
                            i = adapter.data.sortedByDescending { it.recordNumber }[0].recordNumber
                        }
                        presenter.submitScraps(editData, i)
                    })
                    .setNegativeButton("放弃") { _, _ ->
                        startActivityForResult(Intent(this@ScrapActivity, ScrapHotActivity::class.java), 1)
                    }
                    .show()
        } else
            startActivityForResult(Intent(this@ScrapActivity, ScrapHotActivity::class.java), 1)
    }

    /**
     * 得到选中日期
     */
    private fun showDatePickDlg() {
        val calendar = Calendar.getInstance()
        val datePickDialog = DatePickerDialog(this@ScrapActivity, DatePickerDialog.OnDateSetListener { _, year, month, day ->
            run {
                val myCalendar = Calendar.getInstance()
                myCalendar.timeInMillis = System.currentTimeMillis()
                val m = if (month + 1 < 10) "0${month + 1}" else (month + 1).toString()
                val d = if (day < 10) "0$day" else day.toString()
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
                adapter.data.clear()
                editData.clear()
                val callback = ItemTouchHelperCallback(adapter as onMoveAndSwipedListener, MyTimeUtil.nowDate == MyTimeUtil.getTextViewDate(date_util))
                val mItemTOuchHelper = ItemTouchHelper(callback)
                mItemTOuchHelper.attachToRecyclerView(scrap_recycler)
                presenter.getAllScrap()
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePickDialog.show()
    }

    /**
     * 得到从二维码解析页面来的信息
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            when (requestCode) {
                0 -> presenter.searchScrap(data.getStringExtra("message"))
                1 -> refreshData()
            }
        }
    }

    private fun refreshData() {
        presenter.getAllScrap()
    }

    override fun getDate(): String {
        return MyTimeUtil.getTextViewDate(date_util)
    }

    override fun updateDone() {
        //修改动作为update并且修改量修改为0
        for (scb in adapter.data) {
            //insert->报废量大于0代表是修改过，update->直接修改count，delete->还存在的代表只是count=0的删除，改为动作为insert切count=0
            when (scb.action) {
                0 -> {
                    if (scb.mrkCount > 0) {
                        scb.action = 1
                        scb.editCount = 0
                        scb.recordNumber = editData.filter { it.scrapId == scb.scrapId }[0].recordNumber
                    }
                }
                1 -> {
                    scb.editCount = 0
                }
                2 -> {
                    scb.action = 0
                    scb.editCount = 0
                }
            }
        }
        //清空editData
        editData.clear()
        updateStatisticsData()
        adapter.notifyDataSetChanged()
    }

    override fun <T> requestSuccess(rData: T) {
        val handler = Handler()
        try {
            Thread(Runnable {
                rData as ArrayList<ScrapContractBean>
                if (rData.size != 0) {
                    //检查数据中的时间
                    when (rData[0].busiDate) {
                    //新搜索出来的
                        null -> {
                            if (rData.size == 1) {//精确搜索只有一个，count+1
                                var i = 0
                                //先检查adapter是否有，有的话就修改数据+1
                                adapter.data.filter { it.scrapId == rData[0].scrapId }.forEach {
                                    i++
                                    it.mrkCount++
                                    it.editCount++
                                }
                                var e = 0
                                //检查editList，如果有并且不是delete那就+1,如果是delete代表之前被删除了的，修改action为update，不可能为insert，因为insert不会产生delete操作会直接remove掉
                                editData.filter { it.scrapId == rData[0].scrapId }.forEach {
                                    e++
                                    if (it.action != 2) {
                                        it.mrkCount++
                                        it.editCount++
                                    } else {
                                        it.mrkCount = 1
                                        it.editCount = 1
                                        it.action = 1
                                    }
                                }
                                if (i == 0 || e == 0) {//adapter里没有,有的话在循环内已经处理数据
                                    //检查editList
                                    val data = rData[0]
                                    data.mrkCount = 1
                                    data.editCount = 1
                                    data.action = 0
                                    if (i == 0) {
                                        handler.post {
                                            adapter.addItem(data.copy())
                                        }
                                    }
                                    if (e == 0) {
                                        try {
                                            editData.add(adapter.data.filter { it.scrapId == rData[0].scrapId }[0].copy())
                                        } catch (e: Exception) {
                                            Log.e("CStoreScrap", e.message)
                                            editData.add(data.copy())
                                        }
                                    }
                                }
                                handler.post { adapter.notifyDataSetChanged() }
                            } else {//模糊搜索有n个，count保持不变
                                //先检查是否已经存在,检查adapter中的data
                                for (scb in rData) {
                                    val newDatas = ArrayList<ScrapContractBean>()
                                    if (adapter.data.none { it.scrapId == scb.scrapId }) {
                                        //没有查到数据就添加
                                        scb.editCount = 0
                                        scb.mrkCount = 0
                                        scb.action = 0
                                        newDatas.add(scb)
                                    }
                                    handler.post { adapter.addItems(newDatas) }
                                }
                            }
                        }
                    //是今天的
                        MyTimeUtil.nowDate -> {
                            var allPrice = 0.0
                            rData.forEach {
                                it.action = 1
                                allPrice += (it.mrkCount * it.unitPrice)
                            }
                            /**
                             * 这里有个逻辑问题，用户进入下个页面对数据进行修改后回到当前页面，当前页面数据和之前数据冲突发生重复，
                             * 在这种情况下是用之前的数据还是在另一个页面改掉的数据，同时还要考虑到用户在1界面修改了数据并未保存，最初为1，当前
                             * 值为N，到另一个页面后因为上页面未保存，所以显示为1， 此时用户对其他数据修改后回到1界面时最新数据为1，
                             * 是选择这时候该选择N的数据还是1的数据？当前我的决定是选择1数据，总好过出问题
                             */
                            //把已存在的删除并添加最新添加的
                            if (adapter.data.size != 0) {
                                for (sb in rData) {
                                    adapter.data.removeAll(adapter.data.filter { it.scrapId == sb.scrapId })
                                }
                            }
                            handler.post {
                                setStatisticsData(rData.size, allPrice)
                                adapter.addItems(rData)
                            }
                        }
                    //不是今天的
                        else -> {
                            var allPrice = 0.0
                            rData.forEach {
                                it.action = 3
                                allPrice += (it.mrkCount * it.unitPrice)
                            }
                            handler.post {
                                setStatisticsData(rData.size, allPrice)
                                adapter.addItems(rData)
                            }
                        }
                    }
                }
                handler.post {
                    hideLoading()
                    handler.removeCallbacksAndMessages(null)
                }
            }).start()
        } catch (e: Exception) {
            handler.post {
                hideLoading()
                handler.removeCallbacksAndMessages(null)
            }
        }
    }

    private fun showDone(s: Boolean) {
        if (s)
            scrap_done.visibility = View.VISIBLE
        else
            scrap_done.visibility = View.GONE
    }

    /**
     * 写入数据到统计中
     */
    private fun setStatisticsData(count: Int, allPrice: Double) {
        val a = getString(R.string.all_scrap_count) + count.toString()
        scrap_count_statistics.text = a
        val b = getString(R.string.all_scrap_price) + allPrice.toFloat().toString()
        scrap_price_statistics.text = b
    }

    /**
     * 更新统计数据
     */
    private fun updateStatisticsData() {
        var count = 0
        var allPrice = 0.0
        adapter.data.filter { it.mrkCount != 0 }.forEach {
            count++
            allPrice += (it.mrkCount * it.unitPrice)
        }
        setStatisticsData(count, allPrice)
    }

    override fun errorDealWith() {
        val a = getString(R.string.all_scrap_count) + "0"
        scrap_count_statistics.text = a
        val b = getString(R.string.all_scrap_price) + "0.0"
        scrap_price_statistics.text = b
    }

    override fun showLoading() {
        go_hot_scrap.isEnabled = false
        scrap_done.isEnabled = false
        date_util.isEnabled = false
        search_qrcode.isEnabled = false
        search_btn.isEnabled = false
        loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        go_hot_scrap.isEnabled = true
        scrap_done.isEnabled = true
        date_util.isEnabled = true
        search_qrcode.isEnabled = true
        search_btn.isEnabled = true
        loading.visibility = View.GONE
    }

    private fun editCount(nowCount: Int, view: ScrapAdapter.ViewHolder, position: Int) {
        val scb = adapter.data[position]
        when {
            nowCount > 999 -> {
                showPrompt(getString(R.string.maxCNoAdd))
                return
            }
            nowCount < 0 -> {
                showPrompt(getString(R.string.minCNoLess))
                return
            }
            nowCount == 0 -> {
                //只有更新动作的数据才需要改为删除动作
                if (scb.action == 1) scb.action = 2
            }
            nowCount > 0 -> {
                //只有删除动作的数据才需要改为更新动作
                if (scb.action == 2) scb.action = 1
            }
            nowCount == view.mrkCount.text.toString().toInt() -> return
        }
        scb.mrkCount = nowCount
        scb.editCount = nowCount
        var i = 0
        editData.filter { it.scrapId == scb.scrapId }.forEach {
            i++
            it.action = scb.action
            it.mrkCount = scb.mrkCount
            it.editCount = scb.editCount
        }
        if (i == 0) editData.add(scb.copy())
        view.mrkCount.text = nowCount.toString()
        view.allPrice.text = (scb.unitPrice * nowCount).toFloat().toString()
        if (scb.mrkCount > 0)
            view.itemBg.setBackgroundColor(ContextCompat.getColor(MyApplication.instance(), R.color.add_bg))
        else
            view.itemBg.setBackgroundColor(ContextCompat.getColor(MyApplication.instance(), R.color.white))
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

    override fun onStart() {
        super.onStart()
        if (MyTimeUtil.nowHour > 23) mrk_time.visibility = View.VISIBLE
    }

}