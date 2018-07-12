package com.cstore.zhiyazhang.cstoremanagement.view.order.returnpurchase

import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ReasonBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ReturnPurchaseItemBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ReturnedPurchaseBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.returnpurchase.ReturnPurchaseItemAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.returnpurchase.ReturnPurchaseItemPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.activity_purchase_acceptance_item.*
import kotlinx.android.synthetic.main.loading_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2017/11/3 14:36.
 */
class ReturnPurchaseItemActivity(override val layoutId: Int = R.layout.activity_purchase_acceptance_item) : MyActivity() {

    private lateinit var data: ReturnedPurchaseBean
    private lateinit var date: String
    private lateinit var adapter: ReturnPurchaseItemAdapter
    private val layoutManager = MyLinearlayoutManager(this, LinearLayout.VERTICAL, false)
    private val presenter = ReturnPurchaseItemPresenter(this)

    override fun initView() {
        date = intent.getStringExtra("date")
        data = intent.getSerializableExtra("data") as ReturnedPurchaseBean
        val title = "单号：${data.requestNumber}"
        my_toolbar.title = title
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        acceptance_item_recycler.layoutManager = layoutManager
        acceptance_item_save.text = getString(R.string.save)
        acceptance_item_save.setTextColor(ContextCompat.getColor(this, R.color.white))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun initClick() {
        loading.setOnClickListener {
            showPrompt(getString(R.string.wait_loading))
        }
        loading_retry.setOnClickListener {
            saveData()
        }
        acceptance_item_save.setOnClickListener {
            saveData()
        }
        acceptance_search_line.keyListener = DigitsKeyListener.getInstance("1234567890")
        acceptance_search_line.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                //输入结束,根据输入的字符到对应位置
                if (acceptance_search_line.text.length >= 3) {
                    for (i in 0 until data.allItem.size) {
                        if (data.allItem[i].itemNumber.contains(acceptance_search_line.text)) {
                            if (i == data.allItem.size - 1) {
                                layoutManager.stackFromEnd = true
                            } else {
                                layoutManager.scrollToPositionWithOffset(i, 0)
                            }
                            break
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //开始输入
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //文字变化
            }
        })
        if (CStoreCalendar.getCurrentDate(2) != date) {
            acceptance_item_save.visibility = View.GONE
        }
    }

    private fun saveData() {
        if (data.allItem.isEmpty()) {
            showPrompt(getString(R.string.noMessage))
            return
        }
        if (data.allItem.any { it.editCount != 0 }) {
            presenter.updateReturnPurchase(date, data)
        } else {
            showPrompt(getString(R.string.saveDone))
        }
    }

    override fun onBackPressed() {
        judgmentFinish()
    }

    private fun judgmentFinish() {
        if (data.allItem.any { it.editCount != 0 }) {
            AlertDialog.Builder(ContextThemeWrapper(this@ReturnPurchaseItemActivity, R.style.AlertDialogCustom))
                    .setTitle("提示")
                    .setMessage("您未保存修改，是否保存？")
                    .setPositiveButton("保存", { _, _ ->
                        presenter.updateReturnPurchase(date, data)
                    })
                    .setNegativeButton("放弃") { _, _ ->
                        exit()
                    }
                    .show()
            return
        }
        exit()
    }

    private fun exit() {
        //返回主页
        val i = Intent()
        i.putExtra("newData", data)
        setResult(0, i)
        super.onBackPressed()
    }

    override fun initData() {
        adapter = ReturnPurchaseItemAdapter(date, data.allItem, ReasonBean.getAllReason(), object : ItemClickListener {
            //添加按钮
            override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {
                val i = Intent(this@ReturnPurchaseItemActivity, ReturnPurchaseCreateActivity::class.java)
                i.putExtra("date", date)
                i.putExtra("data", data)
                i.putExtra("type", 1)
                startActivityForResult(i, 1)
            }
        })
        acceptance_item_recycler.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data!=null){
            when (resultCode) {
                1 -> {
                    try {
                        val rb = data.getSerializableExtra("newData") as ArrayList<ReturnPurchaseItemBean>
                        addNewDataList(rb)
                        adapter.notifyDataSetChanged()
                    } catch (e: Exception) {
                        Log.e("预约退货item", e.message.toString())
                    }
                }
            }
        }
    }

    //像data里添加新数据并重新计算
    private fun addNewDataList(rb: ArrayList<ReturnPurchaseItemBean>) {
        data.allItem.removeAll(rb)
        data.allItem.addAll(rb)
        data.itemCount = data.allItem.size
        var rtnQTY = 0
        var total = 0.0
        data.allItem.forEach {
            rtnQTY += it.plnRtnQuantity
            total += it.storeUnitPrice * it.plnRtnQuantity
        }
        data.rtnQuantity = rtnQTY
        data.total = total.toFloat()
    }

    override fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loading.visibility = View.GONE
    }

    override fun <T> updateDone(uData: T) {
        val newData = data.allItem.filter { it.editCount != 0 }
        //这里修改 编辑数量  检查data是否已有相同商品，有的话删除，最后添加最新数据进去
        for (rb in newData) {
            rb.editCount = 0
            val filterData = data.allItem.filter { it.itemNumber == rb.itemNumber }
            if (filterData.isNotEmpty()) {
                //存在就先删除
                data.allItem.removeAll(filterData)
            }
            //添加最新数据
            data.allItem.add(rb)
        }
        //重新计算data数据
        addNewDataList(newData as ArrayList<ReturnPurchaseItemBean>)
        adapter.notifyDataSetChanged()
        loading.visibility = View.GONE
        loading_progress.visibility = View.VISIBLE
        loading_text.visibility = View.VISIBLE
        loading_retry.visibility = View.GONE
    }

    override fun errorDealWith() {
        loading_progress.visibility = View.GONE
        loading_text.visibility = View.GONE
        loading_retry.visibility = View.VISIBLE
    }

}