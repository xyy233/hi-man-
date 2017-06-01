package com.c_store.zhiyazhang.cstoremanagement.utils.recycler;

/**
 * Created by zhiya.zhang
 * on 2017/5/26 9:06.
 */

public interface onMoveAndSwipedListener {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

}
