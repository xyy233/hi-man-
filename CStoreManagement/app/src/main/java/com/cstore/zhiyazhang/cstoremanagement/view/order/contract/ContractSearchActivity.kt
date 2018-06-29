package com.cstore.zhiyazhang.cstoremanagement.view.order.contract

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import com.cstore.zhiyazhang.cstoremanagement.utils.QRcodeResolve.qrCodeResolve
import com.cstore.zhiyazhang.cstoremanagement.view.order.category.CategoryItemActivity
import com.uuzuche.lib_zxing.activity.CaptureFragment
import com.uuzuche.lib_zxing.activity.CodeUtils
import kotlinx.android.synthetic.main.activity_contract_search.*


/**
 * Created by zhiya.zhang
 * on 2017/6/19 14:38.
 */
class ContractSearchActivity : AppCompatActivity() {

    companion object {
        val MJB = "MobileGo"
        val FP = "distribution"
        val RTN = "return"
        val RES = "result"
        val UNIT = "unitord"
        val WHERE_IS_IT = "whereIsIt"
    }

    private val isGun = MyApplication.usbGunJudgment()

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contract_search)
        initView()
    }

    private fun initView() {
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
        //qrcode
        val captureFragment = CaptureFragment()
        CodeUtils.setFragmentArgs(captureFragment, R.layout.my_camera)
        captureFragment.analyzeCallback = analyzeCallback
        supportFragmentManager.beginTransaction().replace(R.id.fl_my_container, captureFragment).commit()
        val isReturn = intent.getStringExtra(WHERE_IS_IT)
        if (isReturn != null && isReturn == "return") {
            camera_search_box.visibility = View.VISIBLE
            collect_money.setOnClickListener {
                val searchMsg = refund_out_tran_no.text.toString()
                if (searchMsg.trim() != "") {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(refund_out_tran_no.windowToken, 0)
                    val i = Intent()
                    i.putExtra("message", searchMsg.trim())
                    setResult(0, i)
                    finish()
                } else {
                    MyToast.getShortToast(getString(R.string.please_edit_id))
                }
            }
        }
    }

    private fun runMsg(msg: String) {
        val data = qrCodeResolve(msg)
        when (intent.getStringExtra(WHERE_IS_IT)) {
            UNIT -> {
                val i = Intent(this@ContractSearchActivity, CategoryItemActivity::class.java)
                i.putExtra(WHERE_IS_IT, UNIT)
                i.putExtra("search_message", data[0])
                startActivity(i)
                finish()
            }
            RES -> {
                val i = Intent()
                i.putExtra("message", data[0])
                setResult(0, i)
                finish()
            }
            RTN -> {
                val i = Intent()
                i.putExtra("message", data[0])
                setResult(0, i)
                finish()
            }
            FP -> {
                val i = Intent()
                i.putExtra("message", data[0])
                i.putExtra("bar", data[1])
                setResult(0, i)
                finish()
            }
            MJB -> {
                val i = Intent()
                i.putExtra("message", data[0])
                setResult(1, i)
                finish()
            }
            else -> {
                val i = Intent(this@ContractSearchActivity, ContractActivity::class.java)
                i.putExtra("is_search", true)
                i.putExtra("search_message", data[0])
                i.putExtra("is_all", false)
                startActivity(i)
                finish()
            }
        }
    }

    /**
     * 二维码解析回调函数
     */
    private var analyzeCallback: CodeUtils.AnalyzeCallback = object : CodeUtils.AnalyzeCallback {
        override fun onAnalyzeSuccess(mBitmap: Bitmap, result: String) {
            runMsg(result)
        }

        override fun onAnalyzeFailed() {
            Toast.makeText(this@ContractSearchActivity, getString(com.cstore.zhiyazhang.cstoremanagement.R.string.noMessage), Toast.LENGTH_SHORT).show()
        }
    }

}