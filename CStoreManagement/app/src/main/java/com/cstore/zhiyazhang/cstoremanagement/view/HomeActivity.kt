package com.cstore.zhiyazhang.cstoremanagement.view

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import android.widget.Toast
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.sql.ContractTypeDao
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import com.cstore.zhiyazhang.cstoremanagement.view.coi.COIActivity
import com.cstore.zhiyazhang.cstoremanagement.view.instock.InStockActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.content_home.*
import kotlinx.android.synthetic.main.nav_header_home.view.*

class HomeActivity(override val layoutId: Int = R.layout.activity_home) : MyActivity(), NavigationView.OnNavigationItemSelectedListener {


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

    override fun initData() {
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.cstore.zhiyazhang.UPDATE")
        registerReceiver(updateReceiver, intentFilter)
    }

    override fun onStart() {
        super.onStart()
        if (MyApplication.getVersion()!!.indexOf("Alpha") == -1) {
            this.startService(Intent(this, UpdateService::class.java))
        }
    }

    override fun onDestroy() {
        unregisterReceiver(updateReceiver)
        super.onDestroy()
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        toolbar.title = resources.getString(R.string.app_name)
        setSupportActionBar(toolbar)
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
    }

    override fun initClick() {
        gg1.setOnClickListener { MyToast.getShortToast("未完成") }
        gg2.setOnClickListener { MyToast.getShortToast("未完成") }
        gg3.setOnClickListener {
            startActivity(Intent(this@HomeActivity, ContractOrder::class.java),
                    ActivityOptions.makeSceneTransitionAnimation(this@HomeActivity, gg3, "gg3").toBundle())
        }
        gg4.setOnClickListener { MyToast.getShortToast("未完成") }
        gg5.setOnClickListener { MyToast.getShortToast("未完成") }
        gg6.setOnClickListener { startActivity(Intent(this@HomeActivity, COIActivity::class.java),
                ActivityOptions.makeSceneTransitionAnimation(this@HomeActivity, gg6, "gg3").toBundle()) }
        gg7.setOnClickListener {
            startActivity(Intent(this@HomeActivity, InStockActivity::class.java),
                    ActivityOptions.makeSceneTransitionAnimation(this@HomeActivity, gg7, "gg3").toBundle())
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
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
                AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("是否在相关人员提示下操作？此步骤会对正常使用造成不可预知影响！")
                        .setPositiveButton("确认清空", { _, _ ->
                            AlertDialog.Builder(this)
                                    .setTitle("提示")
                                    .setMessage("请确认是否清空")
                                    .setPositiveButton("确认", { _, _ ->
                                       /* val sd = ScrapDao(this@HomeActivity)
                                        sd.editSQL(null, "deleteTable")*/
                                        val cd = ContractTypeDao(this@HomeActivity)
                                        cd.editSQL(null, "deleteTable")
                                        Toast.makeText(this@HomeActivity, "清除完毕", Toast.LENGTH_SHORT).show()
                                    })
                                    .setNegativeButton("放弃") { _, _ -> }
                                    .show()
                        })
                        .setNegativeButton("放弃") { _, _ -> }
                        .show()
            }
            R.id.nav_about -> {
                Toast.makeText(this@HomeActivity, "当前版本号：" + MyApplication.getVersion(), Toast.LENGTH_SHORT).show()
            }
            R.id.nav_update -> {
                updateButton = true
                this@HomeActivity.startService(Intent(this@HomeActivity, UpdateService::class.java))
            }
            R.id.nav_open_tutorial->{
                val sp=getSharedPreferences("tutorial",Context.MODE_PRIVATE)
                sp.edit().clear().apply()
                MyToast.getLongToast(getString(R.string.open_tutorial_done))
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
