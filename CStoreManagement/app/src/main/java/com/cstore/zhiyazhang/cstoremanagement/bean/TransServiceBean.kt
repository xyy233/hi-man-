package com.cstore.zhiyazhang.cstoremanagement.bean

import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.google.gson.annotations.SerializedName
import net.grandcentrix.tray.AppPreferences
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
        var storeUnitPrice: Double?,
        var items: ArrayList<TransItem>,
        var isSc: Int?,//是否上传至sc
        var isInt: Int?,//是否上传至网络
        var busiDate: String?,
        var isDone: Int?,//是否确定到达，只对调入有效
        @SerializedName("trs_fee")var trsFee: Double?,//配送费用，只对调出有效
        @SerializedName("fee_upd_time")var feeUpdTime: String?//送出时间
) : Serializable

data class TransItem(
        @SerializedName("itemno") val itemNo: String,
        @SerializedName("item_name") val itemName: String,
        @SerializedName("trs_qty") var trsQty: Int,
        @SerializedName("store_trs_qty") var storeTrsQty: Int?,
        var inv: Int?,
        var storeUnitPrice: Double?
) : Serializable

/**
 * http请求返回对象
 */
data class TransResult(
        val code: Int,
        var count: Int,
        val rows: ArrayList<TransServiceBean>
) : Serializable

/**
 * 判断是否能创建的对象
 */
data class TransCanCreate(
        val code: Int,
        @SerializedName("can_create") val canCreate: Boolean
) : Serializable

/**
 * 程序用来判断是否是新调拨的tag类
 */
data class TransTag(
        val store: String,
        //日期
        val date: String,
        //时间
        var hour: String
) : Serializable {
    companion object {
        private var staticTransTag: TransTag? = null
        private val name = "trs_tag"

        /**
         * 如果isNew为true就获得最新的
         */
        fun getTransTag(isNew: Boolean): TransTag {
            if (staticTransTag == null || isNew) {
                val sp = AppPreferences(MyApplication.instance().applicationContext)
                staticTransTag = TransTag(
                        sp.getString("trs_store", User.getUser().storeId)!!,
                        sp.getString("date", MyTimeUtil.nowDate)!!,
                        sp.getString("hour", "0")!!
                )
            }
            //如果保存的日期不是今天或门市不对就初始化
            if (staticTransTag!!.date != MyTimeUtil.nowDate || staticTransTag!!.store != User.getUser().storeId) {
                saveTag(TransTag(User.getUser().storeId, MyTimeUtil.nowDate, "0"))
            }
            return staticTransTag!!
        }

        fun saveTag(tag: TransTag) {
            val sp = AppPreferences(MyApplication.instance().applicationContext)
            sp.put("trs_store", User.getUser().storeId)
            sp.put("date", tag.date)
            sp.put("hour", tag.hour)
            refreshTag()
        }

        /**
         * 清空
         */
        fun cleanTag() {
            val sp = AppPreferences(MyApplication.instance().applicationContext)
            sp.clear()
            refreshTag()
        }

        private fun refreshTag() {
            val sp = AppPreferences(MyApplication.instance().applicationContext)
            staticTransTag = TransTag(
                    sp.getString("trs_store", User.getUser().storeId)!!,
                    sp.getString("date", MyTimeUtil.nowDate)!!,
                    sp.getString("hour", "0")!!
            )
        }
    }
}