package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2017/12/7 15:24.
 *
 * 这个手机绑定的pos号信息，能够组合成单号
 */
data class PosBean(
        @SerializedName("tel_seq")var telSeq:String,
        @SerializedName("ass_pos")var assPos:Int,
        /**
         * 交易序号
         */
        @SerializedName("next_tranno")var nextTranNo:Int
):Serializable