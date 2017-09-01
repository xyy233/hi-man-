package com.cstore.zhiyazhang.cstoremanagement.view.instock

import android.content.Intent
import android.view.MenuItem
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.view.scrap.ScrapActivity
import com.cstore.zhiyazhang.cstoremanagement.view.scrap.ScrapHotActivity
import kotlinx.android.synthetic.main.activity_in_stock.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2017/8/25 16:59.
 */
class InStockActivity(override val layoutId: Int = R.layout.activity_in_stock) : MyActivity() {
    override fun initView() {
        my_toolbar.title = getString(R.string.in_stock)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
    }

    override fun initClick() {
        scrap1.setOnClickListener {
            startActivity(Intent(this@InStockActivity, ScrapActivity::class.java))
        }
        scrap2.setOnClickListener {
            startActivity(Intent(this@InStockActivity,ScrapHotActivity::class.java))
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