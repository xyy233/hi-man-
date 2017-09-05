package com.cstore.zhiyazhang.cstoremanagement.view.order.contract

import android.Manifest
import android.content.Context
import android.content.Intent
import android.hardware.Camera
import android.net.Uri
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ContractTypeBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.contract.ContractTypeAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.contract.ContractTypePresenter
import com.cstore.zhiyazhang.cstoremanagement.sql.ContractTypeDao
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.ContractTypeView
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import kotlinx.android.synthetic.main.activity_contract_type.*
import kotlinx.android.synthetic.main.contract_type_recycler.*
import kotlinx.android.synthetic.main.layout_search_title.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions




/**
 * Created by zhiya.zhang
 * on 2017/6/12 15:03.
 */
class ContractTypeActivity(override val layoutId: Int = R.layout.activity_contract_type) : MyActivity(), ContractTypeView, GenericView, EasyPermissions.PermissionCallbacks {


    override val whereIsIt: String
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun showUsaTime(isShow: Boolean) {
        usa_time.visibility=if (isShow)View.VISIBLE else View.GONE
    }

    var adapter: ContractTypeAdapter? = null
    val ctd = ContractTypeDao(this)
    val presenter = ContractTypePresenter(this, this, ctd)
    private var previousIntent: Intent? = null//上个页面的intent

    override fun initClick() {
        //重试按钮点击事件
        retry.setOnClickListener {
            retry.visibility = View.GONE
            presenter.getAllContractType()
        }
    }

    override fun initData() {
        presenter.getAllContractType()
    }

    override fun initView() {
        previousIntent = intent
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        if (isJustLook) {
            //承包商品修改一览表
            my_toolbar.title = resources.getString(R.string.contract_order_toview)
            search_bar.visibility = View.GONE
            header_text1.text = getString(R.string.type_name)
            header_text2.text = getString(R.string.edit_sky)
            header_text3.text = getString(R.string.original_sales2)
            header_text4.text = getString(R.string.real_count2)
        } else {
            //承包商品订货
            my_toolbar.title = resources.getString(R.string.contract_order)
            search_bar.visibility = View.VISIBLE
            header_text1.text = getString(R.string.type_name)
            header_text2.text = getString(R.string.inventory)
            header_text3.text = getString(R.string.tonightCount)
            header_text4.text = getString(R.string.todayCount)
            //搜索按钮点击事件
            search_btn.setOnClickListener {
                search()
            }
            search_edit.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search()
                    true
                } else
                    false
            }
            //二维码按钮点击事件
            qrcode.setOnClickListener {
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    if (judgmentCarmer()) {
                        startActivity(Intent(this@ContractTypeActivity, ContractSearchActivity::class.java))
                    }
                } else {
                    if (cameraIsCanUse()) {
                        startActivity(Intent(this@ContractTypeActivity, ContractSearchActivity::class.java))
                    }else{
                        MyToast.getLongToast("您未开启相机权限，请开启相机权限！")
                        val intent = Intent()
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                        intent.data = Uri.fromParts("package",this@ContractTypeActivity.packageName, null)
                        this@ContractTypeActivity.startActivity(intent)
                    }
                }
            }
        }
        setSupportActionBar(my_toolbar)

        //给recyclerview分配样式
        type_list.layoutManager = LinearLayoutManager(this@ContractTypeActivity, LinearLayoutManager.VERTICAL, false)
    }

    private fun search() {
        if (search_edit.text.toString() != "") {
            val i = Intent(this@ContractTypeActivity, ContractActivity::class.java)
            i.putExtra("is_search", true)
            i.putExtra("is_all", false)
            i.putExtra("search_message", search_edit.text.toString())
            startActivity(i)
            //隐藏输入法
            val imm = getSystemService(
                    Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(search_edit.windowToken, 0)
            search_edit.setText("")
        } else {
            MyToast.getShortToast(getString(R.string.please_edit_ctb))
        }
    }

    override fun onStart() {
        getPreviousType()
        adapter?.notifyDataSetChanged()
        super.onStart()
    }

    //toolbar的返回按钮事件
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun showLoading() {
        contract_loding.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        contract_loding.visibility = View.GONE
    }

    override fun <T> requestSuccess(rData: T) {
        when (rData) {
            is ContractTypeBean -> {
                rData.isChangeColor = true

                if (!isJustLook) ctd.editSQL(rData, "insert")

                val i = Intent(this, ContractActivity::class.java)
                i.putExtra("ctb", rData)
                i.putExtra("is_search", false)
                i.putExtra("is_all", false)
                i.putExtra("is_just_look", isJustLook)
                startActivity(i)
            }
            else -> MyToast.getShortToast(getString(R.string.system_error))
        }
    }

    override val isJustLook: Boolean
        get() = previousIntent!!.getBooleanExtra("is_just_look", false)

    override fun <T> showView(aData: T) {
        when (aData) {
            is ContractTypeAdapter -> {
                type_list.adapter = aData
                this.adapter = aData
            }
            else -> MyToast.getShortToast(getString(R.string.system_error))
        }
    }

    override fun errorDealWith() {
        retry.visibility = View.VISIBLE
    }

    //获得相机权限
    @pub.devrel.easypermissions.AfterPermissionGranted(1)
    fun judgmentCarmer(): Boolean {
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
    fun cameraIsCanUse(): Boolean {
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
        startActivity(Intent(this@ContractTypeActivity, ContractSearchActivity::class.java))
    }

    /**
     * 获得上一个页面修改完毕的类数据
     * 只有在从类点击过去的才会保存类的信息到SharedPreferences，否则是没有的
     */
    private fun getPreviousType() {
        val sp = getSharedPreferences("ct", Context.MODE_PRIVATE)
        if (sp.getString("typeId", "") != "") {
            val changeCTB = adapter?.ctbs?.find { it.typeId == sp.getString("typeId", "") }
            changeCTB?.todayGh = sp.getInt("todayGh", 0)
            changeCTB?.todayStore = sp.getInt("todayStore", 0)
            changeCTB?.todayCount = sp.getInt("todayCount", 0)
            val editor = sp.edit()
            editor.clear()
            editor.apply()
        }
    }
}