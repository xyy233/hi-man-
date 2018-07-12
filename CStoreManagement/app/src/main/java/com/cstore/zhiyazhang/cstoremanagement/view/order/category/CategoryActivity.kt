package com.cstore.zhiyazhang.cstoremanagement.view.order.category

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.*
import com.cstore.zhiyazhang.cstoremanagement.presenter.ordercategory.OrderCategoryAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.ordercategory.OrderCategoryPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.DataClickListener
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.ContractTypeView
import com.cstore.zhiyazhang.cstoremanagement.view.order.contract.ContractSearchActivity
import kotlinx.android.synthetic.main.activity_order_category.*
import kotlinx.android.synthetic.main.contract_type_recycler.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2017/7/25 13:37.
 */
class CategoryActivity(override val layoutId: Int = R.layout.activity_order_category) : MyActivity(), ContractTypeView {


    private var adapter: OrderCategoryAdapter? = null

    override val isJustLook: Boolean
        get() = false

    override val whereIsIt: String
        get() = intent.getStringExtra(ContractSearchActivity.WHERE_IS_IT)

    override fun showUsaTime(isShow: Boolean) {
    }

    private val mPresenter = OrderCategoryPresenter(this, this)

    override fun initClick() {
        category_retry.setOnClickListener {
            category_retry.visibility = View.GONE
            when (whereIsIt) {
                "category" -> {
                    mPresenter.getAllCategory()
                }
                "shelf" -> {
                    mPresenter.getShelf()
                }
                "self" -> {
                    mPresenter.getSelf()
                }
                "nop" -> {
                    mPresenter.getNOP()
                }
                "fresh1" -> {
                    mPresenter.getFresh(1)
                }
                "fresh2" -> {
                    mPresenter.getFresh(2)
                }
                "ord" -> {
                    mPresenter.getOrdCategory()
                }
            }
        }
    }

    override fun initData() {}

    override fun initView() {
        when (whereIsIt) {
            "category" -> {
                my_toolbar.title = getString(R.string.category_order)
                header_text1.text = getString(R.string.type_name)
                header_text2.text = getString(R.string.all_sku)
                header_text3.text = getString(R.string.ord_qty)
                header_text3_5.visibility = View.VISIBLE
                header_text3_5.text = getString(R.string.ord_count)
                header_text4.text = getString(R.string.order_price)
            }
            "ord" -> {
                my_toolbar.title = getString(R.string.ord_contract)
                header_text1.text = getString(R.string.type_name)
                header_text2.visibility = View.GONE
                header_text3.text = getString(R.string.ord_qty)
                header_text3_5.visibility = View.VISIBLE
                header_text3_5.text = getString(R.string.ord_count)
                header_text4.text = getString(R.string.order_price)
            }
            "shelf" -> {
                my_toolbar.title = getString(R.string.shelf_order)
                header_h.visibility = View.GONE
                header_v.visibility = View.VISIBLE
                header_text1_v.text = getString(R.string.shelf_name)
                header_text2_v.text = getString(R.string.all_sku)
                header_text3_v.text = getString(R.string.ord_qty)
                header_text3_5_v.text = getString(R.string.ord_count)
                header_text4_v.text = getString(R.string.order_price)
            }
            "self" -> {
                my_toolbar.title = getString(R.string.supplies_order)
                header_text1.text = getString(R.string.self_value)
                header_text2.text = getString(R.string.all_sku)
                header_text3.text = getString(R.string.ord_qty)
                header_text3_5.visibility = View.VISIBLE
                header_text3_5.text = getString(R.string.ord_count)
                header_text4.text = getString(R.string.order_sell)
            }
            "nop" -> {
                my_toolbar.title = getString(R.string.new_order)
                header_text1.text = getString(R.string.nop_value)
                header_text2.text = getString(R.string.all_sku)
                header_text3.text = getString(R.string.ord_qty)
                header_text3_5.visibility = View.VISIBLE
                header_text3_5.text = getString(R.string.ord_count)
                header_text4.text = getString(R.string.order_price)
            }
            "fresh1" -> {
                my_toolbar.title = getString(R.string.fresh1)
                header_text1.text = getString(R.string.freshGroup)
                header_text2.text = getString(R.string.all_sku)
                header_text3.text = getString(R.string.ord_qty)
                header_text3_5.visibility = View.VISIBLE
                header_text3_5.text = getString(R.string.ord_count)
                header_text4.text = getString(R.string.order_price)
            }
            "fresh2" -> {
                my_toolbar.title = getString(R.string.fresh2)
                header_text1.text = getString(R.string.freshGroup)
                header_text2.text = getString(R.string.all_sku)
                header_text3.text = getString(R.string.ord_qty)
                header_text3_5.visibility = View.VISIBLE
                header_text3_5.text = getString(R.string.ord_count)
                header_text4.text = getString(R.string.order_price)
            }
        }
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        type_list.layoutManager = LinearLayoutManager(this@CategoryActivity, LinearLayoutManager.VERTICAL, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun <T> requestSuccess(rData: T) {
        when (whereIsIt) {
            "category" -> {
                val i = Intent(this@CategoryActivity, CategoryItemActivity::class.java)
                i.putExtra("category", rData as OrderCategoryBean)
                i.putExtra("whereIsIt", "category")
                i.putExtra("itemIds", (adapter?.data as ArrayList<OrderCategoryBean>))
                startActivity(i)
            }
            "ord" -> {
                val i = Intent(this@CategoryActivity, CategoryItemActivity::class.java)
                i.putExtra("category", rData as OrderCategoryBean)
                i.putExtra("whereIsIt", "ord")
                i.putExtra("itemIds", (adapter?.data as ArrayList<OrderCategoryBean>))
                startActivity(i)
            }
            "shelf" -> {
                val i = Intent(this@CategoryActivity, CategoryItemActivity::class.java)
                i.putExtra("shelf", rData as ShelfBean)
                i.putExtra("whereIsIt", "shelf")
                i.putExtra("itemIds", (adapter?.data as ArrayList<ShelfBean>))
                startActivity(i)
            }
            "self" -> {
                val i = Intent(this@CategoryActivity, CategoryItemActivity::class.java)
                i.putExtra("self", rData as SelfBean)
                i.putExtra("whereIsIt", "self")
                i.putExtra("itemIds", (adapter?.data as ArrayList<SelfBean>))
                startActivity(i)
            }
            "nop" -> {
                val i = Intent(this@CategoryActivity, CategoryItemActivity::class.java)
                i.putExtra("nop", rData as NOPBean)
                i.putExtra("whereIsIt", "nop")
                i.putExtra("itemIds", (adapter?.data as ArrayList<NOPBean>))
                startActivity(i)
            }
            "fresh1" -> {
                val i = Intent(this@CategoryActivity, CategoryItemActivity::class.java)
                i.putExtra("fg", rData as FreshGroup)
                i.putExtra("whereIsIt", "fresh")
                i.putExtra("itemIds", (adapter?.data as ArrayList<FreshGroup>))
                startActivity(i)
            }
            "fresh2" -> {
                val i = Intent(this@CategoryActivity, CategoryItemActivity::class.java)
                i.putExtra("fg", rData as FreshGroup)
                i.putExtra("whereIsIt", "fresh")
                i.putExtra("itemIds", (adapter?.data as ArrayList<FreshGroup>))
                startActivity(i)
            }
        }
    }

    override fun showLoading() {
        category_loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        category_loading.visibility = View.GONE
    }

    override fun <T> showView(aData: T) {
        if (adapter == null) {
            adapter = if (whereIsIt == "fresh1" || whereIsIt == "fresh2") {
                OrderCategoryAdapter("fresh", aData as ArrayList<*>, object : DataClickListener {
                    override fun <T> click(data: T) {
                        requestSuccess(data)
                    }
                })
            } else {
                OrderCategoryAdapter(whereIsIt, aData as ArrayList<*>, object : DataClickListener {
                    override fun <T> click(data: T) {
                        requestSuccess(data)
                    }
                })
            }

            type_list.adapter = adapter
            this.adapter = adapter
        } else {
            adapter!!.data = aData as ArrayList<*>
            adapter!!.notifyDataSetChanged()
        }
    }

    override fun errorDealWith() {
        category_retry.visibility = View.VISIBLE
    }

    override fun onStart() {
        try {
            when (whereIsIt) {
                "category" -> mPresenter.getAllCategory()
                "ord" -> mPresenter.getOrdCategory()
                "shelf" -> mPresenter.getShelf()
                "self" -> mPresenter.getSelf()
                "nop" -> mPresenter.getNOP()
                "fresh1" -> mPresenter.getFresh(1)
                "fresh2" -> mPresenter.getFresh(2)
            }
        } catch (e: Exception) {
        }
        super.onStart()
    }
}
