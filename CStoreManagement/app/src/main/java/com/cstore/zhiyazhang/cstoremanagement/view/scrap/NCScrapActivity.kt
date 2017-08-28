/*
package com.cstore.zhiyazhang.cstoremanagement.view.scrap

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapCategoryBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapContractBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.scrap.BarCodeScrapAdapter
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.sql.ScrapDao
import com.cstore.zhiyazhang.cstoremanagement.utils.ConnectionDetector
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.RecyclerOnTouch
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zhiyazhang.mykotlinapplication.utils.MyApplication
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemTouchHelperCallback
import com.zhiyazhang.mykotlinapplication.utils.recycler.onMoveAndSwipedListener
import kotlinx.android.synthetic.main.activity_scrap_nobarcode.*
import kotlinx.android.synthetic.main.loading_layout.*
import kotlinx.android.synthetic.main.sure_btn.*
import kotlinx.android.synthetic.main.toolbar_layout.*

*/
/**
 * Created by zhiya.zhang
 * on 2017/8/8 10:10.
 *//*

class NCScrapActivity(override val layoutId: Int = R.layout.activity_scrap_nobarcode) : MyActivity(), GenericView {

    val sd = ScrapDao(this)
    var adapter: BarCodeScrapAdapter? = null
    val layoutManager = MyLinearlayoutManager(this, LinearLayoutManager.VERTICAL, false)
    var scrapCategory: ScrapCategoryBean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sure_btn.visibility = View.VISIBLE
        scrapCategory = intent.getSerializableExtra("scrapCategory") as ScrapCategoryBean
        initView()
        initData()
        initClick()
    }

    private fun initView() {
        my_toolbar.title = scrapCategory!!.categoryName
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        recyclerView2.layoutManager = layoutManager
    }

    private fun initData() {
        showLoading()
        if (!ConnectionDetector.getConnectionDetector().isOnline) {
            showPrompt(getString(R.string.noInternet))
            hideLoading()
            return
        }
        val ip = MyApplication.getIP()
        if (ip == MyApplication.instance().getString(R.string.notFindIP)) {
            showPrompt(ip)
            hideLoading()
            return
        }
        SocketUtil.getSocketUtil(ip).inquire(MySql.getAllScrapByCategory(scrapCategory!!.categoryId), 20, @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> {
                        if (msg.obj.toString() == "" || msg.obj.toString() == "[]") {
                            showPrompt(getString(R.string.noMessage))
                            hideLoading()
                            return
                        }
                        //检查已添加数据中是否有相同id的数据，有的话已添加数据覆盖当前数据
                        val scbs = Gson().fromJson<ArrayList<ScrapContractBean>>(msg.obj.toString(), object : TypeToken<ArrayList<ScrapContractBean>>() {}.type)
                        for (scb in sd.allDate) {
                            for (s in scbs) {
                                if (scb.scrapId == s.scrapId) {
                                    s.mrkCount = scb.mrkCount
                                    s.isScrap = scb.isScrap
                                    s.isNew = scb.isNew
                                }
                            }
                        }

                        adapter = BarCodeScrapAdapter(scbs, onClick, 1)
                        recyclerView2.adapter = adapter
                        val callback = ItemTouchHelperCallback(adapter as onMoveAndSwipedListener)
                        val mItemTouchHelper = ItemTouchHelper(callback)
                        mItemTouchHelper.attachToRecyclerView(recyclerView2)
                        hideLoading()
                    }
                    else -> {
                        showPrompt(msg.obj.toString())
                        hideLoading()
                    }
                }
            }
        })
    }

    val onClick = object : RecyclerOnTouch {
        override fun <T> onClickImage(objects: T, position: Int) {}

        override fun <T> onTouchAddListener(objects: T, event: MotionEvent, position: Int) {
            val adapterView: BarCodeScrapAdapter.ViewHolder = recyclerView2.findViewHolderForAdapterPosition(position) as BarCodeScrapAdapter.ViewHolder
            onTouchChange(1, event.action, adapterView, position)
        }

        override fun <T> onTouchLessListener(objects: T, event: MotionEvent, position: Int) {
            val adapterView: BarCodeScrapAdapter.ViewHolder = recyclerView2.findViewHolderForAdapterPosition(position) as BarCodeScrapAdapter.ViewHolder
            onTouchChange(0, event.action, adapterView, position)
        }
    }

    private fun initClick() {
        sure_btn.setOnClickListener {
            showLoading()
            mThread.start()
        }
    }

    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                528 -> {
                    hideLoading()
                    finish()
                }
            }
        }
    }
    private val mThread = Thread(Runnable {
        kotlin.run {
            try {
                Looper.prepare()
                val insertList = ArrayList<ScrapContractBean>()
                val updateList = ArrayList<ScrapContractBean>()
                adapter!!.data
                        .filter { it.mrkCount > 0 && it.isScrap == 0 }
                        .forEach { if (it.isNew != 2) insertList.add(it) else updateList.add(it) }
                if (insertList.size != 0 || updateList.size != 0) {
                    if (insertList.size != 0) sd.editSQL(insertList, "insert")
                    if (updateList.size != 0) sd.editSQL(updateList, "update")
                    val msg = Message()
                    msg.what = 528
                    mHandler.sendMessage(msg)
                } else showPrompt(getString(R.string.noSaveMessage))
            } catch (e: Exception) {
                hideLoading()
                showPrompt(e.message.toString())
                Log.e("NCScrapActivity", "", e)
            } finally {
                Looper.loop()
            }
        }
    })

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finishAfterTransition()
        }
        return true
    }

    override fun <T> requestSuccess(objects: T) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loading.visibility = View.GONE
    }

    override fun <T> showView(adapter: T) {
    }

    override fun errorDealWith() {
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
                    mt!!.start()
                }
                MotionEvent.ACTION_UP -> {
                    if (mt != null)
                        isOnLongClick = false
                    mt = null
                }
                MotionEvent.ACTION_MOVE -> {
                    if (mt != null && Integer.parseInt(view.scrapCount.text.toString()) != 0)
                        isOnLongClick = true
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
                    pt!!.start()
                }
                MotionEvent.ACTION_UP -> {
                    if (pt != null) isOnLongClick = false
                    pt = null
                }

                MotionEvent.ACTION_MOVE -> {
                    if (pt != null) isOnLongClick = true
                }
            }
        }
    }

    private fun addCount(view: BarCodeScrapAdapter.ViewHolder, position: Int) {
        if (adapter!!.data[position].mrkCount < 999) {
            adapter!!.data[position].mrkCount++
            view.scrapCount.text = adapter!!.data[position].mrkCount.toString()
        }
    }

    private fun lessCount(view: BarCodeScrapAdapter.ViewHolder, position: Int) {
        if (adapter!!.data[position].mrkCount > 0) {
            adapter!!.data[position].mrkCount--
            view.scrapCount.text = adapter!!.data[position].mrkCount.toString()
        }
    }
}*/
