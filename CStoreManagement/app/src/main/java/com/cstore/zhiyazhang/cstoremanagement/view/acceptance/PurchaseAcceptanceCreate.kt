package com.cstore.zhiyazhang.cstoremanagement.view.acceptance

import android.app.AlertDialog
import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.AcceptanceBean
import com.cstore.zhiyazhang.cstoremanagement.bean.AcceptanceItemBean
import com.cstore.zhiyazhang.cstoremanagement.bean.VendorBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.acceptance.PurchaseAcceptanceCreateAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.acceptance.PurchaseAcceptancePresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import kotlinx.android.synthetic.main.activity_acceptance_create.*
import kotlinx.android.synthetic.main.loading_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2017/9/14 10:22.
 */
class PurchaseAcceptanceCreate(override val layoutId: Int = R.layout.activity_acceptance_create) : MyActivity(), GenericView {

    private lateinit var date: String
    private var ab: AcceptanceBean? = null
    private val aib=ArrayList<AcceptanceItemBean>()
    private val presenter = PurchaseAcceptancePresenter(this, this, this)
    private var vendor: ArrayList<VendorBean>? = null
    private val layoutManager = MyLinearlayoutManager(this@PurchaseAcceptanceCreate, LinearLayout.VERTICAL, false)
    private var adapter: PurchaseAcceptanceCreateAdapter? = null

    override fun initView() {
        date = intent.getStringExtra("date")
        val a = intent.getSerializableExtra("data")
        if (a != null) {
            ab = a as AcceptanceBean
        }
        my_toolbar.title = getString(R.string.create_acceptance)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        acceptance_create_recycler.layoutManager = layoutManager
    }

    override fun initClick() {
        loading.setOnClickListener {
            showPrompt(getString(R.string.wait_loading))
        }
        loading_retry.setOnClickListener {
            loading_progress.visibility = View.VISIBLE
            loading_text.visibility = View.VISIBLE
            loading_retry.visibility = View.GONE
            presenter.getVendor()
        }

        acceptance_save.setOnClickListener {
            val saveAib = (adapter!!.data.filter { it.isChange } as ArrayList<AcceptanceItemBean>)
            if (saveAib.isEmpty()){
                showPrompt(getString(R.string.please_edit_dlv))
            }else saveAcceptance(saveAib)
        }

        acceptance_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                presenter.getCommodity(ab, vendor!![acceptance_spinner.selectedItemPosition].vendorId)
            }

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
        if (adapter == null){
            super.onBackPressed()
            return
        }
        val saveAib = (adapter!!.data.filter { it.isChange } as ArrayList<AcceptanceItemBean>)
        if (saveAib.isNotEmpty()){
            AlertDialog.Builder(this@PurchaseAcceptanceCreate)
                    .setTitle("提示")
                    .setMessage("您的修改的验收商品尚未保存，是否放弃？")
                    .setPositiveButton("保存", { _, _ ->
                        saveAcceptance(saveAib)
                    })
                    .setNegativeButton("退出", { _, _ ->
                        if (ab != null) {
                            val i = Intent()
                            i.putExtra("aib", aib)
                            setResult(0, i)
                        }
                        super.onBackPressed()
                    })
                    .show()
            return
        }
        if (ab != null) {
            val i = Intent()
            i.putExtra("aib", aib)
            setResult(0, i)
        }
        super.onBackPressed()
    }

    private fun saveAcceptance(saveAib:ArrayList<AcceptanceItemBean>) {
        saveAib.forEach {
            it.totalUnitCost=it.unitCost*it.dlvQuantity
            it.retailTotal=it.storeUnitPrice*it.dlvQuantity
            if (ab!=null){
                it.distributionId=ab!!.distributionId
            }
        }
        presenter.createAcceptance(date, ab, saveAib)
    }

    override fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        super.hideLoading()
        loading.visibility = View.GONE
    }

    //获得配送商失败，重试出来
    override fun errorDealWith() {
        loading_progress.visibility = View.GONE
        loading_text.visibility = View.GONE
        loading_retry.visibility = View.VISIBLE
    }

    //获得spinner内的配送商数据
    override fun <T> showView(aData: T) {
        vendor = aData as ArrayList<VendorBean>
        if (vendor == null) {
            errorDealWith()
            return
        }
        val adapterResource = ArrayList<String>()
        if (ab != null) {
            adapterResource.add(ab!!.vendorName)
            vendor = ArrayList<VendorBean>()
            vendor!!.add(VendorBean(ab!!.vendorId, ab!!.vendorName))
        } else {
            vendor!!.forEach { adapterResource.add(it.vendorName) }
        }
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, adapterResource)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        acceptance_spinner.adapter = adapter
    }

    override fun <T> requestSuccess(rData: T) {
        rData as ArrayList<AcceptanceItemBean>
        adapter = PurchaseAcceptanceCreateAdapter(1, rData)
        acceptance_create_recycler.adapter = adapter
        acceptance_save.visibility = View.VISIBLE
    }

    override fun <T> updateDone(uData: T) {
        if (ab == null) {
            //记录创建好的
            ab = uData as AcceptanceBean
        }

        adapter!!.data.removeAll(adapter!!.data.filter { it.isChange })
        adapter!!.notifyDataSetChanged()
        showPrompt(getString(R.string.saveDone))
        //不允许再选别的配送
        acceptance_spinner.isEnabled = false
    }

}