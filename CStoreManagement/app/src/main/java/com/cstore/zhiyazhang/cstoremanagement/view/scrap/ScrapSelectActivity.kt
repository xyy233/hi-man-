/*
package com.cstore.zhiyazhang.cstoremanagement.view.scrap

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.hardware.Camera
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.MRKBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapContractBean
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.presenter.scrap.SelectScrapAdapter
import com.cstore.zhiyazhang.cstoremanagement.sql.ScrapDao
import com.cstore.zhiyazhang.cstoremanagement.utils.ConnectionDetector
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.cstore.zhiyazhang.cstoremanagement.view.order.contract.ContractSearchActivity
import com.google.gson.Gson
import com.zhiyazhang.mykotlinapplication.utils.MyApplication
import kotlinx.android.synthetic.main.activity_scrap_select.*
import kotlinx.android.synthetic.main.loading_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*


*/
/**
 * Created by zhiya.zhang
 * on 2017/7/10 10:17.
 *//*

class ScrapSelectActivity(override val layoutId: Int = R.layout.activity_scrap_select) : MyActivity(), GenericView, EasyPermissions.PermissionCallbacks {
    var adapterList: ArrayList<ScrapContractBean> = ArrayList<ScrapContractBean>()
    var deleteList: ArrayList<ScrapContractBean> = ArrayList<ScrapContractBean>()
    val sd = ScrapDao(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initClick()
    }

    private fun initClick() {
        haveBarCodeBtn.setOnClickListener {
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                if (judgmentCarmer()) {
                    startActivity(Intent(this@ScrapSelectActivity,BarCodeScrapActivity::class.java))
                }
            } else {
                if (cameraIsCanUse()) {
                    startActivity(Intent(this@ScrapSelectActivity,BarCodeScrapActivity::class.java))
                } else {
                    MyToast.getLongToast("您未开启相机权限，请开启相机权限！")
                    val intent = Intent()
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                    intent.data = Uri.fromParts("package", this@ScrapSelectActivity.packageName, null)
                    this@ScrapSelectActivity.startActivity(intent)
                }
            }
        }

        submit_scrap.setOnClickListener {
            submitScrap()
        }

        noBarCodeBtn.setOnClickListener {
            startActivity(Intent(this@ScrapSelectActivity,NoCodeScrapActivity::class.java))
        }
    }

    */
/**
     * 报废商品
     *//*

    private fun submitScrap() {
        val submitList = ArrayList<ScrapContractBean>()
        val mrkbs = ArrayList<MRKBean>()
        var i = 0
        sd.allDate.forEach {
            i++
            if (it.isScrap == 0) {
                mrkbs.add(MRKBean(User.getUser().storeId, i, it.scrapId, it.unitPrice, it.unitCost, it.mrkCount, User.getUser().uId, it.citemYN, it.sellCost, it.recycleYN))
                it.isScrap = 1
                submitList.add(it)
            }
        }
        if (submitList.size != 0) {
            showLoading()
            if (!ConnectionDetector.getConnectionDetector().isOnline) {
                showPrompt(getString(R.string.noInternet))
                hideLoading()
                return
            }
            val ip = MyApplication.getIP()
            if (ip == MyApplication.instance().getString(R.string.notFindIP)) {
                showPrompt(ip)
                hideLoading()
                return
            }
            SocketUtil.getSocketUtil(ip).inquire(Gson().toJson(mrkbs), 20, @SuppressLint("HandlerLeak")
            object : Handler() {
                override fun handleMessage(msg: Message) {
                    when (msg.what) {
                        0 -> {
                            if (msg.obj.toString() == "Syntax Error!") {
                                showPrompt(msg.obj.toString())
                                hideLoading()
                                return
                            }
                            sd.editSQL(submitList, "update")
                            updateData()
                            hideLoading()
                        }
                        else -> {
                            showPrompt(msg.obj.toString())
                            hideLoading()
                        }
                    }
                }
            })
        } else
            showPrompt("无要报废的商品")
    }

    private fun initView() {
        my_toolbar.title = getString(R.string.scrap)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        select_recycler.layoutManager = MyLinearlayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapterList = sd.allDate//得到要显示的数据
        if (adapterList.size != 0) {
            //进行删除检查
            val today = MyTimeUtil.todayDay
            adapterList.forEach {
                if (it.createDay != today) deleteList.add(it)
            }
            //进行删除操作
            if (deleteList.size != 0) {
                adapterList.removeAll(deleteList)
                Thread(Runnable {
                    sd.editSQL(deleteList, "delete")
                    deleteList.clear()
                }).start()
            }
        }
        val adapter = SelectScrapAdapter(adapterList)
        select_recycler.adapter = adapter

        toolbar_time.visibility=View.VISIBLE
        toolbar_time.setOnTouchListener { _, event ->
            if (event.action==MotionEvent.ACTION_DOWN){
                showDatePickDlg()
                true
            }else false
        }
        toolbar_time.setOnFocusChangeListener { _, b ->
            if (b){
                showDatePickDlg()
            }
        }
    }

    private fun showDatePickDlg() {
        val calendar= Calendar.getInstance()
        val datePickDialog:DatePickerDialog =DatePickerDialog(this@ScrapSelectActivity, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            run {
                val time = "$year-${monthOfYear+1}-$dayOfMonth"
                toolbar_time.text = time
            }
        },
                calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))
        datePickDialog.show()

    }

    private fun updateData(){
        val adapter = SelectScrapAdapter(sd.allDate)
        select_recycler.adapter = adapter
        select_recycler.adapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        updateData()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finishAfterTransition()
        }
        return true
    }

    override fun <T> requestSuccess(objects: T) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loading.visibility = View.GONE
    }

    override fun <T> showView(adapter: T) {
    }

    override fun errorDealWith() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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


    */
/**
     * 返回true 表示可以使用  返回false表示不可以使用
     *//*

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
        val i = Intent(this@ScrapSelectActivity, ContractSearchActivity::class.java)
        i.putExtra("whereIsIt", "unitord")
        startActivity(i)
    }

}*/
