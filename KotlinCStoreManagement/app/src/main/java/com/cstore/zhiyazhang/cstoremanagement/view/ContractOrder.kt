package com.cstore.zhiyazhang.cstoremanagement.view

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
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
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import com.cstore.zhiyazhang.cstoremanagement.view.order.category.CategoryActivity
import com.cstore.zhiyazhang.cstoremanagement.view.order.contract.ContractTypeActivity
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
/*        order1.setOnClickListener {
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
        }*/
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
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@ContractOrder, view, "orderItem").toBundle())
                    }
                    2 -> {
                        MyToast.getShortToast(getString(R.string.writh_code))
                    }
                    3 -> {
                        MyToast.getShortToast(getString(R.string.writh_code))
                    }
                    4 -> {
                        MyToast.getShortToast(getString(R.string.writh_code))
                    }
                    5 -> {
                        MyToast.getShortToast(getString(R.string.writh_code))
                    }
                    6 -> {
                        MyToast.getShortToast(getString(R.string.writh_code))
                    }
                    7 -> {
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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
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