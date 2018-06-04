package com.cstore.zhiyazhang.cstoremanagement.bean

import android.content.Context
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 人的信息
 */
data class User(
        /**
         * 店号
         */
        @SerializedName("storeid") val storeId: String,
        /**
         * 人id
         */
        @SerializedName("employeeid") val uId: String,
        /**
         * 密码
         */
        @SerializedName("emppassword") val password: String,
        /**
         * 名字
         */
        @SerializedName("employeename") val name: String,
        /**
         * 系统权限信息
         */
        @SerializedName("empgroupid") val groupId: String,
        /**
         * 手机号
         */
        @SerializedName("emptelphone") val telphone: String?,
        /**
         * 店名
         */
        @SerializedName("storechinesename") val storeName: String,
        /**
         * 店地址
         */
        @SerializedName("address") val address: String,
        /**
         * 店的类型
         */
        @SerializedName("store_attr") val storeAttr: Int,
        /**
         * 0是非承包店，1是承包店
         */
        val cnt: Int,
        /**
         * 微信分配的公众账号ID
         */
        @SerializedName("weixinappid") val wxAppId: String,
        /**
         * 微信支付分配的商户号
         */
        @SerializedName("weixinmchid") val wxMCHId: String,
        /**
         * 签名密钥
         */
        @SerializedName("weixinpartnerkey") val wxKey: String,
        /**
         * 微信证书名称
         */
        @SerializedName("weixinkeystore") val wxKeyStore: String,
        /**
         * 微信证书密码
         */
        @SerializedName("weixinkeypassword") val wxKeyPassword: String,
        /**
         * 支付宝钱包partner_id
         */
        @SerializedName("alipay_partner_id") val aliPartnerId: String,
        /**
         * 支付宝钱包安全码
         */
        @SerializedName("alipay_security_code") val aliSecurityCode: String,
        /**
         * 0=华东  1=华南
         */
        var type: Int?
) : Serializable {
    companion object {

        private var staticUser: User? = null

        /**
         * 得到人
         */
        fun getUser(): User {
            if (staticUser == null) {
                val sp = MyApplication.instance().applicationContext.getSharedPreferences("user", Context.MODE_PRIVATE)
                staticUser = User(
                        sp.getString("storeId", ""),
                        sp.getString("uid", ""),
                        sp.getString("password", ""),
                        sp.getString("uName", ""),
                        sp.getString("groupId", ""),
                        sp.getString("telphone", ""),
                        sp.getString("storeName", ""),
                        sp.getString("address", ""),
                        sp.getInt("storeAttr", 1),
                        sp.getInt("cnt", 0),
                        sp.getString("wxAppId", ""),
                        sp.getString("wxMCHId", ""),
                        sp.getString("wxKey", ""),
                        sp.getString("wxKeyStore", ""),
                        sp.getString("wxKeyPassword", ""),
                        sp.getString("aliPartnerId", ""),
                        sp.getString("aliSecurityCode", ""),
                        sp.getInt("type", 0)
                )
            }
            return staticUser!!
        }

        /**
         * 保存人
         */
        fun saveUser(user: User) {
            val userShared = MyApplication.instance().getSharedPreferences("user", Context.MODE_PRIVATE)
            val ue = userShared.edit()
            ue.putString("storeId", user.storeId)
            ue.putString("uid", user.uId)
            ue.putString("uName", user.name)
            ue.putString("password", user.password)
            ue.putString("groupId", user.groupId)
            ue.putString("telphone", user.telphone)
            ue.putString("storeName", user.storeName)
            ue.putString("address", user.address)
            ue.putInt("storeAttr", user.storeAttr)
            ue.putInt("cnt", user.cnt)
            ue.putString("wxAppId", user.wxAppId)
            ue.putString("wxMCHId", user.wxMCHId)
            ue.putString("wxKey", user.wxKey)
            ue.putString("wxKeyStore", user.wxKeyStore)
            ue.putString("wxKeyPassword", user.wxKeyPassword)
            ue.putString("aliPartnerId", user.aliPartnerId)
            ue.putString("aliSecurityCode", user.aliSecurityCode)
            val s=user.storeId[0].toLowerCase().toString()
            val type = if (s == "s") 1 else 0
            ue.putInt("type", type)
            ue.apply()
            refreshUser()
        }

        /**
         * 刷新人到static中
         */
        private fun refreshUser() {
            val sp = MyApplication.instance().applicationContext.getSharedPreferences("user", Context.MODE_PRIVATE)
            staticUser = User(
                    sp.getString("storeId", ""),
                    sp.getString("uid", ""),
                    sp.getString("password", ""),
                    sp.getString("uName", ""),
                    sp.getString("groupId", ""),
                    sp.getString("telphone", ""),
                    sp.getString("storeName", ""),
                    sp.getString("address", ""),
                    sp.getInt("storeAttr", 1),
                    sp.getInt("cnt", 0),
                    sp.getString("wxAppId", ""),
                    sp.getString("wxMCHId", ""),
                    sp.getString("wxKey", ""),
                    sp.getString("wxKeyStore", ""),
                    sp.getString("wxKeyPassword", ""),
                    sp.getString("aliPartnerId", ""),
                    sp.getString("aliSecurityCode", ""),
                    sp.getInt("type", 0)
            )
        }
    }
}