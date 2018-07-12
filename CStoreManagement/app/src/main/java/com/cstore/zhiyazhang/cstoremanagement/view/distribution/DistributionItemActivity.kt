package com.cstore.zhiyazhang.cstoremanagement.view.distribution

import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.*
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.presenter.distribution.DisDialogAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.distribution.DistributionItemAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.distribution.DistributionPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyScanUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.AddLessClickListener
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.cstore.zhiyazhang.cstoremanagement.view.order.contract.ContractSearchActivity
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemTouchHelperCallback
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.dialog_dis.view.*
import kotlinx.android.synthetic.main.scan_edit.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2018/5/22 12:25.
 */
class DistributionItemActivity(override val layoutId: Int = R.layout.activity_order) : MyActivity() {
    private lateinit var d: DistributionBean
    private var nowEditDataId: String = ""
    private val dialogDeleteData = ArrayList<String>()
    private val presenter = DistributionPresenter(this)
    private lateinit var adapter: DistributionItemAdapter
    private lateinit var dialogAdapter: DisDialogAdapter
    private val saveData = DistributionRequestResult(0, ArrayList())
    private lateinit var dialog: AlertDialog
    private lateinit var dialogView: View
    private val listener = object : MyListener {
        override fun listenerSuccess(data: Any) {
            if (data is UtilBean) {
                try {
                    presenter.addDistributionItem(data.value!!, data.value2!!)
                } catch (e: Exception) {
                    showPrompt(e.message.toString())
                }
            }
        }
    }

    override fun initView() {
        val data = intent.getSerializableExtra("data")
        if (data == null || data !is DistributionBean) {
            showPrompt("配送数据类型错误!")
            finish()
            return
        }
        d = data
        val title = getString(R.string.distribution) + " - " + d.shelvesName
        my_toolbar.title = title
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        orderRecycler.layoutManager = MyLinearlayoutManager(this@DistributionItemActivity, LinearLayout.VERTICAL, false)
        done.text = getString(R.string.save)
        done2.text = getString(R.string.add)
        done.visibility = View.VISIBLE
        done2.visibility = View.VISIBLE
        MyScanUtil.openScan(scan_edit, listener)
        initDialog()
    }

    private fun initDialog() {
        val builder = AlertDialog.Builder(this@DistributionItemActivity)
        dialogView = View.inflate(this, R.layout.dialog_dis, null)!!
        builder.setView(dialogView)
        builder.setCancelable(true)
        dialogView.dialog_cancel.setOnClickListener { dialog.cancel() }
        dialogView.dialog_save.setOnClickListener {
            if (nowEditDataId != "") {
                adapter.data.rows.filter { it.itemNo == nowEditDataId }.forEach {
                    it.scanQty = it.scanQty!! - dialogDeleteData.size
                    if (it.barContext != null) {
                        for (d in dialogDeleteData) {
                            it.barContext!!.remove(d)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
            dialog.cancel()
        }
        dialogView.dialog_recycler.layoutManager = MyLinearlayoutManager(this@DistributionItemActivity, LinearLayoutManager.VERTICAL, false)
        dialogAdapter = DisDialogAdapter(ArrayList(), object : AddLessClickListener {
            override fun <T> onItemClick(view: RecyclerView.ViewHolder, beanData: T, position: Int, type: Int) {

            }

            override fun <T> onItemRemove(data: T, position: Int) {
                data as String
                dialogDeleteData.add(data)
            }
        })
        dialogView.dialog_recycler.adapter = dialogAdapter
        val callback = ItemTouchHelperCallback(dialogAdapter, true)
        val mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper.attachToRecyclerView(dialogView.dialog_recycler)
        dialog = builder.create()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun initClick() {
        done.setOnClickListener {
            //save data
            getSaveData()
            presenter.saveDistribution()
        }
        done2.setOnClickListener {
            val i = Intent(this@DistributionItemActivity, ContractSearchActivity::class.java)
            i.putExtra(ContractSearchActivity.WHERE_IS_IT, ContractSearchActivity.FP)
            startActivityForResult(i, 0)
        }
    }

    private fun getSaveData() {
        saveData.rows.clear()
        val zxStoreId = User.getUser().storeId
        val wxStoreId = d.shelvesId
        val disTime = d.disTime
        adapter.data.rows.forEach {
            if (it.itemNo != "") {
                saveData.rows.add(DistributionRequestBean(zxStoreId, wxStoreId, disTime, it.itemNo, it.trsQty, it.scanQty, it.barContext))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            val msg = data.getStringExtra("message")
            val bar = data.getStringExtra("bar")
            if (msg != null && bar != null) {
                presenter.addDistributionItem(msg, bar)
            }
        }
    }

    override fun initData() {
        presenter.getDistributionItem()
    }

    override fun <T> showView(aData: T) {
        if (aData !is DistributionItemResult) {
            showPrompt("获得数据类型错误")
            errorDealWith()
            return
        }
        adapter = DistributionItemAdapter(aData, this, object : ItemClickListener {
            override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {
                val i = Intent(this@DistributionItemActivity, ContractSearchActivity::class.java)
                i.putExtra(ContractSearchActivity.WHERE_IS_IT, ContractSearchActivity.FP)
                startActivityForResult(i, 0)
            }

            //less
            override fun <T> onItemRemove(data: T, position: Int) {
                data as DistributionItemBean
                if (data.barContext != null) {
                    if (data.barContext!!.size != 0) {
                        dialogDeleteData.clear()
                        nowEditDataId = data.itemNo
                        dialogAdapter.addItems(data.barContext!!)
                        dialog.show()
                    }
                } else {
                    data.scanQty = data.scanQty!! - 1
                    adapter.notifyDataSetChanged()
                }
            }

            //add
            override fun <T> onItemEdit(data: T, position: Int) {
                data as DistributionItemBean
                data.scanQty = data.scanQty!! + 1
                adapter.notifyDataSetChanged()
            }

            override fun onItemLongClick(view: RecyclerView.ViewHolder, position: Int) {
                if (view is DistributionItemAdapter.ViewHolder) {
                    val barContexts = adapter.data.rows[position].barContext
                    if (barContexts != null) {
                        dialogDeleteData.clear()
                        nowEditDataId = adapter.data.rows[position].itemNo
                        dialogAdapter.addItems(barContexts)
                        dialog.show()
                    }
                }
            }
        })
        orderRecycler.adapter = adapter
    }

    //扫描新商品添加进来
    override fun <T> requestSuccess(rData: T) {
        if (rData !is DistributionItemBean) {
            showPrompt("获得数据类型错误")
            errorDealWith()
            return
        }
        val x = adapter.data.rows.filter { it.itemNo == rData.itemNo }
        if (x.isNotEmpty()) {
            x.forEach {
                if (it.barContext == null) it.barContext = ArrayList()
                it.scanQty = it.scanQty!! + 1
                if (rData.barContext != null) {
                    if (rData.barContext!!.isNotEmpty()) {
                        if (rData.barContext!![0] != "") {
                            it.barContext!!.addAll(rData.barContext!!)
                        }
                    }
                }
            }
            adapter.notifyDataSetChanged()
            try {
                (0 until adapter.data.rows.size)
                        .filter { adapter.data.rows[it].itemNo == rData.itemNo }
                        .forEach { orderRecycler.scrollToPosition(it) }
            } catch (e: Exception) {
            }
        } else {
            rData.newScan = true
            adapter.addData(rData)
        }
        MyScanUtil.getFocusable(scan_edit)
    }

    override fun getData1(): Any? {
        return d.shelvesId
    }

    override fun getData2(): Any? {
        return d.disTime
    }

    override fun getData3(): Any? {
        return saveData
    }

    override fun errorDealWith() {
        showRetry(true)
        orderretry.setOnClickListener {
            initData()
        }
    }

    override fun <T> errorDealWith(eData: T) {
        showRetry(true)
        orderretry.setOnClickListener {
            presenter.saveDistribution()
        }
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