package com.cstore.zhiyazhang.cstoremanagement.view.pay

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Message
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.PayBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.pay.PayAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.pay.PayPresenter
import com.cstore.zhiyazhang.cstoremanagement.sql.CashPayDao
import com.cstore.zhiyazhang.cstoremanagement.sql.WXPayDao
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyScanUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import com.cstore.zhiyazhang.cstoremanagement.utils.QRcodeResolve.qrCodeResolve
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyDividerItemDecoration
import com.uuzuche.lib_zxing.activity.CaptureFragment
import com.uuzuche.lib_zxing.activity.CodeUtils
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.activity_pay.*
import kotlinx.android.synthetic.main.layout_search_line.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.text.DecimalFormat

/**
 * Created by zhiya.zhang
 * on 2017/11/8 16:11.
 *
 * 去PayCollectActivity的时候intent传递参数action，0=收款，1=退款
 */
class PayActivity(override val layoutId: Int = R.layout.activity_pay) : MyActivity() {

    private val isGun = MyApplication.usbGunJudgment()

    private val presenter = PayPresenter(this)

    private val listener = object : ItemClickListener {
        override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {}
        override fun <T> onItemRemove(data: T, position: Int) {
            if (position == 0) {
                //减少
                presenter.updateCommodity(data as String, -1)
            } else {
                //添加
                presenter.updateCommodity(data as String, 1)
            }
        }
    }

    private val adapter = PayAdapter(ArrayList(), listener)

    private var isFlash = true

    private var isOk = true

    private val captureFragment = CaptureFragment()

    private val analyzeCallback = object : CodeUtils.AnalyzeCallback {
        override fun onAnalyzeSuccess(mBitmap: Bitmap, result: String) {
            if (pay_search_line.visibility == View.VISIBLE) return
            if (isOk) {
                isOk = false
                val datas = result.split("|")
                val data = if (datas.size > 1) {
                    datas[datas.size - 1]
                } else {
                    result
                }
                presenter.getCommodity(data)
            }
        }

        override fun onAnalyzeFailed() {
            showPrompt(getString(com.cstore.zhiyazhang.cstoremanagement.R.string.noMessage))
        }

    }

    override fun initView() {
        /*toolbar_time.text = "退款"
        toolbar_time.visibility = View.VISIBLE*/
        search_edit.hint = getString(R.string.idorcode)
        my_toolbar.title = "收款"
        toolbar_btn.text = "清空"
        toolbar_btn.visibility = View.VISIBLE
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        pay_recycler.layoutManager = LinearLayoutManager(this@PayActivity, LinearLayoutManager.VERTICAL, false)
        val dividerItemDecoration = MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        dividerItemDecoration.setDivider(R.drawable.divider_bg)
        pay_recycler.addItemDecoration(dividerItemDecoration)
        pay_recycler.itemAnimator = DefaultItemAnimator()
        pay_recycler.adapter = adapter
        CodeUtils.setFragmentArgs(captureFragment, R.layout.pay_camera)
        captureFragment.analyzeCallback = analyzeCallback
        supportFragmentManager.beginTransaction().replace(R.id.fl_my_container, captureFragment).commit()
        if (isGun) {
            MyScanUtil.getFocusable(search_edit)
            this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            cover.visibility = View.GONE
            pay_search_line.visibility = View.VISIBLE
            isOk = false
            isFlash = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            when (requestCode) {
                0 -> {
                    adapter.removeItem()
                    pay_receivable.text = "0.0"
                    pay_all_money.text = "0.0"
                    pay_all_discount.text = "0.0"
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        judgmentSqlData()
    }

    /**
     * 判断数据库是否有异常数据
     */
    private fun judgmentSqlData() {
        val wxDao = WXPayDao(this)
        val cashDao = CashPayDao(this)
        val wxData = wxDao.getAllData()
        val cashData = cashDao.getAllData()
        if (wxData.any { it.isDone == 0 } && cashData.any { it.isDone == 0 }) {
            MyToast.getLongToast("交易记录异常，正在处理中，请等待处理完毕或联系系统部！")
        }
    }

    override fun onDestroy() {
        CodeUtils.isLightEnable(false)
        super.onDestroy()
    }

    override fun initClick() {
        //退款
        toolbar_time.setOnClickListener {
            val intent = Intent(this@PayActivity, PayCollectActivity::class.java)
            intent.putExtra("action", 1)
            startActivity(intent)
        }
        toolbar_btn.setOnClickListener {
            presenter.deleteData()
            adapter.removeItem()
            updateAllData()
        }
        switch_camera.setOnClickListener {
            cover.visibility = View.GONE
            pay_search_line.visibility = View.VISIBLE
            isOk = false
            CodeUtils.isLightEnable(false)
            isFlash = true
        }
        switch_flash.setOnClickListener {
            CodeUtils.isLightEnable(isFlash)
            isFlash = !isFlash
        }
        qrcode.setOnClickListener {
            cover.visibility = View.VISIBLE
            pay_search_line.visibility = View.GONE
            refreshCamera()
            CodeUtils.isLightEnable(true)
            isFlash = false
        }
        search_btn.setOnClickListener {
            presenter.searchCommodity(search_edit.text.toString())
            val imm = getSystemService(
                    Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(search_edit.windowToken, 0)
            search_edit.setText("")
        }
        search_edit.setOnEditorActionListener { _, actionId, _ ->
            val msg = search_edit.text.toString().replace(" ", "")
            val code = qrCodeResolve(msg)[0]
//            val msg=" http://wx2.rt-store.com/api/wechat/disc-show.jsp?code=\$20180615\$2400\$6932571070011"
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(search_edit.windowToken, 0)
                presenter.searchCommodity(code)
                search_edit.setText("")
                true
            } else if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                if (msg == "") {
                    false
                } else {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(search_edit.windowToken, 0)
                    presenter.searchCommodity(code)
                    search_edit.setText("")
                    true
                }
            } else {
                false
            }
        }
        pay_settle.setOnClickListener {
            if (adapter.data.size == 0) {
                AlertDialog.Builder(this@PayActivity)
                        .setTitle("提示")
                        .setMessage("无商品，请扫描商品后结账")
                        .setPositiveButton("确定", { _, _ ->
                        })
                        .show()
                return@setOnClickListener
            }
            //要给camera切换时间，直接finish
            val intent = Intent(this@PayActivity, PayCollectActivity::class.java)
            intent.putExtra("money", pay_receivable.text)
            intent.putExtra("action", 0)
            startActivityForResult(intent, 0)
        }
    }

    private fun refreshCamera() {
        if (cover.visibility == View.VISIBLE) {
            isOk = true
            val handler = captureFragment.handler
            val msg = Message.obtain()
            msg.what = R.id.restart_preview
            handler!!.sendMessageDelayed(msg, 500)
        }
    }

    override fun initData() {}

    override fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loading.visibility = View.GONE
    }

    private val df = DecimalFormat("######0.00")
    override fun <T> showView(aData: T) {
        if (!toolbar_btn.isEnabled) toolbar_btn.isEnabled = true
        //aData其实就只有一个，数据量太小，不用开线程
        aData as ArrayList<PayBean>
        //如果有值
        if (aData.isNotEmpty()) {
            //检查出相同的
            val newData = adapter.data.filter { it.itemId == aData[0].itemId } as ArrayList<PayBean>
            //如果有相同就从数据中删除，之后再加入adapter的list中
            //直接用从数据库得到的最新数据
            if (newData.isNotEmpty()) {
                adapter.removeItem(newData)
            }
            adapter.addItem(aData)
            updateAllData()
        }
        refreshCamera()

        if (isGun) MyScanUtil.getFocusable(search_edit)
    }

    /**
     * 修改页面下放显示的总数据
     */
    private fun updateAllData() {
        //统计所有品项价格与优惠
        var allMoney = 0.0
        var allDiscount = 0.0
        adapter.data.forEach {
            //所有总价 += 单价 * 数量
            allMoney += (it.storePrice * it.quantity)
            //所有优惠 += 优惠
            allDiscount += it.discAmt
        }
        pay_all_money.text = df.format(allMoney)
        pay_all_discount.text = df.format(allDiscount)
        val receivable = allMoney - allDiscount
        pay_receivable.text = df.format(receivable)
    }

    override fun errorDealWith() {
        refreshCamera()
        if (isGun) MyScanUtil.getFocusable(search_edit)
    }

    //清空数据成功
    override fun <T> requestSuccess(rData: T) {
        toolbar_btn.isEnabled = false
    }

    //清空数据错误
    override fun <T> errorDealWith(eData: T) {
        AlertDialog.Builder(this@PayActivity)
                .setTitle("提示")
                .setMessage("清空数据失败，请重试！")
                .setPositiveButton("重试", { _, _ ->
                    presenter.deleteData()
                })
                .setNegativeButton("取消", { _, _ ->
                })
                .show()
    }
}