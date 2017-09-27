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
        @SerializedName("store_attr") val storeAttr:Int,
        /**
         * 0是非承包店，1是承包店
         */
        val cnt: Int
) : Serializable {
    companion object {

        private var staticUser:User?=null

        fun getUser(): User{
            if (staticUser==null){
                val sp = MyApplication.instance().applicationContext.getSharedPreferences("user", Context.MODE_PRIVATE)
                staticUser = User(
                        sp.getString("storeId", ""),
                        sp.getString("uid", ""),
                        "", //不保存密码
                        sp.getString("uName", ""),
                        sp.getString("telphone", ""),
                        sp.getString("storeName", ""),
                        sp.getString("address", ""),
                        sp.getInt("storeAttr",1),
                        sp.getInt("cnt", 0)
                )
            }
            return staticUser!!
        }

        fun refreshUser(){
            val sp = MyApplication.instance().applicationContext.getSharedPreferences("user", Context.MODE_PRIVATE)
            staticUser = User(
                    sp.getString("storeId", ""),
                    sp.getString("uid", ""),
                    "", //不保存密码
                    sp.getString("uName", ""),
                    sp.getString("telphone", ""),
                    sp.getString("storeName", ""),
                    sp.getString("address", ""),
                    sp.getInt("storeAttr",1),
                    sp.getInt("cnt", 0)
            )
        }
    }
}

/**
 * 人的list
 */
data class UserResult(val total: Int, val users: List<User>)