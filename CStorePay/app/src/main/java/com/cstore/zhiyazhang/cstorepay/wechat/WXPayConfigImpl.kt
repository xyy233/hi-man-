package com.cstore.zhiyazhang.cstorepay.wechat

import com.cstore.zhiyazhang.cstorepay.bean.PayMsgBean
import com.github.wxpay.sdk.WXPayConfig
import java.io.ByteArrayInputStream
import java.io.InputStream

/**
 * Created by zhiya.zhang
 * on 2017/11/14 15:19.
 */
class WXPayConfigImpl private constructor(private val msgBean: PayMsgBean) : WXPayConfig {

    companion object {

        private var INSTANCE: WXPayConfigImpl? = null

        fun getInstance(msgBean: PayMsgBean): WXPayConfigImpl {
            if (INSTANCE == null) {
                synchronized(WXPayConfigImpl::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = WXPayConfigImpl(msgBean)
                    }
                }
            }
            return INSTANCE!!
        }
    }

    override fun getAppID(): String {
        return msgBean.wxAppId
    }

    override fun getMchID(): String {
        return msgBean.wxMchid
    }

    override fun getKey(): String {
        return msgBean.wxPartnerKey
    }

    override fun getCertStream(): InputStream {
        return ByteArrayInputStream(null)
    }

    override fun getHttpConnectTimeoutMs(): Int {
        return 10000
    }

    override fun getHttpReadTimeoutMs(): Int {
        return 10000
    }

}