package com.cstore.zhiyazhang.cstoremanagement.view

import android.app.ActivityOptions
import android.content.Intent
import android.os.Handler
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import com.cstore.zhiyazhang.cstoremanagement.view.order.category.CategoryActivity
import com.cstore.zhiyazhang.cstoremanagement.view.order.category.CategoryItemActivity
import com.cstore.zhiyazhang.cstoremanagement.view.order.contract.ContractTypeActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
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
        val data = ArrayList<OrderData>()
        setData(data)
        orderRecycler.layoutManager = GridLayoutManager(this@ContractOrder, 3, GridLayoutManager.VERTICAL, false)
        orderRecycler.adapter = OrderAdapter(data, object : ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                when (data[position].position) {
                    0 -> {
                        val intent = Intent(this@ContractOrder, ContractTypeActivity::class.java)
                        intent.putExtra("is_just_look", false)
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, view, "orderItem").toBundle())
                    }
                    1 -> {
                        val intent = Intent(this@ContractOrder, CategoryActivity::class.java)
                        intent.putExtra(whereIsIt, "category")
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, view, "orderItem").toBundle())
                    }
                    2 -> {
                        val intent = Intent(this@ContractOrder, CategoryActivity::class.java)
                        intent.putExtra(whereIsIt, "shelf")
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, view, "orderItem").toBundle())
                    }
                    3 -> {
                        val intent = Intent(this@ContractOrder, CategoryItemActivity::class.java)
                        intent.putExtra(whereIsIt, "search")
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, view, "orderItem").toBundle())
                    }
                    4 -> {
                        val intent = Intent(this@ContractOrder, CategoryActivity::class.java)
                        intent.putExtra(whereIsIt, "nop")//new or Promotion
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, view, "orderItem").toBundle())
                    }
                    5 -> {
                        val intent = Intent(this@ContractOrder, CategoryActivity::class.java)
                        intent.putExtra(whereIsIt, "self")
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, view, "orderItem").toBundle())
                    }
                    6 -> {
                        val intent = Intent(this@ContractOrder, ContractTypeActivity::class.java)
                        intent.putExtra("is_just_look", true)
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, view, "orderItem").toBundle())
                    }
                    7->{
                        val intent=Intent(this@ContractOrder,CategoryActivity::class.java)
                        intent.putExtra(whereIsIt,"fresh1")
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, view, "orderItem").toBundle())
                    }
                    8->{
                        val intent=Intent(this@ContractOrder,CategoryActivity::class.java)
                        intent.putExtra(whereIsIt,"fresh2")
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, view, "orderItem").toBundle())
                    }
                }
            }

            override fun onItemLongClick(view: View, position: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    val whereIsIt ="whereIsIt"

    override fun onBackPressed() {
        if (orderLoading.visibility == View.GONE) {
            super.onBackPressed()
        } else {
            MyToast.getLongToast(getString(R.string.loadingCall))
        }
    }

    private fun setData(data: ArrayList<OrderData>) {
        //保持顺序，手动插入
        if (User.getUser().cnt == 0) {
            data.add(OrderData(R.mipmap.ic_categroy_order, getString(R.string.category_order), 1))
            data.add(OrderData(R.mipmap.ic_shelf_order, getString(R.string.shelf_order), 2))
            data.add(OrderData(R.mipmap.ic_unit_order, getString(R.string.unit_order), 3))
            data.add(OrderData(R.mipmap.ic_new_order, getString(R.string.new_order), 4))
            data.add(OrderData(R.mipmap.ic_supplies_order, getString(R.string.supplies_order), 5))
            data.add(OrderData(R.mipmap.ic_order_fresh1,getString(R.string.fresh1),7))
            data.add(OrderData(R.mipmap.ic_order_fresh2,getString(R.string.fresh2),8))
        } else {
            data.add(OrderData(R.mipmap.ic_contract_order, getString(R.string.contract_order), 0))
            data.add(OrderData(R.mipmap.ic_categroy_order, getString(R.string.category_order), 1))
            data.add(OrderData(R.mipmap.ic_shelf_order, getString(R.string.shelf_order), 2))
            data.add(OrderData(R.mipmap.ic_unit_order, getString(R.string.unit_order), 3))
            data.add(OrderData(R.mipmap.ic_new_order, getString(R.string.new_order), 4))
            data.add(OrderData(R.mipmap.ic_supplies_order, getString(R.string.supplies_order), 5))
            data.add(OrderData(R.mipmap.ic_contract_see_order, getString(R.string.contract_order_toview), 6))
            data.add(OrderData(R.mipmap.ic_order_fresh1,getString(R.string.fresh1),7))
            data.add(OrderData(R.mipmap.ic_order_fresh2,getString(R.string.fresh2),8))
        }

      //调试使用
/*            data.add(OrderData(R.mipmap.ic_contract_order, getString(R.string.contract_order), 0))
            data.add(OrderData(R.mipmap.ic_categroy_order, getString(R.string.category_order), 1))
            data.add(OrderData(R.mipmap.ic_shelf_order, getString(R.string.shelf_order), 2))
            data.add(OrderData(R.mipmap.ic_unit_order, getString(R.string.unit_order), 3))
            data.add(OrderData(R.mipmap.ic_new_order, getString(R.string.new_order), 4))
            data.add(OrderData(R.mipmap.ic_supplies_order, getString(R.string.supplies_order), 5))
            data.add(OrderData(R.mipmap.ic_contract_see_order, getString(R.string.contract_order_toview), 6))
            data.add(OrderData(R.mipmap.ic_order_fresh1,getString(R.string.fresh1),7))
            data.add(OrderData(R.mipmap.ic_order_fresh2,getString(R.string.fresh2),8))*/
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
        val handler=Handler()
        Thread(Runnable {
            var result=SocketUtil.initSocket(ip,MySql.judgmentOrdt2).inquire()
            if (result.contains("orderdate")){
                if (result=="[{\"orderdate\":\""+ MyTimeUtil.tomorrowDate+"\"}]"){
                    //运行完成可以操作
                    handler.post {
                        orderLoading.visibility=View.GONE
                    }
                }else{
                    result = SocketUtil.initSocket(ip,MySql.ordT2(),180).inquire()
                    if (result=="0"){
                        handler.post {
                            orderLoading.visibility=View.GONE
                        }
                    }else{
                        handler.post {
                            MyToast.getLongToast(result)
                            orderpro.visibility = View.GONE
                            orderprotext.visibility = View.GONE
                            orderretry.visibility = View.VISIBLE
                        }
                    }
                }
            }else{
                handler.post {
                    MyToast.getLongToast(result)
                    orderpro.visibility = View.GONE
                    orderprotext.visibility = View.GONE
                    orderretry.visibility = View.VISIBLE
                }
            }

        }).start()
    }

    data class OrderData(val img: Int, val msg: String, val position: Int)

    class OrderAdapter(val data: ArrayList<OrderData>, val itemClick: ItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
                ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false))


        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder) {
                is ViewHolder -> {
                    holder.orderImg.setImageResource(data[position].img)
                    holder.orderMsg.text = data[position].msg
                    holder.orderItem.setOnClickListener { itemClick.onItemClick(holder.orderItem, position) }
                }
            }

        }

        override fun getItemCount(): Int = data.size

        class ViewHolder(itemVIew: View) : RecyclerView.ViewHolder(itemVIew) {
            val orderImg = itemView.findViewById<ImageView>(R.id.orderImg)
            val orderMsg = itemView.findViewById<TextView>(R.id.orderMsg)
            val orderItem = itemView.findViewById<LinearLayout>(R.id.orderItem)
        }
    }
}