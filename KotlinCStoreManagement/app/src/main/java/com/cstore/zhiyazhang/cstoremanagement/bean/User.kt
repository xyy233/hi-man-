package com.cstore.zhiyazhang.cstoremanagement.bean

import android.content.Context
import com.google.gson.annotations.SerializedName
import com.zhiyazhang.mykotlinapplication.utils.MyApplication
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
         * 0是非承包店，1是承包店
         */
        val cnt: Int
) : Serializable {
    companion object {
        fun getUser(): User {
            val sp = MyApplication.instance().applicationContext.getSharedPreferences("user", Context.MODE_PRIVATE)
            return User(
                    sp.getString("storeId", ""),
                    sp.getString("uid", ""),
                    "", //不保存密码
                    sp.getString("uName", ""),
                    sp.getString("telphone", ""),
                    sp.getString("storeName", ""),
                    sp.getString("address", ""),
                    sp.getInt("cnt", 0)
            )
        }
    }
}

/**
 * 人的list
 */
data class UserResult(val total: Int, val users: List<User>)