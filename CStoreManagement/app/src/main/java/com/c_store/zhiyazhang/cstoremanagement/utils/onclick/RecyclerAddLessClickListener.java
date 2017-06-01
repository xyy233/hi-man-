package com.c_store.zhiyazhang.cstoremanagement.utils.onclick;

import android.widget.TextView;

/**
 * Created by zhiya.zhang
 * on 2017/5/26 9:48.
 */

public interface RecyclerAddLessClickListener {

    public void addItemOnClick(TextView tv, int position);

    public void lessItemOnClick(TextView tv, int position);
}
