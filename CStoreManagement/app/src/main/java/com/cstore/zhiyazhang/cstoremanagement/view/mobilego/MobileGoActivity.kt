package com.cstore.zhiyazhang.cstoremanagement.view.mobilego

import android.content.Intent
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.LogoBean
import com.cstore.zhiyazhang.cstoremanagement.bean.MobileDriverBean
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.mobile.MobileUtil
import com.cstore.zhiyazhang.cstoremanagement.presenter.LogoAdapter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.view.order.contract.ContractSearchActivity
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.activity_coi.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2018/6/21 9:56.
 */
class MobileGoActivity(override val layoutId: Int = R.layout.activity_coi) : MyActivity() {
    companion object {
        val DIVER_CODE = "driverCode"
    }

    private var driverCode: String? = null
    private val driverListener = object : MyListener {
        override fun listenerSuccess(data: Any) {
            data as MobileDriverBean
            hideLoading()
            showPrompt("司机${data.data.driverName}检测通过！")
        }

        override fun listenerFailed(errorMessage: String) {
            hideLoading()
            showPrompt("检测未通过！$errorMessage")
            finish()
        }
    }


    override fun initView() {
        my_toolbar.title = getString(R.string.mobile_go)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        coi_recycler.addItemDecoration(DividerItemDecoration(this@MobileGoActivity, DividerItemDecoration.VERTICAL))
        coi_recycler.addItemDecoration(DividerItemDecoration(this@MobileGoActivity, DividerItemDecoration.HORIZONTAL))
    }

    override fun initClick() {}

    override fun initData() {
        val data = ArrayList<LogoBean>()
        setData(data)
        coi_recycler.layoutManager = GridLayoutManager(this@MobileGoActivity, 3, GridLayoutManager.VERTICAL, false)
        coi_recycler.adapter = LogoAdapter(this@MobileGoActivity, data, object : ItemClickListener {
            override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {
                when (data[position].position) {
                    0 -> {
                        val i = Intent(this@MobileGoActivity, MobilePurchaseActivity::class.java)
                        i.putExtra(DIVER_CODE, driverCode)
                        startActivity(i)
                    }
                    1 -> {
                        val i = Intent(this@MobileGoActivity, MobileReturnActivity::class.java)
                        i.putExtra(DIVER_CODE, driverCode)
                        startActivity(i)
                    }
                }
            }
        })
        val i = Intent(this@MobileGoActivity, ContractSearchActivity::class.java)
        i.putExtra(ContractSearchActivity.WHERE_IS_IT, ContractSearchActivity.MJB)
        startActivityForResult(i, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1) {
            if (data != null) {
                val msg = data.getStringExtra("message")
                if (msg != null) {
                    driverCode = msg
                    showLoading()
                    MobileUtil.judgmentDriver(driverCode!!, driverListener)
                }
            }
        }
    }

    private fun setData(data: ArrayList<LogoBean>) {
        data.add(LogoBean(R.drawable.no_img, getString(R.string.purchase), 0))
        data.add(LogoBean(R.drawable.no_img, getString(R.string.return_), 1))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loading.visibility = View.GONE
    }

}