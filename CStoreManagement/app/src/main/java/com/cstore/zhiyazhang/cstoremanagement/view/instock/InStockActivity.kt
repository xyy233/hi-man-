package com.cstore.zhiyazhang.cstoremanagement.view.instock

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.view.scrap.ScrapActivity
import kotlinx.android.synthetic.main.activity_in_stock.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2017/8/25 16:59.
 */
class InStockActivity(override val layoutId: Int = R.layout.activity_in_stock) : MyActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        my_toolbar.title=getString(R.string.in_stock)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)

        scrap1.setOnClickListener {
            val intent = Intent(this@InStockActivity, ScrapActivity::class.java)
            startActivity(intent)
//            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@InStockActivity, scrap, "scrap").toBundle())
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->onBackPressed()
        }
        return true
    }
}