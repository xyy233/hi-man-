package com.cstore.zhiyazhang.cstoremanagement.view.interfaceview

import android.view.MotionEvent
import com.cstore.zhiyazhang.cstoremanagement.bean.ContractBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ContractTypeBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.contract.ContractAdapter

/**
 * Created by zhiya.zhang
 * on 2017/6/14 15:52.
 */
interface ContractView {
    /**
     * 得到是否是通过搜索查询的
     */
    val isSearch: Boolean
    /**
     * 得到是否是得到所有改过的商品
     */
    val isJustLook: Boolean
    /**
     * 得到搜索的关键字
     */
    val searchMsg: String
    /**
     * 得到查看的类
     */
    val contractType: ContractTypeBean
    /**
     * 得到是什么排序
     */
    val sort: String

    /**
     * 得到、写入当前页
     */
    fun getPage(): Int

    fun setPage(value: Int)

    /**
     * 订量修改加
     */
    fun touchAdd(cb: ContractBean, event: MotionEvent, position: Int)

    /**
     * 订量修改减
     */
    fun touchLess(cb: ContractBean, event: MotionEvent, position: Int)

    /**
     * 上拉加载
     */
    fun pullLoading(adapter: ContractAdapter)

    /**
     * 显示无信息
     */
    fun showNoMessage()

    /**
     * 得到要修改的数据
     */
    val contractList: List<ContractBean>

    fun removeChangeDate()

    /**
     * 更新成功
     */
    fun updateDone()

    fun clickImage(cb: ContractBean, position: Int)
}
