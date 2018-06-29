package com.cstore.zhiyazhang.cstorepay.bean

import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2018/6/21 16:28.
 */
data class PayMsgBean(
        val alipayPartnerId: String,
        val alipaySellerId: String,
        val alipaySecurityCode: String,
        val wxAppId: String,
        val wxMchid: String,
        val wxPartnerKey: String,
        val wxKeyStore: String,
        val wxKeyPassword: String,
        val storeId: String,
        val storeName: String,
        /**
         * pos机号
         */
        val posId: String,
        /**
         * 交易序号
         */
        val payNum: String,
        /**
         * 交易金额
         */
        val payAmount: Double
) : Serializable