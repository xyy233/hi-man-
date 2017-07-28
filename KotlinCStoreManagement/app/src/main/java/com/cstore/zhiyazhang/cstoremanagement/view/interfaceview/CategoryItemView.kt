package com.cstore.zhiyazhang.cstoremanagement.view.interfaceview

import android.view.MotionEvent
import com.cstore.zhiyazhang.cstoremanagement.bean.CategoryItemBean
import com.cstore.zhiyazhang.cstoremanagement.bean.OrderCategoryBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.ordercategory.CategoryItemAdapter

/**
 * Created by zhiya.zhang
 * on 2017/7/26 14:26.
 */
interface CategoryItemView {
    val category: OrderCategoryBean

    val sort: String

    val whereIsIt: String

    fun touchAdd(cb: CategoryItemBean, event: MotionEvent, position: Int)

    fun touchLess(cb: CategoryItemBean, event: MotionEvent, position: Int)

    val categoryList: ArrayList<CategoryItemBean>

    fun updateDone()
}
