package com.cstore.zhiyazhang.cstoremanagement.view.contract

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2017/6/19 15:39.
 */
class ContractOrder(override val layoutId: Int = R.layout.activity_order) : MyActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        my_toolbar.title = getString(R.string.order)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        order1.setOnClickListener {
            val intent = Intent(this@ContractOrder, ContractTypeActivity::class.java)
            intent.putExtra("is_just_look", false)
            startActivity(intent,
                    ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, order1, "order1").toBundle())
        }
        order3.setOnClickListener {
            val intent = Intent(this@ContractOrder, ContractTypeActivity::class.java)
            intent.putExtra("is_just_look", true)
            startActivity(intent,
                    ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, order3, "order3").toBundle())
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }
}