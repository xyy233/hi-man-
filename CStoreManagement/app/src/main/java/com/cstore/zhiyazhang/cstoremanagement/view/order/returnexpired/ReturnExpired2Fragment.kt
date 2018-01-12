package com.cstore.zhiyazhang.cstoremanagement.view.order.returnexpired

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ReturnExpiredBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.returnexpired.ReturnExpiredAdapter
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil.getYMDStringByDate3
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener
import kotlinx.android.synthetic.main.fragment_return_expired2.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by zhiya.zhang
 * on 2018/1/4 15:58.
 */
class ReturnExpired2Fragment : Fragment() {
    private val data = ArrayList<ReturnExpiredBean>()
    private val adapter = ReturnExpiredAdapter(data, 2, object : ItemClickListener {
        override fun onItemClick(view: RecyclerView.ViewHolder, position: Int) {
        }
    })
    private lateinit var mActivity: ReturnExpiredActivity
    private var mType = 0

    companion object {
        private val PAGE_POSITION = "page_position"

        fun newInstance(position: Int): ReturnExpired2Fragment {
            val result = ReturnExpired2Fragment()
            val bundle = Bundle()
            bundle.putInt(PAGE_POSITION, position)
            result.arguments = bundle
            return result
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_return_expired2, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setTextViewDate(CStoreCalendar.getCurrentDate(2), 1)
        setTextViewDate(CStoreCalendar.getCurrentDate(2), 2)
        initDate()
        initSpinner()
        return_expired_list2.layoutManager = MyLinearlayoutManager(context, LinearLayoutManager.VERTICAL, false)
        return_expired_list2.adapter = adapter
    }


    private fun initDate() {
        return_date1.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                mType = 1
                DatePickerDialog(context, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    kotlin.run {
                        editDate(year, month, dayOfMonth)
                    }
                }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).show()
                true
            } else false
        }
        return_date2.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                mType = 2
                DatePickerDialog(context, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    kotlin.run {
                        editDate(year, month, dayOfMonth)
                    }
                }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).show()
                true
            } else false
        }
    }

    private fun editDate(year: Int, month: Int, dayOfMonth: Int) {
        val cal = Calendar.getInstance()
        cal.time = Date(System.currentTimeMillis())
        cal.add(Calendar.MONTH, -3)
        val threeMonthDate = getYMDStringByDate3(cal.time).toLong()
        val mMonth = if (month + 1 < 10) "0${month + 1}" else (month + 1).toString()
        val mDay = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
        val nowDate = "$year$mMonth$mDay".toLong()
        if (nowDate < threeMonthDate) {
            mActivity.showPrompt("只能查询近期三个月内数据！")
        } else {
            val date = "$year-$mMonth-$mDay"
            if (mType == 1) {
                return_date1.text = date
            } else {
                return_date2.text = date
            }
            mActivity.presenter.getDateAll(return_date1.text.toString(), return_date2.text.toString(), return_spinner.selectedItemPosition)
        }
    }

    private fun initSpinner() {

        return_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mActivity.presenter.getDateAll(return_date1.text.toString(), return_date2.text.toString(), position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val adapterResource = ArrayList<String>()
        adapterResource.add(getString(R.string.all))
        adapterResource.add(getString(R.string.check2))
        adapterResource.add(getString(R.string.check3))
        adapterResource.add(getString(R.string.check4))
        adapterResource.add(getString(R.string.check5))
        val adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, adapterResource)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return_spinner.adapter = adapter
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mActivity = context as ReturnExpiredActivity
    }

    override fun onDestroy() {
        data.clear()
        super.onDestroy()
    }

    /**
     * 供activity使用
     */
    fun addItems(newData: ArrayList<ReturnExpiredBean>) {
        adapter.addItems(newData)
    }

    private fun setTextViewDate(date: String, type: Int) {
        if (type == 1) {
            return_date1.text = date
        } else {
            return_date2.text = date
        }
    }

    fun getDate1(): String {
        return return_date1.text.toString()
    }

    fun getDate2(): String {
        return return_date2.text.toString()
    }

    fun getChesk(): Int {
        return return_spinner.selectedItemPosition
    }
}