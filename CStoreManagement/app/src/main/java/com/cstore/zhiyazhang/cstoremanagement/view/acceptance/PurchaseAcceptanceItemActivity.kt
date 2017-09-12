package com.cstore.zhiyazhang.cstoremanagement.view.acceptance

import android.content.Context
import android.support.v7.app.AlertDialog
import android.text.method.DigitsKeyListener
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.AcceptanceBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.acceptance.PurchaseAcceptanceItemAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.acceptance.PurchaseAcceptancePresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.activity_purchase_acceptance_item.*
import kotlinx.android.synthetic.main.dialog_acceptance_item.view.*
import kotlinx.android.synthetic.main.loading_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.text.DecimalFormat

/**
 * Created by zhiya.zhang
 * on 2017/9/12 8:54.
 */
class PurchaseAcceptanceItemActivity(override val layoutId: Int= R.layout.activity_purchase_acceptance_item) : MyActivity(),GenericView{

    private lateinit var dialog: AlertDialog
    private lateinit var dialogView:View
    private val presenter=PurchaseAcceptancePresenter(this,this,this)
    private lateinit var date:String
    private lateinit var ab:AcceptanceBean
    private lateinit var adapter:PurchaseAcceptanceItemAdapter

    override fun initView() {
        date=intent.getStringExtra("date")
        ab=intent.getSerializableExtra("data") as AcceptanceBean
        if (ab.allItems.isEmpty()){
            showPrompt("数据错误，请重试!")
            finish()
        }
        val title="单号:${ab.distributionId}"
        my_toolbar.title=title
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        acceptance_item_recycler.layoutManager = MyLinearlayoutManager(this@PurchaseAcceptanceItemActivity, LinearLayout.VERTICAL, false)
        val builder=AlertDialog.Builder(this@PurchaseAcceptanceItemActivity)
        dialogView=View.inflate(this,R.layout.dialog_acceptance_item,null)!!
        builder.setView(dialogView)
        builder.setCancelable(true)
        dialog=builder.create()
        dialogView.dialog_cancel.setOnClickListener {
            dialog.cancel()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->onBackPressed()
        }
        return true
    }

    override fun initClick() {
        loading.setOnClickListener {
            showPrompt(getString(R.string.wait_loading))
        }
        loading_retry.setOnClickListener {
            presenter.updateAcceptance(date,ab)
        }
        acceptance_item_save.setOnClickListener {
            presenter.updateAcceptance(date,ab)
        }
        judgmentZhuanRi()
    }

    private fun judgmentZhuanRi(){
//确保是能操作的,选择时间等于换日时间并且换日成功状态
        if (date==CStoreCalendar.getCurrentDate(3)&&CStoreCalendar.getNowStatus(3)==0){
            //是未验收的就显示转次日操作
            if (ab.dlvStatus==0){
                toolbar_btn.visibility=View.VISIBLE
                toolbar_btn.text = "转次日"
                toolbar_btn.setOnClickListener {
                    ab.dlvStatus=3
                    presenter.updateAcceptance(date,ab)
                }
            }else if (ab.dlvStatus==3){//是转次日的就显示取消转次日
                toolbar_btn.visibility=View.VISIBLE
                toolbar_btn.text="取消转次日"
                toolbar_btn.setOnClickListener{
                    ab.dlvStatus=0
                    presenter.updateAcceptance(date,ab)
                }
            }
            //已验收的不允许操作，跳过
        }
    }

    override fun initData() {
        adapter= PurchaseAcceptanceItemAdapter(ab, date, object : ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                when(CStoreCalendar.getNowStatus(1)){
                    1->{
                        showPrompt(getString(R.string.huanri))
                        return
                    }
                    2->{
                        showPrompt(getString(R.string.huanri_error))
                        return
                    }
                }
                val title=ab.allItems[position].itemId+"/"+ab.allItems[position].itemName
                dialogView.dialog_title.text=title
                dialogView.trs.setText(ab.allItems[position].trsQuantity.toString())
                dialogView.dlv.setText(ab.allItems[position].dlvQuantity.toString())
                dialogView.trs.keyListener=DigitsKeyListener.getInstance("1234567890")
                dialogView.dlv.keyListener=DigitsKeyListener.getInstance("1234567890")
                refreshSaveClick(position)
                dialog.show()
            }
        })
        acceptance_item_recycler.adapter=adapter
    }

    /**
     * 判断是否有未提交的信息，根据选择决定finish
     */
    private fun judgmentFinish() {
        if (acceptance_item_save.visibility==View.VISIBLE) {
                AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("您有未保存的修改，是否放弃？")
                        .setPositiveButton("保存", { _, _ ->
                            presenter.updateAcceptance(date,ab)
                        })
                        .setNegativeButton("放弃") { _, _ ->
                            super.onBackPressed()
                        }
                        .show()
        } else super.onBackPressed()
    }

    override fun onBackPressed() {
        judgmentFinish()
    }

    private fun refreshSaveClick(position: Int) {
        dialogView.dialog_save.setOnClickListener {
            //判断输入的到货量
            when(dialogView.trs.text.toString()){
                ""->{
                    //为空报错
                    showPrompt(getString(R.string.noEditMsg))
                    return@setOnClickListener
                }
                ab.allItems[position].trsQuantity.toString()->{//相同就不管
                }
                else->{
                    if (dialogView.trs.text.toString().toInt()<dialogView.dlv.text.toString().toInt()){
                        showPrompt(getString(R.string.dlv_more_trs))
                        return@setOnClickListener
                    }
                    //不同显示保存按钮
                    acceptance_item_save.visibility=View.VISIBLE
                    ab.allItems[position].trsQuantity=dialogView.trs.text.toString().toInt()
                }
            }
            //判断输入的验收量
            when(dialogView.dlv.text.toString()){
                ""->{
                    //为空报错
                    showPrompt(getString(R.string.noEditMsg))
                    return@setOnClickListener
                }
                ab.allItems[position].dlvQuantity.toString()->{//相同就不管
                }
                else->{
                    //因为有可能到货量和之前相同就没有检查
                    if (dialogView.trs.text.toString().toInt()<dialogView.dlv.text.toString().toInt()){
                        showPrompt(getString(R.string.dlv_more_trs))
                        return@setOnClickListener
                    }
                    //不同显示保存按钮
                    acceptance_item_save.visibility=View.VISIBLE
                    ab.allItems[position].dlvQuantity=dialogView.dlv.text.toString().toInt()
                    //修改内部数据
                    val df=DecimalFormat("######0.00")
                    ab.allItems[position].totalUnitCost = df.format(ab.allItems[position].unitCost * ab.allItems[position].dlvQuantity).toDouble()
                    ab.allItems[position].retailTotal = df.format(ab.allItems[position].storeUnitPrice * ab.allItems[position].dlvQuantity).toDouble()
                }
            }
            acceptance_item_recycler.adapter.notifyDataSetChanged()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(dialogView.trs.windowToken, 0)
            imm.hideSoftInputFromWindow(dialogView.dlv.windowToken, 0)
            dialog.hide()
        }
    }

    override fun <T> updateDone(uData: T) {
        judgmentZhuanRi()
        acceptance_item_recycler.adapter.notifyDataSetChanged()
        acceptance_item_save.visibility=View.GONE
        loading.visibility=View.GONE
        loading_progress.visibility=View.VISIBLE
        loading_text.visibility=View.VISIBLE
        loading_retry.visibility=View.GONE
    }

    override fun showLoading() {
        loading.visibility=View.VISIBLE
    }

    override fun hideLoading() {
        super.hideLoading()
        loading.visibility=View.GONE
    }

    override fun errorDealWith() {
        loading_progress.visibility=View.GONE
        loading_text.visibility=View.GONE
        loading_retry.visibility=View.VISIBLE
    }

}