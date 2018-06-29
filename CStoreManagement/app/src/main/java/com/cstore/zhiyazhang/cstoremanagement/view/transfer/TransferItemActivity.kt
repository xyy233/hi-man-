package com.cstore.zhiyazhang.cstoremanagement.view.transfer

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.OStoreBean
import com.cstore.zhiyazhang.cstoremanagement.bean.PluItemBean
import com.cstore.zhiyazhang.cstoremanagement.bean.TrsBean
import com.cstore.zhiyazhang.cstoremanagement.bean.TrsItemBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.transfer.TransferItemAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.transfer.TransferPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.printer.PrinterServiceConnection
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.cstore.zhiyazhang.cstoremanagement.view.order.contract.ContractSearchActivity
import com.gprinter.service.GpPrintService
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.activity_transfer_item.*
import kotlinx.android.synthetic.main.layout_search_line.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2018/1/19 15:27.
 */
class TransferItemActivity(override val layoutId: Int = R.layout.activity_transfer_item) : MyActivity() {
    private val presenter = TransferPresenter(this)
    private lateinit var date: String
    private var data: TrsBean? = null
    private var stores: ArrayList<String>? = null
    private lateinit var adapter: TransferItemAdapter
    private lateinit var showAction: Animation
    private lateinit var hideAction: Animation
    private var oStore: OStoreBean? = null
    private var conn: PrinterServiceConnection? = null

    override fun initView() {
        my_toolbar.title = getString(R.string.transfer)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        trs_recycler.layoutManager = MyLinearlayoutManager(this, LinearLayout.VERTICAL, false)
        showAction = AnimationUtils.loadAnimation(this, R.anim.anim_slide_in)
        hideAction = AnimationUtils.loadAnimation(this, R.anim.anim_slide_out)
        date = intent.getStringExtra("date")
        stores = intent.getStringArrayListExtra("stores")
        val onClick = object : ItemClickListener {
            override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {
                showSearch()
            }

            override fun <T> onItemEdit(data: T, position: Int) {
                //加量或减量
                showDone()
            }
        }
        if (intent.getSerializableExtra("data") == null) {
            //新建
            adapter = TransferItemAdapter(date, ArrayList(), onClick)
            showSearch()
        } else {
            //已有
            data = intent.getSerializableExtra("data") as TrsBean
            adapter = TransferItemAdapter(date, data!!.items, onClick)
        }
        trs_recycler.adapter = adapter
        toolbar_btn.text = "打印"
        toolbar_btn.visibility = View.VISIBLE
        connection()
    }

    private fun connection() {
        conn = PrinterServiceConnection(null)
        val i = Intent(this, GpPrintService::class.java)
        bindService(i, conn, Context.BIND_AUTO_CREATE)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    private fun print() {
        if (data != null) {
            if (conn!!.getConnectState()) {
                //打印
                conn!!.printTrs(data!!)
            } else {
                //去连接
                conn!!.goActivity(this@TransferItemActivity, true)
            }
        } else {
            showPrompt("无可打印数据")
        }
    }

    override fun initClick() {
        done.setOnClickListener {
            if (judgmentIsEdit()) {
                //有需要修改的数据
                if (data == null) {
                    //是完全新建，需要获得调出店号
                    if (judgmentIsStore()) {
                        //已有要调出的门市
                        adapter.data.forEach {
                            it.trsStoreId = oStore!!.oStoreId
                        }
                        presenter.editTrs(adapter.data.filter { it.editCount != 0 } as ArrayList<TrsItemBean>)
                    } else {
                        //无要调出的门市
                        showPrompt(getString(R.string.trs_please_edit_store))
                    }
                } else {
                    presenter.editTrs(adapter.data.filter { it.editCount != 0 } as ArrayList<TrsItemBean>)
                }
            } else {
                //无需要修改的数据
                hideDone()
                showPrompt(getString(R.string.noSaveMessage))
            }
        }
        loading.setOnClickListener { showPrompt(getString(R.string.wait_loading)) }
        search_btn.setOnClickListener {
            if (search_edit.text.trim() != "") {
                presenter.searchCommodity(search_edit.text.toString())
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(search_edit.windowToken, 0)
            } else {
                showPrompt(getString(R.string.please_edit_id))
            }
        }
        search_edit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (search_edit.text.trim() != "") {
                    presenter.searchCommodity(search_edit.text.toString())
                    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(search_edit.windowToken, 0)
                } else {
                    showPrompt(getString(R.string.please_edit_id))
                }
                true
            } else {
                false
            }
        }
        store_search_btn.setOnClickListener {
            if (store_search_edit.text.trim().isNotEmpty()) {
                val searchData = store_search_edit.text.toString()
                if (stores!!.none { it == searchData }) {
                    presenter.searchStore(searchData)
                } else {
                    showPrompt("今日已创建店号:${store_search_edit.text.trim()}的调出单,不能重复创建！")
                }
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(store_search_edit.windowToken, 0)
            } else {
                showPrompt(getString(R.string.please_edit_store))
            }
        }
        store_search_edit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (store_search_edit.text.trim().isNotEmpty()) {
                    val searchData = store_search_edit.text.toString()
                    if (stores!!.none { it == searchData }) {
                        presenter.searchStore(searchData)
                    } else {
                        showPrompt("今日已创建店号:${store_search_edit.text.trim()}的调出单,不能重复创建！")
                    }
                    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(store_search_edit.windowToken, 0)
                } else {
                    showPrompt(getString(R.string.please_edit_store))
                }
                true
            } else {
                false
            }
        }
        qrcode.setOnClickListener {
            val i = Intent(this@TransferItemActivity, ContractSearchActivity::class.java)
            i.putExtra(ContractSearchActivity.WHERE_IS_IT, ContractSearchActivity.RTN)
            startActivityForResult(i, 0)
        }
        toolbar_btn.setOnClickListener {
            print()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (resultCode == 28) {
                print()
            } else {
                val code = data.getStringExtra("message")
                if (code != null) {
                    presenter.searchCommodity(code)
                } else {
                    showPrompt(getString(R.string.qrcode_error))
                }
            }
        }
    }

    override fun initData() {}

    override fun <T> showView(aData: T) {
        aData as ArrayList<PluItemBean>
        if (aData[0].signType == "N") {
            showPrompt("商品:${aData[0].pluName} 不可调拨！")
            return
        }
        val pib = aData[0]
        search_edit.setText("")
        if (adapter.data.any { it.pluId == pib.pluId }) {
            adapter.data.filter { it.pluId == pib.pluId }.forEach {
                it.trsQty++
                it.editCount++
                it.total = it.trsQty * it.storeUnitPrice!!
            }
            adapter.notifyDataSetChanged()
            showDone()
            return
        }
        var newData =
                if (data == null) {
                    //存粹新建
                    TrsItemBean("O", "", "", 0, 0.0, aData[0].storeUnitPrice!!, 1)
                } else {
                    //已有单子数据
                    TrsItemBean(data!!.trsId, data!!.trsStoreId, data!!.trsNumber, 0, 0.0, aData[0].storeUnitPrice!!, 1)
                }

        newData = newData.setData(newData, pib.pluId!!, pib.pluName!!, pib.storeUnitPrice!!, pib.shipNumber!!, pib.unitCost!!, pib.sellCost!!, pib.invQty!!, pib.vendorId!!, pib.supplierId!!, pib.signType!!, pib.stockType!!)
        adapter.addItem(newData)
    }

    override fun <T> requestSuccess(rData: T) {
        oStore = rData as OStoreBean
        store_search_edit.setText("")
        store_name.text = oStore!!.oStoreName
    }

    override fun <T> updateDone(uData: T) {
        showPrompt(getString(R.string.saveDone))
        onBackPressed()
    }

    /**
     * 判断是否有需要修改的内容
     */
    private fun judgmentIsEdit(): Boolean {
        return adapter.data.any { it.editCount != 0 }
    }

    /**
     * 判断是否已输入调出的门市
     */
    private fun judgmentIsStore(): Boolean {
        return oStore != null
    }

    private fun showDone() {
        if (done.visibility == View.GONE) {
            done.visibility = View.VISIBLE
            done.startAnimation(showAction)
        }
    }

    private fun hideDone() {
        if (done.visibility == View.VISIBLE) {
            done.visibility = View.GONE
            done.startAnimation(hideAction)
        }
    }

    private fun showSearch() {
        if (search.visibility == View.GONE) {
            search.visibility = View.VISIBLE
            search.startAnimation(showAction)
            if (data == null) {
                store_box.visibility = View.VISIBLE
                store_box.startAnimation(showAction)
            }
            adapter.updateShowAdd()
        }
    }

    override fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loading.visibility = View.GONE
    }

}