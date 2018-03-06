package com.cstore.zhiyazhang.cstoremanagement.utils

import com.cstore.zhiyazhang.cstoremanagement.bean.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by zhiya.zhang
 * on 2017/11/3 17:07.
 */
object GsonUtil {
    fun getUser(data: String): ArrayList<User> {
        return Gson().fromJson<ArrayList<User>>(data, object : TypeToken<ArrayList<User>>() {}.type)
    }

    fun getScrap(data: String): ArrayList<ScrapContractBean> {
        return Gson().fromJson<ArrayList<ScrapContractBean>>(data, object : TypeToken<ArrayList<ScrapContractBean>>() {}.type)
    }

    fun getCategoryItem(data: String): ArrayList<CategoryItemBean> {
        return Gson().fromJson<ArrayList<CategoryItemBean>>(data, object : TypeToken<ArrayList<CategoryItemBean>>() {}.type)
    }

    fun getCategory(data: String): ArrayList<OrderCategoryBean> {
        return Gson().fromJson<ArrayList<OrderCategoryBean>>(data, object : TypeToken<ArrayList<OrderCategoryBean>>() {}.type)
    }

    fun getShelf(data: String): ArrayList<ShelfBean> {
        return Gson().fromJson<ArrayList<ShelfBean>>(data, object : TypeToken<ArrayList<ShelfBean>>() {}.type)
    }

    fun getSelf(data: String): ArrayList<SelfBean> {
        return Gson().fromJson<ArrayList<SelfBean>>(data, object : TypeToken<ArrayList<SelfBean>>() {}.type)
    }

    fun getFresh(data: String): ArrayList<FreshGroup> {
        return Gson().fromJson<ArrayList<FreshGroup>>(data, object : TypeToken<ArrayList<FreshGroup>>() {}.type)
    }

    fun getNOP(data: String): ArrayList<NOPBean> {
        return Gson().fromJson<ArrayList<NOPBean>>(data, object : TypeToken<ArrayList<NOPBean>>() {}.type)
    }

    fun getScrapHot(data: String): ArrayList<ScrapHotBean> {
        return Gson().fromJson<ArrayList<ScrapHotBean>>(data, object : TypeToken<ArrayList<ScrapHotBean>>() {}.type)
    }

    fun getCashDaily(data: String): ArrayList<CashDailyBean> {
        return Gson().fromJson<ArrayList<CashDailyBean>>(data, object : TypeToken<ArrayList<CashDailyBean>>() {}.type)
    }

    fun getAcceptance(data: String): ArrayList<AcceptanceBean> {
        return Gson().fromJson<ArrayList<AcceptanceBean>>(data, object : TypeToken<ArrayList<AcceptanceBean>>() {}.type)
    }

    fun getAcceptanceItem(data: String): ArrayList<AcceptanceItemBean> {
        return Gson().fromJson<ArrayList<AcceptanceItemBean>>(data, object : TypeToken<ArrayList<AcceptanceItemBean>>() {}.type)
    }

    fun getCstoreCalendar(data: String): ArrayList<CStoreCalendarBean> {
        return Gson().fromJson<ArrayList<CStoreCalendarBean>>(data, object : TypeToken<ArrayList<CStoreCalendarBean>>() {}.type)
    }

    fun getVendor(data: String): ArrayList<VendorBean> {
        return Gson().fromJson<ArrayList<VendorBean>>(data, object : TypeToken<ArrayList<VendorBean>>() {}.type)
    }

    fun getUtilBean(data: String): ArrayList<UtilBean> {
        return Gson().fromJson<ArrayList<UtilBean>>(data, object : TypeToken<ArrayList<UtilBean>>() {}.type)
    }

    fun getReturnAcceptance(data: String): ArrayList<ReturnAcceptanceBean> {
        return Gson().fromJson<ArrayList<ReturnAcceptanceBean>>(data, object : TypeToken<ArrayList<ReturnAcceptanceBean>>() {}.type)
    }

    fun getReturnAcceptanceItem(data: String): ArrayList<ReturnAcceptanceItemBean> {
        return Gson().fromJson<ArrayList<ReturnAcceptanceItemBean>>(data, object : TypeToken<ArrayList<ReturnAcceptanceItemBean>>() {}.type)
    }

    fun getAdjustment(data: String): ArrayList<AdjustmentBean> {
        return Gson().fromJson<ArrayList<AdjustmentBean>>(data, object : TypeToken<ArrayList<AdjustmentBean>>() {}.type)
    }

    fun getReturnPurchase(data: String): ArrayList<ReturnedPurchaseBean> {
        return Gson().fromJson<ArrayList<ReturnedPurchaseBean>>(data, object : TypeToken<ArrayList<ReturnedPurchaseBean>>() {}.type)
    }

    fun getReturnPurchaseItem(data: String): ArrayList<ReturnPurchaseItemBean> {
        return Gson().fromJson<ArrayList<ReturnPurchaseItemBean>>(data, object : TypeToken<ArrayList<ReturnPurchaseItemBean>>() {}.type)
    }

    fun getReason(data: String): ArrayList<ReasonBean> {
        return Gson().fromJson<ArrayList<ReasonBean>>(data, object : TypeToken<ArrayList<ReasonBean>>() {}.type)
    }

    fun getPay(data: String): ArrayList<PayBean> {
        return Gson().fromJson<ArrayList<PayBean>>(data, object : TypeToken<ArrayList<PayBean>>() {}.type)
    }

    fun getPos(data: String): ArrayList<PosBean> {
        return Gson().fromJson<ArrayList<PosBean>>(data, object : TypeToken<ArrayList<PosBean>>() {}.type)
    }

    fun getReturnExpired(data: String): ArrayList<ReturnExpiredBean> {
        return Gson().fromJson<ArrayList<ReturnExpiredBean>>(data, object : TypeToken<ArrayList<ReturnExpiredBean>>() {}.type)
    }

    fun getAli(data:String):AliQueryBean{
        return Gson().fromJson(data,AliQueryBean::class.java)
    }

    fun getTrs(data: String): ArrayList<TrsBean> {
        return Gson().fromJson<ArrayList<TrsBean>>(data, object : TypeToken<ArrayList<TrsBean>>() {}.type)
    }

    fun getTrsItem(data: String): ArrayList<TrsItemBean> {
        return Gson().fromJson<ArrayList<TrsItemBean>>(data, object : TypeToken<ArrayList<TrsItemBean>>() {}.type)
    }

    fun getTrsf(data: String): ArrayList<TrsfItemBean> {
        return Gson().fromJson<ArrayList<TrsfItemBean>>(data, object : TypeToken<ArrayList<TrsfItemBean>>() {}.type)
    }

    fun getOStore(data:String): ArrayList<OStoreBean> {
        return Gson().fromJson<ArrayList<OStoreBean>>(data, object : TypeToken<ArrayList<OStoreBean>>() {}.type)
    }

    fun getPlu(data:String): ArrayList<PluItemBean> {
        return Gson().fromJson<ArrayList<PluItemBean>>(data, object : TypeToken<ArrayList<PluItemBean>>() {}.type)
    }

    fun getUnitInquiry(data: String): ArrayList<UnitInquiryBean> {
        return Gson().fromJson<ArrayList<UnitInquiryBean>>(data, object : TypeToken<ArrayList<UnitInquiryBean>>() {}.type)
    }

    fun getInvError(data: String): ArrayList<InvErrorBean> {
        return Gson().fromJson<ArrayList<InvErrorBean>>(data, object : TypeToken<ArrayList<InvErrorBean>>() {}.type)
    }

    fun getPaiban(data: String): ArrayList<PaibanBean> {
        return Gson().fromJson<ArrayList<PaibanBean>>(data, object : TypeToken<ArrayList<PaibanBean>>() {}.type)
    }

    @JvmStatic fun getAttendance(data: String): ArrayList<AttendanceBean> {
        return Gson().fromJson<ArrayList<AttendanceBean>>(data, object : TypeToken<ArrayList<AttendanceBean>>() {}.type)
    }

    @JvmStatic fun getAttendanceUser(data: String): ArrayList<UserAttendanceBean> {
        return Gson().fromJson<ArrayList<UserAttendanceBean>>(data, object : TypeToken<ArrayList<UserAttendanceBean>>() {}.type)
    }
}