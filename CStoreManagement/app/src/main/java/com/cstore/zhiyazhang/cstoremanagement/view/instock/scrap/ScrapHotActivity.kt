package com.cstore.zhiyazhang.cstoremanagement.view.instock.scrap

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapHotBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.scrap.ScrapHotAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.scrap.ScrapHotPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.activity_contract_type.*
import kotlinx.android.synthetic.main.contract_type_recycler.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2017/9/1 11:48.
 */
class ScrapHotActivity(override val layoutId: Int = R.layout.activity_contract_type) : MyActivity(), GenericView {

    val presenter = ScrapHotPresenter(this, this)

    override fun initView() {
        header_h.visibility = View.GONE
        my_toolbar.title = getString(R.string.scrap2)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        val layoutManager = MyLinearlayoutManager(this, LinearLayoutManager.VERTICAL, false)
        type_list.layoutManager = layoutManager
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun initClick() {
        retry.setOnClickListener { presenter.getScrapHotMid() }
    }

    override fun initData() {
        presenter.getScrapHotMid()
    }

    override fun <T> requestSuccess(rData: T) {
        rData as ArrayList<ScrapHotBean>
        val adapter = ScrapHotAdapter(rData, object : ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val i = Intent(this@ScrapHotActivity, ScrapHotItemActivity::class.java)
                i.putExtra("hotMid", rData)
                i.putExtra("position", position)
                startActivity(i)
            }

            override fun onItemLongClick(view: View, position: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
        type_list.adapter=adapter
    }

    override fun showLoading() {
        contract_loding.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        contract_loding.visibility = View.GONE
    }

    override fun <T> showView(aData: T) {
    }

    override fun errorDealWith() {
        retry.visibility = View.VISIBLE
    }

}