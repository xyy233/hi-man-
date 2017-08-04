package com.cstore.zhiyazhang.cstoremanagement.view

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
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
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil
import com.cstore.zhiyazhang.cstoremanagement.view.order.category.CategoryActivity
import com.cstore.zhiyazhang.cstoremanagement.view.order.category.CategoryItemActivity
import com.cstore.zhiyazhang.cstoremanagement.view.order.contract.ContractTypeActivity
import com.zhiyazhang.mykotlinapplication.utils.MyApplication
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
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
                        intent.putExtra("whereIsIt", "category")
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, view, "orderItem").toBundle())
                    }
                    2 -> {
                        val intent = Intent(this@ContractOrder, CategoryActivity::class.java)
                        intent.putExtra("whereIsIt", "shelf")
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, view, "orderItem").toBundle())
                    }
                    3 -> {
                        val intent = Intent(this@ContractOrder, CategoryItemActivity::class.java)
                        intent.putExtra("whereIsIt", "search")
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, view, "orderItem").toBundle())
                    }
                    4 -> {
                        val intent = Intent(this@ContractOrder, CategoryActivity::class.java)
                        intent.putExtra("whereIsIt", "nop")//new or Promotion
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, view, "ordrItem").toBundle())
                    }
                    5 -> {
                        val intent = Intent(this@ContractOrder, CategoryActivity::class.java)
                        intent.putExtra("whereIsIt", "self")
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, view, "orderItem").toBundle())
                    }
                    6 -> {
                        val intent = Intent(this@ContractOrder, ContractTypeActivity::class.java)
                        intent.putExtra("is_just_look", true)
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, view, "orderItem").toBundle())
                    }

                }
            }

            override fun onItemLongClick(view: View, position: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
        orderRecycler.addItemDecoration(DividerItemDecoration(this@ContractOrder, DividerItemDecoration.VERTICAL))
        orderRecycler.addItemDecoration(DividerItemDecoration(this@ContractOrder, DividerItemDecoration.HORIZONTAL))
        orderLoading.setOnClickListener {
            MyToast.getLongToast(getString(R.string.loadingCall))
        }
        orderLoading.visibility = View.VISIBLE
        runOrdT2()
        orderretry.setOnClickListener {
            orderpro.visibility = View.VISIBLE
            orderprotext.visibility = View.VISIBLE
            orderretry.visibility = View.GONE
            runOrdT2()
        }
    }

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
        } else {
            data.add(OrderData(R.mipmap.ic_contract_order, getString(R.string.contract_order), 0))
            data.add(OrderData(R.mipmap.ic_categroy_order, getString(R.string.category_order), 1))
            data.add(OrderData(R.mipmap.ic_shelf_order, getString(R.string.shelf_order), 2))
            data.add(OrderData(R.mipmap.ic_unit_order, getString(R.string.unit_order), 3))
            data.add(OrderData(R.mipmap.ic_new_order, getString(R.string.new_order), 4))
            data.add(OrderData(R.mipmap.ic_supplies_order, getString(R.string.supplies_order), 5))
            data.add(OrderData(R.mipmap.ic_contract_see_order, getString(R.string.contract_order_toview), 6))
        }

/*      调试使用
        data.add(OrderData(R.mipmap.ic_contract_order, getString(R.string.contract_order), 0))
        data.add(OrderData(R.mipmap.ic_categroy_order, getString(R.string.category_order), 1))
        data.add(OrderData(R.mipmap.ic_shelf_order, getString(R.string.shelf_order), 2))
        data.add(OrderData(R.mipmap.ic_unit_order, getString(R.string.unit_order), 3))
        data.add(OrderData(R.mipmap.ic_new_order, getString(R.string.new_order), 4))
        data.add(OrderData(R.mipmap.ic_supplies_order, getString(R.string.supplies_order), 5))
        data.add(OrderData(R.mipmap.ic_contract_see_order, getString(R.string.contract_order_toview), 6))*/
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    private fun runOrdT2() {
        val ip = MyApplication.getIP()
        if (ip == MyApplication.instance().getString(R.string.notFindIP)) {
            MyToast.getShortToast(ip)
            return
        }
        SocketUtil.getSocketUtil(ip).inquire(MySql.ordT2(), 180, @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> {
                        //运行完成可以操作
                        orderLoading.visibility = View.GONE
                    }
                    else -> {
                        MyToast.getShortToast(msg.obj as String)
                        orderpro.visibility = View.GONE
                        orderprotext.visibility = View.GONE
                        orderretry.visibility = View.VISIBLE
                    }
                }
            }
        })
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
            val orderImg = itemView.findViewById(R.id.orderImg) as ImageView
            val orderMsg = itemView.findViewById(R.id.orderMsg) as TextView
            val orderItem = itemView.findViewById(R.id.orderItem) as LinearLayout
        }
    }
}