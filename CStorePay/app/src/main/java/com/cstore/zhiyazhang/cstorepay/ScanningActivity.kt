package com.cstore.zhiyazhang.cstorepay

import MyWXUtil
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Message
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ContextThemeWrapper
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.cstore.zhiyazhang.cstorepay.ali.AliPayUtil
import com.cstore.zhiyazhang.cstorepay.bean.PayMsgBean
import com.cstore.zhiyazhang.cstorepay.util.*
import com.cstore.zhiyazhang.cstorepay.util.MyHandler.Companion.ERROR
import com.cstore.zhiyazhang.cstorepay.wechat.WXPayConfigImpl
import com.github.wxpay.sdk.WXPay
import com.uuzuche.lib_zxing.activity.CaptureFragment
import com.uuzuche.lib_zxing.activity.CodeUtils
import kotlinx.android.synthetic.main.activity_scanning.*

/**
 * Created by zhiya.zhang
 * on 2018/6/21 16:04.
 */
class ScanningActivity : AppCompatActivity() {
    private val textXml = "<?xml version=\"1.0\" encoding=\"GBK\"?>\n" +
            "<alipay>\n" +
            "    <is_success>T</is_success>\n" +
            "    <request>\n" +
            "        <param name=\"_input_charset\">utf-8</param>\n" +
            "        <param name=\"subject\">C-Store</param>\n" +
            "        <param name=\"dynamic_id\">285224384006121648</param>\n" +
            "        <param name=\"sign\">b451ed4444e0635df4e2687e58bb3be3</param>\n" +
            "        <param name=\"it_b_pay\">5m</param>\n" +
            "        <param name=\"product_code\">BARCODE_PAY_OFFLINE</param>\n" +
            "        <param name=\"dynamic_id_type\">qr_code</param>\n" +
            "        <param name=\"out_trade_no\">1111121636801</param>\n" +
            "        <param name=\"partner\">2088801054284657</param>\n" +
            "        <param name=\"service\">alipay.acquire.createandpay</param>\n" +
            "        <param name=\"total_fee\">3.9</param>\n" +
            "        <param name=\"sign_type\">MD5</param>\n" +
            "        <param name=\"seller_id\">2088801054284657</param>\n" +
            "    </request>\n" +
            "    <response>\n" +
            "        <alipay>\n" +
            "            <buyer_logon_id>186****5028</buyer_logon_id>\n" +
            "            <buyer_user_id>2088802572822707</buyer_user_id>\n" +
            "            <fund_bill_list>\n" +
            "                <TradeFundBill>\n" +
            "                    <amount>3.90</amount>\n" +
            "                    <fund_channel>10</fund_channel>\n" +
            "                </TradeFundBill>\n" +
            "            </fund_bill_list>\n" +
            "            <gmt_payment>2018-06-26 16:42:24</gmt_payment>\n" +
            "            <out_trade_no>1111121636801</out_trade_no>\n" +
            "            <result_code>ORDER_SUCCESS_PAY_SUCCESS</result_code>\n" +
            "            <total_fee>3.90</total_fee>\n" +
            "            <trade_no>2018062621001004700531808391\n" +
            "</trade_no>\n" +
            "        </alipay>\n" +
            "    </response>\n" +
            "    <sign>993bc78bce31e7e40ca5a4997ef4ba88</sign>\n" +
            "    <sign_type>MD5</sign_type>\n" +
            "</alipay>"
    private val isGun = MyApplication.usbGunJudgment()
    private val capture = CaptureFragment()
    private var isOk = true
    private lateinit var payMsg: PayMsgBean
    private lateinit var showAction: Animation
    //确定当前是什么操作
    private lateinit var nowAction: String

    companion object {
        val PAY_MSG = "PayMsg"
        val PAY_USER = "PayUser"
        val MSG = "message"
        val SCAN_RESULT = 528
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanning)
        val x = XMLToJson.xmlToJson(textXml)
        initView()
        initGun()
        initScan()
    }

    private fun initView() {
        nowAction = PAY_MSG
        showAction = AnimationUtils.loadAnimation(this, R.anim.anim_slide_in)
        scan_prompt.text = getString(R.string.scan_prompt_pos)
        scan_prompt.startAnimation(showAction)
    }

    private fun initScan() {
        CodeUtils.setFragmentArgs(capture, R.layout.layout_camera)
        capture.analyzeCallback = analyzeCallback
        supportFragmentManager.beginTransaction().replace(R.id.fl_my_container, capture).commit()
    }

    private fun initGun() {
        if (isGun) {
            gun_key.isEnabled = true
            gun_key.visibility = View.VISIBLE
            gun_key.isFocusable = true
            gun_key.isFocusableInTouchMode = true
            gun_key.requestFocus()
            this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            gun_key.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    val msg = gun_key.text.toString().replace(" ", "")
                    if (msg == "") {
                        false
                    } else {
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(gun_key.windowToken, 0)
                        runMsg(msg)
                        gun_key.setText("")
                        true
                    }
                } else {
                    false
                }
            }
        }
    }

    private fun runMsg(msg: String) {
        when (nowAction) {
            PAY_MSG -> {
                val data = QRCodeResolve.getPayMsg(msg)
                if (data == null) {
                    refreshCamera()
                } else {
                    payMsg = data
                    confirmationAmount()
                }
            }
            PAY_USER -> {
                goPay(msg)
            }
        }
    }

    private fun goPay(msg: String) {
        val tranNo = payMsg.storeId + payMsg.posId + payMsg.payNum + "01"
        showLoading()
        val handler = MyHandler()
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                confirmationPay("成功", "交易完成！", true)
                hideLoading()
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                confirmationPay("失败", errorMessage, false)
                hideLoading()
                handler.cleanAll()
            }
        })

        val one = msg.substring(0, 1)
        if (one == "1") {
            //微信
            wxPay(msg, tranNo, handler)
        } else {
            //支付宝
            aliPay(msg, tranNo, handler)
        }
    }

    private fun aliPay(msg: String, tranNo: String, handler: MyHandler) {
        Thread(Runnable {
            try {
                val aliPay = AliPayUtil(payMsg)
                aliPay.getCreateAndPay(msg, tranNo, handler)
            } catch (e: Exception) {
                val m = Message()
                m.obj = e.message.toString()
                m.what = ERROR
                handler.sendMessage(m)
            }
        }).start()
    }

    private fun wxPay(msg: String, tranNo: String, handler: MyHandler) {
        Thread(Runnable {
            try {
                val data = MyWXUtil.getWXData(payMsg, msg, tranNo)
                val wxPay = WXPay(WXPayConfigImpl.getInstance(payMsg))
                //得到微信的返回结果
                MyWXUtil.microPay(data, wxPay, handler)
            } catch (e: Exception) {
                val m = Message()
                m.obj = e.message.toString()
                m.what = ERROR
                handler.sendMessage(m)
            }
        }).start()
    }

    private fun confirmationPay(title: String, msg: String, isOk: Boolean) {
        val dialog = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle("交易$title")
                .setMessage(msg)
                .setPositiveButton("确认") { _, _ -> }
        if (isOk) {
            dialog.show()
        } else {
            dialog.setNegativeButton("重试") { _, _ ->
                refreshCamera()
            }.show()
        }

        nowAction = PAY_MSG
        scan_prompt.text = getString(R.string.scan_prompt_pos)
        scan_prompt.startAnimation(showAction)
        refreshCamera()
    }

    /**
     * 弹出确认金额提示框
     */
    private fun confirmationAmount() {
        AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle("请确认金额")
                .setMessage("交易金额为 ${payMsg.payAmount} 是否确认？")
                .setPositiveButton("确认") { _, _ ->
                    nowAction = PAY_USER
                    val s = "需支付${payMsg.payAmount}元,${getString(R.string.scan_prompt_user)}"
                    scan_prompt.text = s
                    scan_prompt.startAnimation(showAction)
                    refreshCamera()
                }
                .setNegativeButton("重新扫描") { _, _ ->
                    refreshCamera()
                }
                .show()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle("提示")
                .setMessage("请确认退出系统？")
                .setPositiveButton("退出") { _, _ ->
                    super.onBackPressed()
                    finish()
                }
                .setNegativeButton("按错了", null)
                .show()
    }

    /**
     * 二维码解析回调函数
     */
    private var analyzeCallback: CodeUtils.AnalyzeCallback = object : CodeUtils.AnalyzeCallback {
        override fun onAnalyzeSuccess(mBitmap: Bitmap, result: String) {
            if (isOk) {
                isOk = false
                runMsg(result)
            }
        }

        override fun onAnalyzeFailed() {
            Toast.makeText(this@ScanningActivity, getString(R.string.noMessage), Toast.LENGTH_SHORT).show()
            refreshCamera()
        }
    }

    private fun refreshCamera() {
        isOk = true
        val handler = capture.handler
        val msg = Message.obtain()
        msg.what = R.id.restart_preview
        handler!!.sendMessageDelayed(msg, 500)
    }

    private fun hideLoading() {
        pgb.visibility = View.GONE
    }

    private fun showLoading() {
        pgb.visibility = View.VISIBLE
    }
}