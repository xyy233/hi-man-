package com.cstore.zhiyazhang.cstoremanagement.view.instock.scrap

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.hardware.Camera
import android.net.Uri
import android.os.Handler
import android.os.SystemClock
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapContractBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.scrap.ScrapAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.scrap.ScrapPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.RecyclerOnTouch
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.ScrapView
import com.cstore.zhiyazhang.cstoremanagement.view.order.contract.ContractSearchActivity
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemTouchHelperCallback
import com.zhiyazhang.mykotlinapplication.utils.recycler.onMoveAndSwipedListener
import kotlinx.android.synthetic.main.activity_scrap.*
import kotlinx.android.synthetic.main.layout_date.*
import kotlinx.android.synthetic.main.layout_date.view.*
import kotlinx.android.synthetic.main.loading_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by zhiya.zhang
 * on 2017/8/21 15:44.
 */
class ScrapActivity(override val layoutId: Int = R.layout.activity_scrap) : MyActivity(), GenericView, ScrapView, EasyPermissions.PermissionCallbacks {

    private val presenter = ScrapPresenter(this, this, this)
    private val editData = ArrayList<ScrapContractBean>()
    private val adapter: ScrapAdapter = ScrapAdapter(ArrayList<ScrapContractBean>(), object : RecyclerOnTouch {
        override fun <T> onClickImage(objects: T, position: Int) {
            //在此为左滑右滑删除
            objects as ScrapContractBean
            when (objects.action) {
            //如果是创建就代表是新的，直接删掉就可以
                0 -> {
                    editData.remove(objects)
                }
            //如果是更新代表是之前的
                1 -> {
                    var i = 0
                    editData.filter { it.scrapId == objects.scrapId }.forEach {
                        i++
                        it.action = 2
                    }
                    if (i == 0) {
                        objects.action = 2
                        editData.add(objects)
                    }
                }
            //删除和无代表不用操作
            }
        }

        override fun <T> onTouchAddListener(objects: T, event: MotionEvent, position: Int) {
            val adapterView = scrap_recycler.findViewHolderForAdapterPosition(position) as ScrapAdapter.ViewHolder
            onTouchChange(1, event.action, adapterView, objects as ScrapContractBean)
        }

        override fun <T> onTouchLessListener(objects: T, event: MotionEvent, position: Int) {
            val adapterView = scrap_recycler.findViewHolderForAdapterPosition(position) as ScrapAdapter.ViewHolder
            onTouchChange(0, event.action, adapterView, objects as ScrapContractBean)
        }
    })
    private val layoutManager = MyLinearlayoutManager(this, LinearLayoutManager.VERTICAL, false)

    override fun initView() {
        my_toolbar.title = getString(R.string.scrap)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        //toolbar_time.visibility = View.VISIBLE
        //toolbar_time.text = MyTimeUtil.nowDate
        date_util.visibility=View.VISIBLE
        MyTimeUtil.setTextViewDate(date_util,MyTimeUtil.nowDate)
        setSupportActionBar(my_toolbar)
        scrap_recycler.layoutManager = layoutManager
        scrap_recycler.adapter = adapter
        val callback = ItemTouchHelperCallback(adapter as onMoveAndSwipedListener, MyTimeUtil.nowDate == MyTimeUtil.getTextViewDate(date_util))
        val mItemTOuchHelper = ItemTouchHelper(callback)
        mItemTOuchHelper.attachToRecyclerView(scrap_recycler)
    }

    override fun initData() {
        presenter.getAllScrap()
    }

    override fun initClick() {
        loading.setOnClickListener {
            showPrompt(getString(R.string.wait_loading))
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
        scrap_qrcode.setOnClickListener {
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
        scrap_search_btn.setOnClickListener {
            if (MyTimeUtil.getTextViewDate(date_util) != MyTimeUtil.nowDate) {
                goTodayPrompt()
            } else {
                if (scrap_search_edit.text.toString() != "")
                    presenter.searchScrap(scrap_search_edit.text.toString())
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(scrap_search_edit.windowToken, 0)
                scrap_search_edit.setText("")
            }
        }
        scrap_search_edit.setOnEditorActionListener { _, actionId, _ ->
            if (MyTimeUtil.getTextViewDate(date_util) != MyTimeUtil.nowDate) {
                goTodayPrompt()
                true
            } else {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    presenter.searchScrap(scrap_search_edit.text.toString())
                    val imm = getSystemService(
                            Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(scrap_search_edit.windowToken, 0)
                    scrap_search_edit.setText("")
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
                presenter.submitScraps(editData, i)
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
        AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("进行报废操作要切换回今天，是否切换？")
                .setPositiveButton("切换到今天", { _, _ ->
                    MyTimeUtil.setTextViewDate(date_util,MyTimeUtil.nowDate)
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
                AlertDialog.Builder(this)
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
                AlertDialog.Builder(this)
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
                        }
                        .show()
            }

        } else super.onBackPressed()
    }

    /**
     * 得到选中日期
     */
    private fun showDatePickDlg() {
        val calendar = Calendar.getInstance()
        val datePickDialog: DatePickerDialog = DatePickerDialog(this@ScrapActivity, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            run {
                val myCalendar=Calendar.getInstance()
                myCalendar.timeInMillis=System.currentTimeMillis()
                if (year>myCalendar.get(Calendar.YEAR)||monthOfYear>myCalendar.get(Calendar.MONTH)||dayOfMonth>myCalendar.get(Calendar.DAY_OF_MONTH)){
                    showPrompt("不能选择未来日期")
                    return@run
                }
                val textYear=year.toString()+"年"
                val mm = if (monthOfYear + 1 < 10) "0${monthOfYear + 1}月"//如果小于十月就代表是个位数要手动加上0
                else (monthOfYear + 1).toString()+"月"
                val dd  = if (dayOfMonth < 10) "0$dayOfMonth"//如果小于十日就代表是个位数要手动加上0
                else dayOfMonth.toString()
                date_util.year.text=textYear
                date_util.month.text=mm
                date_util.day.text=dd
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
            presenter.searchScrap(data.getStringExtra("message"))
        }
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
        adapter.notifyDataSetChanged()
    }

    private class MyThread : Thread() {
        private var mRunning = false

        override fun run() {
            mRunning = true
            while (mRunning) {
                SystemClock.sleep(1000)
            }
        }

        fun close() {
            mRunning = false
        }
    }

    override fun <T> requestSuccess(rData: T) {
        val handler = Handler()
        Thread(Runnable {
            rData as ArrayList<ScrapContractBean>
            if (rData.size != 0) {
                //检查数据中的时间
                when (rData[0].busiDate) {
                //新搜索出来的
                    null -> {
                        handler.post { scrap_done.visibility = View.VISIBLE }
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
                                    handler.post { adapter.addItem(data.copy()) }
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
                                if (adapter.data.filter { it.scrapId == scb.scrapId }.isEmpty()) {
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
                        handler.post { scrap_done.visibility = View.VISIBLE }
                        rData.forEach {
                            it.action = 1
                        }
                        handler.post { adapter.addItems(rData) }
                    }
                //不是今天的
                    else -> {
                        handler.post { scrap_done.visibility = View.GONE }
                        rData.forEach {
                            it.action = 3
                        }
                        handler.post { adapter.addItems(rData) }
                    }
                }
            }
            handler.post {
                hideLoading()
                handler.removeCallbacksAndMessages(null)
            }
        }).start()
    }

    override fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        MyHandler.OnlyMyHandler.removeCallbacksAndMessages(null)
        loading.visibility = View.GONE
    }

    override fun onDestroy() {
        isOnLongClick = false
        thread = null
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    companion object {
        var isOnLongClick = false
        var thread: Thread? = null
        val handler: Handler = Handler()
    }

    @Synchronized private fun onTouchChange(addLess: Int, action: Int, view: ScrapAdapter.ViewHolder, scb: ScrapContractBean) {

        if (addLess == 0) {//less
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!isOnLongClick) {
                        thread = Thread(Runnable {
                            while (isOnLongClick) {
                                Thread.sleep(200)
                                lessCount(view, scb)
                            }
                        })
                        isOnLongClick = true
                        runOrStopEdit()
                        thread!!.start()
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (thread != null) {
                        isOnLongClick = false
                        thread = null
                        runOrStopEdit()
                    }
                }
            }
        } else {//add
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!isOnLongClick) {
                        thread = Thread(Runnable {
                            while (isOnLongClick) {
                                Thread.sleep(200)
                                addCount(view, scb)
                            }
                        })
                        isOnLongClick = true
                        runOrStopEdit()
                        thread!!.start()
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (thread != null) {
                        isOnLongClick = false
                        thread = null
                        runOrStopEdit()
                    }
                }
            }
        }
    }

    private fun addCount(view: ScrapAdapter.ViewHolder, scb: ScrapContractBean) {
        val nowCount = scb.mrkCount + 1
        val nowEditCount = scb.editCount + 1
        if (nowCount > 999) {
            handler.post { showPrompt(getString(R.string.maxCNoAdd)) }
            return
        }
        if (scb.action == 2) {
            scb.action = 1
        }
        scb.mrkCount = nowCount
        scb.editCount = nowEditCount
        var i = 0
        editData.filter { it.scrapId == scb.scrapId }.forEach {
            i++
            if (it.action == 2) {
                scb.action = 1
            }
            it.mrkCount = nowCount
            it.editCount = nowEditCount
        }
        if (i == 0) editData.add(scb.copy())
        handler.post { view.scrapCount.text = scb.mrkCount.toString() }
    }

    private fun lessCount(view: ScrapAdapter.ViewHolder, scb: ScrapContractBean) {
        val nowCount = scb.mrkCount - 1
        val nowEditCount = scb.editCount - 1
        if (nowCount < 0) {
            handler.post { showPrompt(getString(R.string.minCNoLess)) }
            return
        }
        if (nowCount == 0) {
            var i = 0
            editData.filter { it.scrapId == scb.scrapId }.forEach {
                when (it.action) {
                    0 -> {
                        editData.remove(it)
                    }//如果是创建就代表是新的直接删除
                    1 -> {
                        i++
                        it.action = 2
                    }//如果是更新就代表是数据库中的，需要修改动作为delete去处理
                }
            }
            scb.mrkCount = nowCount
            scb.editCount = nowEditCount
            if (scb.action!=0)scb.action = 2
            if (i == 0) editData.add(scb.copy())
            handler.post { view.scrapCount.text = scb.mrkCount.toString() }
            return
        }
        scb.mrkCount = nowCount
        scb.editCount = nowEditCount
        var i = 0
        editData.filter { it.scrapId == scb.scrapId }.forEach {
            i++
            it.mrkCount = nowCount
            it.editCount = nowEditCount
        }
        if (i == 0) editData.add(scb.copy())
        handler.post { view.scrapCount.text = scb.mrkCount.toString() }
    }

    private fun runOrStopEdit() {
        if (isOnLongClick) {
            layoutManager.setScrollEnabled(false)
        } else {
            layoutManager.setScrollEnabled(true)
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

    override fun <T> showView(aData: T) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun errorDealWith() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStart() {
        super.onStart()
        if (MyTimeUtil.nowHour > 23) mrk_time.visibility = View.VISIBLE
    }
}