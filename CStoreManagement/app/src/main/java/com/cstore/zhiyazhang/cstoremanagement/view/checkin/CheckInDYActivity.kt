package com.cstore.zhiyazhang.cstoremanagement.view.checkin

import android.content.Intent
import android.graphics.Bitmap
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.CheckInBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.personnel.CheckInDYAdapter
import com.cstore.zhiyazhang.cstoremanagement.presenter.personnel.CheckInDYPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.view.ImageActivity
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.io.ByteArrayOutputStream

/**
 * Created by zhiya.zhang
 * on 2018/3/29 11:37.
 */
class CheckInDYActivity(override val layoutId: Int = R.layout.activity_order) : MyActivity() {
    private val presenter = CheckInDYPresenter(this)
    private var adapter: CheckInDYAdapter? = null
    override fun initView() {
        my_toolbar.title = "大夜打卡查看"
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        orderRecycler.layoutManager = GridLayoutManager(this@CheckInDYActivity, 3, GridLayoutManager.VERTICAL, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return false
    }

    override fun initClick() {
    }

    override fun initData() {
        presenter.getPhotoByDate()
    }

    override fun <T> showView(aData: T) {
        aData as List<*>
        adapter = CheckInDYAdapter(aData, object : ItemClickListener {
            override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {

            }

            override fun <T> onItemEdit(data: T, position: Int) {
                if (data is CheckInBean) {
                    val adapterView = orderRecycler.findViewHolderForAdapterPosition(position) as CheckInDYAdapter.ViewHolder
                    val intent = Intent(this@CheckInDYActivity, ImageActivity::class.java)
                    val transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this@CheckInDYActivity, adapterView.checkImg, "image")
                    val baos = ByteArrayOutputStream()
                    data.fileImg!!.compress(Bitmap.CompressFormat.PNG, 100, baos)
                    intent.putExtra("bmp", baos.toByteArray())
                    intent.putExtra("type", 1)
                    ActivityCompat.startActivity(this@CheckInDYActivity, intent, transitionActivityOptions.toBundle())
                }
            }
        })
        orderRecycler.adapter = adapter
    }



    //显示loading
    override fun showLoading() {
        orderLoading.visibility = View.VISIBLE
    }

    //隐藏loading
    override fun hideLoading() {
        orderLoading.visibility = View.GONE
    }

    override fun getData1(): Any? {
        return this
    }

    override fun getData2(): Any? {
        return MyTimeUtil.nowDate3
    }

}