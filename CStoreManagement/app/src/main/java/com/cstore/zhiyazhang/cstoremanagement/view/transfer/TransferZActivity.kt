package com.cstore.zhiyazhang.cstoremanagement.view.transfer

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.TransResult
import com.cstore.zhiyazhang.cstoremanagement.bean.TransServiceBean
import com.cstore.zhiyazhang.cstoremanagement.bean.TransTag
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.presenter.transfer.TransferServiceAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.transfer.TransferServicePresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.cstore.zhiyazhang.cstoremanagement.view.SignInActivity
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2018/5/10 17:57.
 */
class TransferZActivity(override val layoutId: Int = R.layout.activity_order) : MyActivity() {

    private val presenter = TransferServicePresenter(this)
    private lateinit var adapter: TransferServiceAdapter

    override fun initView() {
        my_toolbar.title = getString(R.string.transz)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        orderRecycler.layoutManager = MyLinearlayoutManager(this@TransferZActivity, LinearLayout.VERTICAL, false)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }


    override fun initClick() {
        orderretry.setOnClickListener { initData() }
    }

    override fun initData() {
        val user = User.getUser()
        //没登陆就去登录
        if (user.storeId == "") {
            val i = Intent(this@TransferZActivity, SignInActivity::class.java)
            i.putExtra("out", 1)
            startActivityForResult(i, 0)
        } else {
            showRetry(false)
            presenter.getAllTrs()
        }
    }

    override fun <T> showView(aData: T) {
        if (aData !is TransResult) {
            showPrompt("获得数据类型错误")
            errorDealWith()
        }

        //更新最新调拨时间
        val transTag = TransTag.getTransTag()
        var hour = 0
        (aData as TransResult).rows.forEach { if (it.disTime.toInt() > hour) hour = it.disTime.toInt() }
        TransTag.saveTag(TransTag(User.getUser().storeId, transTag.date, hour.toString()))

        //测试
        //超过两小时的订单都不显示
        val nowHour = MyTimeUtil.nowHour
        val removeData = ArrayList<TransServiceBean>()
        aData as TransResult
        aData.rows.forEach { if (nowHour >= it.disTime.toInt() + 2) removeData.add(it) }
        aData.rows.removeAll(removeData)
        adapter = TransferServiceAdapter(aData as TransResult, object : ItemClickListener {
            override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {
                val i = Intent(this@TransferZActivity, TransferZItemActivity::class.java)
                i.putExtra("data", aData.rows[position])
                i.putExtra("position", position)
                startActivityForResult(i, 1)
            }
        })
        orderRecycler.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            0 -> {
                //登录回来
                initData()
            }
            1 -> {
                try {
                    //从详细页回来
                    if (data != null) {
                        //如果有更新就不为空，更新items和总数就可以
                        val d = data.getSerializableExtra("data")
                        val p = data.getIntExtra("position", -1)
                        if (d != null && p != -1) {
                            d as TransServiceBean
                            val t = adapter.tr.rows[p]
                            t.items = d.items
                            t.trsQuantities = d.trsQuantities
                            t.requestNumber = d.requestNumber
                        }
                        adapter.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    val m = e.message ?: "返回数据异常！"
                    showPrompt(m)
                }
            }
        }
    }

    override fun errorDealWith() {
        showRetry(true)
    }

    override fun showLoading() {
        orderLoading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        orderLoading.visibility = View.GONE
    }

    /**
     * 显示重试按钮
     * @param type true显示 false隐藏
     */
    private fun showRetry(type: Boolean) {
        if (type) {
            orderretry.visibility = View.VISIBLE
            orderpro.visibility = View.GONE
            orderprotext.visibility = View.GONE
        } else {
            orderretry.visibility = View.GONE
            orderpro.visibility = View.VISIBLE
            orderprotext.visibility = View.VISIBLE
        }
    }

}