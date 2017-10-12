package com.cstore.zhiyazhang.cstoremanagement.view.instock.scrap

import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapContractBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapHotBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.scrap.ScrapAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.scrap.ScrapHotPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.RecyclerOnTouch
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import kotlinx.android.synthetic.main.activity_contract.*

/**
 * Created by zhiya.zhang
 * on 2017/9/1 15:17.
 */
class ScrapHotItemActivity(override val layoutId: Int = R.layout.activity_contract) : MyActivity(), GenericView {

    val presenter = ScrapHotPresenter(this, this)
    private val hotMid: ArrayList<ScrapHotBean>
        get() = intent.getSerializableExtra("hotMid") as ArrayList<ScrapHotBean>
    var nowPosition: Int = 0
    private val changeData = ArrayList<ScrapContractBean>()
    /**
     * 0==none 1==finish 2==next 3==last
     */
    var activityAction = 0
    val layoutManager = MyLinearlayoutManager(this, LinearLayoutManager.VERTICAL, false)

    override fun initView() {
        nowPosition = intent.getIntExtra("position", 0)
        toolbar.title = hotMid[nowPosition].sName
        toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(toolbar)
        mySpinner.visibility = View.GONE
        swipe_recycler.layoutManager = layoutManager
        my_swipe.setProgressViewEndTarget(true, 500)
        //设置loading样式
        my_swipe.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this@ScrapHotItemActivity, R.color.cstore_white))
        my_swipe.setProgressViewOffset(false, 0, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, resources.displayMetrics).toInt())
        my_swipe.setColorSchemeColors(
                ContextCompat.getColor(this@ScrapHotItemActivity, R.color.cstore_red),
                ContextCompat.getColor(this@ScrapHotItemActivity, R.color.yellow),
                ContextCompat.getColor(this@ScrapHotItemActivity, R.color.blue),
                ContextCompat.getColor(this@ScrapHotItemActivity, R.color.cstore_green))
        //设置下拉刷新是否能用
        appbar.addOnOffsetChangedListener { _, verticalOffset ->
            my_swipe.isEnabled = verticalOffset >= 0
        }
    }

    override fun initClick() {
        //下拉刷新
        my_swipe.setOnRefreshListener {
            if (my_swipe.isEnabled) initData()
        }
        //提交修改
        done.setOnClickListener {
            if (MyTimeUtil.nowHour > 23) {
                showPrompt(getString(R.string.mrk_time))
            } else {
                if (changeData.size != 0) {
                    activityAction = 0
                    presenter.submitScrap(changeData)
                } else {
                    showPrompt(getString(R.string.noSaveMessage))
                }
            }
        }
        //下一个
        order_item_next.setOnClickListener {
            my_swipe.isRefreshing = true
            if (MyTimeUtil.nowHour > 23) {
                goNext()
                return@setOnClickListener
            }
            if (changeData.size != 0) {
                AlertDialog.Builder(this@ScrapHotItemActivity)
                        .setTitle("提示")
                        .setMessage("您修改的报废尚未确认，是否放弃修改？")
                        .setPositiveButton("保存", { _, _ ->
                            activityAction = 2
                            presenter.submitScrap(changeData)
                        })
                        .setNegativeButton("放弃", { _, _ ->
                            changeData.clear()
                            goNext()
                        })
                        .show()
            } else goNext()
        }
        //上一个
        order_item_last.setOnClickListener {
            my_swipe.isRefreshing = true
            if (MyTimeUtil.nowHour > 23) {
                goLast()
                return@setOnClickListener
            }
            if (changeData.size != 0) {
                AlertDialog.Builder(this@ScrapHotItemActivity)
                        .setTitle("提示")
                        .setMessage("您修改的报废尚未确认，是否放弃修改？")
                        .setPositiveButton("保存", { _, _ ->
                            activityAction = 3
                            presenter.submitScrap(changeData)
                        })
                        .setNegativeButton("放弃", { _, _ ->
                            changeData.clear()
                            goLast()
                        })
                        .show()
            } else goLast()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->onBackPressed()
        }
        return true
    }

    override fun onBackPressed() {
        if (MyTimeUtil.nowHour<23) {
            if (changeData.size!=0){
                AlertDialog.Builder(this@ScrapHotItemActivity)
                        .setTitle("提示")
                        .setMessage("您修改的报废尚未确认，是否放弃修改？")
                        .setPositiveButton("保存", { _, _ ->
                            activityAction = 1
                            presenter.submitScrap(changeData)
                        })
                        .setNegativeButton("放弃", { _, _ ->
                            changeData.clear()
                            super.onBackPressed()
                        })
                        .show()
            }else super.onBackPressed()
        }else super.onBackPressed()
    }

    private fun goLast() {
        activityAction = 3
        //修改hotMid
        if (nowPosition == 0) {
            //是第一个，变成最后一个
            nowPosition = hotMid.size - 1
        } else nowPosition--
        presenter.getAllScraphot(hotMid[nowPosition].sId)
    }

    private fun goNext() {
        activityAction = 2
        //修改hotMid
        if (nowPosition == hotMid.size - 1) {
            //已经是最后一个，变成第一个
            nowPosition = 0
        } else nowPosition++
        presenter.getAllScraphot(hotMid[nowPosition].sId)
    }

    override fun initData() {
        presenter.getAllScraphot(hotMid[nowPosition].sId)
    }

    override fun <T> requestSuccess(rData: T) {
        if (rData is String) if (rData == "0") {
            changeData.clear()
            //更新完成
            when (activityAction) {
                1 -> onBackPressed()
                2 -> goNext()
                3 -> goLast()
                else->initData()
            }
            showPrompt(getString(R.string.saveDone))
            return
        }
        //加载完成
        when (activityAction) {
            2 -> toolbar.title = hotMid[nowPosition].sName
            3 -> toolbar.title = hotMid[nowPosition].sName
        }
        noMessage.visibility = View.GONE
        swipe_recycler.adapter = ScrapAdapter(rData as ArrayList<ScrapContractBean>, recyclerTouch)
    }

    val recyclerTouch = object : RecyclerOnTouch {
        override fun <T> onClickImage(objects: T, position: Int) {
        }

        override fun <T> onTouchAddListener(objects: T, event: MotionEvent, position: Int) {
            val adapterView = swipe_recycler.findViewHolderForAdapterPosition(position) as ScrapAdapter.ViewHolder
            onTouchChange(1, event.action, adapterView, objects as ScrapContractBean)
        }

        override fun <T> onTouchLessListener(objects: T, event: MotionEvent, position: Int) {
            val adapterView = swipe_recycler.findViewHolderForAdapterPosition(position) as ScrapAdapter.ViewHolder
            onTouchChange(0, event.action, adapterView, objects as ScrapContractBean)
        }
    }

    companion object {
        var isOnLongClick = false
        var thread: Thread? = null
        val handler: Handler = Handler()
    }

    override fun onDestroy() {
        isOnLongClick=false
        thread=null
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    private fun onTouchChange(addLess: Int, action: Int, view: ScrapAdapter.ViewHolder, scb: ScrapContractBean) {
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                if (!isOnLongClick) {
                    thread = Thread(Runnable {
                        while (isOnLongClick) {
                            Thread.sleep(200)
                            if (addLess == 0) lessCount(view, scb) else addCount(view, scb)
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

    private fun addCount(view: ScrapAdapter.ViewHolder, scb: ScrapContractBean) {
        val nowCount=scb.mrkCount+1
        val nowEditCount=scb.editCount+1
        if (nowCount>999){
            handler.post { showPrompt(getString(R.string.maxCNoAdd)) }
            return
        }
        if (scb.action==2)scb.action=1
        scb.mrkCount=nowCount
        scb.editCount=nowEditCount
        var i=0
        changeData.filter { it.scrapId==scb.scrapId }.forEach {
            i++
            if (it.action==2)scb.action=1
            it.mrkCount=nowCount
            it.editCount=nowEditCount
        }
        if (i==0)changeData.add(scb.copy())
        handler.post { view.scrapCount.text=scb.mrkCount.toString() }
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
            changeData.filter { it.scrapId == scb.scrapId }.forEach {
                when (it.action) {
                    0 -> {
                        changeData.remove(it)
                    }//如果是创建就代表是新的直接删除
                    1 -> {
                        i++
                        it.action = 2
                    }//如果是更新就代表是数据库中的，需要修改动作为delete去处理
                }
            }
            scb.mrkCount = nowCount
            scb.editCount = nowEditCount
            //如果不是新建的修改动作为删除
            if (scb.action!=0)scb.action = 2
            if (i == 0) changeData.add(scb.copy())
            handler.post { view.scrapCount.text = scb.mrkCount.toString() }
            return
        }
        scb.mrkCount = nowCount
        scb.editCount = nowEditCount
        var i = 0
        changeData.filter { it.scrapId == scb.scrapId }.forEach {
            i++
            it.mrkCount = nowCount
            it.editCount = nowEditCount
        }
        if (i == 0) changeData.add(scb.copy())
        handler.post { view.scrapCount.text = scb.mrkCount.toString() }
    }

    private fun runOrStopEdit() {
        layoutManager.setScrollEnabled(!isOnLongClick)
        my_swipe.isEnabled = !isOnLongClick
    }

    override fun showLoading() {
        my_swipe.isRefreshing = true
        order_item_next.isEnabled = false
        order_item_last.isEnabled = false
        layoutManager.setScrollEnabled(false)
        MyHandler.OnlyMyHandler.removeCallbacksAndMessages(null)
    }

    override fun hideLoading() {
        my_swipe.isRefreshing = false
        order_item_next.isEnabled = true
        order_item_last.isEnabled = true
        layoutManager.setScrollEnabled(true)
        MyHandler.OnlyMyHandler.removeCallbacksAndMessages(null)
    }

    override fun <T> showView(aData: T) {
    }

    override fun errorDealWith() {
        swipe_recycler.adapter = null
        noMessage.visibility = View.VISIBLE
    }

}