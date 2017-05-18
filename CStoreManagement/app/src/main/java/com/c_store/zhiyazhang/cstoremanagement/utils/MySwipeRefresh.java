package com.c_store.zhiyazhang.cstoremanagement.utils;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by zhiya.zhang
 * on 2017/5/11 9:34.
 */

public class MySwipeRefresh extends SwipeRefreshLayout {

/*    private boolean mHasScrollingChild = false;

    private ScrollingView mScrollingChild = null;

    private float mDownPostion;

    private boolean mIsDragMode = false;*/

    public MySwipeRefresh(Context context) {
        super(context);
    }

    public MySwipeRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
/*
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 1 && getChildAt(1) instanceof ScrollingView) {
            mHasScrollingChild = true;
            mScrollingChild = (ScrollingView) getChildAt(1);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mHasScrollingChild) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    setEnabled(true);
                    mDownPostion = ev.getY();
                    if (mScrollingChild.computeVerticalScrollOffset() != 0) {
                        setEnabled(false);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isEnabled()) {
                        if (ev.getY() < mDownPostion)
                            setEnabled(false);
                        else
                            mIsDragMode = true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    mIsDragMode = false;
                    break;
            }
            return super.dispatchTouchEvent(ev);
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mIsDragMode || super.onInterceptTouchEvent(ev);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (isEnabled()) {
            super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        } else {
            //这里用了反射获取父类私有变量，用来调用dispatchNestedScroll()，保证NestedScroll效果不出错
            try {
                Field mParentOffsetInWindowField =
                        SwipeRefreshLayout.class.getDeclaredField("mParentOffsetInWindow");
                mParentOffsetInWindowField.setAccessible(true);
                int[] mParentOffsetInWindow = (int[]) mParentOffsetInWindowField.get(this);
                // Dispatch up to the nested parent first
                dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                        mParentOffsetInWindow);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }*/

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
