package com.cstore.zhiyazhang.cstoremanagement.bean

import android.content.Context
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2018/5/10 11:00.
 */
data class TransServiceBean(
        @SerializedName("storeid") val storeId: String,
        @SerializedName("store_name") val storeName: String,
        @SerializedName("zx_wx") val type: String,
        @SerializedName("trs_storeid") val trsStoreId: String,//调拨店号
        @SerializedName("trs_store_name") val trsStoreName: String,//调拨店名
        @SerializedName("in_out") val trsType: Int,//调拨类型,正数为调入，负数为调出
        @SerializedName("dis_time") val disTime: String,//调拨时间数，需要更新到TransTag
        @SerializedName("trs_qtys") var trsQuantities: Int,
        @SerializedName("upd_time") val updTime: String,
        @SerializedName("req_no") var requestNumber: String?,
        var sellCost: Double?,
        var items: ArrayList<TransItem>
) : Serializable

data class TransItem(
        @SerializedName("itemno") val itemNo: String,
        @SerializedName("item_name") val itemName: String,
        @SerializedName("trs_qty") var trsQty: Int,
        @SerializedName("store_trs_qty") var storeTrsQty: Int?,
        var inv: Int?,
        var sellCost: Double?
) : Serializable

/**
 * http请求返回对象
 */
data class TransResult(
        val code: Int,
        val count: Int,
        val rows: ArrayList<TransServiceBean>
) : Serializable

/**
 * 程序用来判断是否是新调拨的tag类
 */
data class TransTag(
        val store: String,
        //日期
        val date: String,
        //时间
        val hour: String
) : Serializable {
    companion object {
        private var staticTransTag: TransTag? = null
        private val name = "trs_tag"

        fun getTransTag(): TransTag {
            if (staticTransTag == null) {
                val sp = MyApplication.instance().applicationContext.getSharedPreferences(name, Context.MODE_PRIVATE)
                staticTransTag = TransTag(
                        sp.getString("trs_store", User.getUser().storeId),
                        sp.getString("date", MyTimeUtil.nowDate),
                        sp.getString("hour", "0")
                )
            }
            //如果保存的日期不是今天或门市不对就初始化
            if (staticTransTag!!.date != MyTimeUtil.nowDate || staticTransTag!!.store != User.getUser().storeId) {
                saveTag(TransTag(User.getUser().storeId, MyTimeUtil.nowDate, "0"))
            }
            return staticTransTag!!
        }

        fun saveTag(tag: TransTag) {
            val sp = MyApplication.instance().applicationContext.getSharedPreferences(name, Context.MODE_PRIVATE)
            val ts = sp.edit()
            ts.putString("trs_store", User.getUser().storeId)
            ts.putString("date", tag.date)
            ts.putString("hour", tag.hour)
            ts.apply()
            refreshTag()
        }

        /**
         * 清空
         */
        fun cleanTag() {
            val sp = MyApplication.instance().applicationContext.getSharedPreferences(name, Context.MODE_PRIVATE)
            val ts = sp.edit()
            ts.clear()
            ts.apply()
            refreshTag()
        }

        private fun refreshTag() {
            val sp = MyApplication.instance().applicationContext.getSharedPreferences(name, Context.MODE_PRIVATE)
            staticTransTag = TransTag(
                    sp.getString("trs_store", User.getUser().storeId),
                    sp.getString("date", MyTimeUtil.nowDate),
                    sp.getString("hour", "0")
            )
        }
    }
}