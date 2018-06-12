package com.cstore.zhiyazhang.cstoremanagement.view

import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.MenuItem
import android.view.View
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.LogoBean
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.presenter.LogoAdapter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.view.attendance.AttendanceActivity
import com.cstore.zhiyazhang.cstoremanagement.view.attendance.AttendanceRecordingActivity
import com.cstore.zhiyazhang.cstoremanagement.view.checkin.CheckInActivity
import com.cstore.zhiyazhang.cstoremanagement.view.paiban.PaibanActivity
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.activity_in_stock.*
import kotlinx.android.synthetic.main.dialog_cashdaily.view.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2017/10/11 14:36.
 */
class PersonnelActivity(override val layoutId: Int = R.layout.activity_in_stock) : MyActivity() {
    private lateinit var dialog: AlertDialog
    private lateinit var dialogView: View
    override fun initView() {
        my_toolbar.title = getString(R.string.personnel)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        inv_recycler.addItemDecoration(DividerItemDecoration(this@PersonnelActivity, DividerItemDecoration.VERTICAL))
        inv_recycler.addItemDecoration(DividerItemDecoration(this@PersonnelActivity, DividerItemDecoration.HORIZONTAL))
        val builder = AlertDialog.Builder(this)
        dialogView = View.inflate(this, R.layout.dialog_cashdaily, null)
        builder.setView(dialogView)
        builder.setCancelable(true)
        dialog = builder.create()
        dialogView.dialog_title.text = "登入验证"
        dialogView.dialog_save.text = getString(R.string.sure)
        dialogView.dialog_edit.hint = getString(R.string.please_edit_password)
        dialogView.dialog_edit.inputType = InputType.TYPE_NUMBER_VARIATION_PASSWORD
        dialogView.dialog_edit.transformationMethod = PasswordTransformationMethod.getInstance()
        dialogView.dialog_text.visibility = View.VISIBLE
        val dialogText = "帐号ID：${User.getUser().uId}"
        dialogView.dialog_text.text = dialogText
    }

    override fun initClick() {
        dialogView.dialog_cancel.setOnClickListener {
            dialog.cancel()
        }
        dialogView.dialog_save.setOnClickListener {
            //测试
            val x = dialogView.dialog_edit.text.toString()
            if (x.isEmpty()) {
                showPrompt(getString(R.string.noMessage))
                return@setOnClickListener
            }
            val pwd = User.getUser().password
            if (x != pwd) {
                showPrompt(getString(R.string.pwdError))
                return@setOnClickListener
            }
            startActivity(Intent(this@PersonnelActivity, AttendanceActivity::class.java))
            dialogView.dialog_edit.setText("")
            dialog.cancel()
        }
    }

    override fun initData() {
        val data = ArrayList<LogoBean>()
        setData(data)
        inv_recycler.layoutManager = GridLayoutManager(this@PersonnelActivity, 3, GridLayoutManager.VERTICAL, false)
        inv_recycler.adapter = LogoAdapter(this@PersonnelActivity, data, object : ItemClickListener {
            override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {
                when (data[position].position) {
                    0 -> {
//                        MyToast.getShortToast(getString(R.string.in_development))
                        val groupId = User.getUser().groupId
                        if (groupId == "01" || groupId == "02" || User.getUser().groupId == "03") {
                            startActivity(Intent(this@PersonnelActivity, PaibanActivity::class.java))
                        } else {
                            showPrompt(getString(R.string.noP))
                        }
                    }
                    1 -> {
                        if (User.getUser().type == 0) {
                            startActivity(Intent(this@PersonnelActivity, CheckInActivity::class.java))
                        }else{
                            showPrompt("华南暂未开放")
                        }
                    }
                    2 -> {
                        if (User.getUser().groupId == "01" || User.getUser().groupId == "02" || User.getUser().groupId == "03") {
                            dialog.show()
                        } else {
                            showPrompt(getString(R.string.noP))
                        }
                    }
                    3 -> {
                        startActivity(Intent(this@PersonnelActivity, AttendanceRecordingActivity::class.java))
                    }
                }
            }
        })
    }

    private fun setData(data: ArrayList<LogoBean>) {
        data.add(LogoBean(R.mipmap.ic_scheduling, getString(R.string.scheduling), 0))
        data.add(LogoBean(R.mipmap.ic_check_in, getString(R.string.check_in), 1))
        data.add(LogoBean(R.mipmap.ic_attendance, getString(R.string.attendance), 2))
        data.add(LogoBean(R.mipmap.ic_attendance_record, getString(R.string.attendance_record), 3))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

}