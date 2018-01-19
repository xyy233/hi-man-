package com.cstore.zhiyazhang.cstoremanagement.view.scrap

import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapContractBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapHotBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.scrap.ScrapAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.scrap.ScrapHotPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.AddLessClickListener
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import kotlinx.android.synthetic.main.activity_scrap_hot_item.*
import kotlinx.android.synthetic.main.dialog_cashdaily.view.*

/**
 * Created by zhiya.zhang
 * on 2017/9/1 15:17.
 */
class ScrapHotItemActivity(override val layoutId: Int = R.layout.activity_scrap_hot_item) : MyActivity() {

    val presenter = ScrapHotPresenter(this, this)
    private val hotMid: ArrayList<ScrapHotBean>
        get() = intent.getSerializableExtra("hotMid") as ArrayList<ScrapHotBean>
    private var nowPosition: Int = 0
    private val changeData = ArrayList<ScrapContractBean>()
    /**
     * 0==none 1==finish 2==next 3==last
     */
    private var activityAction = 0
    val layoutManager = MyLinearlayoutManager(this, LinearLayoutManager.VERTICAL, false)
    private lateinit var dialogView: View
    private lateinit var dialog: AlertDialog
    private lateinit var adapter: ScrapAdapter

    private fun initDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this@ScrapHotItemActivity)
        builder.setView(dialogView)
        builder.setCancelable(true)
        dialogView.dialog_cancel.setOnClickListener { dialog.cancel() }
        return builder.create()
    }

    override fun initView() {
        nowPosition = intent.getIntExtra("position", 0)
        toolbar.title = hotMid[nowPosition].sName
        toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(toolbar)
        swipe_recycler.layoutManager = layoutManager
        my_swipe.setProgressViewEndTarget(true, 500)
        //设置loading样式
        my_swipe.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this@ScrapHotItemActivity, R.color.cstore_white))
        my_swipe.setProgressViewOffset(false, 0, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, resources.displayMetrics).toInt())
        my_swipe.setColorSchemeColors(
                ContextCompat.getColor(this@ScrapHotItemActivity, R.color.cstore_red),
                ContextCompat.getColor(this@ScrapHotItemActivity, R.color.sure),
                ContextCompat.getColor(this@ScrapHotItemActivity, R.color.blue),
                ContextCompat.getColor(this@ScrapHotItemActivity, R.color.cstore_green))
        //设置下拉刷新是否能用
        appbar.addOnOffsetChangedListener { _, verticalOffset ->
            my_swipe.isEnabled = verticalOffset >= 0
        }
        dialogView = View.inflate(this@ScrapHotItemActivity, R.layout.dialog_cashdaily, null)
        dialog = initDialog()
        header_text1_v.text = getString(R.string.idorname)
        header_text2_v.text = getString(R.string.unit_price)
        header_text3_v.text = getString(R.string.mrk_count)
        header_text4_v.text = getString(R.string.all_price)
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
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun onBackPressed() {
        if (MyTimeUtil.nowHour < 23) {
            if (changeData.size != 0) {
                AlertDialog.Builder(this@ScrapHotItemActivity)
                        .setTitle("提示")
                        .setMessage("您修改的报废尚未确认，是否放弃修改？")
                        .setPositiveButton("保存", { _, _ ->
                            activityAction = 1
                            presenter.submitScrap(changeData)
                        })
                        .setNegativeButton("放弃", { _, _ ->
                            changeData.clear()
                            finish()
                        })
                        .show()
            } else {
                finish()
            }
        } else {
            finish()
        }
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
                else -> initData()
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
        adapter = ScrapAdapter(rData as ArrayList<ScrapContractBean>, recyclerClick)
        swipe_recycler.adapter = adapter
    }

    private val recyclerClick = object : AddLessClickListener {
        override fun <T> onItemClick(view: RecyclerView.ViewHolder, beanData: T, position: Int, type: Int) {
            view as ScrapAdapter.ViewHolder
            beanData as ScrapContractBean
            if (type == 1)
                editCount(beanData.mrkCount + 1, view, position)
            else
                editCount(beanData.mrkCount - 1, view, position)
        }

        /*override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {
            view as ScrapAdapter.ViewHolder
            dialogView.dialog_title.text = view.idName.text
            dialogView.dialog_edit.setText(view.mrkCount.text)
            dialogView.dialog_edit.inputType = InputType.TYPE_CLASS_NUMBER
            dialogView.dialog_edit.keyListener = DigitsKeyListener.getInstance("1234567890")
            dialogView.dialog_edit.setSelection(dialogView.dialog_edit.text.length)
            dialogView.dialog_save.setOnClickListener {
                if (dialogView.dialog_edit.text.toString() == "") dialogView.dialog_edit.setText("0")
                editCount(dialogView.dialog_edit.text.toString().toInt(), view, position)
                if (done.visibility == View.GONE) done.visibility = View.VISIBLE
                dialog.cancel()
            }
            dialog.show()
        }*/
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
        changeData.filter { it.scrapId == scb.scrapId }.forEach {
            i++
            it.action = scb.action
            it.mrkCount = scb.mrkCount
            it.editCount = scb.editCount
        }
        if (i == 0) changeData.add(scb.copy())
        view.mrkCount.text = nowCount.toString()
        view.allPrice.text = (scb.unitPrice * nowCount).toFloat().toString()
        if (scb.mrkCount>0)
            view.itemBg.setBackgroundColor(ContextCompat.getColor(MyApplication.instance(),R.color.add_bg))
        else
            view.itemBg.setBackgroundColor(ContextCompat.getColor(MyApplication.instance(),R.color.white))
    }

    override fun showLoading() {
        my_swipe.isRefreshing = true
        done.isEnabled = false
        order_item_next.isEnabled = false
        order_item_last.isEnabled = false
        layoutManager.setScrollEnabled(false)
        MyHandler.OnlyMyHandler.removeCallbacksAndMessages(null)
    }

    override fun hideLoading() {
        my_swipe.isRefreshing = false
        done.isEnabled = true
        order_item_next.isEnabled = true
        order_item_last.isEnabled = true
        layoutManager.setScrollEnabled(true)
        MyHandler.OnlyMyHandler.removeCallbacksAndMessages(null)
    }

    override fun errorDealWith() {
        swipe_recycler.adapter = null
        noMessage.visibility = View.VISIBLE
    }

}