package com.cstore.zhiyazhang.cstoremanagement.view

import android.app.ActivityOptions
import android.content.Intent
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.LogoBean
import com.cstore.zhiyazhang.cstoremanagement.bean.OrderCategoryBean
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.presenter.LogoAdapter
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.GsonUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import com.cstore.zhiyazhang.cstoremanagement.view.order.category.CategoryActivity
import com.cstore.zhiyazhang.cstoremanagement.view.order.category.CategoryItemActivity
import com.cstore.zhiyazhang.cstoremanagement.view.order.contract.ContractTypeActivity
import com.cstore.zhiyazhang.cstoremanagement.view.order.returnexpired.ReturnExpiredActivity
import com.cstore.zhiyazhang.cstoremanagement.view.order.returnpurchase.ReturnPurchaseActivity
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2017/6/19 15:39.
 */
class ContractOrder(override val layoutId: Int = R.layout.activity_order) : MyActivity() {

    override fun initView() {
        my_toolbar.title = getString(R.string.order)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)

        orderRecycler.addItemDecoration(DividerItemDecoration(this@ContractOrder, DividerItemDecoration.VERTICAL))
        orderRecycler.addItemDecoration(DividerItemDecoration(this@ContractOrder, DividerItemDecoration.HORIZONTAL))

        orderLoading.visibility = View.VISIBLE
    }

    override fun initClick() {
        val data = ArrayList<LogoBean>()
        setData(data)
        orderRecycler.layoutManager = GridLayoutManager(this@ContractOrder, 3, GridLayoutManager.VERTICAL, false)
        orderRecycler.adapter = LogoAdapter(this@ContractOrder, data, object : ItemClickListener {
            override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {
                view as LogoAdapter.ViewHolder
                when (data[position].position) {
                //测试
                    0 -> {
                        if (User.getUser().cnt == 0) {
                            MyToast.getShortToast(getString(R.string.cnt_not_use))
                            return
                        }
                        val intent = Intent(this@ContractOrder, ContractTypeActivity::class.java)
                        intent.putExtra("is_just_look", false)
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, view.orderItem, "orderItem").toBundle())
                    }
                    1 -> {
                        if (User.getUser().cnt == 0) {
                            MyToast.getShortToast(getString(R.string.cnt_not_use))
                            return
                        }
                        val intent = Intent(this@ContractOrder, ContractTypeActivity::class.java)
                        intent.putExtra("is_just_look", true)
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, view.orderItem, "orderItem").toBundle())
                    }
                    2 -> {
                        val intent = Intent(this@ContractOrder, CategoryActivity::class.java)
                        intent.putExtra(whereIsIt, "category")
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, view.orderItem, "orderItem").toBundle())
                    }
                    3 -> {
                        val intent = Intent(this@ContractOrder, CategoryActivity::class.java)
                        intent.putExtra(whereIsIt, "shelf")
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, view.orderItem, "orderItem").toBundle())
                    }
                    4 -> {
                        val intent = Intent(this@ContractOrder, CategoryItemActivity::class.java)
                        intent.putExtra(whereIsIt, "search")
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, view.orderItem, "orderItem").toBundle())
                    }
                    5 -> {
                        val intent = Intent(this@ContractOrder, CategoryActivity::class.java)
                        intent.putExtra(whereIsIt, "nop")//new or Promotion
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, view.orderItem, "orderItem").toBundle())
                    }
                    6 -> {
                        val intent = Intent(this@ContractOrder, CategoryActivity::class.java)
                        intent.putExtra(whereIsIt, "self")
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, view.orderItem, "orderItem").toBundle())
                    }
                    7 -> {
                        val intent = Intent(this@ContractOrder, CategoryActivity::class.java)
                        intent.putExtra(whereIsIt, "fresh1")
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, view.orderItem, "orderItem").toBundle())
                    }
                    8 -> {
                        val intent = Intent(this@ContractOrder, CategoryActivity::class.java)
                        intent.putExtra(whereIsIt, "fresh2")
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, view.orderItem, "orderItem").toBundle())
                    }
                    9 -> {
                        val intent = Intent(this@ContractOrder, CategoryActivity::class.java)
                        intent.putExtra(whereIsIt, "ord")
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, view.orderItem, "orderItem").toBundle())
                    }
                    10 -> {
                        val intent = Intent(this@ContractOrder, ReturnPurchaseActivity::class.java)
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, view.orderItem, "orderItem").toBundle())
                    }
                    11 -> {
                        AlertDialog.Builder(this@ContractOrder)
                                .setTitle("提示")
                                .setMessage("是否需要一键下单？")
                                .setPositiveButton("保存", { _, _ ->
                                    orderLoading.visibility = View.VISIBLE
                                    runAutoOrd()
                                })
                                .setNegativeButton("退出", { _, _ ->

                                })
                                .show()
                    }
                    12 -> {
                        val intent = Intent(this@ContractOrder, ReturnExpiredActivity::class.java)
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, view.orderItem, "orderItem").toBundle())
                    }
                }
            }

        })
        orderLoading.setOnClickListener {
            MyToast.getLongToast(getString(R.string.loadingCall))
        }
        orderretry.setOnClickListener {
            orderpro.visibility = View.VISIBLE
            orderprotext.visibility = View.VISIBLE
            orderretry.visibility = View.GONE
            runOrdT2()
        }
    }

    override fun initData() {
        runOrdT2()
    }

    val whereIsIt = "whereIsIt"

    override fun onBackPressed() {
        if (orderLoading.visibility == View.GONE) {
            super.onBackPressed()
        } else {
            MyToast.getLongToast(getString(R.string.loadingCall))
        }
    }

    private fun setData(data: ArrayList<LogoBean>) {

//        data.add(LogoBean(R.mipmap.ic_contract_order, getString(R.string.contract_order), 0))
//        data.add(LogoBean(R.mipmap.ic_contract_see_order, getString(R.string.contract_order_toview), 1))
        //保持顺序，手动插入 测试
        if (User.getUser().cnt == 0) {
            data.add(LogoBean(R.mipmap.ic_contract_order_close, getString(R.string.contract_order), 0))
            data.add(LogoBean(R.mipmap.ic_contract_see_order_close, getString(R.string.contract_order_toview), 1))
        } else {
            data.add(LogoBean(R.mipmap.ic_contract_order, getString(R.string.contract_order), 0))
            data.add(LogoBean(R.mipmap.ic_contract_see_order, getString(R.string.contract_order_toview), 1))
        }
        data.add(LogoBean(R.mipmap.ic_categroy_order, getString(R.string.category_order), 2))
        data.add(LogoBean(R.mipmap.ic_shelf_order, getString(R.string.shelf_order), 3))
        data.add(LogoBean(R.mipmap.ic_unit_order, getString(R.string.unit_order), 4))
        data.add(LogoBean(R.mipmap.ic_new_order, getString(R.string.new_order), 5))
        data.add(LogoBean(R.mipmap.ic_supplies_order, getString(R.string.supplies_order), 6))
        data.add(LogoBean(R.mipmap.ic_order_fresh1, getString(R.string.fresh1), 7))
        data.add(LogoBean(R.mipmap.ic_order_fresh2, getString(R.string.fresh2), 8))
        data.add(LogoBean(R.mipmap.ic_ord_contract, getString(R.string.ord_contract), 9))
        data.add(LogoBean(R.mipmap.ic_return_purchase, getString(R.string.return_purchase), 10))
        data.add(LogoBean(R.mipmap.ic_under_order, getString(R.string.under_order), 11))
        data.add(LogoBean(R.mipmap.ic_return_expired, getString(R.string.return_expired), 12))
        data.sortBy { it.position }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    private fun runOrdT2() {
        val ip = MyApplication.getIP()
        if (ip == MyApplication.instance().getString(R.string.notFindIP)) {
            MyToast.getShortToast(ip)
            return
        }
        val handler = Handler()
        Thread(Runnable {
            //2017-10-15  修改为每次进入都进行存储过程，保证为最新数据
            val result = SocketUtil.initSocket(ip, MySql.appOrdT2(), 600).inquire()
            if (result == "0") {
                handler.post {
                    orderLoading.visibility = View.GONE
                }
            } else {
                handler.post {
                    MyToast.getLongToast(result)
                    orderpro.visibility = View.GONE
                    orderprotext.visibility = View.GONE
                    orderretry.visibility = View.VISIBLE
                }
            }
        }).start()
    }

    private fun runAutoOrd() {
        val ip = MyApplication.getIP()
        if (ip == MyApplication.instance().getString(R.string.notFindIP)) {
            MyToast.getShortToast(ip)
            return
        }
        val handler = Handler()
        Thread(Runnable {
            val result = SocketUtil.initSocket(ip, MySql.autoOrd(), 300).inquire()
            if (result == "0") {
                runAutoOrd2(ip, handler)
            } else {
                handler.post {
                    MyToast.getLongToast(getString(R.string.system_error) + "1" + result)
                    orderLoading.visibility = View.GONE
                }
            }
        }).start()
    }

    private fun runAutoOrd2(ip: String, handler: Handler) {
        val result = SocketUtil.initSocket(ip, MySql.appOrdT2(), 600).inquire()
        if (result == "0") {
            getEditDataByAutoOrd(ip, handler)
        } else {
            handler.post {
                MyToast.getLongToast(getString(R.string.system_error) + "2" + result)
                orderLoading.visibility = View.GONE
            }
        }
    }

    private fun getEditDataByAutoOrd(ip: String, handler: Handler) {
        val result = SocketUtil.initSocket(ip, MySql.getAllEditData()).inquire()
        val category = ArrayList<OrderCategoryBean>()
        try {
            category.addAll(GsonUtil.getCategory(result))
        } catch (e: Exception) {
        }
        if (category.isEmpty()) {
            handler.post {
                MyToast.getLongToast(getString(R.string.system_error) + "3" + result)
                orderLoading.visibility = View.GONE
            }
        } else {
            handler.post {
                val msg = " 已订商品数量：${category[0].ordSku},已订货总金额：${category[0].ordPrice}"
                MyToast.getLongToast(getString(R.string.saveDone) + msg)
                orderLoading.visibility = View.GONE
            }
        }
    }


}