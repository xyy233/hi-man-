package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2017/9/14 12:22.
 * 广泛使用，缺什么加什么，用于某些只需要检查获得单一不常用数据的bean
 */
data class UtilBean(
        @SerializedName("value")
        val value: String?,//用于创建验收单时获得的最大单号
        val value2: String?,
        val value3: String?
) : Serializable