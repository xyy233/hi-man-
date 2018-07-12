package com.cstore.zhiyazhang.cstoremanagement.view.order.returnpurchase

import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ReasonBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ReturnPurchaseItemBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ReturnedPurchaseBean
import com.cstore.zhiyazhang.cstoremanagement.bean.VendorBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.returnpurchase.ReturnPurchaseCreateAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.returnpurchase.ReturnPurchaseCreatePresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.ReturnView
import kotlinx.android.synthetic.main.activity_acceptance_create.*
import kotlinx.android.synthetic.main.loading_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2017/11/3 14:41.
 */
class ReturnPurchaseCreateActivity(override val layoutId: Int = R.layout.activity_acceptance_create) : MyActivity(), ReturnView, GenericView {

    private lateinit var date: String
    private var type = 0
    private var rb: ReturnedPurchaseBean? = null
    private var existedVendor: ArrayList<VendorBean>? = null
    private val rib = ArrayList<ReturnPurchaseItemBean>()
    private val presenter = ReturnPurchaseCreatePresenter(this)
    private val vendor = ArrayList<VendorBean>()
    private val layoutManager = MyLinearlayoutManager(this, LinearLayout.VERTICAL, false)
    private val adapter = ReturnPurchaseCreateAdapter(ArrayList())
    private var vendorId = ""

    override fun initView() {
        type = intent.getIntExtra("type", 0)
        date = intent.getStringExtra("date")
        val data = intent.getSerializableExtra("data")
        if (data != null) {
            rb = data as ReturnedPurchaseBean
        }
        val vendorData = intent.getSerializableExtra("vendor")
        if (vendorData != null) {
            existedVendor = vendorData as ArrayList<VendorBean>
        }
        my_toolbar.title = getString(R.string.return_purchase)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        acceptance_create_recycler.layoutManager = layoutManager
        acceptance_create_recycler.adapter = adapter
        return_time_box.visibility = View.VISIBLE
        return_recent.isChecked = true
        return_long.isChecked = false
        judgment_inv.isChecked = false
        acceptance_save.setTextColor(ContextCompat.getColor(this, R.color.white))
    }

    fun getJudgmentInv(): Boolean {
        return judgment_inv.isChecked
    }

    override fun initClick() {
        loading.setOnClickListener { showPrompt(getString(R.string.wait_loading)) }
        loading_retry.setOnClickListener {
            loading_progress.visibility = View.VISIBLE
            loading_text.visibility = View.VISIBLE
            loading_retry.visibility = View.GONE
            presenter.getVendor()
        }
        acceptance_save.setOnClickListener { saveData() }
        acceptance_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                vendorId = vendor[position].vendorId
                presenter.getCommodity()
            }
        }
        return_recent.setOnClickListener {
            return_recent.toggle()
            return_long.toggle()
            presenter.getCommodity()
        }
        return_long.setOnClickListener {
            return_recent.toggle()
            return_long.toggle()
            presenter.getCommodity()
        }
        judgment_inv.setOnClickListener {
            judgment_inv.toggle()
            presenter.getCommodity()
        }
    }

    override fun initData() {
        presenter.getVendor()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun onBackPressed() {
        if (adapter.itemCount == 0) {
            exit()
            super.onBackPressed()
            return
        }
        val saveRb = adapter.data.filter { it.editCount != 0 }
        if (saveRb.isNotEmpty()) {
            AlertDialog.Builder(this@ReturnPurchaseCreateActivity)
                    .setTitle("提示")
                    .setMessage("您的修改的验收商品尚未保存，是否放弃？")
                    .setPositiveButton("保存", { _, _ ->
                        saveData()
                    })
                    .setNegativeButton("退出", { _, _ ->
                        exit()
                    })
                    .show()
            return
        }
        exit()
    }

    /**
     * 退出，把数据返回去
     * rb==null 就代表从主页来，不为空就是从item来，主页来的让主页重新获取数据
     */
    private fun exit() {
        val i = Intent()
        if (rb != null) {
            //返回item页
            i.putExtra("newData", rib)
            setResult(1, i)
        } else {
            //返回主页
            i.putExtra("isNew", true)
            setResult(1, i)
        }
        super.onBackPressed()
    }

    /**
     * 保存数据
     */
    private fun saveData() {
        val saveRb = adapter.data.filter { it.editCount != 0 }
        if (saveRb.isEmpty()) {
            showPrompt(getString(R.string.noEditMsg))
            return
        }
        saveRb.forEach {
            if (rb != null) {
                it.requestNumber = rb!!.requestNumber
            }
        }
        presenter.createReturnPurchase(date)
    }

    //得到配送商
    override fun <T> requestSuccess(rData: T) {
        rData as ArrayList<VendorBean>
        //删除已有退货单的配送商
        if (existedVendor != null) {
            for (v in existedVendor!!) {
                rData.removeAll(rData.filter { it.vendorId == v.vendorId })
            }
        }
        vendor.addAll(rData)
        if (vendor.isEmpty()) {
            errorDealWith()
            return
        }
        val adapterResource = ArrayList<String>()
        if (rb != null) {
            adapterResource.add(rb!!.vendorName)
            vendor.clear()
            vendor.add(VendorBean(rb!!.vendorId, rb!!.vendorName))
        } else {
            vendor.forEach { adapterResource.add(it.vendorName) }
        }
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, adapterResource)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        acceptance_spinner.adapter = adapter
    }

    //获得配送商失败，重试出来
    override fun errorDealWith() {
        loading_progress.visibility = View.GONE
        loading_text.visibility = View.GONE
        loading_retry.visibility = View.VISIBLE
    }

    //获得商品后
    override fun <T> showView(aData: T) {
        aData as ArrayList<ReturnPurchaseItemBean>
        aData.forEach {
            if (it.plnRtnDate0 == null) {
                it.plnRtnDate0 = date
            }
            val reason = ReasonBean.getReason("00")
            it.reasonNumber = reason.reasonId
            it.reasonName = reason.reasonName
        }
        adapter.data.clear()
        adapter.addItem(aData)
        if (aData.size == 0) {
            acceptance_save.visibility = View.GONE
        } else {
            acceptance_save.visibility = View.VISIBLE
        }
    }

    override fun <T> updateDone(uData: T) {
        val newData = adapter.data.filter { it.editCount != 0 }
        adapter.data.removeAll(newData)
        newData.forEach { it.editCount = 0 }
        rib.addAll(newData)
        adapter.notifyDataSetChanged()
        acceptance_spinner.isEnabled = false
        if (type == 0) {
            onBackPressed()
            return
        }
    }

    override fun getRPB(): ReturnedPurchaseBean? {
        return rb
    }

    override fun getSelectVendor(): String {
        return vendorId
    }

    override fun getSelectType(): Int {
        return if (return_recent.isChecked) 0
        else 1
    }

    override fun getCreateData(): ArrayList<ReturnPurchaseItemBean> {
        return adapter.data.filter { it.editCount != 0 } as ArrayList<ReturnPurchaseItemBean>
    }

    override fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loading.visibility = View.GONE
    }

}