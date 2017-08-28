package com.cstore.zhiyazhang.cstoremanagement.utils

import android.content.Context
import android.util.AttributeSet

/**
 * Created by zhiya.zhang
 * on 2017/6/19 8:59.
 */

class MarqueeTextView : android.support.v7.widget.AppCompatTextView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun isFocused(): Boolean {
        return true
    }
}
