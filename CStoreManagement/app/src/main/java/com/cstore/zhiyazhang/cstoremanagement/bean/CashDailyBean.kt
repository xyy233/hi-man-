package com.cstore.zhiyazhang.cstoremanagement.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by zhiya.zhang
 * on 2017/9/4 17:01.
 */
data class CashDailyBean(
        @SerializedName("accountnumber")
        val cdId:String,
        @SerializedName("accountname")
        val cdName:String,
        @SerializedName("storeamount")
        var cdValue:String,
        @SerializedName("updatetype")
        val isEdit:String,//Y代表谁都可以修改，N代表不可以修改，S代表只有管理员可以修改
        @SerializedName("displaytype")
        val cdType:String//0本日销货收入 1销售收入 2代收款项 3服务性商品 4代付 5代现金回收 6代现金溢收 7现金进货退回 8现金进货 9门市费用现金支出 10周转金
): Serializable