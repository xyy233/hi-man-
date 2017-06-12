package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2017/6/12 8:56.
 * 商品类
 */
data class ContractTypeBean(
        /**
         * 类型id
         */
        @SerializedName("type_id") val typeId: String,
        /**
         * 类型名
         */
        @SerializedName("type_name") val typeName: String,
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
        @SerializedName("hord_qty") var todayGh: Int,
        /**
         * 门店订量
         */
        @SerializedName("sord_qty") var todayStore: Int,
        /**
         * 实际订量
         */
        @SerializedName("act_ord_qty") var todayCount: Int,
        /**
         * 上一周销量
         */
        @SerializedName("wk1_sqty") val wk1Sqty: Int,
        /**
         * 上两周销量
         */
        @SerializedName("wk2_sqty") val wk2Sqty: Int,
        /**
         * 上三周销量
         */
        @SerializedName("wk3_sqty") val wk3Sqty: Int,
        /**
         * 预测销量
         */
        @SerializedName("exp_qty") val expQty: Int,
        /**
         * 建议订量
         */
        @SerializedName("sug_qty") val sugQty: Int,
        /**
         * 最小订量
         */
        @SerializedName("min_qty") val minQty: Int,
        /**
         * 最大订量
         */
        @SerializedName("max_qty") val maxQty: Int,
        /**
         * 在adapter中确认是否修改颜色
         */
        var isChangeColor: Boolean
) : Serializable

/**
 * 商品类的list
 */
data class ContractTypeResult(val total: Int, val detail: List<ContractTypeBean>)