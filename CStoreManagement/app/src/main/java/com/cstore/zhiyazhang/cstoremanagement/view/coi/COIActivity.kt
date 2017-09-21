package com.cstore.zhiyazhang.cstoremanagement.view.coi

import android.content.Intent
import android.view.MenuItem
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.view.acceptance.PurchaseAcceptanceActivity
import com.cstore.zhiyazhang.cstoremanagement.view.cashdaily.CashDailyActivity
import kotlinx.android.synthetic.main.activity_coi.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2017/9/15 9:27.
 */
class COIActivity(override val layoutId: Int = R.layout.activity_coi) : MyActivity() {
    override fun initView() {
        my_toolbar.title = getString(R.string.coi)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
    }

    override fun initClick() {
        cash_daily.setOnClickListener {
            startActivity(Intent(this@COIActivity, CashDailyActivity::class.java))
        }
        purchase_acceptance.setOnClickListener {
            val intent=Intent(this@COIActivity, PurchaseAcceptanceActivity::class.java)
            intent.putExtra("type",1)
            startActivity(intent)
        }
        purchase_return_acceptance.setOnClickListener {
            val intent=Intent(this@COIActivity, PurchaseAcceptanceActivity::class.java)
            intent.putExtra("type",2)
            startActivity(intent)
        }
    }

    override fun initData() {
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }
}