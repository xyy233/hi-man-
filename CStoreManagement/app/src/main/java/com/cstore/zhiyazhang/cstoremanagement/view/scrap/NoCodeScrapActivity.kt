/*
package com.cstore.zhiyazhang.cstoremanagement.view.scrap

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapCategoryBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.scrap.NoCodeScrapAdapter
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.ConnectionDetector
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.RecyclerOnClick
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zhiyazhang.mykotlinapplication.utils.MyApplication
import kotlinx.android.synthetic.main.activity_scrap_nobarcode.*
import kotlinx.android.synthetic.main.loading_layout.*
import kotlinx.android.synthetic.main.sure_btn.*
import kotlinx.android.synthetic.main.toolbar_layout.*

*/
/**
 * Created by zhiya.zhang
 * on 2017/8/11 17:01.
 *//*

class NoCodeScrapActivity(override val layoutId: Int = R.layout.activity_scrap_nobarcode) : MyActivity(), GenericView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    private fun initData() {
        showLoading()
        if (!ConnectionDetector.getConnectionDetector().isOnline) {
            showPrompt(getString(R.string.noInternet))
            hideLoading()
            return
        }
        val ip = MyApplication.getIP()
        if (ip == MyApplication.instance().getString(R.string.notFindIP)) {
            showPrompt(ip)
            hideLoading()
            return
        }
        SocketUtil.getSocketUtil(ip).inquire(MySql.scrapCategory, 20, @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> {
                        if (msg.obj.toString() == "" || msg.obj.toString() == "[]") {
                            showPrompt(getString(R.string.noMessage))
                            hideLoading()
                            return
                        }
                        val cbs = Gson().fromJson<ArrayList<ScrapCategoryBean>>(msg.obj.toString(), object : TypeToken<ArrayList<ScrapCategoryBean>>() {}.type)
                        recyclerView2.adapter = NoCodeScrapAdapter(cbs, object : RecyclerOnClick {
                            override fun onItemClick(view: View, positon: Int) {
                                val nowIntent = Intent(this@NoCodeScrapActivity, NCScrapActivity::class.java)
                                nowIntent.putExtra("scrapCategory", cbs[positon])
                                startActivity(nowIntent)
                            }
                        })
                        hideLoading()
                    }
                    else -> {
                        showPrompt(msg.obj.toString())
                        hideLoading()
                    }
                }
            }
        })
    }

    private fun initView() {
        sure_btn.visibility = View.GONE
        my_toolbar.title = getString(R.string.noBarCodeScrap)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        recyclerView2.layoutManager = MyLinearlayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    override fun <T> requestSuccess(objects: T) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loading.visibility = View.GONE
    }

    override fun <T> showView(adapter: T) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun errorDealWith() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
*/
