package com.cstore.zhiyazhang.cstoremanagement.utils.recycler

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.HorizontalScrollView

/**
 * Created by zhiya.zhang
 * on 2018/3/12 14:51.
 */

class MyHorizontalScrollView : HorizontalScrollView {
    private var mView: View? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        //设置控件滚动监听，得到滚动的距离，然后让传进来的view也设置相同的滚动具体
        if (mView != null) {
            mView!!.scrollTo(l, t)
        }
    }

    /**
     * 设置跟它联动的view
     * @param view
     */
    fun setScrollView(view: View) {
        mView = view
    }
}
