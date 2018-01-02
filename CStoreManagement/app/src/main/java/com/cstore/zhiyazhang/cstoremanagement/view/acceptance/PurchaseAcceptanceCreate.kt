package com.cstore.zhiyazhang.cstoremanagement.view.acceptance

import android.app.AlertDialog
import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.*
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
    private var ab: AcceptanceBean? = null//上一页传过来的，如果上一页是在单子里面创建的就是空的，如果是在商品内就有，查询商品的时候要检查是否为空,如果内部有数据那就去除掉那些
    private var rab: ReturnAcceptanceBean? = null
    private var type = 1
    private val aib = ArrayList<AcceptanceItemBean>()
    private val raib = ArrayList<ReturnAcceptanceItemBean>()
    private val presenter = PurchaseAcceptancePresenter(this, this)
    private var vendor: ArrayList<VendorBean>? = null
    private val layoutManager = MyLinearlayoutManager(this@PurchaseAcceptanceCreate, LinearLayout.VERTICAL, false)
    private var adapter: PurchaseAcceptanceCreateAdapter? = null

    override fun initView() {
        type = intent.getIntExtra("type", 1)
        date = intent.getStringExtra("date")
        val a = intent.getSerializableExtra("data")
        if (a != null) {
            if (type == 1) ab = a as AcceptanceBean else rab = a as ReturnAcceptanceBean
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
            val saveAib = if (type == 1) {
                (adapter!!.data as ArrayList<AcceptanceItemBean>).filter { it.isChange } as ArrayList<AcceptanceItemBean>
            } else {
                (adapter!!.data as ArrayList<ReturnAcceptanceItemBean>).filter { it.isChange } as ArrayList<ReturnAcceptanceItemBean>
            }

            if (saveAib.isEmpty()) {
                showPrompt(getString(R.string.please_edit_dlv))
            } else {
                if (type == 1) {
                    saveAib as ArrayList<AcceptanceItemBean>
                    saveAcceptance(saveAib)
                } else {
                    saveAib as ArrayList<ReturnAcceptanceItemBean>
                    saveReturnAcceptance(saveAib)
                }
            }
        }

        acceptance_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (type == 1) presenter.getCommodity(ab, vendor!![acceptance_spinner.selectedItemPosition].vendorId)
                else presenter.getReturnCommodity(rab, vendor!![acceptance_spinner.selectedItemPosition].vendorId)
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
        if (adapter == null) {
            super.onBackPressed()
            return
        }

        val saveAib = if (type == 1) {
            (adapter!!.data as ArrayList<AcceptanceItemBean>).filter { it.isChange } as ArrayList<AcceptanceItemBean>
        } else {
            (adapter!!.data as ArrayList<ReturnAcceptanceItemBean>).filter { it.isChange } as ArrayList<ReturnAcceptanceItemBean>
        }

        if (saveAib.isNotEmpty()) {
            AlertDialog.Builder(this@PurchaseAcceptanceCreate)
                    .setTitle("提示")
                    .setMessage("您的修改的验收商品尚未保存，是否放弃？")
                    .setPositiveButton("保存", { _, _ ->
                        if (type == 1) {
                            saveAib as ArrayList<AcceptanceItemBean>
                            saveAcceptance(saveAib)
                        } else {
                            saveAib as ArrayList<ReturnAcceptanceItemBean>
                            saveReturnAcceptance(saveAib)
                        }
                    })
                    .setNegativeButton("退出", { _, _ ->
                        if (if (type==1)ab != null else rab!=null) {
                            val i = Intent()
                            if (type == 1) i.putExtra("aib", aib) else i.putExtra("aib", raib)
                            setResult(0, i)
                        }
                        super.onBackPressed()
                    })
                    .show()
            return
        }

        if (if (type==1)ab != null else rab!=null) {
            val i = Intent()
            if (type == 1) i.putExtra("aib", aib) else i.putExtra("aib", raib)
            setResult(0, i)
        }

        super.onBackPressed()

    }

    private fun saveAcceptance(saveAib: ArrayList<AcceptanceItemBean>) {
        saveAib.forEach {
            it.totalUnitCost = it.unitCost * it.dlvQuantity
            it.retailTotal = it.storeUnitPrice * it.dlvQuantity
            if (ab != null) {
                it.distributionId = ab!!.distributionId
            }
        }
        presenter.createAcceptance(date, ab, saveAib)
    }

    private fun saveReturnAcceptance(saveAib: ArrayList<ReturnAcceptanceItemBean>) {
        saveAib.forEach {
            if (rab != null) {
                it.distributionId = rab!!.distributionId
            }
        }
        presenter.createReturnAcceptance(date, rab, saveAib)
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
        if (ab != null || rab != null) {
            adapterResource.add(if (type==1)ab!!.vendorName else rab!!.vendorName)
            vendor = ArrayList()
            if (type==1){
                vendor!!.add(VendorBean(ab!!.vendorId, ab!!.vendorName))
            }else{
                vendor!!.add(VendorBean(rab!!.vendorId, rab!!.vendorName))
            }
        } else {
            vendor!!.forEach { adapterResource.add(it.vendorName) }
        }
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, adapterResource)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        acceptance_spinner.adapter = adapter
    }

    override fun <T> requestSuccess(rData: T) {
        adapter = if (type == 1) {
            rData as ArrayList<AcceptanceItemBean>
            PurchaseAcceptanceCreateAdapter(type, rData)
        } else {
            rData as ArrayList<ReturnAcceptanceItemBean>
            PurchaseAcceptanceCreateAdapter(type, rData)
        }
        acceptance_create_recycler.adapter = adapter
        acceptance_save.visibility = View.VISIBLE
    }

    override fun <T> updateDone(uData: T) {
        if (type==1){
            val a=(adapter!!.data as ArrayList<AcceptanceItemBean>).filter { it.isChange }
            aib.addAll(a)
            adapter!!.data.removeAll(a)
        }else{
            val a = (adapter!!.data as ArrayList<ReturnAcceptanceItemBean>).filter { it.isChange }
            raib.addAll(a)
            adapter!!.data.removeAll(a)
        }

        adapter!!.notifyDataSetChanged()
        showPrompt(getString(R.string.saveDone))
        //不允许再选别的配送
        acceptance_spinner.isEnabled = false
    }

}