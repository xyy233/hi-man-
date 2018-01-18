package com.cstore.zhiyazhang.cstoremanagement.view.pay

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.PosBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.pay.ALIPayPresenter
import com.cstore.zhiyazhang.cstoremanagement.presenter.pay.CashPayPresenter
import com.cstore.zhiyazhang.cstoremanagement.presenter.pay.WXPayPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.qrcode.CaptureFragment
import com.cstore.zhiyazhang.cstoremanagement.utils.qrcode.CodeUtils
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import kotlinx.android.synthetic.main.activity_pay_camera.*
import kotlinx.android.synthetic.main.loading_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2017/11/13 17:34.
 */
class PayCollectActivity(override val layoutId: Int = R.layout.activity_pay_camera) : MyActivity(), GenericView {

    private val wxPresenter = WXPayPresenter(this)
    private val aliPresenter = ALIPayPresenter(this)
    private val cashPresenter = CashPayPresenter(this)
    private val pos = PosBean("", 0, 0)
    private val captureFragment = CaptureFragment()
    private var isWherePay = ""
    private var money: String? = null
    private var action: Int = 0
    private val analyzeCallback = object : CodeUtils.AnalyzeCallback {
        override fun onAnalyzeSuccess(mBitmap: Bitmap, result: String?) {
            if (result != null) {
                if (action == 0) {
                    //收款
                    val one = result.substring(0, 1)
                    if (one == "1") {
                        isWherePay = "微信"
                        money = intent.getStringExtra("money")
                        if (money == null) {
                            showPrompt("系统错误，获得金额失败，请联系系统部")
                            return
                        }
                        val finishMoney = money!!.toDouble()
                        wxPresenter.wechatCollectMoney(result, finishMoney)
                        return
                    } else if (one == "2") {
                        isWherePay = "支付宝"
                        money = intent.getStringExtra("money")
                        if (money == null) {
                            showPrompt("系统错误，获得金额失败，请联系系统部")
                            return
                        }
                        val finishMoney = money!!.toDouble()
                        aliPresenter.aliCollectMoney(result, finishMoney)
                        return
                    }
                } else {
                    //退款
                    isWherePay = if (switch1.isChecked) {
                        aliPresenter.aliRefund(result)
                        "支付宝"
                    } else {
                        wxPresenter.wechatRefund(result)
                        "微信"
                    }
                    return
                }
            }
            showPrompt(getString(R.string.cant_qrcode))
            refreshCamera()
        }

        override fun onAnalyzeFailed() {
            showPrompt(getString(R.string.noMessage))
            refreshCamera()
        }

    }
    private lateinit var collectDialog: AlertDialog.Builder


    //刷新镜头，重新开启扫描
    fun refreshCamera() {
        val handler = captureFragment.handler
        val msg = Message.obtain()
        msg.what = R.id.restart_preview
        handler!!.sendMessageDelayed(msg, 500)
    }

    val handler = Handler()

    override fun initView() {

        collectDialog = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle("提示")!!

        action = intent.getIntExtra("action", 0)
        if (action == 0) {
            //收款
            collect_money.text = getString(R.string.collect_money)
            refund_out_tran_no.visibility = View.GONE
            refund_money_box.visibility = View.GONE
        } else {
            //退款
            refund_money_box.visibility = View.VISIBLE
            collect_money.text = getString(R.string.confirm_refund)
            refund_out_tran_no.visibility = View.VISIBLE
            refund_out_tran_no.inputType = InputType.TYPE_CLASS_NUMBER
            refund_out_tran_no.keyListener = DigitsKeyListener.getInstance("1234567890")
            switch1.visibility = View.VISIBLE
        }
        loading_text.setTextColor(ContextCompat.getColor(this, R.color.white))
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        CodeUtils.setFragmentArgs(captureFragment, R.layout.pay_camera2)
        captureFragment.analyzeCallback = analyzeCallback
        supportFragmentManager.beginTransaction().replace(R.id.fl_my_container, captureFragment).commit()
    }

    override fun initClick() {
        loading.setOnClickListener {
            showPrompt(getString(R.string.wait_loading))
        }
        refund_out_tran_no.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                refund()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(refund_out_tran_no.windowToken, 0)
                true
            } else false
        }
        collect_money.setOnClickListener {
            if (action == 0) {
                //现金收款
                AlertDialog.Builder(ContextThemeWrapper(this@PayCollectActivity, R.style.AlertDialogCustom))
                        .setTitle("提示")
                        .setMessage("请确认进行现金收款！")
                        .setPositiveButton("现金收款") { _, _ ->
                            isWherePay = "现金"
                            money = intent.getStringExtra("money")
                            if (money == null) {
                                showPrompt("系统错误，获得金额失败，请联系系统部")
                                return@setPositiveButton
                            }
                            val finishMoney = money!!.toDouble()
                            cashPresenter.cashCollect(finishMoney)
                        }
                        .setNegativeButton("取消") { _, _ -> }
                        .show()
            } else {
                //条码退款
                refund()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(refund_out_tran_no.windowToken, 0)
            }
        }
        refund_money.setOnClickListener {
            //现金退款
            showPrompt(getString(R.string.not_open_now))
        }
    }

    private fun refund() {
        val outTranNo = refund_out_tran_no.text.toString()
        if (outTranNo.isNotEmpty()) {
            isWherePay = if (switch1.isChecked) {
                aliPresenter.aliRefund(outTranNo)
                "支付宝"
            } else {
                wxPresenter.wechatRefund(outTranNo)
                "微信"
            }
        } else {
            showPrompt("请输入商户订单号或扫描退款码！")
        }
    }

    override fun initData() {
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loading.visibility = View.GONE
    }

    fun getPos(): PosBean {
        return pos
    }

    fun setPos(posBean: PosBean) {
        pos.assPos = posBean.assPos
        pos.telSeq = MyApplication.getOnlyid()
        pos.nextTranNo = posBean.nextTranNo
    }



    //收款成功
    override fun <T> requestSuccess(rData: T) {
        when (isWherePay) {
            "微信" -> {
                rData as Map<String, String>
                collectDialog.setMessage("交易成功！已收到 ${rData["total_fee"]!!.toDouble() / 100}元。")
                        .setPositiveButton("确定", { _, _ ->
                            //交易成功才去清空上页的数据
                            setResult(1, Intent())
                            super.onBackPressed()
                            finish()
                        })
                        /*.setNegativeButton("退款", { _, _ ->
                            wxPresenter.wechatRefund(rData)
                        })*/
                        .show()
            }
            "支付宝" -> {
                rData as String
                collectDialog.setMessage("交易成功！已收到$rData 元。")
                        .setPositiveButton("确定", { _, _ ->
                            //交易成功才去清空上页的数据
                            setResult(1, Intent())
                            super.onBackPressed()
                            finish()
                        })
                        .show()
            }
            "现金" -> {
                collectDialog.setMessage("交易完成！")
                        .setPositiveButton("确定", { _, _ ->
                            //交易成功才去清空上页的数据
                            setResult(1, Intent())
                            super.onBackPressed()
                        })
                        .show()
            }
        }
/*        AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle("提示")
                .setMessage("交易成功！已收到 ${rData["total_fee"]!!.toDouble() / 100}元。")
                .setPositiveButton("确定", { _, _ ->
                    //交易成功才去清空上页的数据
                    setResult(1, Intent())
                    super.onBackPressed()
                }).setNegativeButton("退款", { _, _ ->
            wxPresenter.wechatRefund(rData)
        })
                .show()*/
    }


    //收款失败
    override fun <T> errorDealWith(eData: T) {
        collectDialog.setMessage("交易失败！${eData.toString()}")
                .setPositiveButton("确定", { _, _ ->
                    refreshCamera()
                })
                .show()

    }

    /**
     * 退款成功
     */
    fun <T> refundSuccess(data: T) {
        when (isWherePay) {
            "微信" -> {
                data as Map<String, String>
                collectDialog.setMessage("退款成功！已退 ${data["refund_fee"]!!.toDouble() / 100}元")
                        .setPositiveButton("确定", { _, _ ->
                            //退款成功也清空
                            setResult(1, Intent())
                            onBackPressed()
                        })
                        .show()
            }
            "支付宝" -> {
                data as String
                collectDialog.setMessage("退款成功！已退 ${data}元")
                        .setPositiveButton("确定", { _, _ ->
                            //退款成功也清空
                            setResult(1, Intent())
                            onBackPressed()
                        })
                        .show()
            }
        }

    }

    /**
     * 退款失败
     */
    fun refundError(errorMessage: String, data: Map<String, String>) {
        collectDialog.setMessage("退款失败！$errorMessage")
                .setPositiveButton("确定", { _, _ ->
                    //退款失败后返回也要清空
                    setResult(1, Intent())
                    onBackPressed()
                })
                .setNegativeButton("重试", { _, _ ->
                    wxPresenter.wechatRefund(data)
                }).show()
    }

    /**
     * 退款失败
     */
    fun refundError(errorMessage: String, code: String) {
        collectDialog.setMessage("退款失败！$errorMessage")
                .setPositiveButton("确定", { _, _ ->
                    //退款失败后返回也要清空
                    setResult(1, Intent())
                    onBackPressed()
                })
                .setNegativeButton("重试", { _, _ ->
                    when (isWherePay) {
                        "微信" -> {
                            wxPresenter.wechatRefund(code)
                        }
                        "支付宝" -> {
                            aliPresenter.aliRefund(code)
                        }
                    }
                }).show()
    }
}