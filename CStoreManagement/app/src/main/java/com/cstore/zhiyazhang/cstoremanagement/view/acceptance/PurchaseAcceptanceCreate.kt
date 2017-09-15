package com.cstore.zhiyazhang.cstoremanagement.view.acceptance

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.text.method.DigitsKeyListener
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.AcceptanceBean
import com.cstore.zhiyazhang.cstoremanagement.bean.AcceptanceItemBean
import com.cstore.zhiyazhang.cstoremanagement.bean.VendorBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.acceptance.PurchaseAcceptancePresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import kotlinx.android.synthetic.main.activity_acceptance_create.*
import kotlinx.android.synthetic.main.loading_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2017/9/14 10:22.
 */
class PurchaseAcceptanceCreate(override val layoutId: Int=R.layout.activity_acceptance_create) : MyActivity(),GenericView{

    private lateinit var date:String
    private var ab: AcceptanceBean?=null
    private var aib:AcceptanceItemBean?=null
    private val presenter= PurchaseAcceptancePresenter(this,this,this)
    private var vendor:ArrayList<VendorBean>?=null

    override fun initView() {
        date=intent.getStringExtra("date")
        val a=intent.getSerializableExtra("data")
        if (a!=null){
            ab=a as AcceptanceBean
        }
        my_toolbar.title = getString(R.string.create_acceptance)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        acceptance_order_id.keyListener= DigitsKeyListener.getInstance("1234567890")
        dlv_quantity.keyListener=DigitsKeyListener.getInstance("1234567890")
    }

    override fun initClick() {
        loading.setOnClickListener {
            showPrompt(getString(R.string.wait_loading))
        }
        loading_retry.setOnClickListener {
            loading_progress.visibility=View.VISIBLE
            loading_text.visibility=View.VISIBLE
            loading_retry.visibility=View.GONE
            presenter.getVendor()
        }
        acceptance_order_id.setOnEditorActionListener { _, actionId, _ ->
            if (actionId==EditorInfo.IME_ACTION_SEARCH){
                searchCommodity()
                true
            }else{
                false
            }
        }
        acceptance_sure.setOnClickListener {
            searchCommodity()
        }
        dlv_quantity.setOnEditorActionListener { _, actionId, _ ->
            if (actionId== EditorInfo.IME_ACTION_DONE){
                saveAcceptance()
                true
            }else{
                false
            }
        }
        acceptance_save.setOnClickListener {
            saveAcceptance()
        }
    }

    private fun searchCommodity(){
        if (acceptance_order_id.text.toString()==""){
            showPrompt(getString(R.string.please_edit_id))
            return
        }
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(acceptance_order_id.windowToken, 0)
        presenter.getCommodity(acceptance_order_id.text.toString(), vendor!![acceptance_spinner.selectedItemPosition].vendorId)
        acceptance_create_data.visibility=View.GONE
        acceptance_save.visibility=View.GONE
    }

    private fun saveAcceptance(){
        if (dlv_quantity.text.toString()==""){
            showPrompt(getString(R.string.please_edit_dlv))
            return
        }
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(dlv_quantity.windowToken, 0)
        aib!!.dlvQuantity=dlv_quantity.text.toString().toInt()
        aib!!.totalUnitCost=aib!!.unitCost*aib!!.dlvQuantity
        aib!!.retailTotal=aib!!.storeUnitPrice*aib!!.dlvQuantity
        if (ab!=null){
            aib!!.distributionId=ab!!.distributionId
        }
        presenter.createAcceptance(date,ab,aib!!)
    }

    override fun initData() {
        presenter.getVendor()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->onBackPressed()
        }
        return true
    }

    override fun onBackPressed() {
        if (dlv_quantity.text.toString()!=""){
            AlertDialog.Builder(this@PurchaseAcceptanceCreate)
                    .setTitle("提示")
                    .setMessage("您的验收商品尚未保存，是否放弃？")
                    .setPositiveButton("保存", { _, _ ->
                        saveAcceptance()
                    })
                    .setNegativeButton("退出", { _, _ ->
                        if (ab!=null){
                            val i=Intent()
                            i.putExtra("aib",aib)
                            setResult(0,i)
                        }
                        super.onBackPressed()
                    })
                    .show()
            return
        }
        if (ab!=null){
            val i=Intent()
            i.putExtra("aib",aib)
            setResult(0,i)
        }
        super.onBackPressed()
    }

    override fun showLoading() {
        loading.visibility=View.VISIBLE
    }

    override fun hideLoading() {
        super.hideLoading()
        loading.visibility=View.GONE
    }

    //获得配送商失败，重试出来
    override fun errorDealWith() {
        loading_progress.visibility=View.GONE
        loading_text.visibility=View.GONE
        loading_retry.visibility=View.VISIBLE
    }

    //获得spinner内的配送商数据
    override fun <T> showView(aData: T) {
        vendor=aData as ArrayList<VendorBean>
        if (vendor==null){
            errorDealWith()
            return
        }
        val adapterResource=ArrayList<String>()
        if (ab!=null){
            adapterResource.add(ab!!.vendorName)
            vendor=ArrayList<VendorBean>()
            vendor!!.add(VendorBean(ab!!.vendorId,ab!!.vendorName))
        }else{
            vendor!!.forEach { adapterResource.add(it.vendorName) }
        }
        val adapter=ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,adapterResource)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        acceptance_spinner.adapter=adapter
    }

    override fun <T> requestSuccess(rData: T) {
        rData as ArrayList<AcceptanceItemBean>
        aib=rData[0]
        acceptance_create_data.visibility=View.VISIBLE
        commodity_id.text=aib!!.itemId
        commodity_name.text=aib!!.itemName
        tax_sell_cost.text=aib!!.taxSellCost.toString()
        retail.text=aib!!.storeUnitPrice.toString()
        acceptance_save.visibility=View.VISIBLE
    }

    override fun <T> updateDone(uData: T) {
        if(ab==null){
            //记录创建好的
            ab=uData as AcceptanceBean
        }
        showPrompt(getString(R.string.saveDone))
        //不允许再选别的配送
        acceptance_spinner.isEnabled=false
        dlv_quantity.setText("")
        acceptance_create_data.visibility=View.GONE
        acceptance_save.visibility=View.GONE
        acceptance_order_id.setText("")
    }

}