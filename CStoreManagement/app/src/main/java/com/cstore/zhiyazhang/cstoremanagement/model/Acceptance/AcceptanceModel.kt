package com.cstore.zhiyazhang.cstoremanagement.model.acceptance

import android.os.Message
import android.util.Log
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.AcceptanceBean
import com.cstore.zhiyazhang.cstoremanagement.bean.AcceptanceItemBean
import com.cstore.zhiyazhang.cstoremanagement.bean.UtilBean
import com.cstore.zhiyazhang.cstoremanagement.bean.VendorBean
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.MyHandler.ERROR1
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.MyHandler.SUCCESS
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil

/**
 * Created by zhiya.zhang
 * on 2017/9/11 11:30.
 */
class AcceptanceModel : AcceptanceInterface {

    override fun getAcceptanceList(date: String, handler: MyHandler.MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val result = SocketUtil.initSocket(ip,MySql.getAcceptanceList(date)).inquire()
            val abs = ArrayList<AcceptanceBean>()
            //空的话就传空的数据出去
            if (result=="[]"){
                msg.obj = abs
                msg.what = SUCCESS
                handler.sendMessage(msg)
                return@Runnable
            }
            try {
                abs.addAll(SocketUtil.getAcceptance(result))
            } catch (e: Exception) {
            }
            if (abs.isEmpty()) {
                msg.obj = result
                msg.what = ERROR1
                handler.sendMessage(msg)
            }else{
                abs.forEach {
                    //获得每个单下的商品
                    val allItems = getAcceptanceItemList(it, date, handler, msg, ip)
                    if (allItems.size == 0) return@Runnable
                    it.allItems = allItems
                }
                msg.obj = abs
                msg.what = SUCCESS
                handler.sendMessage(msg)
            }
        }).start()
    }

    /**
     * 得到配送表下的商品
     */
    private fun getAcceptanceItemList(ab: AcceptanceBean, date: String, handler: MyHandler.MyHandler, msg: Message, ip: String): ArrayList<AcceptanceItemBean> {
        val abs = ArrayList<AcceptanceItemBean>()
        val result = SocketUtil.initSocket(ip, MySql.getAcceptanceItemList(ab, date)).inquire()
        if (result=="[]")return abs
        try {
            abs.addAll(SocketUtil.getAcceptanceItem(result))
        } catch (e: Exception) {}
        if (abs.isEmpty()){
            msg.obj = result
            msg.what = ERROR1
            handler.sendMessage(msg)
        }
        return abs
    }

    override fun updateAcceptance(date: String, ab: AcceptanceBean, handler: MyHandler.MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            if (!CStoreCalendar.judgmentCalender(date,msg,handler))return@Runnable
            val sql = getUpdateSql(ab)
            val result = SocketUtil.initSocket(ip, sql).inquire()
            if (result == "0") {
                msg.obj = result
                msg.what = SUCCESS
                handler.sendMessage(msg)
            } else {
                msg.obj = result
                msg.what = ERROR1
                handler.sendMessage(msg)
            }
        }).start()
    }

    /**
     * 得到更新用的sql语句,只会有更新，创建都是确定当场保存下来
     */
    private fun getUpdateSql(ab: AcceptanceBean): String {
        val result: StringBuilder = StringBuilder()
        result.append(MySql.affairHeader)//添加事务头
        var allDlvQTY = 0//所有商品的验收量
        var allDCQTY = 0//所有商品的大仓出货量
        var allStoreUnitPrice: Double = 0.00//验收单总计的零售价格
        var costTotal = 0.00
        //重新计算一些修改量
        ab.allItems.forEach {
            allDCQTY += it.dctrsQuantity
            allDlvQTY += it.dlvQuantity
            it.retailTotal = (it.storeUnitPrice * it.dlvQuantity)//修改当前商品的零售小计
            allStoreUnitPrice += it.retailTotal
            it.totalUnitCost = it.unitCost * it.dlvQuantity//得到当前商品总成本价
            costTotal += it.totalUnitCost
            it.varQuantity = it.trsQuantity - it.dctrsQuantity//得到差异值
            result.append(MySql.updateAcceptanceItem(it))//添加修改商品的sql语句到事务中去
        }
        //写入成本总计
        ab.costTotal = costTotal
        //写入零售小计
        ab.retailTotal = allStoreUnitPrice
        //不是转次日的就去修改验收状态
        if (ab.dlvStatus != 3) {
            //相同就是未修正的验收，不相同且不为0就是已修正过的
            if (allDlvQTY == allDCQTY) ab.dlvStatus = 1 else if (allDlvQTY != 0) ab.dlvStatus = 2
        }
        result.append(MySql.updateAcceptance(ab))//添加修改配送单的sql
        result.append(MySql.affairFoot)//添加事务脚
        return result.toString()
    }

    override fun getVendor(handler: MyHandler.MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val result=SocketUtil.initSocket(ip,MySql.getVendor).inquire()
            if (!SocketUtil.judgmentNull(result,msg,handler))return@Runnable
            val vb=ArrayList<VendorBean>()
            try {
                vb.addAll(SocketUtil.getVendor(result))
            }catch (e:Exception){}
            if (vb.isEmpty()){
                msg.obj=result
                msg.what= ERROR1
                handler.sendMessage(msg)
            }else{
                msg.obj=vb
                msg.what= SUCCESS
                handler.sendMessage(msg)
            }

        }).start()
    }


    override fun getCommodity(commodityId: String, vendorId:String, handler: MyHandler.MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            val result=SocketUtil.initSocket(ip,MySql.getAcceptanceCommodity(commodityId,vendorId)).inquire()
            if (!SocketUtil.judgmentNull(result,msg,handler))return@Runnable
            val aib=ArrayList<AcceptanceItemBean>()
            try {
                aib.addAll(SocketUtil.getAcceptanceItem(result))
            }catch (e:Exception){}
            if (aib.isEmpty()){
                msg.obj=result
                msg.what= ERROR1
                handler.sendMessage(msg)
            }else{
                msg.obj=aib
                msg.what= SUCCESS
                handler.sendMessage(msg)
            }
        }).start()
    }

    override fun createAcceptance(date:String, ab: AcceptanceBean?, aib: AcceptanceItemBean, handler: MyHandler.MyHandler) {
        Thread(Runnable {
            val msg = Message()
            val ip = MyApplication.getIP()
            if (!SocketUtil.judgmentIP(ip, msg, handler)) return@Runnable
            if (!CStoreCalendar.judgmentCalender(date,msg,handler))return@Runnable
            //判断是否有重复商品
            val commoditySql=SocketUtil.initSocket(ip,MySql.getJudgmentCommodity(aib.itemId,date)).inquire()
            val commodity=ArrayList<AcceptanceItemBean>()
            try {
                commodity.addAll(SocketUtil.getAcceptanceItem(commoditySql))
            }catch (e:Exception){}
            if (commodity.isNotEmpty()){
                msg.obj="已有此商品的配送单，不能重复创建！"
                msg.what=ERROR1
                handler.sendMessage(msg)
                return@Runnable
            }
            //开始创建,先判断是否有配送单
            val sql=getCreateAcceptance(date,ip,aib,ab)
            if (sql=="0"){
                msg.obj=MyApplication.instance().applicationContext.getString(R.string.socketError)
                msg.what= ERROR1
                handler.sendMessage(msg)
                return@Runnable
            }
            val result=SocketUtil.initSocket(ip,sql).inquire()
            if (result=="0"){
                msg.what= SUCCESS
            }else{
                msg.what=ERROR1
            }
            msg.obj=result
            handler.sendMessage(msg)
        }).start()
    }

    /**
     * 得到创建的sql语句,isAllCreate true就是不存在这个配送单，要创建配送单和商品否则就是存在配送单，只要创建商品
     */
    private fun getCreateAcceptance(date: String, ip: String, aib: AcceptanceItemBean, ab: AcceptanceBean?): String {
        if (ab==null){
            val numHeader=MySql.getNowNum(date)
            val distributionId=getDistribution(ip, date, numHeader)
            if (distributionId=="0")return "0"
            //获得后四位数字
            val numFoot=distributionId.substring(distributionId.length-4).toInt()
            //获得当前的完整单号
            val nowId=numHeader+(numFoot+1).toString()
            //数据加入
            val nowAB=AcceptanceBean(nowId,aib.vendorId,"",1,1,0,aib.storeUnitPrice*aib.dlvQuantity,aib.dlvQuantity,0.00,"","",2,aib.totalUnitCost,ArrayList<AcceptanceItemBean>())
            aib.distributionId=nowId
            val result=StringBuilder()
            result.append(MySql.affairHeader)
            result.append(MySql.createAcceptanceItem(aib,date))
            result.append(MySql.createAcceptance(nowAB,date))
            result.append(MySql.affairFoot)
            return result.toString()
        }else{
            //重新计算一些修改量
            //写入零售小计
            ab.retailTotal+=aib.storeUnitPrice*aib.dlvQuantity
            //写入成本总计
            ab.costTotal+=aib.unitCost*aib.dlvQuantity
            //写入验收量
            ab.dlvQuantity+=aib.dlvQuantity
            //写入验收项
            ab.dlvItemQTY++
            //写入品项
            ab.ordItemQTY++

            val result=StringBuilder()
            result.append(MySql.affairHeader)
            result.append(MySql.createAcceptanceItem(aib,date))
            result.append(MySql.updateAcceptance(ab))
            result.append(MySql.affairFoot)
            return result.toString()
        }
    }

    /**
     * 得到当前最大的单号
     */
    private fun getDistribution(ip: String, date: String, num:String): String {
        val result=SocketUtil.initSocket(ip,MySql.getMaxAcceptanceId(date, num)).inquire()
        if(result=="0"){
            //传输错误
            return result
        }else{
            //有数据
            val ub= ArrayList<UtilBean>()
            try {
                ub.addAll(SocketUtil.getUtilBean(result))
            }catch (e:Exception){
                //出错了
                Log.e("AcceptanceModel",e.message)
            }
            if (ub[0].value==null){
                //没有数据
                return num+"9000"
            }
            return ub[0].value!!
        }
    }
}

interface AcceptanceInterface {
    /**
     * 得到配送列表
     */
    fun getAcceptanceList(date: String, handler: MyHandler.MyHandler)

    /**
     * 更新验收
     */
    fun updateAcceptance(date: String, ab: AcceptanceBean, handler: MyHandler.MyHandler)

    /**
     * 得到配送商
     */
    fun getVendor(handler:MyHandler.MyHandler)

    /**
     * 得到商品
     */
    fun getCommodity(commodityId:String, vendorId:String, handler: MyHandler.MyHandler)

    /**
     * 创建配送
     */
    fun createAcceptance(date:String, ab: AcceptanceBean?,aib:AcceptanceItemBean, handler: MyHandler.MyHandler)
}