package com.cstore.zhiyazhang.cstoremanagement.view.interfaceview

import android.view.MotionEvent
import com.cstore.zhiyazhang.cstoremanagement.bean.*

/**
 * Created by zhiya.zhang
 * on 2017/7/26 14:26.
 */
interface CategoryItemView {
    val category: OrderCategoryBean

    val shelf: ShelfBean

    val self: SelfBean

    val nop:NOPBean

    val sort: String

    val whereIsIt: String

    fun touchAdd(cb: CategoryItemBean, event: MotionEvent, position: Int)

    fun touchLess(cb: CategoryItemBean, event: MotionEvent, position: Int)

    val categoryList: ArrayList<CategoryItemBean>

    fun updateDone()
}
