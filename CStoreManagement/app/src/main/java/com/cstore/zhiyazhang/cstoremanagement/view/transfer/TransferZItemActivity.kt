package com.cstore.zhiyazhang.cstoremanagement.view.transfer

import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.TransServiceBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.transfer.TransferServicePresenter
import com.cstore.zhiyazhang.cstoremanagement.presenter.transfer.TransferZItemAdapter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil.nowHour
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2018/5/11 12:11.
 */
class TransferZItemActivity(override val layoutId: Int = R.layout.activity_order) : MyActivity() {

    private lateinit var trsData: TransServiceBean
    private val presenter = TransferServicePresenter(this)
    private lateinit var adapter: TransferZItemAdapter

    //如果是处理过的( hour>= trsData.disTime ) 就不允许修改，activity隐藏确定，adapter隐藏输入
    override fun initView() {
        val intentData = intent.getSerializableExtra("data")
        if (intentData == null || intentData !is TransServiceBean) {
            showPrompt("数据获得异常！")
            onBackPressed()
        }
        trsData = intentData as TransServiceBean
        my_toolbar.title = if (trsData.trsType > 0) {
            //调入
            trsData.trsStoreName + " 调入"
        } else {
            //调出
            "调出至 " + trsData.trsStoreName
        }
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        orderRecycler.layoutManager = MyLinearlayoutManager(this@TransferZItemActivity, LinearLayout.VERTICAL, false)
        //没有记录的门市、调出的、产生两小时内的才确认需要处理
        if (trsData.requestNumber == null && trsData.trsType < 0 && nowHour <= trsData.disTime.toInt() + 2) {
            done.visibility = View.VISIBLE
        } else {
            done.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun initClick() {
        done.setOnClickListener {
            presenter.doneTrs()
        }
        orderretry.setOnClickListener {
            showRetry(false)
            presenter.doneTrs()
        }
    }

    override fun <T> updateDone(uData: T) {
        adapter.changeShowEdit(false)
        done.visibility = View.GONE
    }

    override fun initData() {
        adapter = TransferZItemAdapter(trsData.items, trsData.requestNumber == null && trsData.trsType < 0 && nowHour <= trsData.disTime.toInt() + 2, this)
        orderRecycler.adapter = adapter
        hideLoading()
    }

    override fun onBackPressed() {
        val i = Intent()
        i.putExtra("data", trsData)
        i.putExtra("position", intent.getIntExtra("position", -1))
        setResult(1, i)
        super.onBackPressed()
    }

    override fun errorDealWith() {
        showRetry(true)
    }

    override fun showLoading() {
        orderLoading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        orderLoading.visibility = View.GONE
    }

    override fun getData1(): Any? {
        return trsData
    }

    /**
     * 显示重试按钮
     * @param type true显示 false隐藏
     */
    private fun showRetry(type: Boolean) {
        if (type) {
            orderretry.visibility = View.VISIBLE
            orderpro.visibility = View.GONE
            orderprotext.visibility = View.GONE
        } else {
            orderretry.visibility = View.GONE
            orderpro.visibility = View.VISIBLE
            orderprotext.visibility = View.VISIBLE
        }
    }

}