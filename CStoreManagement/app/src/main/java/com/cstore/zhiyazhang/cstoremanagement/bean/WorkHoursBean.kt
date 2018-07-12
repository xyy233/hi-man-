package com.cstore.zhiyazhang.cstoremanagement.bean

import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2018/3/12 16:44.
 * 每个月上班小时
 */
data class WorkHoursBean(
        /**
         * 年月
         */
        val ym: String,
        /**
         * 总小时
         */
        val hours: Int
) : Serializable