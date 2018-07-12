package com.cstore.zhiyazhang.cstoremanagement.utils.recycler

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet

/**
 * Created by zhiya.zhang
 * on 2017/5/17 12:18.
 */

/*可以动态设置能否滑动*/
class MyLinearlayoutManager : LinearLayoutManager {
    private var isScrollEnabled = true

    constructor(context: Context) : super(context) {}

    constructor(context: Context, orientation: Int, reverseLayout: Boolean) : super(context, orientation, reverseLayout) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {}

    fun setScrollEnabled(flag: Boolean) {
        this.isScrollEnabled = flag
    }


    override fun canScrollVertically(): Boolean {
        return isScrollEnabled && super.canScrollVertically()
    }
}
