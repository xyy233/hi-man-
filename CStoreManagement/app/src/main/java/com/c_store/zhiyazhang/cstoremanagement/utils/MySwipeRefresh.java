package com.c_store.zhiyazhang.cstoremanagement.utils;

import android.content.Context;
import android.support.v4.view.ScrollingView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by zhiya.zhang
 * on 2017/5/11 9:34.
 */

public class MySwipeRefresh extends SwipeRefreshLayout {
    private boolean mHasScrollingChild = false;
    private ScrollingView mScrollingChild = null;


    public MySwipeRefresh(Context context) {
        super(context);
    }

    public MySwipeRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // 此时SwipeRefreshLayout会有一个CircleImageView的子View
        if(getChildCount() > 1 && getChildAt(1) instanceof ScrollingView) {
            mHasScrollingChild = true;
            mScrollingChild = (ScrollingView) getChildAt(1);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(mHasScrollingChild) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    setEnabled(true);   //每次按下时先开启SwipeRefreshLayout，保证正常工作
                    if(mScrollingChild.computeVerticalScrollOffset() != 0) {
                        setEnabled(false);    //如果子View不处于顶部则禁用SwipeRefreshLayout
                    }
            }
            return super.dispatchTouchEvent(ev);
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    /**
     * 自动刷新
     */
    public void autoRefresh() {
        try {
            //这里是拿到下拉那个进度条动画的控件
            Field mCircleView = SwipeRefreshLayout.class.getDeclaredField("mCircleView");
            mCircleView.setAccessible(true);
            View progress = (View) mCircleView.get(this);
            progress.setVisibility(VISIBLE);

            //这里是为了获取刷新函数，这里设置为true，就可以
            Method setRefreshing = SwipeRefreshLayout.class.getDeclaredMethod("setRefreshing", boolean.class, boolean.class);
            setRefreshing.setAccessible(true);
            setRefreshing.invoke(this, true, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
