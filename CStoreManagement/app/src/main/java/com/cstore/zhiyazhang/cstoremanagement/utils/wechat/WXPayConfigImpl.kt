package com.cstore.zhiyazhang.cstoremanagement.utils.wechat

import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.utils.JavaFunUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.github.wxpay.sdk.WXPayConfig
import java.io.ByteArrayInputStream
import java.io.InputStream

/**
 * Created by zhiya.zhang
 * on 2017/11/14 15:19.
 */
class WXPayConfigImpl () : WXPayConfig {

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

        fun getInstance(): WXPayConfigImpl {
            if (INSTANCE == null) {
                synchronized(WXPayConfigImpl::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = WXPayConfigImpl()
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
        return User.getUser().wxAppId
    }

    override fun getMchID(): String {
        return User.getUser().wxMCHId
    }

    override fun getKey(): String {
        return User.getUser().wxKey
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