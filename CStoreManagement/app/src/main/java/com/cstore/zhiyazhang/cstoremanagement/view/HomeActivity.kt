package com.cstore.zhiyazhang.cstoremanagement.view

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.*
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.TransTag
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.sql.ALIPayDao
import com.cstore.zhiyazhang.cstoremanagement.sql.CashPayDao
import com.cstore.zhiyazhang.cstoremanagement.sql.ContractTypeDao
import com.cstore.zhiyazhang.cstoremanagement.sql.WXPayDao
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar.ERROR_MSG
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import com.cstore.zhiyazhang.cstoremanagement.view.pay.PayActivity
import com.cstore.zhiyazhang.cstoremanagement.view.transfer.TransferZActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.dialog_cashdaily.view.*
import kotlinx.android.synthetic.main.nav_header_home.view.*

class HomeActivity(override val layoutId: Int = R.layout.activity_home) : MyActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var bulletinShared: SharedPreferences

    private lateinit var dialog: AlertDialog.Builder
    private lateinit var dialogView: View
    private lateinit var deleteDialog: AlertDialog


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
        toolbar.title = resources.getString(R.string.app_name)
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

        //华南关闭调拨入口
        if (User.getUser().type == 1) {
            gg6.visibility = View.GONE
        }
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
        gg1.setOnClickListener {
            startActivity(Intent(this@HomeActivity, ContractOrder::class.java),
                    ActivityOptions.makeSceneTransitionAnimation(this@HomeActivity, gg1, "gg3").toBundle())
        }
        gg2.setOnClickListener {
            startActivity(Intent(this@HomeActivity, COIActivity::class.java),
                    ActivityOptions.makeSceneTransitionAnimation(this@HomeActivity, gg2, "gg3").toBundle())
        }
        gg3.setOnClickListener {
            startActivity(Intent(this@HomeActivity, InStockActivity::class.java),
                    ActivityOptions.makeSceneTransitionAnimation(this@HomeActivity, gg3, "gg3").toBundle())
        }
        gg4.setOnClickListener {
            startActivity(Intent(this@HomeActivity, PersonnelActivity::class.java),
                    ActivityOptions.makeSceneTransitionAnimation(this@HomeActivity, gg4, "gg3").toBundle())
        }
        gg5.setOnClickListener {
            val wxDao = WXPayDao(this)
            val cashDao = CashPayDao(this)
            val wxData = wxDao.getAllData()
            val cashData = cashDao.getAllData()
            if (wxData.any { it.isDone == 0 } && cashData.any { it.isDone == 0 }) {
                dialog.show()
            } else {
                startActivity(Intent(this@HomeActivity, PayActivity::class.java),
                        ActivityOptions.makeSceneTransitionAnimation(this@HomeActivity, gg5, "gg3").toBundle())
            }
        }
        gg6.setOnClickListener {
            startActivity(Intent(this@HomeActivity, TransferZActivity::class.java))
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
            closeOperating()
        }
    }

    /**
     * 关闭所有可点击选项
     */
    private fun closeOperating() {
        gg1.setOnClickListener(errorListener)
        gg2.setOnClickListener(errorListener)
        gg3.setOnClickListener(errorListener)
        gg4.setOnClickListener(errorListener)
        gg5.setOnClickListener(errorListener)
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
}
