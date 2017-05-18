package com.c_store.zhiyazhang.cstoremanagement.utils.onclick;

import android.view.MotionEvent;
import android.view.View;

import com.c_store.zhiyazhang.cstoremanagement.bean.ContractBean;

/**
 * Created by zhiya.zhang
 * on 2017/5/10 8:59.
 */

public interface RecyclerOnItemClickListener {
    void onItemClick(View view, int positon);
    void onItemLongClick(View view, int positon);
    void onTouchAddListener(ContractBean cb,int positon, MotionEvent event);
    void onTouchLessListener(ContractBean cb, int positon, MotionEvent event);
}
