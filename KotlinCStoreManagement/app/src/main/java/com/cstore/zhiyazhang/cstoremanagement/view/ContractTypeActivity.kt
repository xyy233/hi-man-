package com.cstore.zhiyazhang.cstoremanagement.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.TestActivity
import com.cstore.zhiyazhang.cstoremanagement.bean.ContractTypeBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.contract.ContractTypeAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.contract.ContractTypePresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import com.cstore.zhiyazhang.cstoremanagement.view.interface_view.ContractTypeView
import com.zhiyazhang.mykotlinapplication.utils.MyActivity
import kotlinx.android.synthetic.main.activity_contract_type.*
import kotlinx.android.synthetic.main.contract_type_recycler.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

/**
 * Created by zhiya.zhang
 * on 2017/6/12 15:03.
 */
class ContractTypeActivity(override val layoutId: Int = R.layout.activity_contract_type) : MyActivity(), ContractTypeView, EasyPermissions.PermissionCallbacks {

    var adapter: ContractTypeAdapter? = null
    val presenter = ContractTypePresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        my_toolbar.title = resources.getString(R.string.order)
        setSupportActionBar(my_toolbar)

        //搜索按钮点击事件
        search_btn.setOnClickListener {
            if (search_edit.text.toString() != "") {
                val i = Intent(this@ContractTypeActivity, TestActivity::class.java)
                i.putExtra("is_search", true)
                i.putExtra("search_message", search_edit.text.toString())
                startActivity(i)
                //隐藏输入法
                val imm = getSystemService(
                        Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(search_edit.windowToken, 0)
                search_edit.setText("")
            } else {
                MyToast.getShortToast(this@ContractTypeActivity, getString(R.string.please_edit_ctb))
            }
        }

        //给recyclerview分配样式
        type_list.layoutManager = LinearLayoutManager(this@ContractTypeActivity, LinearLayoutManager.VERTICAL, false)

        //重试按钮点击事件
        retry.setOnClickListener {
            showLoading()
            retry.visibility = View.GONE
            presenter.getAllContractType()
        }

        //二维码按钮点击事件
        qrcode.setOnClickListener {
            if (judgmentCarmer()) {
                startActivity(Intent(this@ContractTypeActivity, TestActivity::class.java))
            }
        }

        presenter.getAllContractType()
    }

    override fun onStart() {
        getPreviousType()
        adapter?.notifyDataSetChanged()
        super.onStart()
    }

    //toolbar的返回按钮事件
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        finish()
    }

    override fun toActivity(ctb: ContractTypeBean) {
        //还原已被点击改过图片颜色的数据
        adapter?.ctbs?.filter { c -> c.isChangeColor }?.forEach { it.isChangeColor = false }
        ctb.isChangeColor = true
        //带着类数据跳转
        val i = Intent(this, TestActivity::class.java)
        i.putExtra("ctb", ctb)
        i.putExtra("is_search", false)
        startActivity(i)
    }

    override fun showLoading() {
        contract_loding.visibility = View.GONE
    }

    override fun hideLoading() {
        contract_loding.visibility = View.GONE
    }

    override fun showView(adapter: ContractTypeAdapter) {
        type_list.adapter = adapter
        this.adapter = adapter
    }

    override fun showFailedError(errorMessage: String) {
        MyToast.getShortToast(this, errorMessage)
        retry.visibility = View.VISIBLE
    }

    //获得相机权限
    @AfterPermissionGranted(1)
    fun judgmentCarmer(): Boolean {
        val perms = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (!EasyPermissions.hasPermissions(this, *perms)) {
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开权限", 1, *perms)
            return false
        }
        return true
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
        startActivity(Intent(this@ContractTypeActivity, TestActivity::class.java))
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