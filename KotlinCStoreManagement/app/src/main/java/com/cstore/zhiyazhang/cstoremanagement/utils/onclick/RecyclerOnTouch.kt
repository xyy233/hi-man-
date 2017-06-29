package com.cstore.zhiyazhang.cstoremanagement.utils.onclick

import android.view.MotionEvent
import com.cstore.zhiyazhang.cstoremanagement.bean.ContractBean

/**
 * Created by zhiya.zhang
 * on 2017/6/14 16:41.
 */
interface RecyclerOnTouch {
    fun onClickImage(cb: ContractBean, position: Int)
    fun onTouchAddListener(cb: ContractBean, event: MotionEvent, position:Int)
    fun onTouchLessListener(cb: ContractBean, event: MotionEvent, position:Int)
}