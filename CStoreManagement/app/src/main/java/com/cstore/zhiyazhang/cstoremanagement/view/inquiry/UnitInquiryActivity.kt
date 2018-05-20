package com.cstore.zhiyazhang.cstoremanagement.view.inquiry

import android.content.Context
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.bumptech.glide.Glide
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.UnitInquiryBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.inquiry.UnitInquiryPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyCameraUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.view.order.contract.ContractSearchActivity
import kotlinx.android.synthetic.main.activity_unit_inquiry.*
import kotlinx.android.synthetic.main.dialog_cashdaily.view.*
import kotlinx.android.synthetic.main.layout_search_line.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.text.DecimalFormat

/**
 * Created by zhiya.zhang
 * on 2018/1/30 16:17.
 */
class UnitInquiryActivity(override val layoutId: Int = R.layout.activity_unit_inquiry) : MyActivity() {
    private val presenter = UnitInquiryPresenter(this)
    private lateinit var dialogView: View
    private lateinit var saveDialog: AlertDialog
    private var allData: UnitInquiryBean? = null
    /**
     * 0=库存 1=最小量
     */
    private var whereSave = 0
    /**
     * 0=品号/品名 1=条码
     */
    private var whereSearch = 0
    private var msg = ""
    private lateinit var showAction: Animation
    private lateinit var hideAction: Animation
    private var uib: UnitInquiryBean? = null
    /**
     * flag用于判断是从那里来做什么的。
     * 0->库存管理点进来的 1->库存异常单品进入
     */
    private var flag = 0
    /**
     * 从库存异常传入，其他无用
     */
    private var pluId: String? = null
    /**
     * 用来判断是否有保存，如果有返回上页面的时候需要处理提醒上页面更行数据
     */
    private var isSave = false


    override fun initView() {
        my_toolbar.title = getString(R.string.unitSearch)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        showAction = AnimationUtils.loadAnimation(this, R.anim.anim_slide_in)
        hideAction = AnimationUtils.loadAnimation(this, R.anim.anim_slide_out)
        val builder = AlertDialog.Builder(this)
        dialogView = View.inflate(this, R.layout.dialog_cashdaily, null)!!
        builder.setView(dialogView)
        builder.setCancelable(true)
        saveDialog = builder.create()
        dialogView.dialog_edit.inputType = InputType.TYPE_CLASS_NUMBER
        dialogView.dialog_edit.keyListener = DigitsKeyListener.getInstance("1234567890")
        if (intent.getIntExtra("flag", 0) != 0) {
            flag = intent.getIntExtra("flag", 0)
            search.visibility = View.GONE
            pluId = intent.getStringExtra("pluId")
            if (pluId == null) {
                showPrompt("出现异常，请联系系统部！")
                onBackPressed()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun onBackPressed() {
        if (flag == 1 || flag == 2) {
            val i = Intent()
            if (allData != null) {
                i.putExtra("inv", allData!!.invQty)
                i.putExtra("min", allData!!.minQty)
            }
            i.putExtra("pluId", pluId)
            setResult(flag, i)
            finish()
        } else
            super.onBackPressed()
    }

    override fun initClick() {
        dialogView.dialog_cancel.setOnClickListener {
            saveDialog.cancel()
        }
        dialogView.dialog_save.setOnClickListener {
            dialogView.dialog_progress.visibility = View.VISIBLE
            if (dialogView.dialog_edit.text.toString().isEmpty()) {
                showPrompt(getString(R.string.please_edit_id))
                return@setOnClickListener
            }
            val value = dialogView.dialog_edit.text.toString().toInt()
            if (whereSave == 0) {
                //保存库存
                uib!!.invQty = value
                presenter.saveInv()
            } else {
                //保存最小量-自设排面量
                uib!!.minQty = value
                presenter.saveMinQty()
            }
        }
        inv_box.setOnClickListener {
            dialogView.dialog_title.text = getString(R.string.inventory)
            whereSave = 0
            saveDialog.show()
        }
        min_box.setOnClickListener {
            dialogView.dialog_title.text = getString(R.string.min_qty)
            whereSave = 1
            saveDialog.show()
        }
        search_edit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                editSearch()
                true
            } else {
                false
            }
        }
        search_btn.setOnClickListener {
            editSearch()
        }
        qrcode.setOnClickListener {
            if (MyCameraUtil.getPermissions(this)) {
                val i = Intent(this@UnitInquiryActivity, ContractSearchActivity::class.java)
                i.putExtra("whereIsIt", "return")
                startActivityForResult(i, 0)
            }
        }
    }

    private fun editSearch() {
        msg = search_edit.text.toString().trim()
        if (msg.isEmpty()) {
            showPrompt(getString(R.string.noMessage))
        } else {
            whereSearch = 0
            showDetail(false)
            search_edit.setText("")
            presenter.getData()
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(search_edit.windowToken, 0)
        }
    }

    override fun initData() {
        if (flag == 1 || flag == 2) {
            presenter.getData()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            when (resultCode) {
                0 -> {
                    msg = data.getStringExtra("message")
                    whereSearch = 1
                    showDetail(false)
                    presenter.getData()
                }
            }
        }
    }

    //获得activity
    override fun getData1(): Any {
        return this@UnitInquiryActivity
    }

    //获得搜索关键字
    override fun getData2(): Any {
        return if (flag == 0) msg else pluId!!
    }

    //获得是搜索什么的
    override fun getData3(): Any {
        return whereSearch
    }

    //得到单品数据
    override fun getData4(): Any? {
        return uib
    }

    //显示loading
    override fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    //隐藏loading
    override fun hideLoading() {
        loading.visibility = View.GONE
    }

    //显示数据
    override fun <T> showView(aData: T) {
        if (aData is UnitInquiryBean) {
            uib = aData
            mShowView(aData)
        } else {
            showPrompt("错误的数据类型！")
        }
    }

    override fun errorDealWith() {
        dialogView.dialog_progress.visibility = View.GONE
        dialogView.dialog_edit.setText("")
    }

    override fun <T> requestSuccess(rData: T) {
        if (flag == 1) isSave = true
        plu_inv.text = uib!!.invQty.toString()
        dialogView.dialog_progress.visibility = View.GONE
        dialogView.dialog_edit.setText("")
        saveDialog.cancel()
    }

    override fun <T> requestSuccess2(rData: T) {
        if (flag == 1) isSave = true
        plu_min.text = uib!!.minQty.toString()
        dialogView.dialog_progress.visibility = View.GONE
        dialogView.dialog_edit.setText("")
        saveDialog.cancel()
    }

    private val ip = MyApplication.getIP()
    private fun mShowView(data: UnitInquiryBean) {
        allData = data
        val df = DecimalFormat("#####.##")
        Glide.with(this).load("http://$ip:8666/uploadIMG/${data.pluId}.png")
                .placeholder(R.mipmap.loading)
                .error(R.mipmap.load_error)
                .crossFade()
                .into(plu_img)
        plu_id.text = data.pluId
        plu_name.text = data.pluName
        plu_inv.text = data.invQty.toString()
        plu_min.text = data.minQty.toString()
        pln_retail.text = df.format(data.storeUnitPrice)
        enter_cost.text = df.format(data.storeSellCost)
        sales_cost.text = df.format(data.storeUnitCost)
        schedule1.text = data.inThCode
        schedule2.text = data.stopThCode
        schedule3.text = data.outThCode
        dms.text = df.format(data.dms)
        dma.text = df.format(data.dma)
        scrap_check.isChecked = data.mrkType == "Y"
        date_return_check.isChecked = data.sReturnType == "Y"
        trs_check.isChecked = data.trsType == "Y"
        sales_check.isChecked = data.saleType == "Y"
        order_check.isChecked = data.orderType == "Y"
        return_check.isChecked = data.returnType == "Y"

        val salesDate = if (sales_check.isChecked) "销售日期：${MyTimeUtil.deleteTime(data.saleBeginDate)} — ${MyTimeUtil.deleteTime(data.saleEndDate)}" else ""
        sales_date.text = salesDate
        val orderDate = if (order_check.isChecked) "订货日期：${MyTimeUtil.deleteTime(data.orderBeginDate)} — ${MyTimeUtil.deleteTime(data.orderEndDate)}" else ""
        order_date.text = orderDate
        val returnDate = if (return_check.isChecked) "退货日期：${MyTimeUtil.deleteTime(data.returnBeginDate)} — ${MyTimeUtil.deleteTime(data.returnEndDate)}" else ""
        return_date.text = returnDate
        supplier_id.text = data.supplierId
        supplier_name.text = data.supplierName
        vendor.text = data.vendorName
        showDetail(true)
    }

    private fun showDetail(action: Boolean) {
        if (action) {
            if (detail_box.visibility == View.GONE) {
                detail_box.visibility = View.VISIBLE
                detail_box.startAnimation(showAction)
            }
        } else {
            if (detail_box.visibility == View.VISIBLE) {
                detail_box.visibility = View.GONE
                detail_box.startAnimation(hideAction)
            }
        }
    }

}