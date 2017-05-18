package com.c_store.zhiyazhang.cstoremanagement.utils;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

/**
 * Created by zhiya.zhang
 * on 2017/5/17 12:18.
 */

/*可以动态设置能否滑动*/
public class MyLinearlayoutManager extends LinearLayoutManager {
    private boolean isScrollEnabled = true;
    public MyLinearlayoutManager(Context context) {
        super(context);
    }

    public MyLinearlayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public MyLinearlayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }


    @Override
    public boolean canScrollVertically() {
        return isScrollEnabled && super.canScrollVertically();
    }
}
