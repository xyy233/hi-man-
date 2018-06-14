package com.cstore.zhiyazhang.cstoremanagement.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.*
import android.hardware.Camera
import android.net.Uri
import android.os.RemoteException
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.LogoBean
import com.cstore.zhiyazhang.cstoremanagement.bean.TransTag
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.presenter.LogoAdapter
import com.cstore.zhiyazhang.cstoremanagement.sql.*
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar.ERROR_MSG
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import com.cstore.zhiyazhang.cstoremanagement.utils.printer.PrinterServiceConnection
import com.cstore.zhiyazhang.cstoremanagement.view.pay.PayActivity
import com.cstore.zhiyazhang.cstoremanagement.view.transfer.TransferErrorService
import com.cstore.zhiyazhang.cstoremanagement.view.transfer.TransferService
import com.cstore.zhiyazhang.cstoremanagement.view.transfer.TransferZActivity
import com.gprinter.service.GpPrintService
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.dialog_cashdaily.view.*
import kotlinx.android.synthetic.main.nav_header_home.view.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class HomeActivity(override val layoutId: Int = R.layout.activity_home) : MyActivity(), NavigationView.OnNavigationItemSelectedListener, EasyPermissions.PermissionCallbacks {

    private lateinit var bulletinShared: SharedPreferences

    private lateinit var dialog: AlertDialog.Builder
    private lateinit var dialogView: View
    private lateinit var deleteDialog: AlertDialog
    private var conn: PrinterServiceConnection? = null


    var updateButton = false//确认是否是通过更新按钮更新的

    private val updateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.getBooleanExtra("is_new", false)) MyToast.getShortToast("发现新版本，正在下载中") else {
                if (updateButton) {
                    MyToast.getShortToast("已经是最新版本")
                    updateButton = false
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        if (User.getUser().type == 0) {
            toolbar.title = resources.getString(R.string.app_name)
        } else {
            toolbar.title = resources.getString(R.string.app_name2)
        }
        setSupportActionBar(toolbar)
        bulletinShared = getSharedPreferences("bulletin", Context.MODE_PRIVATE)
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        val userShared = getSharedPreferences("user", Context.MODE_PRIVATE)
        val headerLayout = nav_view.inflateHeaderView(R.layout.nav_header_home)
        headerLayout.user_name.text = userShared.getString("uName", "")
        headerLayout.store_name.text = userShared.getString("storeName", "")
        headerLayout.store_address.text = userShared.getString("address", "")
        headerLayout.app_version.text = "v: ${MyApplication.getVersion()}"
        dialog = AlertDialog.Builder(this@HomeActivity)
                .setTitle("提示")
                .setMessage("交易记录异常，正在处理中，请等待处理完毕或联系系统部！")
                .setPositiveButton("确定", { _, _ ->
                })
        val builder = AlertDialog.Builder(this)
        dialogView = View.inflate(this, R.layout.dialog_cashdaily, null)!!
        builder.setView(dialogView)
        builder.setCancelable(true)
        deleteDialog = builder.create()
        dialogView.dialog_title.text = getString(R.string.delete_error_pay_data)
        dialogView.dialog_save.text = getString(R.string.clear)
        dialogView.dialog_edit.hint = getString(R.string.please_edit_password_by_developer)
        dialogView.dialog_edit.inputType = InputType.TYPE_CLASS_NUMBER
        dialogView.dialog_edit.keyListener = DigitsKeyListener.getInstance("1234567890")

        home_recycler.addItemDecoration(DividerItemDecoration(this@HomeActivity, DividerItemDecoration.VERTICAL))
        home_recycler.addItemDecoration(DividerItemDecoration(this@HomeActivity, DividerItemDecoration.HORIZONTAL))
        home_recycler.layoutManager = GridLayoutManager(this@HomeActivity, 3, GridLayoutManager.VERTICAL, false)

        connection()
    }

    private fun connection() {
        conn = PrinterServiceConnection(null)
        val i = Intent(this, GpPrintService::class.java)
        bindService(i, conn, Context.BIND_AUTO_CREATE)
    }

    override fun initClick() {
        dialogView.dialog_cancel.setOnClickListener {
            deleteDialog.cancel()
        }
        dialogView.dialog_save.setOnClickListener {
            val value = dialogView.dialog_edit.text.toString()
            if (value.isEmpty()) {
                MyToast.getShortToast(getString(R.string.please_edit_password_by_developer))
                return@setOnClickListener
            }
            if (value == "666666") {
                val wxDao = WXPayDao(this@HomeActivity)
                val cashDao = CashPayDao(this@HomeActivity)
                val aliDao = ALIPayDao(this@HomeActivity)
                wxDao.deleteAll()
                cashDao.deleteAll()
                aliDao.deleteAll()
                MyToast.getShortToast("清除完毕")
                dialogView.dialog_edit.setText("")
                deleteDialog.cancel()
            } else {
                MyToast.getShortToast(getString(R.string.pwdError))
            }
        }
    }

    override fun initData() {
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.cstore.zhiyazhang.UPDATE")
        registerReceiver(updateReceiver, intentFilter)
        this.startService(Intent(this, UpdateService::class.java))
        beginBulletin()
        //华东才开启调拨服务
        if (User.getUser().type == 0) {
            if (!LivingService.isServiceWorked(this, TransferService.TAG)) {
                startService(Intent(this, TransferService::class.java))
            }
        }
        if (!LivingService.isServiceWorked(MyApplication.instance().applicationContext, TransferErrorService.TAG)) {
            startService(Intent(MyApplication.instance().applicationContext, TransferErrorService::class.java))
        }
        val td = TranDao.instance()
        td.deleteSQL()
        val data = ArrayList<LogoBean>()
        setData(data)
        home_recycler.adapter = LogoAdapter(this@HomeActivity, data, object : ItemClickListener {
            override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {
                when (data[position].position) {
                    0 -> {
                        startActivity(Intent(this@HomeActivity, ContractOrder::class.java),
                                ActivityOptions.makeSceneTransitionAnimation(this@HomeActivity, view.itemView, "gg3").toBundle())
                    }
                    1 -> {
                        startActivity(Intent(this@HomeActivity, COIActivity::class.java),
                                ActivityOptions.makeSceneTransitionAnimation(this@HomeActivity, view.itemView, "gg3").toBundle())
                    }
                    2 -> {
                        startActivity(Intent(this@HomeActivity, InStockActivity::class.java),
                                ActivityOptions.makeSceneTransitionAnimation(this@HomeActivity, view.itemView, "gg3").toBundle())
                    }
                    3 -> {
                        startActivity(Intent(this@HomeActivity, PersonnelActivity::class.java),
                                ActivityOptions.makeSceneTransitionAnimation(this@HomeActivity, view.itemView, "gg3").toBundle())
                    }
                    4 -> {
                        val wxDao = WXPayDao(this@HomeActivity)
                        val cashDao = CashPayDao(this@HomeActivity)
                        val wxData = wxDao.getAllData()
                        val cashData = cashDao.getAllData()
                        if (wxData.any { it.isDone == 0 } && cashData.any { it.isDone == 0 }) {
                            dialog.show()
                        } else {
                            startActivity(Intent(this@HomeActivity, PayActivity::class.java),
                                    ActivityOptions.makeSceneTransitionAnimation(this@HomeActivity, view.itemView, "gg3").toBundle())
                        }
                    }
                    5 -> {
                        startActivity(Intent(this@HomeActivity, TransferZActivity::class.java),
                                ActivityOptions.makeSceneTransitionAnimation(this@HomeActivity, view.itemView, "gg3").toBundle())
                    }
                    6 -> {
                        startActivity(Intent(this@HomeActivity, WShelvesActivity::class.java),
                                ActivityOptions.makeSceneTransitionAnimation(this@HomeActivity, view.itemView, "gg3").toBundle())
                    }
                }
            }
        })
    }

    private fun setData(data: ArrayList<LogoBean>) {
        data.add(LogoBean(R.mipmap.ic_home_shopping, getString(R.string.order), 0))
        data.add(LogoBean(R.mipmap.ic_home_invest, getString(R.string.coi), 1))
        data.add(LogoBean(R.mipmap.ic_home_instock, getString(R.string.in_stock), 2))
        data.add(LogoBean(R.mipmap.ic_personnel, getString(R.string.personnel), 3))
//        data.add(LogoBean(R.mipmap.ic_collect, getString(R.string.collect), 4))
        //华东才开启中卫
        if (User.getUser().type == 0) {
            data.add(LogoBean(R.mipmap.zw_trs, getString(R.string.transz), 5))
            data.add(LogoBean(R.mipmap.w_shelves, getString(R.string.shelves), 6))
        }
    }

    /**
     * 开始公告处理
     */
    private fun beginBulletin() {
        //是否显示公告
        val versionNum = bulletinShared.getInt("versionNum", 0)
        //保存的版本号小于当前版本号就弹出公告牌
        if (versionNum < MyApplication.getVersionNum()) {
            showBulletin()
            changeBulletin()
        }
    }

    /**
     * 显示公告
     */
    private fun showBulletin() {
        //需要显示的内容
        val bulletinMessage = "${getString(R.string.bulletin)}当前版本：${MyApplication.getVersion()}"
        AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle("更新提示")
                .setMessage(bulletinMessage)
                .setPositiveButton(getString(R.string.sure), { v, _ ->
                    v.cancel()
                })
                .show()
    }

    /**
     * 更新公告数据
     */
    private fun changeBulletin() {
        val bs = bulletinShared.edit()
        bs.putInt("versionNum", MyApplication.getVersionNum())
        bs.apply()
    }

    override fun onDestroy() {
        unregisterReceiver(updateReceiver)
        if (conn != null) {
            unbindService(conn)
        }
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        if (!CStoreCalendar.judgmentStatus()) {
            val data = CStoreCalendar.getCStoreCalendar()
            if (data == null) {
                home_notice.text = ERROR_MSG//没获得换日数据
            } else {
                home_notice.text = "应用崩溃，请退出应用后重启即可恢复"//曾经崩溃过
            }
        }
    }

    /**
     * 错误异常不允许点击
     */
    private val errorListener = View.OnClickListener {
        MyToast.getShortToast(home_notice.text.toString())
    }

    override fun onBackPressed() {
        AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle("提示")
                .setMessage("请确认退出系统？")
                .setPositiveButton("退出") { _, _ ->
                    val intent = Intent()
                    intent.action = EXIT_APP_ACTION
                    sendBroadcast(intent)//发送退出系统广播  每个接收器都会收到 调动finish（）关闭activity
                    if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                        drawer_layout.closeDrawer(GravityCompat.START)
                    }
                    finish()
                }
                .setNegativeButton("按错了", null)
                .show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_bluetooth -> {
                getPermissions()
            }
            R.id.nav_manage -> {
                AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                        .setTitle("提示")
                        .setMessage("是否在相关人员提示下操作？此步骤会对正常使用造成不可预知影响！")
                        .setPositiveButton("确认清空", { _, _ ->
                            AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                                    .setTitle("提示")
                                    .setMessage("请确认是否清空")
                                    .setPositiveButton("确认", { _, _ ->
                                        /* val sd = ScrapDao(this@HomeActivity)
                                         sd.editSQL(null, "deleteTable")*/
                                        val cd = ContractTypeDao(this@HomeActivity)
                                        cd.editSQL(null, "deleteAll")
                                        val td = TranDao.instance()
                                        td.clearSQL()
                                        TransTag.cleanTag()
                                        Toast.makeText(this@HomeActivity, "清除完毕", Toast.LENGTH_SHORT).show()
                                    })
                                    .setNegativeButton("放弃") { _, _ -> }
                                    .show()
                        })
                        .setNegativeButton("放弃") { _, _ -> }
                        .show()
            }
            R.id.nav_about -> {
                showBulletin()
            }
            R.id.nav_update -> {
                updateButton = true
                this@HomeActivity.startService(Intent(this@HomeActivity, UpdateService::class.java))
            }
            R.id.nav_open_tutorial -> {
                val sp = getSharedPreferences("tutorial", Context.MODE_PRIVATE)
                sp.edit().clear().apply()
                MyToast.getLongToast(getString(R.string.open_tutorial_done))
            }
            R.id.nav_delete_sql -> {
                deleteDialog.show()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun getPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (judgmentPermissions()) {
                try {
                    conn!!.goActivity(this@HomeActivity, false)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
        } else {
            if (!cameraIsCanUse()) {
                MyToast.getLongToast("您未开启权限，请开启权限！")
                val intent = Intent()
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                intent.data = Uri.fromParts("package", this@HomeActivity.packageName, null)
                this@HomeActivity.startActivity(intent)
            } else {
                try {
                    conn!!.goActivity(this@HomeActivity, false)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
        }
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

    //获得权限
    @pub.devrel.easypermissions.AfterPermissionGranted(1)
    private fun judgmentPermissions(): Boolean {
        val perms = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE)
        if (!EasyPermissions.hasPermissions(this, *perms)) {
            EasyPermissions.requestPermissions(this, getString(R.string.open_permission), 1, *perms)
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
        try {
            conn!!.goActivity(this@HomeActivity, false)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }
}
