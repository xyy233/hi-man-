package com.cstore.zhiyazhang.cstoremanagement.view.order.category

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.NOPBean
import com.cstore.zhiyazhang.cstoremanagement.bean.OrderCategoryBean
import com.cstore.zhiyazhang.cstoremanagement.bean.SelfBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ShelfBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.ordercategory.OrderCategoryAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.ordercategory.OrderCategoryPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.ContractTypeView
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import kotlinx.android.synthetic.main.activity_order_category.*
import kotlinx.android.synthetic.main.contract_type_recycler.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2017/7/25 13:37.
 */
class CategoryActivity(override val layoutId: Int = R.layout.activity_order_category) : MyActivity(), GenericView, ContractTypeView {
    var adapter: OrderCategoryAdapter? = null

    override val isJustLook: Boolean
        get() = false

    override val whereIsIt: String
        get() = intent.getStringExtra("whereIsIt")

    override fun showUsaTime(isShow: Boolean) {
    }

    val mPresenter = OrderCategoryPresenter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        when (whereIsIt) {
            "category" -> {
                my_toolbar.title = getString(R.string.category_order)
                header_text1.text = getString(R.string.type_name)
                header_text2.text = getString(R.string.all_sku)
                header_text3.text = getString(R.string.ord_qty)
                header_text4.text = getString(R.string.order_price)
            }
            "shelf" -> {
                my_toolbar.title = getString(R.string.shelf_order)
                header_text1.text = getString(R.string.shelf_name)
                header_text2.visibility = View.GONE
                header_text3.visibility = View.GONE
                header_text4.visibility = View.GONE
            }
            "self" -> {
                my_toolbar.title = getString(R.string.supplies_order)
                header_text1.text = getString(R.string.self_value)
                header_text2.visibility = View.GONE
                header_text3.visibility = View.GONE
                header_text4.visibility = View.GONE
            }
            "nop" -> {
                my_toolbar.title = getString(R.string.new_order)
                header_text1.text = getString(R.string.nop_value)
                header_text2.visibility = View.GONE
                header_text3.visibility = View.GONE
                header_text4.visibility = View.GONE
            }
        }
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        type_list.layoutManager = LinearLayoutManager(this@CategoryActivity, LinearLayoutManager.VERTICAL, false)
        category_retry.setOnClickListener {
            showLoading()
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
            }
        }
        showLoading()
        when (whereIsIt) {
            "category" -> mPresenter.getAllCategory()
            "shelf" -> mPresenter.getShelf()
            "self" -> mPresenter.getSelf()
            "nop" -> mPresenter.getNOP()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun <T> requestSuccess(objects: T) {
        when (whereIsIt) {
            "category" -> {
                val i = Intent(this@CategoryActivity, CategoryItemActivity::class.java)
                i.putExtra("category", objects as OrderCategoryBean)
                i.putExtra("whereIsIt", "category")
                startActivity(i)
            }
            "shelf" -> {
                val i = Intent(this@CategoryActivity, CategoryItemActivity::class.java)
                i.putExtra("shelf", objects as ShelfBean)
                i.putExtra("whereIsIt", "shelf")
                startActivity(i)
            }
            "self" -> {
                val i = Intent(this@CategoryActivity, CategoryItemActivity::class.java)
                i.putExtra("self", objects as SelfBean)
                i.putExtra("whereIsIt", "self")
                startActivity(i)
            }
            "nop"->{
                val i = Intent(this@CategoryActivity, CategoryItemActivity::class.java)
                i.putExtra("nop", objects as NOPBean)
                i.putExtra("whereIsIt", "nop")
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

    override fun <T> showView(adapter: T) {
        when (adapter) {
            is OrderCategoryAdapter -> {
                type_list.adapter = adapter
                this.adapter = adapter
            }
            else -> MyToast.getShortToast(getString(R.string.system_error))
        }
    }

    override fun errorDealWith() {
        category_retry.visibility = View.VISIBLE
    }

    override fun onStart() {
        try{
            when (whereIsIt) {
                "category" -> {
                    getPreviousType()
                    adapter?.notifyDataSetChanged()
                }
            }
        }catch (e:Exception){}
        super.onStart()
    }

    private fun getPreviousType() {
        val sp = getSharedPreferences("cib", Context.MODE_PRIVATE)
        if (sp.getString("categoryId", "") != "") {
            val changeCIB = (adapter?.data as ArrayList<OrderCategoryBean>).find { it.categoryId == sp.getString("categoryId", "") }
            changeCIB?.ordSku = sp.getInt("ordSku", changeCIB?.ordSku!!)
            val editor = sp.edit()
            editor.clear()
            editor.apply()
        }
    }
}
