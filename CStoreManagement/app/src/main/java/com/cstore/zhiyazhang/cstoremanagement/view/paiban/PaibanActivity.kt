package com.cstore.zhiyazhang.cstoremanagement.view.paiban

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.PaibanBean
import com.cstore.zhiyazhang.cstoremanagement.bean.SortPaiban
import com.cstore.zhiyazhang.cstoremanagement.presenter.paiban.PaibanAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.paiban.PaibanPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil.getWeekSundayByDate
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.activity_paiban.*
import kotlinx.android.synthetic.main.loading_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2018/2/8 14:39.
 */
class PaibanActivity(override val layoutId: Int = R.layout.activity_paiban) : MyActivity() {
    private val presenter = PaibanPresenter(this)
    private val dateList = ArrayList<String>()
    private lateinit var adapter: PaibanAdapter
    override fun initView() {
        my_toolbar.title = getString(R.string.paiban)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        paiban_recycler.layoutManager = MyLinearlayoutManager(this, LinearLayoutManager.VERTICAL, false)
        getWeek()
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dateList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        paiban_spinner.adapter = adapter
    }

    private fun getWeek() {
        //得到从上周开始到上四周的日期
        (1..4).mapTo(dateList) { MyTimeUtil.getWeekMondayDate(it * -1) }
        //循环得到从这周开始到第八周的日期
        (0..7).mapTo(dateList) { MyTimeUtil.getWeekMondayDate(it) }
        dateList.sort()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun initClick() {
        loading.setOnClickListener {
            showPrompt(getString(R.string.wait_loading))
        }
        loading_retry.setOnClickListener {
            loading_retry.visibility = View.GONE
            loading_text.visibility = View.VISIBLE
            loading_progress.visibility = View.VISIBLE
            presenter.getDataByDate()
        }
        paiban_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                presenter.getDataByDate()
            }

        }
    }

    override fun initData() {
        presenter.getDataByDate()
    }

    override fun <T> showView(aData: T) {
        aData as ArrayList<PaibanBean>
        aData.sortBy { it.employeeId }
        val data = ArrayList<SortPaiban>()
        var id = ""
        //循环所有数据
        for (pb in aData) {
            //如果记录id和循环id不同就代表是新的人需要添加
            if (id != pb.employeeId) {
                id = pb.employeeId
                //查询到这个新的人的所有数据添加进去
                val sp = SortPaiban(aData.filter { it.employeeId == id } as ArrayList<PaibanBean>)
                data.add(sp)
            }
        }
        adapter = PaibanAdapter(data, object : ItemClickListener {
            override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
        paiban_recycler.adapter = adapter
    }

    override fun errorDealWith() {
        loading_retry.visibility = View.VISIBLE
        loading_text.visibility = View.GONE
        loading_progress.visibility = View.GONE
    }

    override fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loading.visibility = View.GONE
    }

    override fun getData1(): Any? {
        return this@PaibanActivity
    }

    //查询时间
    override fun getData2(): Any? {
        return paiban_spinner.selectedItem.toString()
    }

    //修改数据
    override fun getData3(): Any? {
        return super.getData3()
    }

    override fun getData4(): Any? {
        return getWeekSundayByDate(getData2() as String)
    }

}