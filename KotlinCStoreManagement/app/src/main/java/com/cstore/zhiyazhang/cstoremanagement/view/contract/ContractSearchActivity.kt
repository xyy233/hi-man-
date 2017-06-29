package com.cstore.zhiyazhang.cstoremanagement.view.contract

import com.cstore.zhiyazhang.cstoremanagement.R
import com.uuzuche.lib_zxing.activity.CaptureFragment
import com.uuzuche.lib_zxing.activity.CodeUtils
import com.zhiyazhang.mykotlinapplication.utils.MyActivity

/**
 * Created by zhiya.zhang
 * on 2017/6/19 14:38.
 */
class ContractSearchActivity(override val layoutId: Int = R.layout.activity_contract_search) : MyActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        //qrcode
        val captureFragment = CaptureFragment()
        com.uuzuche.lib_zxing.activity.CodeUtils.setFragmentArgs(captureFragment, R.layout.my_camera)
        captureFragment.analyzeCallback = analyzeCallback
        supportFragmentManager.beginTransaction().replace(R.id.fl_my_container, captureFragment).commit()
    }

    /**
     * 二维码解析回调函数
     */
    internal var analyzeCallback: CodeUtils.AnalyzeCallback = object : CodeUtils.AnalyzeCallback {
        override fun onAnalyzeSuccess(mBitmap: android.graphics.Bitmap, result: String) {
            val i = android.content.Intent(this@ContractSearchActivity, ContractActivity::class.java)
            i.putExtra("is_search", true)
            i.putExtra("search_message", result)
            i.putExtra("is_all",false)
            startActivity(i)
            finish()
        }

        override fun onAnalyzeFailed() {
            android.widget.Toast.makeText(this@ContractSearchActivity, getString(com.cstore.zhiyazhang.cstoremanagement.R.string.qrcode_nomessage), android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}