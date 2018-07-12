package com.cstore.zhiyazhang.cstoremanagement.view.mobilego

import android.Manifest
import android.content.Context
import android.content.Intent
import android.hardware.Camera
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.MobileDetailBean
import com.cstore.zhiyazhang.cstoremanagement.bean.MobileDetailItemBean
import com.cstore.zhiyazhang.cstoremanagement.bean.MobilePluBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.mobile.MobilePresenter
import com.cstore.zhiyazhang.cstoremanagement.presenter.mobile.MobilePurchaseAdapter
import com.cstore.zhiyazhang.cstoremanagement.utils.*
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.cstore.zhiyazhang.cstoremanagement.view.order.contract.ContractSearchActivity
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.activity_contract.*
import kotlinx.android.synthetic.main.layout_search_title.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

/**
 * Created by zhiya.zhang
 * on 2018/6/21 10:21.
 */
class MobileReturnActivity(override val layoutId: Int = R.layout.activity_contract) : MyActivity(), EasyPermissions.PermissionCallbacks {

    private val presenter = MobilePresenter(this)
    private val isGun = MyApplication.usbGunJudgment()

    private lateinit var driverCode: String
    private lateinit var showAction: Animation
    private lateinit var hideAction: Animation
    private val adapter = MobilePurchaseAdapter(ArrayList(), object : ItemClickListener {
        override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {}
        override fun <T> onItemEdit(data: T, position: Int) {

        }
    })

    override fun initView() {
        val dc = intent.getStringExtra(MobileGoActivity.DIVER_CODE)
        if (dc == null) {
            onBackPressed()
            finish()
        }
        driverCode = dc
        showAction = AnimationUtils.loadAnimation(this, R.anim.anim_slide_in)
        hideAction = AnimationUtils.loadAnimation(this, R.anim.anim_slide_out)
        order_item_next.visibility = View.GONE
        order_item_last.visibility = View.GONE
        mySpinner.visibility = View.GONE
        done.visibility = View.GONE
        search_bar.visibility = View.VISIBLE
        toolbar.setNavigationIcon(R.drawable.ic_action_back)
        toolbar.title = getString(R.string.return_)
        setSupportActionBar(toolbar)
        initGun()

        swipe_recycler.layoutManager = MyLinearlayoutManager(this@MobileReturnActivity, LinearLayoutManager.VERTICAL, false)
        swipe_recycler.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun initClick() {
        search_btn.setOnClickListener {
            presenter.getMobileItem(search_edit.text.toString())
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(search_edit.windowToken, 0)
        }
        search_edit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                presenter.getMobileItem(search_edit.text.toString())
                val imm = getSystemService(
                        Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(search_edit.windowToken, 0)
                search_edit.setText("")
                true
            } else if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                val msg = search_edit.text.toString().replace(" ", "")
                if (msg == "") {
                    false
                } else {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(search_edit.windowToken, 0)
                    presenter.getMobileItem(QRcodeResolve.qrCodeResolve(msg)[0])
                    search_edit.setText("")
                    true
                }
            } else {
                false
            }
        }
        qrcode.setOnClickListener {
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                if (judgmentCarmer()) {
                    val i = Intent(this@MobileReturnActivity, ContractSearchActivity::class.java)
                    i.putExtra(ContractSearchActivity.WHERE_IS_IT, ContractSearchActivity.MJB)
                    startActivityForResult(i, 0)
                }
            } else {
                if (cameraIsCanUse()) {
                    val i = Intent(this@MobileReturnActivity, ContractSearchActivity::class.java)
                    i.putExtra(ContractSearchActivity.WHERE_IS_IT, ContractSearchActivity.MJB)
                    startActivityForResult(i, 0)
                } else {
                    MyToast.getLongToast("您未开启相机权限，请开启相机权限！")
                    val intent = Intent()
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                    intent.data = Uri.fromParts("package", this@MobileReturnActivity.packageName, null)
                    this@MobileReturnActivity.startActivity(intent)
                }
            }
        }
        done.setOnClickListener {
            if (adapter.data.size < 1) {
                showPrompt("没有数据")
            } else {
                val d = MobileDetailBean(null, driverCode, null, 1.0, "1", ArrayList())
                adapter.data.forEach {
                    d.detail.add(MobileDetailItemBean(it.pluId, it.goodsNum!!, it.goodsNum!!))
                }
                presenter.getMobilInsert(d)
            }
        }
    }

    override fun initData() {
    }

    override fun <T> updateDone(uData: T) {
        adapter.data.clear()
        AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle("提示")
                .setMessage("退货完成！")
                .setPositiveButton("确定", { _, _ ->
                    onBackPressed()
                })
                .show()
    }

    override fun <T> errorDealWith(eData: T) {
        AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle("提示")
                .setMessage("退货异常！${eData.toString()}")
                .setPositiveButton("重试", { _, _ ->
                    val d = MobileDetailBean(null, driverCode, null, 1.0, "1", ArrayList())
                    adapter.data.forEach {
                        d.detail.add(MobileDetailItemBean(it.pluId, it.goodsNum!!, it.goodsNum!!))
                    }
                    presenter.getMobilInsert(d)
                }).setNegativeButton("取消", { _, _ -> })
                .show()
    }

    override fun <T> showView(aData: T) {
        val data = aData as MobilePluBean
        val datas = adapter.data.filter { it.pluId == data.pluId }
        if (datas.isNotEmpty()) {
            datas.forEach { it.goodsNum = it.goodsNum!! + 1 }
        } else {
            adapter.data.add(data)
        }
        swipe_recycler.adapter.notifyDataSetChanged()
        if (done.visibility == View.GONE) {
            done.visibility = View.VISIBLE
            done.startAnimation(showAction)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1) {
            if (data != null) {
                val msg = data.getStringExtra("message")
                if (msg != null) {
                    presenter.getMobileItem(msg)
                    return
                }
            }
            showPrompt("获得条码异常！")
        }
    }

    private fun initGun() {
        if (isGun) {
            MyScanUtil.getFocusable(search_edit)
            this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        }
    }

    //获得相机权限
    @pub.devrel.easypermissions.AfterPermissionGranted(1)
    private fun judgmentCarmer(): Boolean {
        val perms = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (!EasyPermissions.hasPermissions(this, *perms)) {
            EasyPermissions.requestPermissions(this, "请打开权限以操作扫描更新", 1, *perms)
            return false
        }
        return true
    }


    /**
     * 返回true 表示可以使用  返回false表示不可以使用
     */
    private fun cameraIsCanUse(): Boolean {
        var isCanUse = true
        var mCamera: Camera? = null
        try {
            mCamera = Camera.open()
            val mParameters = mCamera!!.parameters //针对魅族手机
            mCamera.parameters = mParameters
        } catch (e: Exception) {
            isCanUse = false
        }

        if (mCamera != null) {
            try {
                mCamera.release()
            } catch (e: Exception) {
                e.printStackTrace()
                return isCanUse
            }

        }
        return isCanUse
    }

    //请求权限结果
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    //获取权限失败
    override fun onPermissionsDenied(requestCode: Int, list: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    //获取权限成功
    override fun onPermissionsGranted(requestCode: Int, list: List<String>) {
        val i = Intent(this@MobileReturnActivity, ContractSearchActivity::class.java)
        i.putExtra(ContractSearchActivity.WHERE_IS_IT, ContractSearchActivity.MJB)
        startActivityForResult(i, 0)
    }

    override fun showLoading() {
        my_swipe.isRefreshing = true
    }

    override fun hideLoading() {
        my_swipe.isRefreshing = false
    }
}