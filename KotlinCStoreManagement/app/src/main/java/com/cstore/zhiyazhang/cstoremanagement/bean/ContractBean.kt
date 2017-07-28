package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2017/6/12 9:39.
 * 商品
 */
data class ContractBean(
        /**
         * 商品id
         */
        @SerializedName("item_id") val cId: String,
        /**
         * 商品名字
         */
        @SerializedName("item_name") val cName: String,
        /**
         * 商品类别
         */
        @SerializedName("type_name") val cType: String,
        /**
         * 库存
         */
        @SerializedName("inv_qty") val inventory: Int,
        /**
         * 今晚到量
         */
        @SerializedName("on_qty") val tonightCount: Int,
        /**
         * 总部订量
         */
        @SerializedName("hord_qty") val todayGh: Int,
        /**
         * 门店增减量
         */
        @SerializedName("sord_qty") var todayStore: Int,
        /**
         * 实际订量
         */
        @SerializedName("act_ord_qty") var todayCount: Int,
        /**
         * 每次增量数
         */
        @SerializedName("step_qty") val stepQty: Int,
        /**
         * 最小量
         */
        @SerializedName("min_qty") val minQty: Int,
        /**
         * 最大量
         */
        @SerializedName("max_qty") val maxQty: Int,
        /**
         * 零售价
         */
        @SerializedName("price") val cPrice: Double,
        /**
         * 建议订量
         */
        @SerializedName("sug_qty") val cSug: Int,
        /**
         * 图片url
         */
        @SerializedName("image_url") val img_url: String,
        /**
         * 告诉后台是更新还是创建的动作
         */
        @SerializedName("action") var action: String,
        /**
         * 标记是第几天到
         */
        @SerializedName("arrive_date") val arriveDate: Int,
        /**
         * 用于添加标记是否修改过
         */
        var changeCount: Int,
        /**
         * 是否能修改
         */
        var isCanChange: Boolean = true
) : Serializable

/**
 * 商品list
 */
data class ContractResult(var total: Int, val page: Int, val detail: List<ContractBean>) : Serializable
