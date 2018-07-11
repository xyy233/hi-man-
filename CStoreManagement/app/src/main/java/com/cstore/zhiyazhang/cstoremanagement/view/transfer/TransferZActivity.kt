package com.cstore.zhiyazhang.cstoremanagement.view.transfer

import android.app.TimePickerDialog
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.TransResult
import com.cstore.zhiyazhang.cstoremanagement.bean.TransServiceBean
import com.cstore.zhiyazhang.cstoremanagement.bean.TransTag
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.presenter.transfer.TransferServiceAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.transfer.TransferServicePresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.cstore.zhiyazhang.cstoremanagement.view.LivingService
import com.cstore.zhiyazhang.cstoremanagement.view.SignInActivity
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.dialog_trs.view.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.math.BigDecimal
import java.util.*

/**
 * Created by zhiya.zhang
 * on 2018/5/10 17:57.
 */
class TransferZActivity(override val layoutId: Int = R.layout.activity_order) : MyActivity() {

    private val presenter = TransferServicePresenter(this)
    private lateinit var adapter: TransferServiceAdapter
    private var updateData: TransServiceBean? = null
    private lateinit var dialog: AlertDialog
    private lateinit var dialogView: View

    override fun initView() {
        my_toolbar.title = getString(R.string.transz)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        orderRecycler.layoutManager = MyLinearlayoutManager(this@TransferZActivity, LinearLayout.VERTICAL, false)
        initDialog()
    }

    private fun initDialog() {
        val builder = AlertDialog.Builder(this)
        dialogView = View.inflate(this, R.layout.dialog_trs, null)
        builder.setView(dialogView)
        builder.setCancelable(true)
        dialog = builder.create()
        dialogView.dialog_cancel.setOnClickListener {
            dialog.cancel()
        }
        dialogView.delivery_time.text = getTime(MyTimeUtil.nowTimeString)
        dialogView.trs_fee.inputType = InputType.TYPE_CLASS_NUMBER
        dialogView.trs_fee.keyListener = DigitsKeyListener.getInstance("1234567890.")
        dialogView.trs_fee.setSelection(dialogView.trs_fee.text.length)
    }

    /**
     * 获得时间
     * @return HH:mm格式String
     */
    private fun getTime(dateTime: String): String {
        val c = MyTimeUtil.getCalendarByStringHMS(dateTime)
        val hour = c.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')
        val minute = c.get(Calendar.MINUTE).toString().padStart(2, '0')
        return hour + ":" + minute
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }


    override fun initClick() {
        orderretry.setOnClickListener { initData() }
    }

    override fun initData() {
        val user = User.getUser()
        //没登陆就去登录
        if (user.storeId == "") {
            val i = Intent(this@TransferZActivity, SignInActivity::class.java)
            i.putExtra("out", 1)
            startActivityForResult(i, 0)
        } else {
            showRetry(false)
            presenter.getAllTrs()
        }
    }

    override fun <T> showView(aData: T) {
        if (aData !is TransResult) {
            showPrompt("获得数据类型错误")
            errorDealWith()
            return
        }

        //更新最新调拨时间
        val transTag = TransTag.getTransTag(false)
        var hour = 0
        (aData as TransResult).rows.forEach { if (it.disTime.toInt() > hour) hour = it.disTime.toInt() }
        transTag.hour = hour.toString()
        TransTag.saveTag(TransTag(User.getUser().storeId, transTag.date, hour.toString()))

        Log.e("TranService", TransTag.getTransTag(true).hour)

        for (d in aData.rows) {
            val storeUnitPrice: Double = d.items
                    .filter { it.storeUnitPrice != null }
                    .map {
                        try {
                            val b = BigDecimal(it.storeUnitPrice!!)
                            it.storeUnitPrice = b.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                            if (it.storeTrsQty != null) {
                                it.storeUnitPrice!! * it.storeTrsQty!!
                            } else {
                                it.storeUnitPrice!! * it.trsQty
                            }
                        } catch (e: Exception) {
                            it.storeUnitPrice!!
                        }
                    }
                    .sum()
            val b = BigDecimal(storeUnitPrice)
            val result = b.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
            d.storeUnitPrice = result
        }
        //超过两小时的订单都不显示
        val nowHour = MyTimeUtil.nowHour
        val removeData = ArrayList<TransServiceBean>()
        aData.rows.forEach {
            if (nowHour >= it.disTime.toInt() + 2) removeData.add(it)
            //2018-06-26新增，小于当前记录小时的数据不显示
            if (it.disTime.toInt() < transTag.hour.toInt()) removeData.add(it)
        }
        aData.rows.removeAll(removeData)
        adapter = TransferServiceAdapter(aData as TransResult, object : ItemClickListener {
            override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {
                val i = Intent(this@TransferZActivity, TransferZItemActivity::class.java)
                i.putExtra("data", aData.rows[position])
                i.putExtra("position", position)
                startActivityForResult(i, 1)
            }

            override fun <T> onItemEdit(data: T, position: Int) {
                updateData = (data as TransServiceBean)
                if (updateData!!.trsType > 0) {
                    //调入
                    showMyDialog(0)
                } else {
                    //调出
                    showMyDialog(1)
                }
            }
        })
        orderRecycler.adapter = adapter
    }

    /**
     * @param type 0=调入  1=调出
     */
    private fun showMyDialog(type: Int) {
        dialogView.dialog_progress.visibility = View.GONE
        dialogView.delivery_box.visibility = View.GONE
        if (type == 0) {
            dialogView.dialog_title.text="确认已送达"
            dialogView.dialog_save.text="确认收货"
            dialogView.trs_fee_box.visibility = View.GONE
            dialogView.trs_whether_done.visibility = View.VISIBLE
            dialogView.dialog_save.setOnClickListener {
                presenter.updateDone()
                dialogView.dialog_progress.visibility = View.VISIBLE
            }
        } else {
            dialogView.dialog_title.text="调出配送信息"
            dialogView.dialog_save.text="确认调出"
            dialogView.trs_fee_box.visibility = View.VISIBLE
//            dialogView.delivery_box.visibility = View.VISIBLE
            dialogView.trs_whether_done.visibility = View.GONE
            dialogView.trs_fee.setText("0")
            dialogView.dialog_save.setOnClickListener {
                if (dialogView.trs_fee.text.toString() == "") {
                    showPrompt("配送金额不能为空")
                } else {
                    updateData!!.trsFee = dialogView.trs_fee.text.toString().toDouble()
//                    if (updateData!!.feeUpdTime==null)updateData!!.feeUpdTime=MyTimeUtil.nowTimeString
                    updateData!!.feeUpdTime=MyTimeUtil.nowTimeString
                    presenter.updateFee()
                    dialogView.dialog_progress.visibility = View.VISIBLE
                }
            }
            dialogView.delivery_time.setOnClickListener {
                setTimeDialog()
            }
        }
        dialog.show()
    }

    private fun setTimeDialog() {
        val c = Calendar.getInstance()
        TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            val hour = hourOfDay.toString().padStart(2, '0')
            val mMinute = minute.toString().padStart(2, '0')
            val selectTime = hour + ":" + mMinute
            dialogView.delivery_time.text = selectTime
            updateData!!.feeUpdTime = "${MyTimeUtil.nowDate3} $selectTime:00"
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            0 -> {
                //登录回来
                initData()
            }
            1 -> {
                try {
                    //从详细页回来
                    if (data != null) {
                        //如果有更新就不为空，更新items和总数就可以
                        val d = data.getSerializableExtra("data")
                        val p = data.getIntExtra("position", -1)
                        if (d != null && p != -1) {
                            d as TransServiceBean
                            val t = adapter.tr.rows[p]
                            t.items = d.items
                            t.storeUnitPrice = d.storeUnitPrice
                            t.trsQuantities = d.trsQuantities
                            t.requestNumber = d.requestNumber
                        }
                        adapter.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    val m = e.message ?: "返回数据异常！"
                    showPrompt(m)
                }
            }
        }
    }

    override fun errorDealWith() {
        showRetry(true)
    }

    override fun <T> errorDealWith(eData: T) {
        updateData!!.trsFee=null
        dialogView.dialog_progress.visibility = View.GONE
    }

    override fun <T> updateDone(uData: T) {
        dialog.cancel()
        adapter.notifyDataSetChanged()
        showPrompt(getString(R.string.done))
    }

    override fun showLoading() {
        orderLoading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        orderLoading.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()
        if (!LivingService.isServiceWorked(MyApplication.instance().applicationContext, TransferErrorService.TAG)) {
            startService(Intent(MyApplication.instance().applicationContext, TransferErrorService::class.java))
        }
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

    override fun getData1(): Any? {
        return updateData
    }

    override fun getData2(): Any? {
        return this
    }

}