package com.cstore.zhiyazhang.cstorepay.wechat

import com.cstore.zhiyazhang.cstorepay.bean.PayMsgBean
import com.cstore.zhiyazhang.cstorepay.util.JavaFunUtil
import com.cstore.zhiyazhang.cstorepay.util.MyApplication
import com.github.wxpay.sdk.WXPayConfig
import java.io.ByteArrayInputStream
import java.io.InputStream

/**
 * Created by zhiya.zhang
 * on 2017/11/14 15:19.
 */
class WXPayConfigImpl private constructor(private val msgBean: PayMsgBean) : WXPayConfig {

    private var certData: ByteArray

    companion object {
        private val HZ = "wechatCert/wx_hz_cert.p12"
        private val KS = "wechatCert/wx_ks_cert.p12"
        private val NB = "wechatCert/wx_nb_cert.p12"
        private val SH = "wechatCert/wx_sh_cert.p12"
        private val SX = "wechatCert/wx_sx_cert.p12"
        private val SZ = "wechatCert/wx_sz_cert.p12"
        private val WX = "wechatCert/wx_wx_cert.p12"

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

    init {
        //获得资源
        val ass = MyApplication.instance().resources.assets.open(SH)
        /*//inputStream转换成file
        val file = JavaFunUtil.toFile(ass)*/
        //赋值certData
        this.certData = JavaFunUtil.toByteArray(ass)
        //写入数据
        ass.read(this.certData)
        ass.close()
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
        return ByteArrayInputStream(this.certData)
    }

    override fun getHttpConnectTimeoutMs(): Int {
        return 10000
    }

    override fun getHttpReadTimeoutMs(): Int {
        return 10000
    }

}