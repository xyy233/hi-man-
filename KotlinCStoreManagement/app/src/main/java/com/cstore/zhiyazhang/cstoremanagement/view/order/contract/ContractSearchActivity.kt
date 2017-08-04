package com.cstore.zhiyazhang.cstoremanagement.view.order.contract

import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.view.order.category.CategoryItemActivity
import com.uuzuche.lib_zxing.activity.CaptureFragment
import com.uuzuche.lib_zxing.activity.CodeUtils
import kotlinx.android.synthetic.main.activity_contract_search.*

/**
 * Created by zhiya.zhang
 * on 2017/6/19 14:38.
 */
class ContractSearchActivity(override val layoutId: Int = R.layout.activity_contract_search) : MyActivity() {

    var nowLayout = 0

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        //qrcode
        var captureFragment = CaptureFragment()
        CodeUtils.setFragmentArgs(captureFragment, R.layout.my_camera)
        captureFragment.analyzeCallback = analyzeCallback
        supportFragmentManager.beginTransaction().replace(R.id.fl_my_container, captureFragment).commit()
        qrcode.setOnClickListener {
            if (nowLayout != 0) {
                captureFragment = CaptureFragment()
                CodeUtils.setFragmentArgs(captureFragment, R.layout.my_camera_qrcode)
                captureFragment.analyzeCallback = analyzeCallback
                supportFragmentManager.beginTransaction().replace(R.id.fl_my_container, captureFragment).commit()
                nowLayout = 0
            }
        }
        barcode.setOnClickListener {
            if (nowLayout != 1) {
                captureFragment = CaptureFragment()
                CodeUtils.setFragmentArgs(captureFragment, R.layout.my_camera_barcode)
                captureFragment.analyzeCallback = analyzeCallback
                supportFragmentManager.beginTransaction().replace(R.id.fl_my_container, captureFragment).commit()
                nowLayout = 1
            }
        }
    }

    /**
     * 二维码解析回调函数
     */
    internal var analyzeCallback: CodeUtils.AnalyzeCallback = object : CodeUtils.AnalyzeCallback {
        override fun onAnalyzeSuccess(mBitmap: Bitmap, result: String) {
            when(intent.getStringExtra("whereIsIt")){
                "unitord"->{
                    val i = Intent(this@ContractSearchActivity, CategoryItemActivity::class.java)
                    i.putExtra("whereIsIt", "unitord")
                    i.putExtra("search_message", result)
                    startActivity(i)
                    finish()
                }
                else->{
                    val i = Intent(this@ContractSearchActivity, ContractActivity::class.java)
                    i.putExtra("is_search", true)
                    i.putExtra("search_message", result)
                    i.putExtra("is_all", false)
                    startActivity(i)
                    finish()
                }
            }
        }

        override fun onAnalyzeFailed() {
            Toast.makeText(this@ContractSearchActivity, getString(com.cstore.zhiyazhang.cstoremanagement.R.string.qrcode_nomessage), Toast.LENGTH_SHORT).show()
        }
    }
}