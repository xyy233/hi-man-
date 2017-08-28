/*
package com.cstore.zhiyazhang.cstoremanagement.view.scrap

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapContractBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.scrap.BarCodeScrapAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.scrap.BarCodeScrapPresenter
import com.cstore.zhiyazhang.cstoremanagement.sql.ScrapDao
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.RecyclerOnTouch
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.uuzuche.lib_zxing.activity.CaptureFragment
import com.uuzuche.lib_zxing.activity.CodeUtils
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemTouchHelperCallback
import com.zhiyazhang.mykotlinapplication.utils.recycler.onMoveAndSwipedListener
import kotlinx.android.synthetic.main.activity_scrap_barcode.*
import kotlinx.android.synthetic.main.loading_layout.*
import kotlinx.android.synthetic.main.sure_btn.*

*/
/**
 * Created by zhiya.zhang
 * on 2017/8/11 17:32.
 *//*

class BarCodeScrapActivity(override val layoutId: Int = R.layout.activity_scrap_barcode) : MyActivity(), GenericView {

    val captureFragment: CaptureFragment = CaptureFragment()
    val presenter = BarCodeScrapPresenter(this)
    val sd = ScrapDao(this)
    var adapter: BarCodeScrapAdapter? = null
    val layoutManager = MyLinearlayoutManager(this, LinearLayoutManager.VERTICAL, false)

    */
/**
     * 二维码解析回调函数
     *//*

    val analyzeCallback = object : CodeUtils.AnalyzeCallback {
        override fun onAnalyzeSuccess(mBitmap: Bitmap, result: String) {
            val datas=result.split("|")
            var data=""
            if (datas.size>1){
                data=datas[datas.size-1]
            }else{
                data=result
            }
            presenter.getScrap(data)
            scanBtn.isChecked = false
            scanBtn.isEnabled = true
        }

        override fun onAnalyzeFailed() {
            showPrompt("扫描错误，请重新扫描")
            refreshCamera()
        }

    }

    */
/**
     * 刷新相机重新启动识别
     *//*

    fun refreshCamera() {
        val handler = captureFragment.handler
        val msg = Message.obtain()
        msg.what = R.id.restart_preview
        handler.sendMessageDelayed(msg, 500)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initClick()
        initSearch()
    }

    private fun initSearch() {
        scrap_search_btn.setOnClickListener {
            presenter.getScrap(scrap_search_edit.text.toString())
            val imm = getSystemService(
                    Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(scrap_search_edit.windowToken, 0)
            scrap_search_edit.setText("")
        }
        scrap_search_edit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                presenter.getScrap(scrap_search_edit.text.toString())
                val imm = getSystemService(
                        Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(scrap_search_edit.windowToken, 0)
                scrap_search_edit.setText("")
                true
            } else {
                false
            }
        }
    }

    private fun initClick() {
        sure_btn.setOnClickListener {
            if (adapter == null || adapter!!.data.size == 0) {
                showPrompt(getString(R.string.noSaveMessage))
                return@setOnClickListener
            }
            val insertList = ArrayList<ScrapContractBean>()
            val updateList = ArrayList<ScrapContractBean>()
            adapter!!.data.forEach {
                if (it.isScrap == 0)
                    if ( it.isNew == 0) insertList.add(it) else if(it.isNew == 1) updateList.add(it)
            }
            if (insertList.size != 0 || updateList.size != 0) {
                showLoading()
                Thread(Runnable {
                    kotlin.run {
                        if (insertList.size != 0) sd.editSQL(insertList, "insert")
                        if (updateList.size != 0) sd.editSQL(updateList, "update")
                        finish()
                    }
                }).start()
            } else showPrompt(getString(R.string.noSaveMessage))
        }
    }

    override fun onBackPressed() {
        if (adapter == null || adapter?.data?.size == 0) finish() else {
            val insertList = ArrayList<ScrapContractBean>()
            val updateList = ArrayList<ScrapContractBean>()
            adapter!!.data.forEach {
                if (it.isScrap == 0)
                    if ( it.isNew == 0) insertList.add(it) else if(it.isNew == 1) updateList.add(it)
            }
            if (insertList.size != 0 || updateList.size != 0) {
                AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("您添加的报废尚未保存，是否放弃？")
                        .setPositiveButton("保存退出", { _, _ ->
                            showLoading()
                            Thread(Runnable {
                                kotlin.run {
                                    if (insertList.size != 0) sd.editSQL(insertList, "insert")
                                    if (updateList.size != 0) sd.editSQL(updateList, "update")
                                    finish()
                                }
                            }).start()
                        })
                        .setNegativeButton("放弃") { _, _ ->
                            finish()
                        }
                        .show()
            } else finish()
        }
    }

    private fun initView() {
        sure_btn.visibility = View.VISIBLE
        CodeUtils.setFragmentArgs(captureFragment, R.layout.my_camera_scrap)
        captureFragment.analyzeCallback = analyzeCallback
        supportFragmentManager.beginTransaction().replace(R.id.scrap_container, captureFragment).commit()
        scanBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                refreshCamera()
                scanBtn.isEnabled = false
                scanBtn.background = ContextCompat.getDrawable(this@BarCodeScrapActivity, R.drawable.round_scan)
            } else scanBtn.background = ContextCompat.getDrawable(this@BarCodeScrapActivity, R.drawable.round_noscan)
        }
        scrap_recycler.layoutManager = layoutManager
        val adapterList = sd.allDate
        adapter = BarCodeScrapAdapter(adapterList, onClick, 0)
        scrap_recycler.adapter = adapter
        val callback = ItemTouchHelperCallback(adapter as onMoveAndSwipedListener,false)
        val mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper.attachToRecyclerView(scrap_recycler)
    }

    val onClick = object : RecyclerOnTouch {
        //代替删除功能
        override fun <T> onClickImage(objects: T, position: Int) {
            if ((objects as ScrapContractBean).isScrap==0&&objects.isNew!=0){
                val editData=ArrayList<ScrapContractBean>()
                editData.add(objects)
                sd.editSQL(editData,"delete")
            }
        }

        override fun <T> onTouchAddListener(objects: T, event: MotionEvent, position: Int) {
            val adapterView = scrap_recycler.findViewHolderForAdapterPosition(position) as BarCodeScrapAdapter.ViewHolder
            onTouchChange(1, event.action, adapterView, position)
        }

        override fun <T> onTouchLessListener(objects: T, event: MotionEvent, position: Int) {
            val adapterView = scrap_recycler.findViewHolderForAdapterPosition(position) as BarCodeScrapAdapter.ViewHolder
            onTouchChange(0, event.action, adapterView, position)
        }
    }

    override fun <T> requestSuccess(objects: T) {
        adapter!!.data.forEach {
            if ((objects as ScrapContractBean).scrapId == it.scrapId) {
                if (it.isScrap == 1) {
                    showPrompt("不能添加已提交的商品")
                    return@forEach
                }
                it.mrkCount = it.mrkCount + 1
                adapter!!.notifyDataSetChanged()
                return
            }
        }
        adapter!!.addItem(objects as ScrapContractBean)
        adapter!!.notifyDataSetChanged()
    }

    override fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loading.visibility = View.GONE
    }

    var mt: Thread? = null
    var pt: Thread? = null
    var hd: Handler? = null
    var isOnLongClick = false

    private fun onTouchChange(addLess: Int, action: Int, view: BarCodeScrapAdapter.ViewHolder, position: Int) {
        hd = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> if (view.less.isEnabled) lessCount(view, position)
                    1 -> if (view.add.isEnabled) addCount(view, position)
                }
            }
        }
        if (addLess == 0) {//less
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    mt = object : Thread(Runnable {
                        while (isOnLongClick) {
                            Thread.sleep(200)
                            hd!!.sendEmptyMessage(0)
                        }
                    }) {}
                    isOnLongClick = true
                    runOrStopEdit()
                    mt!!.start()
                }
                MotionEvent.ACTION_UP -> {
                    if (mt != null)
                        isOnLongClick = false
                    runOrStopEdit()
                    mt = null
                }
                MotionEvent.ACTION_MOVE -> {
                    if (mt != null && Integer.parseInt(view.scrapCount.text.toString()) != 0)
                        isOnLongClick = true
                    runOrStopEdit()
                }
            }
        } else {//add
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    pt = object : Thread(Runnable {
                        while (isOnLongClick) {
                            Thread.sleep(200)
                            hd!!.sendEmptyMessage(1)
                        }
                    }) {}
                    isOnLongClick = true
                    runOrStopEdit()
                    pt!!.start()
                }
                MotionEvent.ACTION_UP -> {
                    if (pt != null) isOnLongClick = false
                    runOrStopEdit()
                    pt = null
                }

                MotionEvent.ACTION_MOVE -> {
                    if (pt != null) isOnLongClick = true
                    runOrStopEdit()
                }
            }
        }
    }

    */
/**
     * 根据是否是在运行中来静止滑动或开启滑动,因为分发机制，不禁止会被截取
     *//*

    private fun runOrStopEdit() {
        //长按中禁止所有滑动
        if (isOnLongClick) {
            layoutManager.setScrollEnabled(false)
        } else {//停止长按开启所有滑动
            layoutManager.setScrollEnabled(true)
        }
    }

    private fun addCount(view: BarCodeScrapAdapter.ViewHolder, position: Int) {
        if (adapter!!.data[position].mrkCount < 999) {
            adapter!!.data[position].mrkCount++
            if (adapter!!.data[position].isNew==2){
                adapter!!.data[position].isNew=1
            }
            view.scrapCount.text = adapter!!.data[position].mrkCount.toString()
        }
    }

    private fun lessCount(view: BarCodeScrapAdapter.ViewHolder, position: Int) {
        if (adapter!!.data[position].mrkCount > 1) {
            adapter!!.data[position].mrkCount--
            if (adapter!!.data[position].isNew==2){
                adapter!!.data[position].isNew=1
            }
            view.scrapCount.text = adapter!!.data[position].mrkCount.toString()
        } else showPrompt("报废数必须大于0,如要删除请左滑或右滑")
    }

    override fun <T> showView(adapter: T) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun errorDealWith() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}*/
