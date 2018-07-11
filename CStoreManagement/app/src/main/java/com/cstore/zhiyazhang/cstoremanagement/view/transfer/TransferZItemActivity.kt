package com.cstore.zhiyazhang.cstoremanagement.view.transfer

import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
import java.math.BigDecimal

/**
 * Created by zhiya.zhang
 * on 2018/5/11 12:11.
 */
class TransferZItemActivity(override val layoutId: Int = R.layout.activity_order) : MyActivity() {
    private lateinit var trsData: TransServiceBean
    private val presenter = TransferServicePresenter(this)
    private lateinit var adapter: TransferZItemAdapter
//    private var conn: PrinterServiceConnection? = null

    private lateinit var showAction: Animation
    private lateinit var hideAction: Animation

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
        toolbar_btn.text = "打印"
        //没有记录的门市、调出的、产生两小时内的才确认需要处理
        if (trsData.requestNumber == null && trsData.trsType < 0 && nowHour <= trsData.disTime.toInt() + 2) {
            done.visibility = View.VISIBLE
        } else {
            done.visibility = View.GONE
        }
//        toolbar_btn.visibility = View.VISIBLE
        showAction = AnimationUtils.loadAnimation(this, R.anim.anim_slide_in)
        hideAction = AnimationUtils.loadAnimation(this, R.anim.anim_slide_out)
//        connection()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }


    /*private fun connection() {
        conn = PrinterServiceConnection(null)
        val i = Intent(this, GpPrintService::class.java)
        bindService(i, conn, Context.BIND_AUTO_CREATE)
    }*/

    override fun initClick() {
        done.setOnClickListener {
            AlertDialog.Builder(ContextThemeWrapper(this@TransferZItemActivity, R.style.AlertDialogCustom))
                    .setTitle("提示")
                    .setMessage("是否提交？")
                    .setPositiveButton("提交") { _, _ ->
                        showRetry(false)
                        presenter.doneTrs()
                    }
                    .setNegativeButton("取消", null)!!.show()
        }
        orderretry.setOnClickListener {
            AlertDialog.Builder(ContextThemeWrapper(this@TransferZItemActivity, R.style.AlertDialogCustom))
                    .setTitle("提示")
                    .setMessage("是否提交？")
                    .setPositiveButton("提交") { _, _ ->
                        showRetry(false)
                        presenter.doneTrs()
                    }
                    .setNegativeButton("取消", null)!!.show()
        }
        orderRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (recyclerView != null) {
                    if (trsData.requestNumber == null && trsData.trsType < 0 && nowHour <= trsData.disTime.toInt() + 2) {
                        val manager = recyclerView.layoutManager as LinearLayoutManager
                        val position = manager.findLastVisibleItemPosition()
                        val itemCount = manager.childCount
                        val totalItemCount = manager.itemCount
                        if (itemCount > 0 && position == totalItemCount - 1) {
                            done.visibility = View.VISIBLE
                            done.startAnimation(showAction)
                        } else {
                            done.visibility = View.GONE
                            done.startAnimation(hideAction)
                        }
                    }
                }
            }
        })
        toolbar_btn.setOnClickListener {
//            print()
        }
        orderLoading.setOnClickListener {
            showPrompt(getString(R.string.wait_loading))
        }
    }

    /*private fun print() {
        if (conn!!.getConnectState()) {
            //打印
            if (trsData.requestNumber == null || trsData.requestNumber == "") {
                conn!!.printZWTrs(trsData, "调拨拣货单")
            } else {
                conn!!.printZWTrs(trsData, "调拨送货单")
            }
        } else {
            //去连接
            conn!!.goActivity(this@TransferZItemActivity, true)
        }
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 28) {
//            print()
        }
    }

    override fun <T> updateDone(uData: T) {
        adapter.changeShowEdit(false)
        done.visibility = View.GONE
        toolbar_btn.visibility = View.VISIBLE
        /*AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle("打印提示")
                .setMessage("是否打印拣货单？")
                .setPositiveButton("确认", { _, _ ->
                    print()
                })
                .setNegativeButton("放弃") { _, _ -> }
                .show()*/
        AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle("提示")
                .setMessage("调拨完成！")
                .setPositiveButton("确认", { _, _ ->
                })
                .show()
    }

    override fun initData() {
        adapter = TransferZItemAdapter(trsData.items, trsData.requestNumber == null && trsData.trsType < 0 && nowHour <= trsData.disTime.toInt() + 2, this)
        orderRecycler.adapter = adapter
        hideLoading()
    }

    override fun onBackPressed() {
        val i = Intent()
        var trsQty = 0
        trsData.items.forEach {
            trsQty += if (it.storeTrsQty == null) {
                it.trsQty
            } else {
                it.storeTrsQty!!
            }
        }
        trsData.trsQuantities = trsQty
        val storeUnitPrice: Double = trsData.items.filter { it.storeUnitPrice != null }
                .map {
                    try {
                        if (it.storeTrsQty != null) {
                            it.storeUnitPrice!! * it.storeTrsQty!!
                        } else {
                            it.storeUnitPrice!! * it.trsQty
                        }
                    } catch (e: Exception) {
                        it.storeUnitPrice!!
                    }
                }.sum()
        val b = BigDecimal(storeUnitPrice)
        val result = b.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
        trsData.storeUnitPrice = result
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

    override fun getData2(): Any? {
        return this
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