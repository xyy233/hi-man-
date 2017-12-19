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
import com.cstore.zhiyazhang.cstoremanagement.presenter.pay.PayMoneyPresenter
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

    private val presenter = PayMoneyPresenter(this)
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
                        presenter.wechatCollectMoney(result, finishMoney)
                        return
                    } else if (one == "2") {
                        isWherePay = "支付宝"
                        showPrompt("暂不支持支付宝收款！")
                        return
                    }
                } else {
                    //退款
                    presenter.wechatRefund(result)
                    return
                }
            }
            showPrompt(getString(R.string.cant_qrcode))
        }

        override fun onAnalyzeFailed() {
            showPrompt(getString(R.string.noMessage))
            refreshCamera()
        }

    }


    private lateinit var refundErrorDialog: AlertDialog.Builder

    //刷新镜头，重新开启扫描
    fun refreshCamera() {
        val handler = captureFragment.handler
        val msg = Message.obtain()
        msg.what = R.id.restart_preview
        handler!!.sendMessageDelayed(msg, 500)
    }

    val handler = Handler()

    override fun initView() {
        refundErrorDialog = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle("提示")
                .setPositiveButton("确定", { _, _ ->
                    //退款失败后返回也要清空
                    setResult(1, Intent())
                    onBackPressed()
                })!!
        action = intent.getIntExtra("action", 0)
        if (action == 0) {
            //收款
            collect_money.text = getString(R.string.collect_money)
            refound_out_tran_no.visibility = View.GONE
        } else {
            //退款
            collect_money.text = getString(R.string.confirm_refund)
            refound_out_tran_no.visibility = View.VISIBLE
            refound_out_tran_no.inputType = InputType.TYPE_CLASS_NUMBER
            refound_out_tran_no.keyListener = DigitsKeyListener.getInstance("1234567890")
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
        refound_out_tran_no.setOnEditorActionListener { _, actionId, _ ->
            if (actionId==EditorInfo.IME_ACTION_DONE){
                refund()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(refound_out_tran_no.windowToken, 0)
                true
            }else false
        }
        collect_money.setOnClickListener {
            if (action == 0) {
                //收款
                showPrompt("暂未开发完毕")
            } else {
                //退款
                refund()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(refound_out_tran_no.windowToken, 0)
            }
        }
    }

    private fun refund(){
        val outTranNo = refound_out_tran_no.text.toString()
        if (outTranNo.isNotEmpty()) {
            presenter.wechatRefund(outTranNo)
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
        rData as Map<String, String>
        AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle("提示")
                .setMessage("交易成功！已收到 ${rData["total_fee"]!!.toDouble() / 100}元。")
                .setPositiveButton("确定", { _, _ ->
                    //交易成功才去清空上页的数据
                    setResult(1, Intent())
                    super.onBackPressed()
                }).setNegativeButton("退款", { _, _ ->
            presenter.wechatRefund(rData)
        })
                .show()
    }

    //收款失败
    override fun <T> errorDealWith(eData: T) {
        AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle("提示")
                .setMessage("交易失败！${eData.toString()}")
                .setPositiveButton("确定", { _, _ ->
                    refreshCamera()
                })
                .show()
    }

    /**
     * 退款成功
     */
    fun refundSuccess(data: Map<String, String>) {
        AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle("提示")
                .setMessage("退款成功！已退 ${data["refund_fee"]!!.toDouble() / 100}元")
                .setPositiveButton("确定", { _, _ ->
                    //退款成功也晴空
                    setResult(1, Intent())
                    onBackPressed()
                })
                .show()
    }

    /**
     * 退款失败
     */
    fun refundError(errorMessage: String, data: Map<String, String>) {
        refundErrorDialog.setMessage("退款失败！$errorMessage")
                .setNegativeButton("重试", { _, _ ->
                    presenter.wechatRefund(data)
                }).show()
    }

    /**
     * 退款失败
     */
    fun refundError(errorMessage: String, code: String) {
        refundErrorDialog.setMessage("退款失败！$errorMessage")
                .setNegativeButton("重试", { _, _ ->
                    presenter.wechatRefund(code)
                }).show()
    }
}