package com.cstore.zhiyazhang.cstoremanagement.view.acceptance

import android.content.Context
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.AcceptanceBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ReturnAcceptanceBean
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
    private lateinit var rab:ReturnAcceptanceBean
    private var type:Int=1
    private lateinit var adapter:PurchaseAcceptanceItemAdapter
    private val layoutManager=MyLinearlayoutManager(this@PurchaseAcceptanceItemActivity, LinearLayout.VERTICAL, false)

    override fun initView() {
        date=intent.getStringExtra("date")
        type=intent.getIntExtra("type",1)
        if (type==1){
            ab=intent.getSerializableExtra("data") as AcceptanceBean
        }else{
            rab=intent.getSerializableExtra("data") as ReturnAcceptanceBean
        }
        val title="单号:${if (type==1)ab.distributionId else rab.distributionId}"
        my_toolbar.title=title
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        acceptance_item_recycler.layoutManager = layoutManager
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
            saveDate()
        }
        acceptance_item_save.setOnClickListener {
            saveDate()
        }
        judgmentZhuanRi()
        acceptance_search_line.keyListener=DigitsKeyListener.getInstance("1234567890")
        acceptance_search_line.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                //输入结束
                if (acceptance_search_line.text.length>=3){
                    for (i in 0 until if (type==1)ab.allItems.size else rab.allItems.size){
                        if (if (type==1)ab.allItems[i].itemId.contains(acceptance_search_line.text) else rab.allItems[i].itemId.contains(acceptance_search_line.text)){
                            if (i==if (type==1)ab.allItems.size-1 else rab.allItems.size-1){
                                //直接到最后
                                layoutManager.stackFromEnd = true
                            }else{
                                //滑动到指定位置
                                layoutManager.scrollToPositionWithOffset(i, 0)
                            }
                            break
                        }
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //开始输入
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //文字变化
            }
        })
    }

    private fun judgmentZhuanRi(){
//确保是能操作的,选择时间等于换日时间并且换日成功状态
        if (date==CStoreCalendar.getCurrentDate(3)&&CStoreCalendar.getNowStatus(3)==0){
            //是未验收的就显示转次日操作
            if (if (type==1)ab.dlvStatus==3 else rab.rtnStatus==3){//是转次日的就显示取消转次日
                toolbar_btn.visibility=View.VISIBLE
                toolbar_btn.text="取消转次日"
                toolbar_btn.setOnClickListener{
                    if (type==1)ab.isChange=true else rab.isChange=true
                    if (type==1)ab.dlvStatus=0 else rab.rtnStatus=0
                    saveDate()
                }
            }else{
                toolbar_btn.visibility=View.VISIBLE
                toolbar_btn.text = "转次日"
                toolbar_btn.setOnClickListener {
                    if (type==1)ab.isChange=true else rab.isChange=true
                    if (type==1)ab.dlvStatus=3 else rab.rtnStatus=3
                    saveDate()
                }
            }
        }else{
            acceptance_item_save.visibility=View.GONE
        }
    }

    override fun initData() {
        adapter= PurchaseAcceptanceItemAdapter(type, if (type==1)ab.allItems else rab.allItems, date, object : ItemClickListener {
            override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {
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
                val title=if (type==1)ab.allItems[position].itemId+"/"+ab.allItems[position].itemName else rab.allItems[position].itemId+"/"+rab.allItems[position].itemName
                dialogView.dialog_title.text=title
                if (type==1){
                    dialogView.dlv.setText(ab.allItems[position].dlvQuantity.toString())
                    dialogView.dlv.keyListener=DigitsKeyListener.getInstance("1234567890")
                }else{
                    dialogView.dlv.visibility=View.GONE
                    dialogView.dlv_text.visibility=View.GONE
                    dialogView.trs_text.text=getString(R.string.returnQuantity)
                }
                dialogView.trs.setText(if (type==1)ab.allItems[position].trsQuantity.toString() else rab.allItems[position].rtnQuantity.toString())
                dialogView.trs.keyListener=DigitsKeyListener.getInstance("1234567890")
                refreshSaveClick(position)
                dialog.show()
            }

            override fun onItemLongClick(view: RecyclerView.ViewHolder, position: Int) {
                val i= Intent(this@PurchaseAcceptanceItemActivity,PurchaseAcceptanceCreate::class.java)
                i.putExtra("date",date)
                i.putExtra("type",type)
                i.putExtra("data",if (type==1)ab else rab)
                startActivityForResult(i,0)
            }
        })
        acceptance_item_recycler.adapter=adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(resultCode){
            0->{
                try {
                    val aib=data!!.getSerializableExtra("aib") as ArrayList<*>
                    //ab.allItems.addAll(aib)
                    adapter.addItem(aib)
                }catch (e:Exception){
                    Log.e("验收item",e.message)
                }
            }
        }
    }

    /**
     * 判断是否有未提交的信息，根据选择决定finish
     */
    private fun judgmentFinish() {
        if (if (type==1)ab.dlvStatus==0||ab.isChange else rab.rtnStatus==0||rab.isChange) {
                AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("您未进行验收，是否放弃？")
                        .setPositiveButton("验收", { _, _ ->
                            saveDate()
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

    private fun saveDate(){
        if (if (type==1)ab.allItems.isEmpty()else rab.allItems.isEmpty()){
            showPrompt(getString(R.string.noMessage))
            return
        }
        //未验收和有修改就去保存
        if (if (type==1)ab.dlvStatus==0||ab.isChange else rab.rtnStatus==0||rab.isChange){
            if (type==1)presenter.updateAcceptance(date,ab)else presenter.updateReturnAcceptance(date,rab)
        }else{
            showPrompt(getString(R.string.saveDone))
        }
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
                if (type==1)ab.allItems[position].trsQuantity.toString()else rab.allItems[position].rtnQuantity.toString()->{//相同就不管
                }
                else->{
                    if (type==1){
                        if (dialogView.trs.text.toString().toInt()<dialogView.dlv.text.toString().toInt()){
                            showPrompt(getString(R.string.dlv_more_trs))
                            return@setOnClickListener
                        }
                        ab.allItems[position].trsQuantity=dialogView.trs.text.toString().toInt()
                        ab.isChange=true
                    }
                    else {
                        rab.allItems[position].rtnQuantity=dialogView.trs.text.toString().toInt()
                        rab.isChange=true
                    }
                }
            }
            if (type==1){
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
                        ab.allItems[position].dlvQuantity=dialogView.dlv.text.toString().toInt()
                        //修改内部数据
                        val df=DecimalFormat("######0.00")
                        ab.allItems[position].totalUnitCost = df.format(ab.allItems[position].unitCost * ab.allItems[position].dlvQuantity).toDouble()
                        ab.allItems[position].retailTotal = df.format(ab.allItems[position].storeUnitPrice * ab.allItems[position].dlvQuantity).toDouble()
                        ab.isChange=true
                    }
                }
            }
            acceptance_item_recycler.adapter.notifyDataSetChanged()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(dialogView.trs.windowToken, 0)
            dialog.hide()
        }
    }

    override fun <T> updateDone(uData: T) {
        judgmentZhuanRi()
        if (type==1){
            ab.isChange=false
        } else {
            rab.isChange=false
        }
        acceptance_item_recycler.adapter.notifyDataSetChanged()
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