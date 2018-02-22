package com.cstore.zhiyazhang.cstoremanagement.sql

import com.alipay.api.response.AlipayTradePayResponse
import com.alipay.api.response.AlipayTradeQueryResponse
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.*
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil.dayOfYear
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil.deleteTime
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil.nowYear

/**
 * Created by zhiya.zhang
 * on 2017/5/24 11:40.
 * 得到sql语句然后用socket发送sql语句给服务器查询,活久见,很强很无敌
 */

object MySql {

    /**********************************************换日************************************************************/
    /**
     * 得到换日表
     */
    val cstoreCalendar = "select storeid,datetype,to_char(currentdate,'yyyy-MM-dd') currentdate,changetime,sceodresult,typename from calendar order by datetype\u0004"

    /**********************************************批量处理事务头脚************************************************************/
    /**
     * 事务头，用于执行批量sql语句
     */
    val affairHeader = "begin "

    /**
     * 事务脚，用于执行批量sql语句
     */
    val affairFoot = " commit;exception when others then rollback;end;\u0004"


    /**********************************************登录************************************************************/

    /**
     * 传入帐号得到新生成的sql语句

     * @param uid 输入框内的帐号
     * *
     * @return 新的sql语句
     */
    fun signIn(uid: String): String {
        return "select storeid,employeeid,employeename,emppassword,emptelphone,storechinesename,address,store_attr,(SELECT COUNT(*) CNT FROM CONT_ITEM X,PLU Y WHERE X.STOREID = Y.STOREID AND X.ITEMNO = Y.ITEMNUMBER AND TO_CHAR(SYSDATE-1,'YYYYMMDD') BETWEEN X.TRAN_DATE_ST AND X.TRAN_DATE_ED) cnt, WEIXINAPPID ,WEIXINMCHID,WEIXINPARTNERKEY,WEIXINKEYSTORE,WEIXINKEYPASSWORD,ALIPAY_PARTNER_ID,ALIPAY_SECURITY_CODE from (select A.storeid,A.Employeeid,A.Employeename,A.Emppassword,A.Emptelphone,B.Storechinesename,B.Address,B.Store_Attr,B.WEIXINAPPID,B.WEIXINMCHID,B.WEIXINPARTNERKEY,B.WEIXINKEYSTORE,B.WEIXINKEYPASSWORD,B.ALIPAY_PARTNER_ID,B.ALIPAY_SECURITY_CODE from employee A,store B) where employeeid='$uid'\u0004"
    }


    /**********************************************订货************************************************************/
    /**
     * 获得大类信息的存储过程
     */
    fun appOrdT2(): String {
        return "call scb02_new_p07('${CStoreCalendar.getCurrentDate(0)}','${CStoreCalendar.getCurrentDate(2)}','0')"
    }

    /**
     * 按建议量自动下单存储过程
     */
    fun autoOrd(): String {
        return "call scb02_new_p08('${CStoreCalendar.getCurrentDate(2)}')"
    }

    /**
     * 检测是否存在正确存储过程
     */
    val judgmentOrdt2: String
        get() {
            return "select distinct to_char(orderdate,'yyyy-mm-dd') orderdate from appord_t2\u0004"
        }

    /**
     * 获得大类信息
     */
    fun getAllCategory(): String {
        return "select plu.categoryNumber,cat.categoryName, " +
                "count(*) tot_sku,sum(decode(sign(ord.ordActualQuantity+ord.ordActualQuantity1), 1,1,0)) ord_sku, " +
                "sum((ord.ordActualQuantity+ord.ordActualQuantity1)*ord.storeUnitPrice) amt,ord.orderDate  " +
                "from cat,plu,ord,cat cat1  " +
                "WHERE cat.storeid='${User.getUser().storeId}'  " +
                "AND trim(cat.categoryNumber) is not null  " +
                "AND trim(cat.midcategoryNumber) is null  " +
                "AND trim(cat.microcategoryNumber) is null  " +
                "AND plu.storeID=cat.storeID  " +
                "AND plu.categoryNumber=cat.categoryNumber  " +
                "AND plu.storeID=ord.storeID  " +
                "AND plu.itemNumber=ord.itemNumber  " +
                "AND ord.orderDate=to_date('${CStoreCalendar.getCurrentDate(2)}','YYYY-MM-DD')  " +
                "AND plu.storeID=cat1.storeID  " +
                "AND plu.categoryNumber=cat1.categoryNumber  " +
                "AND plu.midCategoryNumber=cat1.midCategoryNumber  " +
                "AND trim(cat1.midCategoryNumber) is not null  " +
                "AND trim(cat1.microCategoryNumber) is null  " +
                "AND cat1.GROUP_YN='N'  " +
                "AND cat.categoryNumber<>'99'  " +
                "GROUP BY ord.orderDate,plu.categoryNumber,cat.categoryName  " +
                "union all " +
                "select '-1' categorynumber, '${MyApplication.instance().getString(R.string.statistics)} ' categoryname, sum(tot_sku) tot_sku, sum(ord_sku) ord_sku, sum(amt) amt, to_date('${CStoreCalendar.getCurrentDate(2)}','YYYY-MM-DD')  orderdate from(select plu.categoryNumber,cat.categoryName, " +
                "count(*) tot_sku,sum(decode(sign(ord.ordActualQuantity+ord.ordActualQuantity1), 1,1,0)) ord_sku, " +
                "sum((ord.ordActualQuantity+ord.ordActualQuantity1)*ord.storeUnitPrice) amt,ord.orderDate  " +
                "from cat,plu,ord,cat cat1  " +
                "WHERE cat.storeid='${User.getUser().storeId}'  " +
                "AND trim(cat.categoryNumber) is not null  " +
                "AND trim(cat.midcategoryNumber) is null  " +
                "AND trim(cat.microcategoryNumber) is null  " +
                "AND plu.storeID=cat.storeID  " +
                "AND plu.categoryNumber=cat.categoryNumber  " +
                "AND plu.storeID=ord.storeID  " +
                "AND plu.itemNumber=ord.itemNumber  " +
                "AND ord.orderDate=to_date('${CStoreCalendar.getCurrentDate(2)}','YYYY-MM-DD')  " +
                "AND plu.storeID=cat1.storeID  " +
                "AND plu.categoryNumber=cat1.categoryNumber  " +
                "AND plu.midCategoryNumber=cat1.midCategoryNumber  " +
                "AND trim(cat1.midCategoryNumber) is not null  " +
                "AND trim(cat1.microCategoryNumber) is null  " +
                "AND cat1.GROUP_YN='N'  " +
                "AND cat.categoryNumber<>'99'  " +
                "GROUP BY ord.orderDate,plu.categoryNumber,cat.categoryName) " +
                "order by CATEGORYNUMBER\u0004"
    }

    /**
     * 得到所有修改过的数据统计
     */
    fun getAllEditData(): String {
        return "select '-1' categorynumber, '${MyApplication.instance().getString(R.string.statistics)}' categoryname, sum(tot_sku) tot_sku, sum(ord_sku) ord_sku, sum(amt) amt, to_date('${CStoreCalendar.getCurrentDate(2)}','YYYY-MM-DD')  orderdate from(select plu.categoryNumber,cat.categoryName, " +
                "count(*) tot_sku,sum(decode(sign(ord.ordActualQuantity+ord.ordActualQuantity1), 1,1,0)) ord_sku, " +
                "sum((ord.ordActualQuantity+ord.ordActualQuantity1)*ord.storeUnitPrice) amt,ord.orderDate " +
                "from cat,plu,ord,cat cat1 " +
                "WHERE cat.storeid='${User.getUser().storeId}' " +
                "AND trim(cat.categoryNumber) is not null " +
                "AND trim(cat.midcategoryNumber) is null " +
                "AND trim(cat.microcategoryNumber) is null " +
                "AND plu.storeID=cat.storeID " +
                "AND plu.categoryNumber=cat.categoryNumber " +
                "AND plu.storeID=ord.storeID " +
                "AND plu.itemNumber=ord.itemNumber " +
                "AND ord.orderDate=to_date('${CStoreCalendar.getCurrentDate(2)}','YYYY-MM-DD') " +
                "AND plu.storeID=cat1.storeID " +
                "AND plu.categoryNumber=cat1.categoryNumber " +
                "AND plu.midCategoryNumber=cat1.midCategoryNumber " +
                "AND trim(cat1.midCategoryNumber) is not null " +
                "AND trim(cat1.microCategoryNumber) is null " +
                "AND cat1.GROUP_YN='N' " +
                "AND cat.categoryNumber<>'99' " +
                "GROUP BY ord.orderDate,plu.categoryNumber,cat.categoryName)\u0004"
    }

    /**
     * 通过大类id获得商品
     */
    fun getItemByCategoryId(categoryId: String, sort: String): String {
        return "Select to_char(x.sell_cost, '999999990.00') sell_cost," +
                " x.itemnumber,x.pluname,x.quantity,x.invquantity,x.ordactualquantity,x.dlv_qty,x.d1_dfs,x.INCREASEORDERQUANTITY,x.minimaorderquantity,x.maximaorderquantity,x.dms,x.ordertype,x.pro_yn, " +
                "substr(p.signType,12,1) s_returntype," +
                "round(p.storeunitprice,2) storeunitprice " +
                "From appord_t2 x,plu p," +
                "(select itemnumber item_no from itemgondra where storeid='${User.getUser().storeId}' and gondranumber like '%' " +
                "group by itemnumber) y " +
                "where x.itemnumber= y.item_no(+) " +
                "and x.itemnumber = p.itemnumber(+) " +
                "and x.categorynumber like '$categoryId' " +
                "and x.midCategoryNumber like '%' " +
                "AND Fresh_YN='N' $sort\u0004"
    }

    fun getItemByEditCategory(sort: String): String {
        return "Select to_char(x.sell_cost, '999999990.00') sell_cost, " +
                "x.itemnumber,x.pluname,x.quantity,x.invquantity,x.ordactualquantity,x.dlv_qty,x.d1_dfs,x.INCREASEORDERQUANTITY,x.minimaorderquantity,x.maximaorderquantity,x.dms,x.ordertype,x.pro_yn, " +
                "substr(p.signType,12,1) s_returntype, " +
                "round(p.storeunitprice,2) storeunitprice " +
                "From appord_t2 x,plu p, " +
                "(select itemnumber item_no from itemgondra where storeid='${User.getUser().storeId}' and gondranumber like '%' " +
                "group by itemnumber) y " +
                "where x.itemnumber= y.item_no(+) " +
                "and x.itemnumber = p.itemnumber(+)  " +
                "and x.ordactualquantity!=0 " +
                "AND Fresh_YN='N' " +
                "and p.categorynumber in (" +
                "select c.categorynumber " +
                "from cat c,plu pp,ord o,cat c1 " +
                "where c.storeid='${User.getUser().storeId}' " +
                "and trim(c.categorynumber) is not null  " +
                "and trim(c.midcategorynumber) is null  " +
                "and trim(c.microcategorynumber) is null " +
                "and pp.storeid=c.storeid " +
                "and pp.categorynumber=c.categorynumber " +
                "and pp.storeid=o.storeid " +
                "and pp.itemnumber=o.itemnumber " +
                "AND o.orderDate=to_date('${CStoreCalendar.getCurrentDate(2)}','YYYY-MM-DD')  " +
                "and pp.storeid=c1.storeid " +
                "and pp.categorynumber=c1.categorynumber " +
                "and pp.midcategorynumber=c1.midcategorynumber  " +
                "AND trim(c1.midCategoryNumber) is not null " +
                "AND trim(c1.microCategoryNumber) is  null  " +
                "and c1.group_yn='N' " +
                "and c.categorynumber<>'99' " +
                "group by c.categorynumber" +
                ")" +
                "$sort\u0004"
    }

    /**
     * 得到货架分类
     */
    val getAllShelf: String
        get() {
            return "Select gondra.GondraNumber,gondra.GondraNumber || ' - ' || gondra.GondraName as gondraname,count(*) tot_sku,sum(decode(sign(ord.ordActualQuantity+ord.ordActualQuantity1), 1,1,0)) ord_sku,sum((ord.ordActualQuantity+ord.ordActualQuantity1)*ord.storeUnitPrice) amt " +
                    "from itemgondra,ord,gondra " +
                    "where itemgondra.StoreID= '${User.getUser().storeId}' " +
                    "and ord.StoreID=itemgondra.StoreID " +
                    "and ord.itemNumber = itemgondra.itemNumber " +
                    "and ord.orderdate=to_date('${MyTimeUtil.tomorrowDate}','yyyy-MM-dd') " +
                    "and gondra.Storeid=itemgondra.StoreID " +
                    "and gondra.GondraNumber=itemgondra.GondraNumber " +
                    "group by gondra.GondraNumber,  gondra.GondraName " +
                    "order by gondra.GondraNumber\u0004"
        }

    /**
     * 通过货架id获得商品
     */
    fun getItemByShelfId(shelfId: String, sort: String): String {
        return "Select to_char(x.sell_cost, '999999990.00') sell_cost," +
                " x.itemnumber,x.pluname,x.quantity,x.invquantity,x.ordactualquantity,x.dlv_qty,x.d1_dfs,x.INCREASEORDERQUANTITY,x.minimaorderquantity,x.maximaorderquantity,x.dms,x.ordertype,x.pro_yn, " +
                "substr(p.signType,12,1) s_returntype," +
                "round(p.storeunitprice,2) storeunitprice " +
                "From appord_t2 x,plu p," +
                "(select itemnumber item_no from itemgondra where storeid='${User.getUser().storeId}' and gondranumber like '$shelfId' " +
                "group by itemnumber) y " +
                "where x.itemnumber= y.item_no(+) " +
                "and x.itemnumber = p.itemnumber(+) " +
                "and x.categorynumber like '%' " +
                "and x.midCategoryNumber like '%' " +
                "and trim(y.item_no) is not null $sort\u0004"
    }

    /**
     * 更新单品语句${MyApplication.instance().getString(R.string.pei)}合事务头和脚使用
     */
    fun updateOrdItem(itemId: String, value: Int): String {
        return "update ord set updateuserid='${User.getUser().uId}', updatedate=sysdate, ordstatus='1', ordactualquantity=$value where storeid='${User.getUser().storeId}' and orderdate=to_date('${MyTimeUtil.tomorrowDate}','yyyy-MM-dd') and itemnumber='$itemId'; update appord_t2 set ordactualquantity=$value,status='1' where orderdate=to_date('${MyTimeUtil.tomorrowDate}','yyyy-MM-dd') and itemnumber='$itemId';"
    }

    /**
     * 单品订货去搜索
     */
    fun unitOrder(value: String): String {
        return "select * from " +
                "(Select distinct x.itemnumber, to_char(x.sell_cost, '999999990.00') sell_cost," +
                "x.pluname,x.quantity,x.invquantity,x.ordactualquantity,x.dlv_qty,x.d1_dfs,x.INCREASEORDERQUANTITY,x.minimaorderquantity,x.maximaorderquantity,x.ordertype,x.pro_yn, " +
                "substr(p.signType,12,1) s_returntype, " +
                "round(p.storeunitprice,2) storeunitprice " +
                "From appord_t2 x,plu p, itemplu i " +
                "where x.itemnumber = p.itemnumber " +
                "and i.itemnumber=x.itemnumber " +
                "and (x.itemnumber like '%$value%' or x.pluname like'%$value%' or i.plunumber like '%$value%')) " +
                "where rownum<100" +
                "order by itemnumber\u0004"
    }

    /**
     * 获得自用品分类
     */
    val getSelf: String
        get() {
            return "select c.midcategorynumber,c.categoryname, " +
                    "count(*) tot_sku, " +
                    "sum(decode(sign(a.ordActualQuantity+a.ordActualQuantity1), 1,1,0)) ord_sku, " +
                    "to_char(sum((a.ordActualQuantity+a.ordActualQuantity1)* a.sell_cost),'999999990.00') amt " +
                    "from ( " +
                    "select p.itemnumber,p.pluname,o.ordactualquantity,o.ordactualquantity1,p.categorynumber,p.midcategorynumber,p.minimaorderquantity,p.maximaorderquantity,p.increaseorderquantity, p.sell_cost " +
                    "from ord o, plu p " +
                    "where o.itemnumber=p.itemnumber " +
                    "and o.orderdate=to_date('${MyTimeUtil.tomorrowDate}','yyyy-MM-dd') " +
                    "and p.categorynumber =99) a, cat c " +
                    "where c.categorynumber=a.categorynumber " +
                    "and c.midcategorynumber!=' ' " +
                    "and c.microcategorynumber!=' ' " +
                    "and c.midcategorynumber=a.midcategorynumber " +
                    "group by c.midcategorynumber,c.categoryname " +
                    "order by 1\u0004"
        }

    /**
     * 通过自用品id获得商品
     */
    fun getSelfBySelfId(selfId: String, orderBy: String): String {
        return "select p.itemnumber,p.pluname,x.ordactualquantity,p.midcategorynumber,p.minimaorderquantity,p.maximaorderquantity,p.increaseorderquantity, to_char(p.sell_cost,'999999990.00') storeunitprice,x.inv_qty,x.dlv_qty,p.ordertype,'N' pro_yn " +
                "from ord x, plu p " +
                "where x.itemnumber=p.itemnumber " +
                "and x.orderdate=to_date('${MyTimeUtil.tomorrowDate}','yyyy-MM-dd') " +
                "and p.categorynumber =99 " +
                "and p.midcategorynumber=$selfId $orderBy\u0004"
    }

    /**
     * 获得促销品和新品统计
     */
    val getNewItemId: String
        get() {
            return "select * from (select distinct in_th_code,'${MyApplication.instance().getString(R.string.di)}'|| in_th_code ||'${MyApplication.instance().getString(R.string.qi)}' as title, sum(tot_sku) tot_sku, sum(ord_sku) ord_sku, sum(amt) amt " +
                    "from (select substr(p.in_th_code,1,3) in_th_code, " +
                    "count(*) tot_sku, " +
                    "sum(decode(sign(p.ordactualquantity+p.ordactualquantity1),1,1,0)) ord_sku, " +
                    "sum((p.ordactualquantity+p.ordactualquantity1)*p.storeunitprice) amt " +
                    "from (select a.in_th_code,a.itemnumber,b.ordActualQuantity,b.ordActualQuantity1,b.storeUnitPrice " +
                    "from plu a,ord b,appord_t2 c " +
                    "where a.itemnumber = b.itemnumber " +
                    "and c.itemnumber=b.itemnumber " +
                    "and c.orderdate=b.orderdate " +
                    "and b.orderdate=to_date('${CStoreCalendar.getCurrentDate(2)}','yyyy-MM-dd') " +
                    "and a.in_th_code is not null " +
                    "AND nvl(c.market_date,TO_DATE('1970-01-01','YYYY-MM-DD'))>=(sysdate-60 )AND c.item_level='L0') p " +
                    "group by p.in_th_code) " +
                    "group by in_th_code " +
                    "order by in_th_code desc) where rownum<5 " +
                    "union " +
                    "select '0' in_th_code,'${MyApplication.instance().getString(R.string.promotion)}' title, count(*) tot_sku, " +
                    "sum(decode(sign(ordActualQuantity+ordActualQuantity1), 1,1,0)) ord_sku, " +
                    "sum((ordActualQuantity+ordActualQuantity1)*storeunitprice) amt " +
                    "from (Select to_char(x.sell_cost, '999999990.00') sell_cost, " +
                    "x.itemnumber,x.pluname,x.quantity,x.invquantity,x.ordactualquantity,x.ordactualquantity1,x.dlv_qty,x.d1_dfs,x.INCREASEORDERQUANTITY,x.minimaorderquantity,x.maximaorderquantity, " +
                    "substr(p.signType,12,1) s_returntype, " +
                    "round(p.storeunitprice,2) storeunitprice " +
                    "From appord_t2 x,plu p " +
                    "where x.itemnumber = p.itemnumber(+) " +
                    "and x.pro_yn ='Y') " +
                    "order by 1 desc\u0004"
        }

    /**
     * 根据档期获得商品
     */
    fun getNewItemById(id: String, orderBy: String): String {
        return "Select to_char(x.sell_cost, 'fm999999990.00') sell_cost,x.itemnumber,x.pluname,x.quantity,x.invquantity,x.ordactualquantity,x.dlv_qty,x.d1_dfs,x.INCREASEORDERQUANTITY,x.minimaorderquantity,x.maximaorderquantity,x.dms,x.ordertype,x.pro_yn, substr(p.signType,12,1) s_returntype, round(p.storeunitprice,2) storeunitprice " +
                "From appord_t2 x,plu p " +
                "where x.itemnumber = p.itemnumber(+) " +
                "AND nvl(x.market_date,TO_DATE('1970-01-01','YYYY-MM-DD'))>=(sysdate-60 ) " +
                "AND x.item_level='L0' " +
                "AND x.IN_TH_CODE like'%$id%' $orderBy\u0004"
    }

    /**
     * 获得促销品
     */
    fun getPromotion(orderBy: String): String {
        return "Select to_char(x.sell_cost, '999999990.00') sell_cost, x.itemnumber,x.pluname,x.quantity,x.invquantity,x.ordactualquantity,x.dlv_qty,x.d1_dfs,x.INCREASEORDERQUANTITY,x.minimaorderquantity,x.maximaorderquantity,x.dms,x.ordertype,x.pro_yn,substr(p.signType,12,1) s_returntype,round(p.storeunitprice,2) storeunitprice From appord_t2 x,plu p where x.itemnumber = p.itemnumber(+)and pro_yn ='Y' $orderBy\u0004"
    }

    /**
     * 即期鲜食订货
     */
    val getFreshGroup1: String
        get() {
            return "SELECT distinct cat.categoryNumber,cat.midCategoryNumber," +
                    "(cat.categoryNumber || cat.midCategoryNumber || ':' || cat.categoryName) name," +
                    "count(*) tot_sku," +
                    "sum(decode(sign(ord.ordActualQuantity+ord.ordActualQuantity1), 1,1,0)) ord_sku," +
                    "sum((ord.ordActualQuantity+ord.ordActualQuantity1)*ord.storeUnitPrice) amt " +
                    "FROM cat,plu,ord " +
                    "WHERE cat.storeID='${User.getUser().storeId}' " +
                    "AND trim(cat.microCategoryNumber) is null " +
                    "AND trim(cat.midcategorynumber) is not null " +
                    "AND cat.GROUP_YN='Y' " +
                    "AND plu.storeID=cat.storeID " +
                    "AND plu.categoryNumber=cat.categoryNumber " +
                    "AND plu.midCategoryNumber=cat.midCategoryNumber " +
                    "AND plu.storeID=ord.storeID " +
                    "AND plu.itemNumber=ord.itemNumber " +
                    "AND ord.orderDate=to_date('${MyTimeUtil.tomorrowDate}','yyyy-mm-dd') " +
                    "group by cat.categoryNumber , cat.midCategoryNumber, " +
                    "(cat.categoryNumber || cat.midCategoryNumber || ':' || cat.categoryName) " +
                    "ORDER BY cat.categoryNumber ||'-'|| cat.midCategoryNumber\u0004"
        }

    /**
     * 其他鲜食订货
     */
    val getFreshGroup2: String
        get() {
            return "SELECT distinct cat.categoryNumber, cat.midCategoryNumber," +
                    "(cat.categoryNumber || cat.midCategoryNumber || ':' || cat.categoryName) name," +
                    "count(*) tot_sku," +
                    "sum(decode(sign(ord.ordActualQuantity+ord.ordActualQuantity1), 1,1,0)) ord_sku," +
                    "sum((ord.ordActualQuantity+ord.ordActualQuantity1)*ord.storeUnitPrice) amt " +
                    "FROM cat,plu,ord " +
                    "WHERE cat.storeID='${User.getUser().storeId}' " +
                    "AND trim(cat.microCategoryNumber) is null " +
                    "AND trim(cat.midcategorynumber) is not null " +
                    "AND cat.GROUP_YN='Z' " +
                    "AND plu.storeID=cat.storeID " +
                    "AND plu.categoryNumber=cat.categoryNumber " +
                    "AND plu.midCategoryNumber=cat.midCategoryNumber " +
                    "AND plu.storeID=ord.storeID " +
                    "AND plu.itemNumber=ord.itemNumber " +
                    "AND ord.orderDate=to_date('${MyTimeUtil.tomorrowDate}','yyyy-mm-dd') " +
                    "group by cat.categoryNumber , cat.midCategoryNumber, " +
                    "(cat.categoryNumber || cat.midCategoryNumber || ':' || cat.categoryName) " +
                    "ORDER BY cat.categoryNumber ||'-'|| cat.midCategoryNumber\u0004"
        }

    /**
     * 根据大类、中类获得鲜食
     */
    fun getFreashItem(categoryId: String, midId: String, orderBy: String): String {
        return "Select to_char(x.sell_cost, '999999990.00') sell_cost," +
                "x.itemnumber,x.pluname,x.quantity,x.invquantity,x.ordactualquantity,x.dlv_qty,x.d1_dfs,x.INCREASEORDERQUANTITY,x.minimaorderquantity,x.maximaorderquantity, x.dms,x.ordertype,x.pro_yn," +
                "substr(p.signType,12,1) s_returntype," +
                "round(p.storeunitprice,2) storeunitprice " +
                "From appord_t2 x,plu p " +
                "where x.itemnumber = p.itemnumber " +
                "and x.categorynumber = '$categoryId' " +
                "and x.midCategoryNumber = '$midId' $orderBy\u0004"
    }

    /**
     * 检测订货的系统异常
     */
    val getOrderSystemError: String
        get() {
            return "SELECT ord.itemNumber,plu.pluName,(ord.ordActualQuantity+ord.ordActualQuantity1) ordActualQuantity, " +
                    "(ord.ordActualQuantity+ord.ordActualQuantity1)*ord.storeUnitPrice amt, " +
                    "unit.unitName,ord.dlv_qty, " +
                    "to_char(decode( trim(plu.sale_item),null,nvl(inv.befInvQuantity,0)+nvl(inv.accDlvQuantity,0)-nvl(inv.accRtnQuantity,0)- " +
                    "nvl(inv.accSaleQuantity,0)+nvl(inv.accSaleRtnQuantity,0)-nvl(inv.accMrkQuantity,0)+ " +
                    "nvl(inv.accCshDlvQuantity,0)-nvl(inv.accCshRtnQuantity,0)+nvl(inv.accTrsQuantity,0)+ " +
                    "nvl(inv.accLeibianQuantity,0)+nvl(inv.accAdjQuantity,0)+nvl(inv.accHqAdjQuantity,0), " +
                    "(nvl(inv.befInvQuantity,0)+nvl(inv.accDlvQuantity,0)-nvl(inv.accRtnQuantity,0)- " +
                    "nvl(inv.accSaleQuantity,0)+nvl(inv.accSaleRtnQuantity,0)-nvl(inv.accMrkQuantity,0)+ " +
                    "nvl(inv.accCshDlvQuantity,0)-nvl(inv.accCshRtnQuantity,0)+nvl(inv.accTrsQuantity,0)+ " +
                    "nvl(inv.accLeibianQuantity,0)+nvl(inv.accAdjQuantity,0)+nvl(inv.accHqAdjQuantity,0))/plu.pcs)) as InvQuantity, " +
                    "ord.dlv_qty,nvl(itemsaleMonth.dma,0) dma " +
                    "FROM ord left join sfitem on sfitem.storeid=ord.storeID AND sfitem.item=ord.itemNumber, " +
                    "plu left join itemsaleMonth on itemsaleMonth.storeID=plu.storeID AND itemsaleMonth.itemNumber=plu.itemNumber " +
                    "left join unit on plu.storeID=unit.storeID AND plu.smallUnitID=unit.unitID, " +
                    "inv " +
                    "where ord.StoreID='${User.getUser().storeId}' " +
                    "and ord.orderdate=to_date('${CStoreCalendar.getCurrentDate(2)}','yyyy-MM-dd') " +
                    "and plu.storeID = ord.StoreID " +
                    "and plu.itemNumber = ord.itemnumber " +
                    "and plu.shipnumber= ord.shipnumber " +
                    "and ord.orderdate between plu.orderBeginDate and  plu.orderEndDate " +
                    "AND (ord.ordActualQuantity+ord.ordActualQuantity1)>0 " +
                    "AND inv.storeid = plu.storeid " +
                    "and inv.busidate = to_date('${CStoreCalendar.getCurrentDate(2)}','yyyy-MM-dd') " +
                    "and inv.shipnumber=plu.shipnumber " +
                    "and inv.itemnumber = decode( trim(plu.sale_item),null,plu.itemNumber,plu.sale_item) " +
                    "AND nvl(inv.befInvQuantity,0)+nvl(inv.accDlvQuantity,0)-nvl(inv.accRtnQuantity,0)- " +
                    "nvl(inv.accSaleQuantity,0)+nvl(inv.accSaleRtnQuantity,0)-nvl(inv.accMrkQuantity,0)+ " +
                    "nvl(inv.accCshDlvQuantity,0)-nvl(inv.accCshRtnQuantity,0)+nvl(inv.accTrsQuantity,0)+ " +
                    "nvl(inv.accLeibianQuantity,0)+nvl(inv.accAdjQuantity,0)+nvl(inv.accHqAdjQuantity,0)+ord.dlv_qty>0 " +
                    "AND((decode( trim(plu.sale_item),null,nvl(inv.befInvQuantity,0)+nvl(inv.accDlvQuantity,0)-nvl(inv.accRtnQuantity,0)- " +
                    "nvl(inv.accSaleQuantity,0)+nvl(inv.accSaleRtnQuantity,0)-nvl(inv.accMrkQuantity,0)+ " +
                    "nvl(inv.accCshDlvQuantity,0)-nvl(inv.accCshRtnQuantity,0)+nvl(inv.accTrsQuantity,0)+ " +
                    "nvl(inv.accLeibianQuantity,0)+nvl(inv.accAdjQuantity,0)+nvl(inv.accHqAdjQuantity,0), " +
                    "(nvl(inv.befInvQuantity,0)+nvl(inv.accDlvQuantity,0)-nvl(inv.accRtnQuantity,0)- " +
                    "nvl(inv.accSaleQuantity,0)+nvl(inv.accSaleRtnQuantity,0)-nvl(inv.accMrkQuantity,0)+ " +
                    "nvl(inv.accCshDlvQuantity,0)-nvl(inv.accCshRtnQuantity,0)+nvl(inv.accTrsQuantity,0)+ " +
                    "nvl(inv.accLeibianQuantity,0)+nvl(inv.accAdjQuantity,0)+nvl(inv.accHqAdjQuantity,0))/plu.pcs)+ord.dlv_qty " +
                    ">nvl(sfitem.safe_qty,plu.face_qty) " +
                    "AND decode( trim(plu.sale_item), null ,nvl(inv.befInvQuantity,0)+nvl(inv.accDlvQuantity,0)-nvl(inv.accRtnQuantity,0)- " +
                    "nvl(inv.accSaleQuantity,0)+nvl(inv.accSaleRtnQuantity,0)-nvl(inv.accMrkQuantity,0)+ " +
                    "nvl(inv.accCshDlvQuantity,0)-nvl(inv.accCshRtnQuantity,0)+nvl(inv.accTrsQuantity,0)+ " +
                    "nvl(inv.accLeibianQuantity,0)+nvl(inv.accAdjQuantity,0)+nvl(inv.accHqAdjQuantity,0), " +
                    "(nvl(inv.befInvQuantity,0)+nvl(inv.accDlvQuantity,0)-nvl(inv.accRtnQuantity,0)- " +
                    "nvl(inv.accSaleQuantity,0)+nvl(inv.accSaleRtnQuantity,0)-nvl(inv.accMrkQuantity,0)+ " +
                    "nvl(inv.accCshDlvQuantity,0)-nvl(inv.accCshRtnQuantity,0)+nvl(inv.accTrsQuantity,0)+ " +
                    "nvl(inv.accLeibianQuantity,0)+nvl(inv.accAdjQuantity,0)+nvl(inv.accHqAdjQuantity,0))/plu.pcs)+ord.dlv_qty " +
                    ">10*nvl(itemsaleMonth.dma,0)) or ((ord.ordActualQuantity+ord.ordActualQuantity1)>30*nvl(itemsaleMonth.dma,0)))"
        }

    /**********************************************现金日报************************************************************/

    /**
     * 更新现金日报的存储过程
     */
    fun cashDaily(): String {
        return "call Scc01_P01('${MyTimeUtil.nowDate3}','${User.getUser().storeId}') \u000c"
    }

    /**
     * 得到现金日报
     */
    fun getAllCashDaily(date: String): String {
        return " select * from ( " +
                "Select x.Accountnumber, x.Accountname,'N' updatetype,x.displaytype, " +
                "trim(to_char(Sum(Nvl(x.Storeamount, 0)),Decode(sign(instr(x.accountname,'来客')),0,'9999990.00','9999990'))) storeamount " +
                "From Accdayrpt x " +
                "Where x.Storeid ='${User.getUser().storeId}' " +
                "And x.Accountdate = to_date('$date','yyyy-MM-dd') " +
                "And x.Displaytype = '0' " +
                "Group By x.Accountnumber, x.Accountname, x.displaytype " +
                "union all " +
                "Select x.Accountnumber, x.Accountname, x.updatetype, x.displaytype,x.storeamount " +
                "From (Select Accountnumber, Accountname, trim(to_char(nvl(Storeamount,0),'9999990.00')) Storeamount, Updatetype,Displaytype " +
                "From Accdayrpt " +
                "Where Storeid ='${User.getUser().storeId}' " +
                "And Accountdate =to_date('$date','yyyy-MM-dd') " +
                "And Displaytype in (1,2,3,5) " +
                "Order By To_Number(Displaysequence),Accountnumber) x " +
                "union all " +
                "Select x.Accountnumber, x.Accountname, x.updatetype, x.displaytype,x.storeamount " +
                "From (Select Accountnumber, Accountname, decode(accountnumber,'1100',datasource,'1097',(Select weather_desc From weathercode Where to_number(weather_id)=Accdayrpt.storeamount) ,trim(to_char(nvl(Storeamount,0),'9999990.00'))) Storeamount, DECODE(Updatetype,'P','Y',Updatetype) Updatetype,Displaytype " +
                "From Accdayrpt " +
                "Where Storeid ='${User.getUser().storeId}' " +
                "And Accountdate = to_date('$date','yyyy-MM-dd') " +
                "And Displaytype between 6 and 10 " +
                "Order By To_Number(Displaysequence),Accountnumber) x) order by displaytype,accountnumber\u0004"
    }

    /**
     * 更新现金日报
     */
    fun updateCashDaily(cdId: String, cdValue: String): String {
        return "update accdayrpt a set a.storeamount='$cdValue' where a.storeid='${User.getUser().storeId}' and a.accountdate=to_date('${MyTimeUtil.nowDate}','yyyy-MM-dd') and a.accountnumber='$cdId'\u0004"
    }

    /**
     * 更新现金日报的事件
     */
    fun updateCashDaily2(cdId: String, cdValue: String): String {
        return "update accdayrpt a set a.datasource='$cdValue' where a.storeid='${User.getUser().storeId}' and a.accountdate=to_date('${MyTimeUtil.nowDate}','yyyy-MM-dd') and a.accountnumber='$cdId'\u0004"
    }

    /**********************************************报废************************************************************/

    /**
     * 创建报废,${MyApplication.instance().getString(R.string.pei)}合事务头脚使用
     */
    fun insertScrap(data: ScrapContractBean): String {
        return "insert into mrk (storeid, busidate, recordnumber, itemnumber, shipnumber, storeunitprice, unitcost, mrkquantity, mrkreasonnumber, updateuserid, updatedatetime, citem_yn, sell_cost, recycle_yn) values (${User.getUser().storeId}, trunc(sysdate), ${data.recordNumber}, ${data.scrapId}, '0', ${data.unitPrice}, ${data.unitCost}, ${data.mrkCount}, '00', ${User.getUser().uId}, sysdate, '${data.citemYN}', ${data.sellCost}, '${data.recycleYN}');"
    }

    /**
     * 更新报废，${MyApplication.instance().getString(R.string.pei)}合事务头脚使用
     */
    fun updateScrap(data: ScrapContractBean): String {
        return "update mrk set mrkquantity=${data.mrkCount}, updateuserid='${User.getUser().uId}',updatedatetime=sysdate where itemnumber='${data.scrapId}' and busidate=to_date('${MyTimeUtil.nowDate}','yyyy-mm-dd');"
    }

    /**
     * 删除报废，${MyApplication.instance().getString(R.string.pei)}合事务头脚使用
     */
    fun deleteScrap(data: ScrapContractBean): String {
        return "delete from mrk where itemnumber='${data.scrapId}' and busidate=to_date('${MyTimeUtil.nowDate}','yyyy-mm-dd');"
    }

    /**
     * 得到指定某天的所有报废信息
     */
    fun allScrap(date: String): String {
        return "select a.itemnumber,to_char(m.busidate,'yyyy-mm-dd') busidate,a.pluname,a.STOREUNITPRICE,a.UNITCOST,a.SELL_COST,a.CITEM_YN,a.recycle_yn,a.barcode_yn, m.mrkquantity,m.recordnumber from app_mrkitem_view a, mrk m where a.itemnumber=m.itemnumber and busidate=to_date('$date','yyyy-mm-dd') order by itemnumber\u0004"
    }

    /**
     * 通过扫码得到的数据加到sql查询语句的字符串中然后返回新的sql语句
     *
     * @param data 扫码得到的数据
     */
    fun getScrapByMessage(data: String): String {
        return "select distinct itemnumber, pluname, categorynumber, storeunitprice, unitcost, sell_cost, citem_yn, recycle_yn, barcode_yn, mrk_date, sale_date, dlv_date from app_mrkitem_view where (itemnumber in (select distinct itemnumber from ITEMPLU where plunumber like '%$data%') or itemnumber like '%$data%' or pluname like '%$data%') and rownum<50\u0004"
    }

    /**
     * 得到当前recodenumber
     */
    val getRecodeNumber: String
        get() {
            return "select recordnumber from (select recordnumber from mrk where busidate=to_date('${MyTimeUtil.nowDate}','yyyy-mm-dd') order by recordnumber desc) where rownum=1\u0004"
        }

    /**
     * 报废80大类热食分类
     */
    val getScrap80: String
        get() {
            //老版：select midcategorynumber,categoryname from cat where categorynumber='80' and midcategorynumber!=' ' and microcategorynumber=' ' order by midcategorynumber
            return "select a.midcategorynumber,a.categoryname,nvl(b.price,0) price,nvl(b.count,0) count " +
                    "from cat a, " +
                    "(select a.Midcategorynumber, " +
                    "round(sum(b.mrkquantity)*avg(b.storeunitprice),1) price , count(b.itemnumber) count " +
                    "from app_mrkitem_view a,mrk b where a.CATEGORYNUMBER = '80' and a.ITEMNUMBER = b.itemnumber " +
                    "and busidate=trunc(sysdate) " +
                    "group by a.Midcategorynumber) b " +
                    "where a.categorynumber='80' " +
                    "and a.midcategorynumber=b.midcategorynumber(+) " +
                    "and a.midcategorynumber!=' '  " +
                    "and a.microcategorynumber=' ' " +
                    "order by a.midcategorynumber\u0004"
        }

    /**
     * 报废80大类里的中类的item
     */
    fun getScrap80Item(midId: String): String {
        val time = MyTimeUtil.nowDate
        return "select a.itemnumber, " +
                "decode(m.busidate,to_date('$time','yyyy-mm-dd'),m.busidate,'') busidate, " +
                "a.pluname,a.STOREUNITPRICE, " +
                "a.UNITCOST,a.SELL_COST,a.CITEM_YN,a.recycle_yn,a.barcode_yn, " +
                "decode(m.busidate,to_date('$time','yyyy-mm-dd'),m.mrkquantity,'') mrkquantity, " +
                "decode(m.busidate,to_date('$time','yyyy-mm-dd'),m.recordnumber,'') recordnumber " +
                "from app_mrkitem_view a, " +
                "(select * from mrk where busidate = to_date('$time','yyyy-mm-dd')) m " +
                "where categorynumber='80' " +
                "and midcategorynumber='$midId' " +
                "and m.itemnumber(+)=a.itemnumber " +
                "order by itemnumber\u0004"
    }

    /**********************************************进货验收************************************************************/

    /**
     * 得到${MyApplication.instance().getString(R.string.pei)}送列表
     */
    fun getAcceptanceList(date: String): String {
        return "select dlh.requestnumber,ven.vendorname, " +
                "to_date(to_char(plnDlvDate + dlv_days - 1,'yyyy-MM-dd'),'yyyy-MM-dd') end_dlv, " +
                "(case when dlv_days='999' then null else to_date(to_char(plnDlvDate + dlv_days - 1,'yyyy-MM-dd')||'17:00','yyyy-MM-dd HH24:mi')end) end_dlvTimes , " +
                "dlh.dlvStatus, " +
                "dlh.actdlvtime,dlh.plndlvdate,dlh.orddate, " +
                "dlh.orditemqty,nvl(sum(ordQuantity),0)ordQuantity, " +
                "dlh.dlvitemqty, " +
                "nvl(sum(dlvQuantity),0)dlvQuantity, " +
                "dlh.retailtotal, " +
                "nvl(to_char(sum(dlvQuantity*dlvdtl.sell_cost*(1+nvl(taxtype.taxRate,0))), '999999990.00'),0.00) sellcost_tot, " +
                "decode(upper(substr(dlh.requestnumber,2,1)),'C', dlh.requestNumber,'OD' || dlh.requestnumber) dcNumber, " +
                "dlh.vendorid,dlh.dlvstatus,to_char(dlh.dlvdate,'yyyy-mm-dd') dlvdate " +
                "from dlvhead dlh left join dlvdtl on dlvdtl.storeid=dlh.storeID and dlvdtl.dlvdate=dlh.dlvdate and dlvdtl.requestnumber=dlh.requestnumber " +
                "left join plu on plu.storeID=dlvdtl.storeID AND plu.itemNumber=dlvdtl.itemNumber " +
                "left join taxtype on taxtype.storeID=plu.storeID AND taxtype.taxID=plu.taxID, " +
                "vendor ven " +
                "where dlh.dlvDate= to_date('$date','yyyy-mm-dd') and dlh.storeID='${User.getUser().storeId}' " +
                "and ven.vendorID=dlh.vendorID and ven.storeid=dlh.storeID " +
                "group by  dlh.requestnumber,ven.vendorname, " +
                "dlh.dlvStatus, " +
                "dlh.actdlvtime,dlh.plndlvdate,dlh.orddate, " +
                "dlh.orditemqty, " +
                "dlh.dlvitemqty, " +
                "dlh.retailtotal, " +
                "decode(upper(substr(dlh.requestnumber,2,1)),'C',dlh.requestNumber,'OD' || dlh.requestnumber), " +
                "to_date(to_char(plnDlvDate + dlv_days - 1,'yyyy-MM-dd'),'yyyy-MM-dd') , " +
                "dlh.vendorid, dlh.dlvstatus , dlh.dlvdate, " +
                "(case when dlv_days='999' then null else to_date(to_char(plnDlvDate + dlv_days - 1,'yyyy-MM-dd')||'17:00','yyyy-MM-dd HH24:mi') end ) " +
                "order by dlh.requestnumber\u0004"
    }

    /**
     * 得到${MyApplication.instance().getString(R.string.pei)}送表下的商品
     */
    fun getAcceptanceItemList(ab: AcceptanceBean, date: String): String {
        return "select dlv.itemNumber,plu.stocktype,plu.order_item, dlv.ordQuantity,dlv.hqquantity,dlv.dctrsquantity ,nvl(dlv.varQuantity,0)varQuantity, " +
                "dlv.pmcode, " +
                "decode(head.dlvstatus,'1',dlv.dlvquantity,'2', dlv.dlvquantity, case when head.vendorid='00000000000099999801' or head.vendorid='00000000000099999701' or head.vendorid='00000000000099999100' or head.vendorid='00000000000099999101' or head.vendorid='00000000000099999102' " +
                "then dlv.trsquantity else dlv.dlvquantity end ) dlvquantity, " +
                "plu.pluName,unit.unitName, dlv.storeUnitPrice,plu.storeunitprice storeunitprice_now, " +
                "dlv.storeUnitPrice-plu.storeunitprice price_diff,dlv.unitCost, " +
                "dlv.SupplierID,dlv.requestNumber,dlv.shipNumber,dlv.vendorID,plu.sale_item,plu.pcs, dlv.varReason, " +
                "Round(dlv.sell_cost,6) sell_cost,round(dlv.sell_cost*(1+nvl(taxtype.taxRate,0)),6) tax_sell_cost, " +
                "round(round(dlv.sell_cost*(1+nvl(taxtype.taxRate,0)),6)*dlvquantity,2) tax_sell_cost_total, " +
                "dlvquantity old_dlvquantity, " +
                "(nvl(dlv.storeunitprice,0)* decode(head.dlvstatus,'2', dlv.dlvquantity, '1', dlv.dlvquantity, case when head.vendorid='00000000000099999801' or head.vendorid='00000000000099999701' or head.vendorid='00000000000099999100' or head.vendorid='00000000000099999101' or head.vendorid='00000000000099999102' " +
                "then dlv.trsquantity else dlv.dlvquantity end ) ) retailPriceTotal, 'FromDB' data_status, " +
                "(dlv.unitCost*dlv.dlvQuantity) TotalUnitCost, nvl(dlv.trsQuantity,0) trsQuantity,Dlv.Itemnumber||upper(pub_get_string_py(Plu.Pluname)||Plu.Pluname) FindTxt " +
                "from dlvdtl dlv " +
                "left join  plu  on  plu.storeid=dlv.storeID  and plu.ItemNumber=dlv.ItemNumber " +
                "left join unit  on  unit.storeid=plu.storeid and  unit.UnitID=plu.smallUnitID " +
                "left join taxtype on taxtype.storeID=plu.storeID AND taxtype.taxID=plu.taxID " +
                ",dlvhead head " +
                "where head.storeID ='${User.getUser().storeId}' " +
                "and head.dlvdate=to_date('$date','yyyy-mm-dd') " +
                "and head.requestnumber='${ab.distributionId}' " +
                "and dlv.storeid=head.storeid " +
                "and dlv.dlvdate=head.dlvdate " +
                "and dlv.requestnumber=head.requestnumber " +
                "order by dlv.itemNumber\u0004"
    }

    /**
     * 更新${MyApplication.instance().getString(R.string.pei)}送单,${MyApplication.instance().getString(R.string.pei)}合事务使用
     */
    fun updateAcceptance(ab: AcceptanceBean): String {
        return "update dlvhead set " +
                "dlvstatus='${ab.dlvStatus}'," +
                "orditemqty=${ab.ordItemQTY}," +
                "dlvitemqty=${ab.dlvItemQTY}," +
                "retailtotal=${ab.retailTotal}," +
                "costtotal=${ab.costTotal}," +
                "updateuserid='${User.getUser().uId}'," +
                "updatedate=sysdate " +
                "where storeid='${User.getUser().storeId}' " +
                "and dlvdate=to_date('${MyTimeUtil.nowDate}','yyyy-MM-dd') " +
                "and vendorid='${ab.vendorId}' " +
                "and requestnumber='${ab.distributionId}';"
    }

    /**
     * 更新${MyApplication.instance().getString(R.string.pei)}送单的商品,${MyApplication.instance().getString(R.string.pei)}合事务使用
     */
    fun updateAcceptanceItem(aib: AcceptanceItemBean): String {
        return "update dlvdtl set " +
                "dlvquantity=${aib.dlvQuantity}, " +
                "trsquantity=${aib.trsQuantity}, " +
                "updateuserid='${User.getUser().uId}', " +
                "updatedate=sysdate " +
                "where storeid='${User.getUser().storeId}' " +
                "and itemnumber='${aib.itemId}' " +
                "and vendorid='${aib.vendorId}' " +
                "and dlvdate=to_date('${MyTimeUtil.nowDate}','yyyy-MM-dd') " +
                "and requestnumber='${aib.distributionId}';"
    }

    /**
     * 得到${MyApplication.instance().getString(R.string.pei)}送商
     */
    val getVendor: String
        get() {
            return "SELECT x.vendorID,substr(x.vendorID,13,8) || x.vendorName vendorNames ,x.VendorType,x.supplierType " +
                    "from vendor x, " +
                    "(select vendorid,supplierid from plu where storeid='${User.getUser().storeId}' " +
                    "group by vendorid,supplierid) y, " +
                    "(select vendorid from dlvhead  where storeid='${User.getUser().storeId}' " +
                    "and dlvdate > sysdate-365 group by vendorid) z " +
                    "where x.vendortype='Y'and x.suppliertype='Y' and x.vendorid=y.vendorid and x.vendorid=z.vendorid(+) " +
                    "order by x.vendorid\u0004"
        }

    /**
     * 创建${MyApplication.instance().getString(R.string.pei)}送单,${MyApplication.instance().getString(R.string.pei)}合事务使用
     */
    fun createAcceptance(ab: AcceptanceBean, date: String): String {
        val time = "to_date('$date','yyyy-MM-dd')"
        return "insert into dlvhead " +
                "(storeID, dlvDate, requestNumber, vendorID, shipNumber,ordDate, plnDlvDate, " +
                "actDlvTime, dlvStatus, ordItemQty, dlvItemQty,retailTotal, costTotal, busiDate, updateUserID, updateDate) " +
                "values " +
                "('${User.getUser().storeId}',$time,'${ab.distributionId}','${ab.vendorId}','0',$time,$time,$time,'2',${ab.ordItemQTY},${ab.dlvItemQTY},${ab.retailTotal},${ab.costTotal},$time,'${User.getUser().uId}',sysdate);"
    }

    /**
     * 创建${MyApplication.instance().getString(R.string.pei)}送单下的商品，${MyApplication.instance().getString(R.string.pei)}合事务使用
     */
    fun createAcceptanceItem(aib: AcceptanceItemBean, date: String): String {
        val time = "to_date('$date','yyyy-MM-dd')"
        return "insert into dlvdtl " +
                "(storeid,dlvdate,vendorid,itemnumber,shipnumber,dlvquantity,varquantity,storeunitprice, " +
                "unitcost,requestnumber,pmcode,supplierid,updateuserid,updatedate,varreason,sell_cost,trsquantity,hqquantity,dctrsquantity,ordquantity) " +
                "values" +
                "('${User.getUser().storeId}',$time,'${aib.vendorId}','${aib.itemId}','${aib.shipNumber}',${aib.dlvQuantity},${aib.varQuantity},${aib.storeUnitPrice}," +
                "${aib.unitCost},'${aib.distributionId}','-','${aib.supplierId}','${User.getUser().uId}',sysdate,null,${aib.sellCost},${aib.trsQuantity},${aib.hqQuantity},${aib.dctrsQuantity},${aib.ordQuantity});"
    }

    /**
     * 得到今天最大的${MyApplication.instance().getString(R.string.pei)}送单
     */
    fun getMaxAcceptanceId(date: String, num: String, type: Int): String {
        return if (type == 1) {
            "select max(requestNumber) value from dlvHead where DlvDate = to_date('$date','yyyy-MM-dd') and RequestNumber< 'A00000000' and RequestNumber like'$num%'\u0004"
        } else {
            "select max(requestNumber) value from rtnHead where rtnDate = to_date('$date','yyyy-MM-dd') and RequestNumber< 'A00000000' and RequestNumber like'$num%'\u0004"
        }
    }

    /**
     * 得到${MyApplication.instance().getString(R.string.pei)}送单号的前缀
     */
    fun getNowNum(date: String): String {
        var month = ""
        when (date.substring(5, 7).toInt()) {
            1 -> month = "A"
            2 -> month = "B"
            3 -> month = "C"
            4 -> month = "D"
            5 -> month = "E"
            6 -> month = "F"
            7 -> month = "G"
            8 -> month = "H"
            9 -> month = "I"
            10 -> month = "J"
            11 -> month = "K"
            12 -> month = "L"
        }
        val year = date.substring(2, 4)
        val day = date.substring(8)
        return year + month + day
    }

    /**
     * 得到创建验收输入的商品
     */
    fun getAcceptanceCommodity(ab: AcceptanceBean?, vendorId: String): String {
        var sql = ""
        if (ab != null) {
            sql += "select * from (select plu.itemNumber,plu.stocktype,plu.order_item,plu.pluName,plu.storeUnitPrice,plu.VendorID,plu.SupplierID, " +
                    "plu.basic_cost unitCost,plu.sell_cost sell_cost,unit.UnitName,plu.shipNumber,orderBeginDate,orderEndDate, " +
                    "round(plu.sell_cost*(1+nvl(taxtype.taxRate,0)),6) tax_sell_cost, plu.pcs, plu.sale_item " +
                    "from plu left join unit on  unit.storeID=plu.storeid and unit.unitID=plu.SmallUnitID " +
                    "left join taxtype on taxtype.storeID=plu.storeID AND taxtype.taxID=plu.taxID " +
                    "where plu.StoreID='${User.getUser().storeId}' " +
                    "and vendorid like '%$vendorId%' " +
                    "and trim(SupplierID) is not null and trim(VendorID) is not null " +
                    "and itemnumber not in ("
            ab.allItems.forEach { sql += "${it.itemId}," }//得到not in的品号
            sql = sql.substring(0, sql.length - 1)//去掉逗号
            sql += ") ) where rownum<100\u0004"
        } else {
            sql = "select * from (select plu.itemNumber,plu.stocktype,plu.order_item,plu.pluName,plu.storeUnitPrice,plu.VendorID,plu.SupplierID, " +
                    "plu.basic_cost unitCost,plu.sell_cost sell_cost,unit.UnitName,plu.shipNumber,orderBeginDate,orderEndDate, " +
                    "round(plu.sell_cost*(1+nvl(taxtype.taxRate,0)),6) tax_sell_cost, plu.pcs, plu.sale_item " +
                    "from plu left join unit on  unit.storeID=plu.storeid and unit.unitID=plu.SmallUnitID " +
                    "left join taxtype on taxtype.storeID=plu.storeID AND taxtype.taxID=plu.taxID " +
                    "where plu.StoreID='${User.getUser().storeId}' " +
                    "and vendorid like '%$vendorId%' " +
                    "and trim(SupplierID) is not null and trim(VendorID) is not null" +
                    ") where rownum<100\u0004"
        }
        return sql
    }

    /**
     * 得到创建验收输入的商品
     */
    fun getReturnAcceptanceCommodity(rab: ReturnAcceptanceBean?, vendorId: String): String {
        var sql = ""
        if (rab != null) {
            sql += "select * from (select plu.itemNumber,plu.stocktype,plu.order_item,plu.pluName,plu.storeUnitPrice,plu.VendorID,plu.SupplierID, " +
                    "plu.basic_cost unitCost,plu.sell_cost sell_cost,unit.UnitName,plu.shipNumber,orderBeginDate,orderEndDate, " +
                    "round(plu.sell_cost*(1+nvl(taxtype.taxRate,0)),6) tax_sell_cost, plu.pcs, plu.sale_item " +
                    "from plu left join unit on  unit.storeID=plu.storeid and unit.unitID=plu.SmallUnitID " +
                    "left join taxtype on taxtype.storeID=plu.storeID AND taxtype.taxID=plu.taxID " +
                    "where plu.StoreID='${User.getUser().storeId}' " +
                    "and vendorid like '%$vendorId%' " +
                    "and trim(SupplierID) is not null and trim(VendorID) is not null " +
                    "and itemnumber not in ("
            rab.allItems.forEach { sql += "${it.itemId}," }//得到not in的品号
            sql = sql.substring(0, sql.length - 1)//去掉逗号
            sql += ") ) where rownum<100\u0004"
        } else {
            sql = "select * from (select plu.itemNumber,plu.stocktype,plu.order_item,plu.pluName,plu.storeUnitPrice,plu.VendorID,plu.SupplierID, " +
                    "plu.basic_cost unitCost,plu.sell_cost sell_cost,unit.UnitName,plu.shipNumber,orderBeginDate,orderEndDate, " +
                    "round(plu.sell_cost*(1+nvl(taxtype.taxRate,0)),6) tax_sell_cost, plu.pcs, plu.sale_item " +
                    "from plu left join unit on  unit.storeID=plu.storeid and unit.unitID=plu.SmallUnitID " +
                    "left join taxtype on taxtype.storeID=plu.storeID AND taxtype.taxID=plu.taxID " +
                    "where plu.StoreID='${User.getUser().storeId}' " +
                    "and vendorid like '%$vendorId%' " +
                    "and trim(SupplierID) is not null and trim(VendorID) is not null" +
                    ") where rownum<100\u0004"
        }
        return sql
    }

    /**
     * 检测输入的商品是否重复
     */
    fun getJudgmentCommodity(aib: ArrayList<AcceptanceItemBean>, date: String): String {
        var sql = "select * from dlvdtl where dlvdate=to_date('$date','yyyy-MM-dd') and itemnumber in("
        aib.forEach { sql += "'${it.itemId}'," }
        sql = sql.substring(0, sql.length - 1)//去掉逗号
        sql += ")\u0004"
        return sql
    }

    /**
     * 检测输入的${MyApplication.instance().getString(R.string.tui)}货商品是否重复
     */
    fun getJudgmentReturnCommodity(raib: ArrayList<ReturnAcceptanceItemBean>, date: String): String {
        var sql = "select * from rtndtl where rtndate=to_date('$date','yyyy-MM-dd') and itemnumber in ("
        raib.forEach { sql += "'${it.itemId}'," }
        sql = sql.substring(0, sql.length - 1)//去掉逗号
        sql += ")\u0004"
        return sql
    }

    /**********************************************${MyApplication.instance().getString(R.string.tui)}货验收************************************************************/

    /**
     * 得到${MyApplication.instance().getString(R.string.tui)}货验收单
     */
    fun getReturnAcceptanceList(date: String): String {
        return "select rtn.requestNumber, to_char(rtn.rtnDate,'yyyy-MM-dd') rtndate,rtn.plnrtnDate,rtn.preRtnDate, " +
                "nvl(rtn.plnRtnItemQty,0) plnRtnItemQty, " +
                "nvl(rtn.actRtnItemQty,0)actRtnItemQty, " +
                "nvl(rtn.retailTotal,0) retailTotal,rtn.rtnStatus,rtn.actRtnTime ,rtn.vendorID,ven.vendorName, " +
                "sum(rtnQuantity*rtndtl.sell_cost*(1+nvl(taxtype.taxRate,0))) sellcost_tot, " +
                "sum(rtnquantity) rtnquantity,sum(ordQuantity) ordQuantity " +
                "from vendor ven,rtnhead rtn " +
                "left join rtndtl on rtn.storeID=rtndtl.storeid AND rtn.RtnDate=rtndtl.Rtndate AND rtn.requestnumber=rtndtl.requestnumber " +
                "left join plu on plu.storeID=rtndtl.storeID AND plu.itemNumber=rtndtl.itemNumber " +
                "left join taxtype on taxtype.storeID=plu.storeID AND taxtype.taxID=plu.taxID " +
                "where rtn.storeID='${User.getUser().storeId}' and rtn.RtnDate= to_date('$date','yyyy-MM-dd') " +
                "and ven.storeid=rtn.storeID and ven.vendorID=rtn.vendorid " +
                "group by rtn.requestNumber, rtn.rtnDate,rtn.plnrtnDate,rtn.preRtnDate,plnRtnItemQty,actRtnItemQty, retailTotal, rtn.rtnStatus, rtn.actRtnTime, rtn.vendorID,ven.vendorName order by requestnumber\u0004"
    }

    /**
     * 得到${MyApplication.instance().getString(R.string.tui)}货单下的商品
     */
    fun getReturnAcceptanceItemList(rab: ReturnAcceptanceBean, date: String): String {
        return "select to_char(rtn.rtnDate,'yyyy-MM-dd') rtndate,rtn.itemNumber,nvl(rtn.ordQuantity,0)ordQuantity, rtn.RequestNumber,rtn.supplierID,NVL(rtn.rtnQuantity,0) rtnQuantity, nvl(rtn.storeUnitPrice,0) storeUnitPrice,rtn.shipNumber,rtn.vendorID,rtn.unitCost, " +
                "plu.pluName,unit.unitName,plu.sale_item,plu.pcs, nvl(rtn.sell_cost,plu.sell_cost) sell_cost , " +
                "Round(nvl(rtn.sell_cost, plu.sell_cost) *(1+nvl(taxtype.taxRate,0)),6) tax_sell_cost, " +
                "Round(Round(nvl(rtn.sell_cost, plu.sell_cost) *(1+nvl(taxtype.taxRate,0)),6)*NVL(rtn.rtnQuantity,0),2) tax_sell_cost_total, " +
                "rtnQuantity old_plnquantity, rtn.unitCost*rtn.rtnQuantity totalUnitCost, " +
                "(nvl(rtn.rtnQuantity,0))*(nvl(rtn.storeUnitPrice,0)) Total " +
                "from rtndtl rtn " +
                "left join  plu  on plu.storeID=rtn.storeID and plu.itemNumber=rtn.itemNumber " +
                "left join unit on  unit.storeid=plu.storeid and unit.unitID=plu.smallUnitID " +
                "left join taxtype on taxtype.storeID=plu.storeID AND taxtype.taxID=plu.taxID " +
                "where rtn.storeID='${User.getUser().storeId}' " +
                "and rtn.requestNumber='${rab.distributionId}' " +
                "and rtn.rtnDate=to_date('$date','yyyy-MM-dd') " +
                "order by requestNumber\u0004"
    }

    /**
     * 更新${MyApplication.instance().getString(R.string.tui)}货验收单，${MyApplication.instance().getString(R.string.pei)}合事务使用
     */
    fun updateReturnAcceptance(rab: ReturnAcceptanceBean): String {
        return "update rtnHead set " +
                "rtnStatus='${rab.rtnStatus}', " +
                "plnRtnItemQty=${rab.plnRtnItemQTY}, " +
                "actRtnItemQty=${rab.actRtnItemQTY}, " +
                "retailTotal=${rab.retailTotal}, " +
                "costTotal=${rab.costTotal}, " +
                "updateUserId='${User.getUser().uId}', " +
                "updateDate=sysdate, " +
                "actRtnTime=sysdate " +
                "where storeId='${User.getUser().storeId}' " +
                "and rtnDate=to_date('${rab.rtnDate}','yyyy-MM-dd') " +
                "and requestNumber='${rab.distributionId}';"
    }

    /**
     * 更新验收单下的商品，${MyApplication.instance().getString(R.string.pei)}合事务使用
     */
    fun updateReturnAcceptanceItem(raib: ReturnAcceptanceItemBean): String {
        return "update rtndtl set " +
                "rtnQuantity=${raib.rtnQuantity},updateUserId='${User.getUser().uId}',updateDate=sysdate " +
                "where storeId='${User.getUser().storeId}' " +
                "and itemNumber='${raib.itemId}' " +
                "and rtnDate=to_date('${raib.rtnDate}','yyyy-MM-dd') " +
                "and requestNumber='${raib.distributionId}';"
    }

    /**
     * 创建${MyApplication.instance().getString(R.string.tui)}货验收单，${MyApplication.instance().getString(R.string.pei)}合事务使用
     */
    fun createReturnAcceptance(rab: ReturnAcceptanceBean): String {
        return "insert into rtnHead " +
                "(storeId, rtnDate, requestNumber, vendorId, shipNumber, " +
                "preRtnDate, plnRtnDate, rtnStatus, plnRtnItemQty, actRtnItemQty, " +
                "retailTotal, costTotal, busiDate, updateUserId, updateDate, actRtnTime) " +
                "values " +
                "('${User.getUser().storeId}', to_date('${rab.rtnDate}','yyyy-MM-dd'), '${rab.distributionId}','${rab.vendorId}',0, " +
                "to_date('${rab.rtnDate}','yyyy-MM-dd'), to_date('${rab.rtnDate}','yyyy-MM-dd'), '${rab.rtnStatus}', ${rab.plnRtnItemQTY}, ${rab.actRtnItemQTY}, " +
                "${rab.retailTotal}, ${rab.costTotal}, to_date('${rab.rtnDate}','yyyy-MM-dd'), '${User.getUser().uId}',sysdate, sysdate);"
    }

    /**
     * 穿件${MyApplication.instance().getString(R.string.tui)}货验收单下的商品，${MyApplication.instance().getString(R.string.pei)}合事务使用
     */
    fun createReturnAcceptanceItem(raib: ReturnAcceptanceItemBean): String {
        return "insert into rtndtl " +
                "(storeId, rtnDate, vendorID, itemNumber, shipNumber, rtnQuantity, ordQuantity, storeUnitPrice, " +
                "unitCost, requestNumber, supplierId, UpdateUserId, updateDate, sell_cost) " +
                "values " +
                "('${User.getUser().storeId}',to_date('${MyTimeUtil.nowDate}','yyyy-MM-dd'),'${raib.vendorId}','${raib.itemId}','${raib.shipNumber}',${raib.rtnQuantity},${raib.ordQuantity},${raib.storeUnitPrice}, " +
                "${raib.unitCost}, '${raib.distributionId}','${raib.supplierId}','${User.getUser().uId}',sysdate, ${raib.sellCost});"
    }

    /**********************************************库调************************************************************/

    /**
     * 得到某一天的所有库调操作
     */
    fun getAdjustmentList(date: String): String {
        return "select adj.recordnumber, adj.itemnumber, plu.pluname, (adj.actStockQuantity+adj.adjQuantity) currStockQuantity, adj.actstockquantity,adj.adjquantity, plu.shipnumber, adjreasonnumber " +
                "from adj left join plu on plu.storeid=adj.storeid and plu.itemnumber=adj.itemnumber " +
                "where adj.storeid='${User.getUser().storeId}' " +
                "and adj.busidate=to_date('$date','yyyy-MM-dd') " +
                "order by adj.recordnumber,adj.itemnumber"
    }

    /**
     * 库调的单品搜索
     */
    fun searchAdjustment(date: String, msg: String): String {
        return "select " +
                "plu.itemnumber,plu.shipnumber,plu.storeUnitPrice, plu.unitCost,plu.pluname," +
                "nvl(inv.befInvQuantity,0)+nvl(inv.accDlvQuantity,0)-nvl(inv.accRtnQuantity,0)- nvl(inv.accSaleQuantity,0)+nvl(inv.accSaleRtnQuantity,0)-nvl(inv.accMrkQuantity,0)+ nvl(inv.accCshDlvQuantity,0)-nvl(inv.accCshRtnQuantity,0)+nvl(inv.accTrsQuantity,0)+ nvl(inv.accLeibianQuantity,0)+nvl(inv.accAdjQuantity,0)+nvl(inv.accHqAdjQuantity,0) as actStockQuantity," +
                "plu.storeunitprice,plu.unitcost " +
                "from plu " +
                "right join inv on plu.storeID=inv.StoreID " +
                "where  inv.StoreID= plu.StoreID  " +
                "and plu.itemnumber=inv.itemnumber " +
                "and inv.busidate=to_date('$date','yyyy-MM-dd') " +
                "AND substr(plu.signType, 9, 1) = 'Y' " +
                "and (plu.itemnumber='$msg' or plu.pluname like '%$msg%' or plu.itemnumber = (select itemnumber from itemplu where plunumber='$msg')) " +
                "and rownum<100 order by plu.itemnumber"
    }

    /**
     * 得到当前最大的recodeNumber
     */
    fun getAdjustmentMaxId(date: String): String {
        return "select MAX(recordnumber) value from adj where busidate = to_date('$date','yyyy-MM-dd')"
    }

    /**
     * 创建库调,必须${MyApplication.instance().getString(R.string.pei)}合事务使用!
     */
    fun createAdjustment(ab: AdjustmentBean, date: String, recordNumber: Int): String {
        //创建库调
        var result =
                "Insert into adj " +
                        "(StoreID,BusiDate,RecordNumber,Itemnumber,Shipnumber,StoreUnitPrice, " +
                        "UnitCost,AdjQuantity,adj.ActStockQuantity,adjReasonNumber,UpdateUserID,UpdateDateTime) " +
                        "Values " +
                        "('${User.getUser().storeId}',to_date('$date','yyyy-MM-dd'),$recordNumber,'${ab.itemId}',${ab.shipNumber},${ab.storeUnitPrice}, " +
                        "${ab.unitCost}, ${ab.adjQTY}, ${ab.actStockQTY}, ${ab.adjReasonNumber}, ${User.getUser().uId}, sysdate);"
        //更新库存,为了更好分辨就多加一个+=
        result +=
                "update inv set accAdjQuantity=accAdjQuantity + ${ab.adjQTY} " +
                        "where storeID='${User.getUser().storeId}' " +
                        "and busiDate=to_date('$date','yyyy-MM-dd') " +
                        "and ItemNumber='${ab.itemId}' " +
                        "and shipNumber='${ab.shipNumber}';"
        return result
    }

    /*******************************************考勤*******************************************************/

    /**
     * 得到创建签到的sql语句
     * @param uId 输入的UserId，并不是登录用户的id
     */
    fun getInsCheckIn(uId: String): String {
        val nowTime = MyTimeUtil.nowTimeString
        return " insert into arrtime  " +
                " values( " +
                " to_date('${CStoreCalendar.getCurrentDate(0)}','yyyy-mm-dd'), " +//营业日
                " '${User.getUser().storeId}','$uId', " +
                " to_date('$nowTime','yyyy-MM-dd HH24:MI:SS'), " +
                " '${User.getUser().uId}', " +
                " to_date('$nowTime','yyyy-MM-dd HH24:MI:SS'))"
    }

    /**********************************************${MyApplication.instance().getString(R.string.tui)}货************************************************************/

    /**
     * 获得${MyApplication.instance().getString(R.string.tui)}货原因
     */
    val getReason: String
        get() {
            return "select reasonNumber, reasonName from reason where storeID='${User.getUser().storeId}' and reasonCategory='01' order by reasonNumber\u0004"
        }

    /**
     * 获得${MyApplication.instance().getString(R.string.tui)}货${MyApplication.instance().getString(R.string.pei)}送商
     */
    val getReturnVendor: String
        get() {
            return "select ven.vendorID,ven.vendorName || ven.vendorID vendorNames " +
                    "from vendor ven,plu b " +
                    "where ven.vendortype='Y' " +
                    "and ven.storeid=b.storeid " +
                    "and ven.vendorid=b.vendorid " +
                    "and b.returntype='Y' " +
                    "and b.status<='8' " +
                    "and nvl(b.orderday1,'X')||NVL(b.orderday2,'X')||NVL(b.orderday3,'X')||NVL(b.orderday4,'X')|| " +
                    "nvl(b.orderday5,'X')||NVL(b.orderday6,'X')||NVL(b.orderday7,'X')<>'XXXXXXX' " +
                    "GROUP BY ven.vendorID, ven.vendorName || ven.vendorID " +
                    "order by ven.vendorID desc\u0004"
        }

    /**
     * 根据${MyApplication.instance().getString(R.string.pei)}送商得到近期商品
     */
    fun getRecentlyCommodity(rpb: ReturnedPurchaseBean?, vendorId: String): String {
        var sql: String
        if (rpb != null) {
            sql = "SELECT A.STOREID,A.ITEMNUMBER,A.PLUNAME,A.STOREUNITPRICE,A.UNITCOST,A.VENDORID,A.SUPPLIERID,A.SELL_COST,A.RETURN_ATTR, " +
                    "round(A.SELL_COST*(1+nvl(E.taxRate,0)),6) tax_sell_cost, " +
                    "NVL(B.FACEQUANTITY,0) FACEQUANTITY,DECODE(NVL(B.FACEQUANTITY,0),0,9999,DECODE(SUBSTR(A.STOREID,1,1),'S',9999,DECODE(A.RETURN_ATTR,'2',0,'3',0,9999))) CHECK_FACEQTY, " +
                    "NVL(C.END_QTY,0) END_QTY, " +
                    "NVL(D.RTN_QUANTITY_ALLOWED,9999) RTN_QUANTITY_ALLOWED " +
                    "FROM PLU A,ITEMFACEQTY B,TODAY_INV C,HQ_RTN_CTL D, TAXTYPE E " +
                    "WHERE TRUNC(SYSDATE) BETWEEN A.RETURNBEGINDATE-1 AND A.RETURNENDDATE-1 " +
                    "and A.RETURNBEGINDATE>SYSDATE-8 " +
                    "AND A.RETURNENDDATE<SYSDATE+7     " +
                    "AND A.VENDORID='$vendorId' " +
                    "AND A.RETURNTYPE='Y' " +
                    "AND A.STOREID = B.STOREID(+) " +
                    "AND A.ITEMNUMBER  = B.ITEMNUMBER(+)    " +
                    "AND A.STOREID = D.STOREID(+) " +
                    "AND A.ITEMNUMBER = D.ITEMNUMBER(+)    " +
                    "AND A.STOREID = C.STOREID(+) " +
                    "AND A.ITEMNUMBER = C.ITEMNUMBER(+) " +
                    "AND A.STOREID = E.STOREID(+) " +
                    "AND A.TAXID = E.TAXID(+) " +
                    "AND A.ITEMNUMBER not in("
            rpb.allItem.forEach { sql += "'${it.itemNumber}'," }
            sql = sql.substring(0, sql.length - 1)
            sql += ") ORDER BY A.ITEMNUMBER\u0004"
        } else {
            sql = "SELECT A.STOREID,A.ITEMNUMBER,A.PLUNAME,A.STOREUNITPRICE,A.UNITCOST,A.VENDORID,A.SUPPLIERID,A.SELL_COST,A.RETURN_ATTR, " +
                    "round(A.SELL_COST*(1+nvl(E.taxRate,0)),6) tax_sell_cost, " +
                    "NVL(B.FACEQUANTITY,0) FACEQUANTITY,DECODE(NVL(B.FACEQUANTITY,0),0,9999,DECODE(SUBSTR(A.STOREID,1,1),'S',9999,DECODE(A.RETURN_ATTR,'2',0,'3',0,9999))) CHECK_FACEQTY, " +
                    "NVL(C.END_QTY,0) END_QTY, " +
                    "NVL(D.RTN_QUANTITY_ALLOWED,9999) RTN_QUANTITY_ALLOWED " +
                    "FROM PLU A,ITEMFACEQTY B,TODAY_INV C,HQ_RTN_CTL D, TAXTYPE E " +
                    "WHERE TRUNC(SYSDATE) BETWEEN A.RETURNBEGINDATE-1 AND A.RETURNENDDATE-1 " +
                    "and A.RETURNBEGINDATE>SYSDATE-8 " +
                    "AND A.RETURNENDDATE<SYSDATE+7     " +
                    "AND A.VENDORID='$vendorId' " +
                    "AND A.RETURNTYPE='Y' " +
                    "AND A.STOREID = B.STOREID(+) " +
                    "AND A.ITEMNUMBER  = B.ITEMNUMBER(+)    " +
                    "AND A.STOREID = D.STOREID(+) " +
                    "AND A.ITEMNUMBER = D.ITEMNUMBER(+)    " +
                    "AND A.STOREID = C.STOREID(+) " +
                    "AND A.ITEMNUMBER = C.ITEMNUMBER(+) " +
                    "AND A.STOREID = E.STOREID(+) " +
                    "AND A.TAXID = E.TAXID(+) " +
                    "ORDER BY A.ITEMNUMBER \u0004"
        }
        return sql
    }

    /**
     * 根据${MyApplication.instance().getString(R.string.pei)}送商得到长期商品
     */
    fun getLongCommodity(rpb: ReturnedPurchaseBean?, vendorId: String): String {
        var sql = ""
        if (rpb != null) {
            sql += "SELECT A.RETURNENDDATE,A.STOREID,A.ITEMNUMBER,A.PLUNAME,A.STOREUNITPRICE,A.UNITCOST,A.VENDORID,A.SUPPLIERID,A.SELL_COST,A.RETURN_ATTR, " +
                    "ROUND(A.SELL_COST*(1+nvl(E.taxRate,0)),6) tax_sell_cost, " +
                    "NVL(B.FACEQUANTITY,0) FACEQUANTITY,DECODE(NVL(B.FACEQUANTITY,0),0,9999,DECODE(SUBSTR(A.STOREID,1,1),'S',9999,DECODE(A.RETURN_ATTR,'2',0,'3',0,9999))) CHECK_FACEQTY, " +
                    "NVL(C.END_QTY,0) END_QTY, " +
                    "NVL(D.RTN_QUANTITY_ALLOWED,9999) RTN_QUANTITY_ALLOWED " +
                    "FROM PLU A,ITEMFACEQTY B,TODAY_INV C,HQ_RTN_CTL D, TAXTYPE E " +
                    "WHERE TRUNC(SYSDATE) BETWEEN A.RETURNBEGINDATE-1 AND A.RETURNENDDATE-1 " +
                    "AND A.RETURNENDDATE>SYSDATE+7     " +
                    "AND A.VENDORID='$vendorId' " +
                    "AND A.RETURNTYPE='Y' " +
                    "AND NVL(B.FACEQUANTITY,0)=0 " +
                    "AND NVL(C.END_QTY,0)>0 " +
                    "AND A.STOREID = B.STOREID(+) " +
                    "AND A.ITEMNUMBER  = B.ITEMNUMBER(+)    " +
                    "AND A.STOREID = D.STOREID(+) " +
                    "AND A.ITEMNUMBER = D.ITEMNUMBER(+)    " +
                    "AND A.STOREID = C.STOREID(+) " +
                    "AND A.ITEMNUMBER = C.ITEMNUMBER(+) " +
                    "AND A.STOREID = E.STOREID(+) " +
                    "AND A.TAXID = E.TAXID(+) " +
                    "AND A.ITEMNUMBER not in("
            rpb.allItem.forEach { sql += "'${it.itemNumber}'," }
            sql = sql.substring(0, sql.length - 1)
            sql += ") ORDER BY A.ITEMNUMBER\u0004"
        } else {
            sql += "SELECT A.RETURNENDDATE,A.STOREID,A.ITEMNUMBER,A.PLUNAME,A.STOREUNITPRICE,A.UNITCOST,A.VENDORID,A.SUPPLIERID,A.SELL_COST,A.RETURN_ATTR, " +
                    "ROUND(A.SELL_COST*(1+nvl(E.taxRate,0)),6) tax_sell_cost, " +
                    "NVL(B.FACEQUANTITY,0) FACEQUANTITY,DECODE(NVL(B.FACEQUANTITY,0),0,9999,DECODE(SUBSTR(A.STOREID,1,1),'S',9999,DECODE(A.RETURN_ATTR,'2',0,'3',0,9999))) CHECK_FACEQTY, " +
                    "NVL(C.END_QTY,0) END_QTY, " +
                    "NVL(D.RTN_QUANTITY_ALLOWED,9999) RTN_QUANTITY_ALLOWED " +
                    "FROM PLU A,ITEMFACEQTY B,TODAY_INV C,HQ_RTN_CTL D, TAXTYPE E " +
                    "WHERE TRUNC(SYSDATE) BETWEEN A.RETURNBEGINDATE-1 AND A.RETURNENDDATE-1 " +
                    "AND A.RETURNENDDATE>SYSDATE+7     " +
                    "AND A.VENDORID='$vendorId' " +
                    "AND A.RETURNTYPE='Y' " +
                    "AND NVL(B.FACEQUANTITY,0)=0 " +
                    "AND NVL(C.END_QTY,0)>0 " +
                    "AND A.STOREID = B.STOREID(+) " +
                    "AND A.ITEMNUMBER  = B.ITEMNUMBER(+)    " +
                    "AND A.STOREID = D.STOREID(+) " +
                    "AND A.ITEMNUMBER = D.ITEMNUMBER(+)    " +
                    "AND A.STOREID = C.STOREID(+) " +
                    "AND A.ITEMNUMBER = C.ITEMNUMBER(+) " +
                    "AND A.STOREID = E.STOREID(+) " +
                    "AND A.TAXID = E.TAXID(+) " +
                    "ORDER BY A.ITEMNUMBER\u0004"
        }
        return sql
    }

    /**
     * 获得${MyApplication.instance().getString(R.string.tui)}货单
     */
    fun getReturnPurchase(date: String): String {
        return "select pln.RequestNumber, pln.PlnRtnDate RtnDate,vendor.vendorName, " +
                "sum(pln.plnRtnQuantity)RtnQuantity,sum(pln.plnRtnQuantity*pln.StoreUnitPrice) Total,pln.vendorID , " +
                "0.0+count(*) itempln " +
                "from plnrtn pln, vendor " +
                "where pln.storeID='${User.getUser().storeId}'  " +
                "and vendor.storeid=pln.storeID  " +
                "and pln.plnRtnDate0=to_date('$date','yyyy-MM-dd') " +
                "and pln.vendorID=vendor.vendorID " +
                "group by pln.RequestNumber, pln.PlnRtnDate,vendor.vendorName,pln.vendorID\u0004"
    }

    /**
     * 获得${MyApplication.instance().getString(R.string.tui)}货单下的商品
     */
    fun getReturnPurchaseItem(date: String, vendorId: String, requestNumber: String): String {
        return "select pln.recordNumber, pln.requestNumber, pln.itemNumber,nvl(pln.storeunitprice,0)*nvl(pln.plnrtnquantity,0) Total,pln.StoreUnitprice,pln.UnitCost,pln.plnRtnUnitQuantity, pln.plnRtnQuantity, pln.plnRtnDate, pln.vendorID, pln.supplierID, " +
                "plu.PluName, unit.UnitName, " +
                "nvl(inv.befInvQuantity,0)+nvl(inv.accDlvQuantity,0)-nvl(inv.accRtnQuantity,0)-nvl(inv.accSaleQuantity,0) " +
                "+nvl(inv.accSaleRtnQuantity,0)-nvl(inv.accMrkQuantity,0)+nvl(inv.accCshDlvQuantity,0)-nvl(inv.accCshRtnQuantity,0) " +
                "+nvl(inv.accTrsQuantity,0)+nvl(inv.accLeibianQuantity,0)+nvl(inv.accAdjQuantity,0)+nvl " +
                "(inv.accHqAdjQuantity,0) as InvQuantity, " +
                "ReasonNumber ,plu.shipNumber,'FromDB' as data_status,pln.sell_cost*(1+nvl(taxtype.taxRate,0)) tax_sell_cost,pln.sell_cost, " +
                "nvl(to_char(decode(sign(rtn_quantity_allowed),-1,0,rtn_quantity_allowed)),-1) lsjjh " +
                "from plnrtn pln " +
                "left join plu  on  plu.storeID=pln.storeid and plu.Itemnumber=pln.ItemNumber " +
                "left join unit  on unit.storeid=plu.storeid and unit.UnitID=plu.SmallunitID " +
                "left join taxtype on taxtype.storeID=plu.storeID AND taxtype.taxID=plu.taxID " +
                "left join inv on inv.itemnumber=pln.itemnumber and inv.storeid=pln.storeid and inv.busidate=to_date('$date','yyyy-MM-dd') " +
                " left join hq_rtn_ctl h on h.itemnumber=plu.itemnumber " +
                "where pln.storeID='${User.getUser().storeId}' " +
                "and pln.vendorID='$vendorId' " +
                "and pln.plnRtnDate0=to_date('$date','yyyy-MM-dd') " +
                "and pln.requestNumber='$requestNumber'\u0004"
    }

    /**
     * 创建${MyApplication.instance().getString(R.string.tui)}货商品,${MyApplication.instance().getString(R.string.pei)}合事务使用
     */
    fun createReturnPurchase(rpb: ReturnPurchaseItemBean): String {
        return "insert into plnrtn  " +
                "(storeID, plnRtnDate0, recordNumber, plnRtnType, requestNumber, plnRtnStore, itemNumber, shipNumber,storeUnitPrice, " +
                "unitCost, plnRtnUnitQuantity, plnRtnQuantity, plnRtnDate, vendorID, supplierID, plnRtnStatus, reasonNumber,updateUserID, updateDate, sell_cost) " +
                "values " +
                "('${User.getUser().storeId}',to_date('${rpb.plnRtnDate0}','yyyy-MM-dd'),'${rpb.recordNumber}','0','${rpb.requestNumber}','${User.getUser().storeId}','${rpb.itemNumber}', 0,${rpb.storeUnitPrice}, " +
                "${rpb.unitCost},${rpb.plnRtnUnitQuantity},${rpb.plnRtnQuantity},to_date('${rpb.plnRtnDate0}','yyyy-MM-dd'),'${rpb.vendorId}','${rpb.supplierId}','1','${rpb.reasonNumber}','${User.getUser().uId}',sysdate,${rpb.sellCost});"
    }

    /**
     * 更新${MyApplication.instance().getString(R.string.tui)}货商品，${MyApplication.instance().getString(R.string.pei)}合事务使用
     */
    fun updateReturnPurchase(rpb: ReturnPurchaseItemBean): String {
        return "Update plnrtn " +
                "set plnRtnUnitQuantity=${rpb.plnRtnUnitQuantity}, " +
                "plnRtnQuantity=${rpb.plnRtnQuantity}, " +
                "reasonNumber='${rpb.reasonNumber}', " +
                "UpdateUserID='${User.getUser().uId}', " +
                "UpdateDate=sysdate " +
                "where storeId='${User.getUser().storeId}' " +
                "and plnRtnDate0=to_date('${rpb.plnRtnDate0}','yyyy-MM-dd') " +
                "and VendorID='${rpb.vendorId}' " +
                "and RequestNumber='${rpb.requestNumber}' " +
                "and itemNumber='${rpb.itemNumber}' " +
                "and shipNumber='${rpb.shipNumber}' " +
                "and recordNumber='${rpb.recordNumber}';"
    }

    /**
     * 检测这个预约日是否已有此商品
     */
    fun judgmentReturnPurchase(aib: ArrayList<ReturnPurchaseItemBean>): String {
        //因为来检测的必定是同一${MyApplication.instance().getString(R.string.pei)}送商的，因此拿第一个的预约时间就好了
        if (aib.isEmpty()) throw Exception("method judgmentReturnPurchase aib list's item is null")
        var sql = "select *  " +
                "from plnrtn  " +
                "where plnrtndate0=to_date('${aib[0].plnRtnDate0}','yyyy-MM-dd')  " +
                "and itemnumber in ("
        //再插入品号查询
        aib.forEach { sql += "${it.itemNumber}," }
        sql = sql.substring(0, sql.length - 1)
        sql += ")\u0004"
        return sql
    }

    /**
     * 得到今日最大的requestNumber
     */
    fun getMaxReturnPurchaseId(date: String, num: String): String {
        return "select max(requestNumber) value from plnrtn where plnrtndate0=to_date('$date','yyyy-MM-dd') and requestNumber like '$num%'\u0004"
    }

    /**
     * 得到${MyApplication.instance().getString(R.string.tui)}货商品的最大排序id
     */
    fun getMaxRecordNumber(date: String): String {
        return "select max(recordNumber) value from plnrtn where plnrtndate0=to_date('$date','yyyy-MM-dd')\u0004"
    }


    /**********************************************过期品${MyApplication.instance().getString(R.string.tui)}货*******************************************************/

    /**
     * 得到今天所有的过期${MyApplication.instance().getString(R.string.tui)}货商品
     */
    fun expiredReturnGetAll(): String {
        val context = MyApplication.instance().applicationContext
        return "SELECT pln.plnRtnDate,pln.itemNumber,pln.itemNumber as itemnumber2 ,plu.PluName,pln.vendorID,vendor.vendorName,pln.supplierID,  " +
                "decode( plnRtnType,'1','${context.getString(R.string.return_sql12)}','2','${context.getString(R.string.return_sql13)}','3','${context.getString(R.string.return_sql14)}','4','${context.getString(R.string.return_sql15)}','5','${context.getString(R.string.return_sql16)}','${context.getString(R.string.return_sql17)}' )  return_attr,  " +
                "pln.plnRtnQuantity,pln.StoreUnitprice,pln.UnitCost basic_cost,pln.sell_cost,unit.UnitName,  " +
                "decode(ReasonNumber,'01','01 ${context.getString(R.string.return_sql01)}','02','02 ${context.getString(R.string.return_sql02)}','03','03 ${context.getString(R.string.return_sql03)}','04','04 ${context.getString(R.string.return_sql04)}',  " +
                "'05','05 ${context.getString(R.string.return_sql05)}','06','06 ${context.getString(R.string.return_sql06)}','07','07 ${context.getString(R.string.return_sql07)}','08',  " +
                "'08 ${context.getString(R.string.return_sql08)}','09','09 ${context.getString(R.string.return_sql09)}','10','10 ${context.getString(R.string.return_sql10)}',  " +
                "'00 ${context.getString(R.string.return_sql11)}') ReasonNumber,  " +
                "pln.sell_cost*(1+nvl(taxtype.taxRate,0)) tax_sell_cost,  " +
                "nvl((befInvQuantity+accDlvQuantity-  " +
                "accRtnQuantity-accSaleQuantity+  " +
                "accSaleRtnQuantity-accMrkQuantity+  " +
                "accCshDlvQuantity-accCshRtnQuantity+  " +
                "accTrsQuantity+accLeibianQuantity+  " +
                "accAdjQuantity+accHqAdjQuantity),0) stock_qty,  " +
                "sb053.Dlv_qty dlv_qty,  " +
                "sb053.Sale_qty sale_qty,  " +
                "sb053.rtn_qty rtn_qty,  " +
                "sb053.hq_ordqty hq_ordqty,  " +
                "'FromDB' as data_status ,  " +
                "decode(plu.vendorid,'00000000000099999100',nvl(min(sb021.RtnDate),to_date('${CStoreCalendar.getCurrentDate(2)}','yyyy-MM-dd')),to_date('${CStoreCalendar.getCurrentDate(2)}','yyyy-MM-dd'))RtnDate,  " +
                "decode(plu.vendorid,'00000000000099999100','Y','N') check_yn  " +
                "FROM plnrtn1 pln  " +
                "left join plu  on  plu.storeID=pln.storeid and plu.Itemnumber=pln.ItemNumber  " +
                "left join unit  on unit.storeid=plu.storeid and unit.UnitID=plu.SmallunitID  " +
                "left join taxtype on taxtype.storeID=plu.storeID AND taxtype.taxID=plu.taxID  " +
                "left join sb053 on sb053.StoreID=pln.storeID AND sb053.Item_No=pln.itemNumber  " +
                "left join inv on inv.storeID=pln.storeID AND inv.itemNumber=pln.itemNumber AND inv.busiDate= to_date('${CStoreCalendar.getCurrentDate(0)}','yyyy-MM-dd')  " +
                "left join sb021 on sb021.storeID=pln.storeID AND sb021.RtnDate>= to_date('${CStoreCalendar.getCurrentDate(2)}','yyyy-MM-dd')  " +
                "AND sb021.RtnType=pln.plnrtntype ,vendor  " +
                "WHERE pln.storeID='${User.getUser().storeId}'  " +
                "AND pln.storeID=vendor.storeID  " +
                "AND pln.vendorID=vendor.vendorID  " +
                "and pln.plnRtnStatus='0'  " +
                "GROUP BY pln.plnRtnDate,pln.itemNumber,plu.PluName,pln.vendorID,vendor.vendorName,pln.supplierID,  " +
                "plnRtnType,pln.plnRtnQuantity,pln.StoreUnitprice,  plu.vendorid,  " +
                "pln.UnitCost,pln.sell_cost,  " +
                "unit.UnitName,pln.ReasonNumber,  " +
                "taxtype.taxRate,  " +
                "(befInvQuantity+accDlvQuantity-  " +
                "accRtnQuantity-accSaleQuantity+  " +
                "accSaleRtnQuantity-accMrkQuantity+  " +
                "accCshDlvQuantity-accCshRtnQuantity+  " +
                "accTrsQuantity+accLeibianQuantity+  " +
                "accAdjQuantity+accHqAdjQuantity),  " +
                "sb053.Dlv_qty ,  " +
                "sb053.Sale_qty ,  " +
                "sb053.rtn_qty ,  " +
                "sb053.hq_ordqty  " +
                "order by itemnumber\u0004"
    }

    /**
     * 得到商品过期品${MyApplication.instance().getString(R.string.tui)}货的商品SQL语句，使用时间为订货换日，其中一个是营业换日
     * @param data 用来搜索的品号或条形码
     */
    fun expiredReturnGetCommodity(data: String): String {
        val context = MyApplication.instance().applicationContext
        return "SELECT plu.itemNumber,plu.PluName,plu.vendorID,vendor.vendorName,plu.itemNumber as itemnumber2, " +
                "decode( plu.return_attr,'1','${context.getString(R.string.return_sql12)}','2','${context.getString(R.string.return_sql13)}','3','${context.getString(R.string.return_sql14)}','4','${context.getString(R.string.return_sql15)}','5','${context.getString(R.string.return_sql16)}','${context.getString(R.string.return_sql17)}' )  return_attr, " +
                "nvl(inv.befInvQuantity,0)+nvl(inv.accDlvQuantity,0)-nvl(inv.accRtnQuantity,0)-nvl(inv.accSaleQuantity,0) " +
                "+nvl(inv.accSaleRtnQuantity,0)-nvl(inv.accMrkQuantity,0)+nvl(inv.accCshDlvQuantity,0)-nvl(inv.accCshRtnQuantity,0) " +
                "+nvl(inv.accTrsQuantity,0)+nvl(inv.accLeibianQuantity,0)+nvl(inv.accAdjQuantity,0)+nvl " +
                "(inv.accHqAdjQuantity,0) as stock_qty, " +
                "sb053.Dlv_qty dlv_qty, " +
                "sb053.Sale_qty sale_qty, " +
                "sb053.rtn_qty rtn_qty, " +
                "sb053.hq_ordqty hq_ordqty, " +
                "plu.StoreUnitprice,plu.basic_cost,plu.sell_cost, " +
                "unit.UnitName,plu.shipNumber,plu.sell_cost*(1+nvl(taxtype.taxRate,0)) tax_sell_cost, " +
                "plu.returnType,plu.ReturnBeginDate,plu.ReturnEndDate,plu.supplierID, " +
                "decode(plu.vendorid,'00000000000099999100',nvl(min(sb021.RtnDate),to_date('${CStoreCalendar.getCurrentDate(2)}','yyyy-MM-dd')),to_date('${CStoreCalendar.getCurrentDate(2)}','yyyy-MM-dd'))RtnDate, " +
                "decode(plu.vendorid,'00000000000099999100','Y','N') check_yn,Substr(Plu.Signtype, 12, 1) vendor_yn,plu.Stop_Th_Code,plu.Out_Th_Code " +
                "FROM plu " +
                "left join unit  on unit.storeid=plu.storeid and unit.UnitID=plu.SmallunitID " +
                "left join taxtype on taxtype.storeID=plu.storeID AND taxtype.taxID=plu.taxID " +
                "left join sb053 on sb053.StoreID=plu.storeID AND sb053.Item_No=plu.itemNumber " +
                "left join inv on inv.storeID=plu.storeID AND inv.itemNumber=plu.itemNumber AND inv.busiDate=to_date('${CStoreCalendar.getCurrentDate(0)}','yyyy-MM-dd') " +
                "left join sb021 on sb021.storeID=plu.storeID AND sb021.RtnDate>=to_date('${CStoreCalendar.getCurrentDate(2)}','yyyy-MM-dd') AND sb021.RtnType=plu.return_attr " +
                ",vendor,ITEMPLU " +
                "WHERE PLU.ITEMNUMBER=ITEMPLU.ITEMNUMBER " +
                "and plu.StoreID='${User.getUser().storeId}' " +
                "AND plu.ItemNumber =decode(length('$data'),6,'$data', " +
                "(select t.itemnumber from ITEMPLU t " +
                "where t.plunumber='$data')) " +
                "AND plu.storeID=vendor.storeID " +
                "AND plu.vendorID=vendor.vendorID " +
                "GROUP BY plu.itemNumber,plu.PluName,plu.return_attr,plu.vendorID,vendor.vendorName,plu.StoreUnitprice,plu.basic_cost , " +
                "unit.UnitName,plu.shipNumber,plu.sell_cost,taxtype.taxRate,plu.returnType,plu.ReturnBeginDate, " +
                "plu.ReturnEndDate,plu.supplierID,sb053.Dlv_qty, " +
                "sb053.Sale_qty, " +
                "sb053.rtn_qty, " +
                "sb053.hq_ordqty, " +
                "nvl(inv.befInvQuantity,0)+nvl(inv.accDlvQuantity,0)-nvl(inv.accRtnQuantity,0)-nvl(inv.accSaleQuantity,0) " +
                "+nvl(inv.accSaleRtnQuantity,0)-nvl(inv.accMrkQuantity,0)+nvl(inv.accCshDlvQuantity,0)-nvl(inv.accCshRtnQuantity,0) " +
                "+nvl(inv.accTrsQuantity,0)+nvl(inv.accLeibianQuantity,0)+nvl(inv.accAdjQuantity,0)+nvl " +
                "(inv.accHqAdjQuantity,0),Substr(Plu.Signtype, 12, 1),plu.Stop_Th_Code,plu.Out_Th_Code\u0004"
    }

    /**
     * 根据时间范围和选择的状态获得数据
     */
    fun getDateExpiredReturnAll(date1: String, date2: String, checkIndex: Int): String {
        val context = MyApplication.instance().applicationContext
        var result = "SELECT Pln.Plnrtndate, Pln.Itemnumber, Pln.Storeunitprice, " +
                "Pln.Plnrtnquantity, Pln.Storeunitprice * Pln.Plnrtnquantity Price_Amt,vendor.vendorName, plu.pluname, " +
                "decode( pln.plnRtnStatus,'1','${context.getString(R.string.check2)}','2','${context.getString(R.string.check3)}','3',Decode(Sign(5 - (Sysdate - Pln.Updatedate)), 1," +
                " '${context.getString(R.string.check4)}', '${context.getString(R.string.return_sql1)}'),'9','${context.getString(R.string.check5)}' )  plnRtnStatus2, " +
                "decode(ReasonNumber,'01','01 ${context.getString(R.string.return_sql01)}','02','02 ${context.getString(R.string.return_sql02)}','03','03 ${context.getString(R.string.return_sql03)}','04','04 ${context.getString(R.string.return_sql04)}', " +
                "'05','05 ${context.getString(R.string.return_sql05)}','06','06 ${context.getString(R.string.return_sql06)}','07','07 ${context.getString(R.string.return_sql07)}','08', " +
                "'08 ${context.getString(R.string.return_sql08)}','09','09 ${context.getString(R.string.return_sql09)}','10','10 ${context.getString(R.string.return_sql10)}', " +
                "'00 ${context.getString(R.string.return_sql11)}') ReasonNumber " +
                "FROM plnrtn1 pln " +
                "left join plu  on  plu.storeID=pln.storeid and plu.Itemnumber=pln.ItemNumber " +
                ",vendor " +
                "WHERE pln.storeID='${User.getUser().storeId}' "
        when (checkIndex) {
            0 -> result += "AND pln.plnrtndate between to_date('$date1','yyyy-mm-dd') and to_date('$date2','yyyy-mm-dd') "
            1 -> result += "AND pln.plnrtndate between to_date('$date1','yyyy-mm-dd') and to_date('$date2','yyyy-mm-dd') and pln.plnRtnStatus='1' "
            2 -> result += "AND pln.plnrtndate between to_date('$date1','yyyy-mm-dd') and to_date('$date2','yyyy-mm-dd') and pln.plnRtnStatus='2' "
            3 -> result += "AND pln.plnrtndate between to_date('$date1','yyyy-mm-dd') and to_date('$date2','yyyy-mm-dd') and pln.plnRtnStatus='9' "
            4 -> result += "AND pln.Updatedate > Sysdate - 5 and pln.plnRtnStatus='3' "
        }
        result += "AND pln.storeID=vendor.storeID " +
                "AND pln.vendorID=vendor.vendorID " +
                "and pln.plnRtnStatus in ('1','2','3','9') " +
                "order by pln.plnrtndate desc\u0004"
        return result
    }

    /**
     * 保存过期品预约${MyApplication.instance().getString(R.string.tui)}货
     */
    fun saveExpiredReturn(data: ReturnExpiredBean): String {
        val date = deleteTime(data.plnRtnDate)
        return "call rtn_expired_01('${data.itemNumber}','${User.getUser().uId}',${data.plnRtnQTY}, to_date('$date','yyyy-mm-dd'))\u000c"
    }

    /**********************************************收款************************************************************/

    /**
     * 创建shopping_basket_temp表的内容
     */
    fun insertShoppingBasket(barCode: String, quantity: Int): String {
        return "insert into shopping_basket_temp (tel_seq, bar_code,quantity) values ('${MyApplication.getOnlyid()}', '$barCode',$quantity)\u000c"
    }

    /**
     * 先执行insertShoppingBasket再执行callShoppingBasket再getShoppingBasket获得数据
     */
    fun callShoppingBasket(): String {
        return "call ref_Shopping_basket('${MyApplication.getOnlyid()}')\u000c"
    }

    /**
     * 获得商品
     */
    fun getShoppingBasket(barCode: String): String {
        return "select * from shopping_basket where bar_code='$barCode' and tel_seq='${MyApplication.getOnlyid()}'\u0004"
    }

    /**
     * 存储过程用于得到下单单号
     */
    fun callPayShopping(): String {
        return "call pay_Shopping_basket_p01('${MyApplication.getOnlyid()}')\u000c"
    }

    fun updateAss2(): String {
        return "update app_pos set next_tranno=next_tranno+1 where tel_seq='${MyApplication.getOnlyid()}' and tran_date=trunc(sysdate)\u000c"
    }

    /**
     * 得到下单单号
     */
    fun getShoppingId(): String {
        return "select storeid,ass_pos,next_tranno  " +
                "from APP_POS a " +
                "where a.storeid = '${User.getUser().storeId}' " +
                "and a.tran_date=trunc(sysdate) " +
                "and a.tel_seq='${MyApplication.getOnlyid()}'\u0004"
    }

    /**
     * 搜索商品
     */
    fun searchPlunumber(data: String): String {
        return "select plunumber value from itemplu where (plunumber='$data' or itemnumber='$data')"
    }

    /**
     * 收款完毕更新posul_trandtl的存储过程
     */
    fun payDoneCall(isWhere: String, amt: Double): String {
        return "call pay_Shopping_basket_p02('${MyApplication.getOnlyid()}','$isWhere','$amt') \u000c"
    }

    /**
     * 收款完毕或${MyApplication.instance().getString(R.string.tui)}款完毕更新到posul_weixin_detail的sql语句
     * @param isRefund 判断是否是${MyApplication.instance().getString(R.string.tui)}款的布尔
     */
    fun createWXPayDone(posBean: PosBean, payData: Map<String, String>, isRefund: Boolean): String {
        return try {
            val storeId = User.getUser().storeId
            val assPos = posBean.assPos
            val nextTranNo = posBean.nextTranNo
            var totalFee = payData["total_fee"]!!.toDouble() / 100
            val transactionId = payData["transaction_id"]
            var seq = payData["seq"]
            if (seq == null) {
                seq = "01"
            }
            val openId = payData["openid"]
            var couponFee = 0.0
            if (payData["coupon_fee"] != null) {
                couponFee = payData["coupon_fee"]!!.toDouble() / 100
            }
            val nonCoupondFee = totalFee - couponFee
            val result = if (isRefund) {
                //${MyApplication.instance().getString(R.string.tui)}款记录
                val refundId = payData["refund_id"]
                //${MyApplication.instance().getString(R.string.tui)}款需要负数
                totalFee *= -1
                "insert into posul_weixin_detail " +
                        "(storenumber, posnumber, transactionnumber, total_fee, transaction_id, systemdate, seq, REFUND_ID, openid, coupon_fee, non_coupond_fee) " +
                        "values " +
                        "('$storeId', '$assPos', $nextTranNo, $totalFee, '$transactionId', sysdate, '$seq', '$refundId', '$openId', $couponFee, $nonCoupondFee) \u000c"
            } else {
                //收款记录
                "insert into posul_weixin_detail " +
                        "(storenumber, posnumber, transactionnumber, total_fee, transaction_id, systemdate, seq, openid, coupon_fee, non_coupond_fee) " +
                        "values " +
                        "('$storeId', '$assPos', $nextTranNo, $totalFee, '$transactionId', sysdate, '$seq', '$openId', $couponFee, $nonCoupondFee) \u0004"
            }

            result
        } catch (e: Exception) {
            e.message.toString()
        }
    }

    /**
     * 处理异常2步骤更新到posul_weixin_detail的sql语句
     */
    fun createWXPayDone(bean: WXPaySqlBean): String {
        return "insert into posul_weixin_detail " +
                "(storenumber, posnumber, transactionnumber, total_fee, transaction_id, systemdate, seq, openid, coupon_fee, non_coupond_fee) " +
                "values " +
                "('${bean.storeId}', '${bean.assPos}', ${bean.nextTranNo}, ${bean.totalFee}, '${bean.transactionId}', sysdate, '${bean.seq}', '${bean.openId}', ${bean.couponFee}, ${bean.totalFee - bean.couponFee}) \u0004"
    }

    fun createAliPayDone(bean: ALIPaySqlBean): String {
        return "insert into posul_alipay_detail " +
                "(storenumber, posnumber, transactionnumber, seq, trade_no, buyer_logon_id, total_fee, systemdate, fund_channel) " +
                "values " +
                "('${bean.storeId}','${bean.assPos}', '${bean.nextTranNo}', '${bean.seq}', '${bean.tradeNo}', '${bean.buyerLogonId}', ${bean.totalFee}, sysdate, 'zz')\u0004"
    }

    fun createAliPayDone(pos: PosBean, payData: AlipayTradePayResponse, isRefund: Boolean): String {
        return try {
            val storeId = User.getUser().storeId
            val nextTranNo = pos.nextTranNo
            var totalFee = payData.totalAmount.toDouble()
            val tradeNo = payData.tradeNo
            val seq = "01"
            val buyId = payData.buyerLogonId
            if (isRefund) {
                totalFee *= -1
            }
            "insert into posul_alipay_detail " +
                    "(storenumber, posnumber, transactionnumber, seq, trade_no, buyer_logon_id, total_fee, systemdate, fund_channel) " +
                    "values " +
                    "('$storeId','${pos.assPos}', '$nextTranNo', '$seq', '$tradeNo', '$buyId', $totalFee, sysdate, 'zz')\u0004"
        } catch (e: Exception) {
            e.message.toString()
        }
    }

    fun createAliPayDone(pos: PosBean, payData: AlipayTradeQueryResponse, isRefund: Boolean): String {
        return try {
            val storeId = User.getUser().storeId
            val nextTranNo = pos.nextTranNo
            var totalFee = payData.totalAmount.toDouble()
            val tradeNo = payData.tradeNo
            val seq = "01"
            val buyId = payData.buyerLogonId
            if (isRefund) {
                totalFee *= -1
            }
            "insert into posul_alipay_detail " +
                    "(storenumber, posnumber, transactionnumber, seq, trade_no, buyer_logon_id, total_fee, systemdate, fund_channel) " +
                    "values " +
                    "('$storeId', ,'${pos.assPos}', '$nextTranNo', '$seq', '$tradeNo', '$buyId', $totalFee, sysdate, 'zz')"
        } catch (e: Exception) {
            e.message.toString()
        }
    }

    /**
     * 扫描后取消交易，清空扫描的商品，此时需要把表里的数据也删掉
     */
    val deleteBasket: String
        get() {
            return "delete from shopping_basket where tel_seq='${MyApplication.getOnlyid()}'\u0004"
        }

    /**
     * 撤销订单后需要修改app_pos的next_tranno，不然再次提交订单微信会报错订单号重复
     * ${MyApplication.instance().getString(R.string.tui)}款同时需要进行此操作
     */
    val updateAppPos: String
        get() {
            return "update app_pos set next_tranno=next_tranno+1 where tel_seq='${MyApplication.getOnlyid()}' and tran_date=trunc(sysdate)\u0004"
        }

    /**
     * 撤销后执行的sql
     */
    val reverseCall: String
        get() {
            return "call reverse_shopping_basket_r01('${MyApplication.getOnlyid()}')\u0004"
        }

    /**
     * 像微信申请${MyApplication.instance().getString(R.string.tui)}款成功后执行的存储过程
     * @param pay_tranno 收款时的对方交易序号
     * @param refoundId 微信返回${MyApplication.instance().getString(R.string.tui)}款单号
     * @param isWhere 是在微信还是现金还是支付宝执行的${MyApplication.instance().getString(R.string.tui)}款
     */
    fun refoundCall(pay_tranno: String, refoundId: String, isWhere: String): String {
        return "call refound_Shopping_basket_r01('${MyApplication.getOnlyid()}',$pay_tranno,'$refoundId','$isWhere')\u0004"
    }

    /**********************************************商品调拨************************************************************/

    /**
     * 得到指定日期所有调出单
     */
    fun getTrs(date: String): String {
        return "SELECT trsid, " +
                "trs.busidate, " +
                "trsStoreID, " +
                "trsNumber, " +
                "sum(trsQuantity) trsQuantity, " +
                "0.0 + Count(*) trsItem, " +
                "trs.storeID, " +
                "sum(trsQuantity*trs.storeUnitPrice) storeUnitPrice, " +
                "round(sum(trsQuantity * trs.sell_cost * " +
                "(1 + nvl(taxtype.taxRate, 0))), " +
                "6) tax_sell_tot " +
                "FROM trs, plu " +
                "left join taxtype " +
                "on taxtype.storeID = plu.storeID " +
                "AND taxtype.taxID = plu.taxID " +
                "WHERE (trs.storeid = '${User.getUser().storeId}' AND " +
                "trs.busidate = to_date('$date', 'yyyy-mm-dd')) " +
                "AND (plu.ItemNumber = trs.ItemNumber AND plu.storeid = trs.storeid) " +
                "group by trs.storeID, " +
                "trsNumber, " +
                "trs.busidate, " +
                "trsid, " +
                "trs.storeUnitPrice, " +
                "trsStoreID " +
                "ORDER BY storeid, trs.busidate, trsid, trsNumber"
    }

    /**
     * 得到调出单下的商品
     */
    fun getTrsItems(date: String, trsNum: String): String {
        return "select trsid, " +
                "trs.trsStoreID, " +
                "trs.trsNumber, " +
                "trs.shipnumber, " +
                "trs.itemnumber, " +
                "trs.storeUnitPrice, " +
                "trs.storeunitPrice * trs.trsQuantity Total, " +
                "0.0 + trs.UnitCost UnitCost, " +
                "trs.trsQuantity, " +
                "trs.busidate, " +
                "trs.sell_cost, " +
                "'FromDB' dtl_data_status, " +
                "round(trs.sell_cost * (1 + nvl(taxtype.taxRate, 0)), 6) tax_sell_cost, " +
                "plu.pluName, " +
                "plu.vendorid, " +
                "plu.supplierid, " +
                "(nvl(i.befinvquantity, 0) + nvl(i.accDlvQuantity, 0) - " +
                "nvl(i.accRtnQuantity, 0) - nvl(i.accSaleQuantity, 0) + " +
                "nvl(i.accSaleRtnQuantity, 0) - nvl(i.accMrkQuantity, 0) + " +
                "nvl(i.accCshDlvQuantity, 0) - nvl(i.accCshRtnQuantity, 0) + " +
                "nvl(i.accTrsQuantity, 0) + nvl(i.accLeibianQuantity, 0) + " +
                "nvl(i.accAdjQuantity, 0) + nvl(i.accHqAdjQuantity, 0)) as inv_qty " +
                "FROM trs, plu " +
                "left join unit " +
                "on unit.storeid = plu.storeid " +
                "and unit.unitID = plu.SmallUnitID " +
                "left join taxtype " +
                "on taxtype.storeID = plu.storeID " +
                "AND taxtype.taxID = plu.taxID  " +
                "left join inv i " +
                "on i.storeID = plu.storeID " +
                "AND i.itemNumber = plu.itemNumber " +
                "AND i.busiDate = to_date('$date', 'yyyy-mm-dd') " +
                "WHERE (trs.storeid = '${User.getUser().storeId}' and " +
                "trs.busidate = to_date('$date', 'yyyy-mm-dd')) " +
                "AND (plu.ItemNumber = trs.ItemNumber) " +
                "and (plu.storeid = trs.storeid) " +
                "and trs.trsnumber = '$trsNum' " +
                " order by trs.itemnumber"
    }

    /**
     * 得到今天最大的TrsNumber
     */
    val getMaxTrsNumber: String
        get() {
            return "select Max(TrsNumber) value from trs\n" +
                    "where storeID='${User.getUser().storeId}' and  trsID='O' and\n" +
                    "trsnumber like '%${nowYear() + dayOfYear()}%'\u0004"
        }

    /**
     * 判断是否存在此单号
     */
    fun isExistTrs(trsNumber: String): String {
        return "select * from trs where trsnumber like '%$trsNumber%'\u0004"
    }

    /**
     * 得到商品
     */
    fun getTrsCommodity(data: String): String {
        return "select plu.itemNumber, " +
                "plu.pluName, " +
                "plu.storeUnitPrice, " +
                "plu.VendorID, " +
                "plu.SupplierID, " +
                "unit.UnitName, " +
                "plu.shipnumber, " +
                "substr(plu.SignType, 6, 1) SignType, " +
                "plu.basic_cost unitCost, " +
                "plu.sell_cost, " +
                "plu.sell_cost * (1 + nvl(taxtype.taxRate, 0)) tax_sell_cost, " +
                "plu.vendorid, " +
                "plu.supplierid, " +
                "plu.stocktype, " +
                "(nvl(i.befinvquantity, 0) + nvl(i.accDlvQuantity, 0) - " +
                "nvl(i.accRtnQuantity, 0) - nvl(i.accSaleQuantity, 0) + " +
                "nvl(i.accSaleRtnQuantity, 0) - nvl(i.accMrkQuantity, 0) + " +
                "nvl(i.accCshDlvQuantity, 0) - nvl(i.accCshRtnQuantity, 0) + " +
                "nvl(i.accTrsQuantity, 0) + nvl(i.accLeibianQuantity, 0) + " +
                "nvl(i.accAdjQuantity, 0) + nvl(i.accHqAdjQuantity, 0)) as inv_qty " +
                "from plu " +
                "left join unit " +
                "on unit.storeid = plu.storeid " +
                "and unit.unitID = plu.SmallUnitID " +
                "left join taxtype " +
                "on taxtype.storeID = plu.storeID " +
                "AND taxtype.taxID = plu.taxID " +
                "left join inv i " +
                "on i.storeID = plu.storeID " +
                "AND i.itemNumber = plu.itemNumber " +
                "AND i.busiDate = trunc(sysdate) " +
                "where plu.StoreID = '${User.getUser().storeId}' " +
                "and plu.ItemNumber = decode(length('$data'),6,'$data'," +
                "(select t.itemnumber from ITEMPLU t " +
                "where t.plunumber='$data'))\u0004"
    }

    /**
     * 创建调出单
     */
    fun createTrs(tib: TrsItemBean): String {
        return "Insert into trs (Storeid,busidate,trsID,trsNumber,itemnumber,shipnumber,storeUnitPrice,unitCost, " +
                "trsStoreID,trsQuantity,UpdateUserID,UpdateDateTime,trsTime,trsReasonNumber,sell_cost,vendorid,supplierid) " +
                "values " +
                "('${User.getUser().storeId}',to_date('${CStoreCalendar.getCurrentDate(0)}','yyyy-mm-dd'),'0','${tib.trsNumber}','${tib.pluId}','${tib.shipNumber}',${tib.storeUnitPrice},${tib.unitCost}," +
                "'${tib.trsStoreId}',${tib.trsQty},'${User.getUser().uId}',sysdate + 0.001,sysdate,'00',${tib.sellCost},'${tib.vendorId}','${tib.supplierId}')\u000c"
    }

    /**
     * 更新调出单
     */
    fun updateTrs(tib: TrsItemBean): String {
        return "update trs  " +
                "set trsquantity = ${tib.trsQty}  " +
                "where storeid = '${User.getUser().storeId}'  " +
                "and busidate = to_date('${CStoreCalendar.getCurrentDate(0)}', 'yyyy-mm-dd')  " +
                "and trsid = '0'  " +
                "and trsnumber = '${tib.trsNumber}'  " +
                "and itemnumber = '${tib.pluId}'\u000c"
    }

    /**
     * 查找店号
     */
    fun searchStore(storeId: String): String {
        return "select ostoreid,ostorename,DECODE(ostore_attr,'1','${MyApplication.instance().getString(R.string.zhi)}营','2','委托','3','特I','4','特II','5','2F') ostore_attr from ostore where ostoreid='$storeId' order by ostoreid"
    }

    val getTrsf: String
        get() {
            return "select trsnumber,count(*) cnt from trs_hq t  " +
                    "where t.status <> '1'  " +
                    "and t.busidate > sysdate - 31  " +
                    "group by trsnumber  " +
                    "order by trsnumber desc\u0004"
        }

    /**
     * 创建调入单
     */
    fun createTrsf(tib: TrsfItemBean): String {
        return "insert into trs(STOREID, BUSIDATE, TRSID, TRSNUMBER, ITEMNUMBER, SHIPNUMBER,  " +
                "STOREUNITPRICE, UNITCOST, TRSSTOREID, TRSQUANTITY, TRSTIME, TRSREASONNUMBER,  " +
                "UPDATEUSERID, UPDATEDATETIME, SELL_COST, VENDORID, SUPPLIERID)  " +
                "select P.STOREID,to_date('${CStoreCalendar.getCurrentDate(0)}' ,'yyyy-mm-dd') busidate, 'I' TRSID, TRSNUMBER,P.ITEMNUMBER,SHIPNUMBER,  " +
                "P.STOREUNITPRICE, T.UNITCOST, TRSSTOREID, TRSQUANTITY, sysdate TRSTIME, '00' TRSREASONNUMBER,  " +
                "'${User.getUser().uId}',SYSDATE, P.SELL_COST, VENDORID, SUPPLIERID  " +
                "FROM TRS_HQ T, PLU P  " +
                "WHERE T.STOREID = P.STOREID  " +
                "AND T.ITEMNUMBER = P.ITEMNUMBER  " +
                "AND T.TRSNUMBER = '${tib.trsNumber}'\u000c " +
                "update trs_hq set status = '1' where trsnumber ='${tib.trsNumber}'\u0004"
    }


    /******************************************单品查询**********************************************************/

    /**
     * 单品搜索商品
     * @param data 搜索关键字
     * @param type 0=品号品名搜索，1=条码档期搜索
     */
    fun getUnitPlu(data: String, type: Int): String {
        return if (type == 0) {
            //根据品名或品号
            "select plu.itemNumber,plu.pluName,plu.storeUnitPrice,plu.VendorID,plu.SupplierID,plu.unitCost,plu.shipNumber, " +
                    "round(plu.unitCost*(1+taxtype.taxrate),6) storeunitCost,round(plu.sell_cost*(1+taxtype.taxrate),6) storesell_cost , " +
                    "plu.ordertype,plu.returnType,plu.saletype,substr(plu.signType,7,1) mrktype,substr(plu.signType,6,1) trsType, " +
                    "decode(plu.ordertype,'Y',to_char(plu.orderbeginDate,'yyyy-mm-dd'),'') orderbeginDate,decode(plu.ordertype,'Y',to_char(plu.orderendDate,'yyyy-mm-dd'),'') orderendDate, " +
                    "decode(plu.returntype,'Y',to_char(plu.returnbeginDate,'yyyy-mm-dd'),'') returnbeginDate,decode(plu.returntype,'Y',to_char(plu.returnendDate,'yyyy-mm-dd'),'') returnendDate, " +
                    "decode(plu.saletype,'Y',to_char(plu.SaleBeginDate,'yyyy-mm-dd'),'') SaleBeginDate,decode(plu.saletype,'Y',to_char(plu.SaleendDate,'yyyy-mm-dd'),'') SaleendDate, " +
                    "unit.UnitName,c1.categoryname,c2.categoryname midCateGoryname,v1.vendorName,v2.vendorName supplierName, plu.status, " +
                    "plu.out_th_code,plu.stop_th_code,plu.in_th_code,substr(plu.signType,9,1) STOCKCHANGE,substr(plu.signType,12,1) s_returntype, " +
                    "plu.return_attr, " +
                    "(nvl(inv.befInvQuantity,0) " +
                    "+nvl(inv.accDlvQuantity,0) " +
                    "-nvl(inv.accRtnQuantity,0) " +
                    "-nvl(inv.accSaleQuantity,0) " +
                    "+nvl(inv.accSaleRtnQuantity,0) " +
                    "-nvl(inv.accMrkQuantity,0) " +
                    "+nvl(inv.accCshDlvQuantity,0) " +
                    "-nvl(inv.accCshRtnQuantity,0) " +
                    "+nvl(inv.accTrsQuantity,0) " +
                    "+nvl(inv.accLeibianQuantity,0) " +
                    "+nvl(inv.accAdjQuantity,0) " +
                    "+nvl(inv.accHqAdjQuantity,0) " +
                    ") as InvQuantity, " +
                    "decode(trim(plu.mmid),null,inv.dms,inv.pdms) dms,nvl(itemsalemonth.dma,0) dma, " +
                    "plu.item_level,plu.keep_days,nvl(sfitem.safe_qty,plu.face_qty) min_qty,  " +
                    "to_date(to_char(sysdate,'yyyymmdd'),'yyyymmdd')- plu.market_date+1 go_market_days " +
                    "from plu  left join  unit on unit.storeid = plu.storeid and unit.unitid = plu.smallunitid " +
                    "left join cat c1 on c1.storeID = plu.storeID and c1.categoryNumber=plu.categorynumber and trim(c1.midCategorynumber) is null and trim(c1.microCategorynumber) is  null " +
                    "left join cat c2 on c2.storeID = plu.storeID and c2.categoryNumber=plu.categorynumber and c2.midCategorynumber=plu.midcategorynumber and trim(c2.microCategorynumber) is null " +
                    "left join vendor v1 on v1.storeid = plu.storeID and v1.vendorID=plu.vendorID " +
                    "left join vendor v2 on v2.storeid = plu.storeID and v2.vendorID=plu.supplierID " +
                    "left join inv on inv.storeID=plu.storeID AND inv.itemNumber=plu.itemNumber AND inv.busiDate= to_date('${CStoreCalendar.getCurrentDate(0)}','yyyy-mm-dd') " +
                    "left join itemsaleMonth on itemsaleMonth.storeID=plu.storeID AND itemsaleMonth.itemNumber=plu.itemNumber AND itemsaleMonth.busiDate= to_date('${CStoreCalendar.getCurrentDate(0)}','yyyy-mm-dd') " +
                    "left join sfitem on sfitem.storeid=plu.storeID AND sfitem.item=plu.itemNumber " +
                    "left join taxtype on taxtype.storeid=plu.storeid and taxtype.taxid=plu.taxid " +
                    "where plu.storeID   = '${User.getUser().storeId}' " +
                    "and (plu.pluName = '$data' or plu.itemNumber = '$data')\u0004"
        } else {
            //根据条码和档期
            "select plu.itemNumber,plu.pluName,plu.storeUnitPrice,plu.VendorID,plu.SupplierID,plu.unitCost,plu.shipNumber, " +
                    "round(plu.unitCost*(1+taxtype.taxrate),6) storeunitCost,round(plu.sell_cost*(1+taxtype.taxrate),6) storesell_cost , " +
                    "plu.ordertype,plu.returnType,plu.saletype,substr(plu.signType,7,1) mrktype,substr(plu.signType,6,1) trsType, " +
                    "plu.orderbeginDate,plu.orderendDate,plu.returnBeginDate,plu.returnEndDate,plu.SaleBeginDate,plu.SaleEndDate ,itemplu.plunumber, " +
                    "unit.UnitName,c1.categoryname,c2.categoryname midCateGoryname,v1.vendorName,v2.vendorName supplierName, " +
                    "plu.status,plu.STOP_TH_CODE,plu.OUT_TH_CODE,substr(plu.signType,9,1) STOCKCHANGE, substr(plu.signType,12,1) s_returntype, " +
                    "plu.return_attr, " +
                    "(nvl(inv.befInvQuantity,0) " +
                    "+nvl(inv.accDlvQuantity,0) " +
                    "-nvl(inv.accRtnQuantity,0) " +
                    "-nvl(inv.accSaleQuantity,0) " +
                    "+nvl(inv.accSaleRtnQuantity,0) " +
                    "-nvl(inv.accMrkQuantity,0) " +
                    "+nvl(inv.accCshDlvQuantity,0) " +
                    "-nvl(inv.accCshRtnQuantity,0) " +
                    "+nvl(inv.accTrsQuantity,0) " +
                    "+nvl(inv.accLeibianQuantity,0) " +
                    "+nvl(inv.accAdjQuantity,0) " +
                    "+nvl(inv.accHqAdjQuantity,0) " +
                    ") as InvQuantity,decode(trim(plu.mmid),null,inv.dms,inv.pdms) dms, " +
                    "nvl(itemsaleMonth.dma,0)  dma,plu.IN_TH_CODE,plu.item_level,nvl(sfitem.safe_qty,plu.face_qty) min_qty,plu.KEEP_DAYS, " +
                    "to_date(to_char(sysdate,'yyyymmdd'),'yyyymmdd')- plu.market_date+1 go_market_days " +
                    "from itemplu left join plu on plu.storeid = itemplu.storeID and plu.itemnumber = itemplu.itemnumber " +
                    "left join unit on unit.storeid = itemplu.storeid and unit.unitid = plu.smallunitid " +
                    "left join cat c1 on c1.storeID = itemplu.storeID and c1.categoryNumber=plu.categorynumber and Trim(c1.midCategorynumber) is null and Trim(c1.microCategorynumber)is null " +
                    "left join cat c2 on c2.storeID = itemplu.storeID and c2.categoryNumber=plu.categorynumber and c2.midCategorynumber=plu.midcategorynumber and Trim(c2.microCategorynumber)is null " +
                    "left join vendor v1 on v1.storeid = itemplu.storeID and v1.vendorID=plu.vendorID " +
                    "left join vendor v2 on v2.storeid = plu.storeID and v2.vendorID=plu.supplierID " +
                    "left join taxtype on taxtype.storeid=plu.storeID and taxtype.taxid=plu.taxid " +
                    "left join inv on inv.storeID=plu.storeID AND inv.itemNumber=plu.itemNumber AND inv.busiDate=to_date('${CStoreCalendar.getCurrentDate(0)}','yyyy-mm-dd') " +
                    "left join itemsaleMonth on itemsaleMonth.storeID=plu.storeID AND itemsaleMonth.itemNumber=plu.itemNumber AND itemsaleMonth.busiDate= to_date('${CStoreCalendar.getCurrentDate(0)}','yyyy-mm-dd') " +
                    "left join sfitem on sfitem.storeid=plu.storeID AND sfitem.item=plu.itemNumber " +
                    "where itemplu.storeID = '${User.getUser().storeId}' " +
                    "and itemplu.pluNumber = '$data'\u0004"
        }
    }

    /**
     * 创建自设排面量流程，删除
     */
    fun getDeleteMin(itemId: String): String {
        return "delete from sfitem where storeid='${User.getUser().storeId}' and item='$itemId';"
    }

    /**
     * 创建自设排面量流程，创建
     */
    fun getInsertMin(minQty: Int, itemId: String): String {
        return "insert into sfitem values('${User.getUser().storeId}','$itemId',$minQty);"
    }


    /******************************************库存异常查询**********************************************************/

    /**
     * 每次获得数据都要运行
     */
    val invErrorFirst: String
        get() {
            return "insert into dlv_temp " +
                    "SELECT dlvdtl.storeID,dlvdtl.itemNumber,SUM(dlvdtl.dlvQuantity) dlvQuantity " +
                    "FROM dlvhead,dlvdtl " +
                    "WHERE dlvhead.storeID=dlvdtl.storeID   AND dlvhead.dlvDate=dlvdtl.dlvDate " +
                    "AND dlvhead.requestNumber=dlvdtl.requestNumber  AND dlvhead.dlvStatus='0' " +
                    "AND dlvhead.storeID='${User.getUser().storeId}' " +
                    "AND dlvhead.dlvDate>=trunc(sysdate) " +
                    "GROUP BY dlvdtl.storeID,dlvdtl.itemNumber\u0004"
        }

    /**
     * 负库存商品
     */
    val negativeInv: String
        get() {
            return "select '1' flag, " +
                    "plu.itemNumber,plu.pluName,plu.categoryNumber, " +
                    "cat.categoryName,plu.orderMode,ana55.Class_type,120 NOSALEDAY, " +
                    "nvl(sfitem.safe_qty,plu.face_qty) sfqty,plu.status,plu.market_date, " +
                    "plu.item_level layclass,plu.storeUnitPrice, " +
                    "(case when plu.vendorID=plu.supplierID then '${MyApplication.instance().getString(R.string.zhi)}' else '${MyApplication.instance().getString(R.string.pei)}' end ) send_type, " +
                    "ana55.DMS,ana55.befInvQuantity, " +
                    "(case when ana55.days<0 then 0 else ana55.days end ) days, " +
                    "plu.returnType,nvl(dlvdtl.dlvQuantity,0) dlv, " +
                    "unit.unitName,ana55.Sale_Date,case when ana55.Dlv_Date='700101' or ana55.Dlv_Date='000000' then '' else " +
                    "ana55.Dlv_Date end Dlv_Date, plu.UNIT_CLASS,plu.minimaOrderQuantity, " +
                    "(case when trim(plu.STOP_TH_CODE) is null then '' else plu.stop_th_code||'(${MyApplication.instance().getString(R.string.ting)})' end)|| " +
                    "(case when trim(plu.STOP_TH_CODE) is null then '' else (case when trim(plu.OUT_TH_CODE) is null then '' else '-' end) end)|| " +
                    "(case when trim(plu.OUT_TH_CODE) is null then '' else plu.OUT_TH_CODE||'(${MyApplication.instance().getString(R.string.tui)})' end) th_code, " +
                    "case when nvl(trim(plu.STOP_TH_CODE),0)>nvl(trim(plu.OUT_TH_CODE),0) then " +
                    "nvl(trim(plu.STOP_TH_CODE),0) else nvl(trim(plu.OUT_TH_CODE),0) end th_code1, " +
                    "(nvl(inv.befInvQuantity,0)+nvl(inv.accDlvQuantity,0)-nvl(inv.accRtnQuantity,0)-nvl(inv.accSaleQuantity,0) " +
                    "+nvl(inv.accSaleRtnQuantity,0)-nvl(inv.accMrkQuantity,0)+nvl(inv.accCshDlvQuantity,0)-nvl(inv.accCshRtnQuantity,0) " +
                    "+nvl(inv.accTrsQuantity,0)+nvl(inv.accLeibianQuantity,0)+nvl(inv.accAdjQuantity,0)+nvl(inv.accHqAdjQuantity,0)) as InvQuantity " +
                    ",case when (nvl(inv.befInvQuantity,0)+nvl(inv.accDlvQuantity,0)-nvl(inv.accRtnQuantity,0)-nvl(inv.accSaleQuantity,0)+ " +
                    "nvl(inv.accSaleRtnQuantity,0)-nvl(inv.accMrkQuantity,0)+nvl(inv.accCshDlvQuantity,0)-nvl(inv.accCshRtnQuantity,0)+ " +
                    "nvl(inv.accTrsQuantity,0)+nvl(inv.accLeibianQuantity,0)+nvl(inv.accAdjQuantity,0)+nvl(inv.accHqAdjQuantity,0))>0 " +
                    "then 1 else (case when nvl(dlvdtl.dlvQuantity,0)>0 then 2 else 3 end) end orderSeq, " +
                    "decode(trim(itemfaceqty.ITEMNUMBER),null,'N','Y') display_yn " +
                    "FROM ana55,cat,plu " +
                    "left join itemfaceqty on itemfaceqty.storeID=plu.storeID AND plu.itemNumber=itemfaceqty.ITEMNUMBER " +
                    "left join unit on unit.storeID=plu.storeID AND unit.unitID=plu.smallUnitID " +
                    "left join dlvdtl on dlvdtl.storeID=plu.storeID AND dlvdtl.itemNumber=plu.itemNumber and dlvdtl.dlvdate =to_date('${CStoreCalendar.getCurrentDate(0)}','yyyy-mm-dd')+1 " +
                    "left join inv on inv.storeID=plu.storeID AND inv.itemNumber=plu.itemNumber AND inv.shipNumber='0' " +
                    "AND inv.busiDate = to_date('${CStoreCalendar.getCurrentDate(0)}','yyyy-mm-dd') " +
                    "left join sfitem on sfitem.storeid=plu.storeID AND sfitem.item=plu.itemNumber " +
                    "WHERE ana55.storeID='${User.getUser().storeId}' " +
                    "AND ana55.busiDate= to_date('${CStoreCalendar.getCurrentDate(0)}','yyyy-mm-dd') " +
                    "AND ana55.EXCEPTION_type='1' " +
                    "AND plu.storeID=ana55.storeID " +
                    "AND plu.itemNumber=ana55.itemNumber " +
                    "AND plu.shipNumber='0' " +
                    "AND trim(cat.midCategoryNumber) is null " +
                    "AND plu.categoryNumber=cat.categoryNumber " +
                    "order by plu.categoryNumber,plu.itemNumber\u0004"
        }

    /**
     * 滞销品
     */
    val noSales: String
        get() {
            return "select '4' flag, " +
                    "plu.itemNumber,plu.pluName,plu.categoryNumber,  " +
                    "cat.categoryName,plu.orderMode,ana55.Class_type,120 nosaleday, " +
                    "nvl(sfitem.safe_qty,plu.face_qty) sfqty,plu.status, " +
                    "plu.market_date, " +
                    "plu.item_level layclass,plu.storeUnitPrice, " +
                    "(case when plu.vendorID=plu.supplierID then '${MyApplication.instance().getString(R.string.zhi)}' else '${MyApplication.instance().getString(R.string.pei)}' end ) send_type, " +
                    "ana55.DMS,ana55.befInvQuantity, case when ana55.days<0 then 0 else ana55.days end days  , " +
                    "plu.returnType,nvl(dlv_temp.dlvQuantity,0) dlv, " +
                    "unit.unitName,ana55.Sale_Date,ana55.Dlv_Date,plu.UNIT_CLASS,plu.minimaOrderQuantity, " +
                    "'' th_code,'' th_code1, " +
                    "to_date('${CStoreCalendar.getCurrentDate(0)}','yyyy-mm-dd') -    " +
                    "( case when to_date(case when sale_date is null then '010101' else sale_date end ,'rrmmdd')<=  " +
                    "to_date('2001-01-01','yyyy-mm-dd')  then   ( case when to_date(case when Dlv_date is null then '010101'  " +
                    "else dlv_date end ,'rrmmdd')<=to_date('2001-01-01','yyyy-mm-dd') then  ( case when plu.market_date<   " +
                    "store.storeopendate  then store.storeopendate   else plu.market_date end ) else to_date(Dlv_date,'rrmmdd') end )   " +
                    "else to_date(Sale_Date,'rrmmdd') end  ) no_sale_day, " +
                    "(nvl(inv.befInvQuantity,0)+nvl(inv.accDlvQuantity,0)-nvl(inv.accRtnQuantity,0)-nvl(inv.accSaleQuantity,0)  " +
                    "+nvl(inv.accSaleRtnQuantity,0)-nvl(inv.accMrkQuantity,0)+nvl(inv.accCshDlvQuantity,0)-nvl(inv.accCshRtnQuantity,0)  " +
                    "+nvl(inv.accTrsQuantity,0)+nvl(inv.accLeibianQuantity,0)+nvl(inv.accAdjQuantity,0)+nvl(inv.accHqAdjQuantity,0)) as InvQuantity, " +
                    "to_number(to_char( case when to_date(case when sale_date is null then '010101' else sale_date end ,'rrmmdd')<=  " +
                    "to_date('2001-01-01','yyyy-mm-dd')  then   ( case when to_date(case when Dlv_date is null then '010101'  " +
                    "else dlv_date end ,'rrmmdd')<=to_date('2001-01-01','yyyy-mm-dd') then  ( case when plu.market_date<  " +
                    "store.storeopendate  then store.storeopendate   else plu.market_date end ) else to_date(Dlv_date,'rrmmdd') end )  " +
                    "else to_date(Sale_Date,'rrmmdd') end ,'yyyymmdd')) orderseq, " +
                    "'N' display_yn " +
                    "FROM ana55 left join dlv_temp on dlv_temp.storeID=ana55.storeID AND dlv_temp.itemNumber=ana55.itemnumber  " +
                    "left join itemfaceqty iy on iy.storeID=ana55.storeID AND iy.ITEMNUMBER=ana55.itemNumber  " +
                    ",cat, plu left join unit on unit.storeID=plu.storeID AND unit.unitID=plu.smallUnitID  " +
                    "left join inv on inv.storeID=plu.storeID AND inv.itemNumber=plu.itemNumber AND inv.shipNumber='0'  " +
                    "AND inv.busiDate =to_date('${CStoreCalendar.getCurrentDate(0)}','yyyy-mm-dd')  " +
                    "left join sfitem on sfitem.storeid=plu.storeID AND sfitem.item=plu.itemNumber , " +
                    "store   " +
                    "WHERE ana55.storeID= '${User.getUser().storeId}' " +
                    "AND ana55.busiDate= to_date('${CStoreCalendar.getCurrentDate(0)}','yyyy-mm-dd') " +
                    "AND ana55.EXCEPTION_type='3' " +
                    "AND ana55.storeID=store.storeID " +
                    "AND to_date('${CStoreCalendar.getCurrentDate(0)}','yyyy-mm-dd')-store.storeOpenDate>=60   " +
                    "AND plu.storeID=ana55.storeID " +
                    "AND plu.itemNumber=ana55.itemNumber " +
                    "AND plu.shipNumber='0' " +
                    "AND trim(cat.midCategoryNumber) is null  " +
                    "AND plu.categoryNumber=cat.categoryNumber " +
                    "and to_date('${CStoreCalendar.getCurrentDate(0)}','yyyy-mm-dd') -  " +
                    "( case when to_date(case when sale_date is null then '010101' else sale_date end ,'rrmmdd')<=to_date('2001-01-01','yyyy-mm-dd')  " +
                    "then  " +
                    "(  case when to_date(case when Dlv_date is null then '010101' else dlv_date end ,'rrmmdd')<=to_date('2001-01-01','yyyy-mm-dd')   " +
                    "then   " +
                    "(  case when plu.market_date<store.storeopendate   " +
                    "then store.storeopendate   " +
                    "else plu.market_date   " +
                    "end  )   " +
                    "else to_date(Dlv_date,'rrmmdd')   " +
                    "end  )   " +
                    "else to_date(Sale_Date,'rrmmdd')   " +
                    "end   ) >=60  " +
                    "order by plu.categoryNumber,plu.itemnumber,orderseq desc\u0004"
        }

    /**
     * 获得状态6、8的商品
     */
    val errorStatus: String
        get() {
            return "select '6' flag," +
                    "plu.itemNumber,plu.pluName,plu.categoryNumber," +
                    "cat.categoryName,plu.orderMode,ana55.Class_type,120 NOSALEDAY," +
                    "nvl(sfitem.safe_qty,plu.face_qty) sfqty,plu.status,plu.market_date," +
                    "plu.item_level layclass,plu.storeUnitPrice, " +
                    "(case when plu.vendorID=plu.supplierID then '${MyApplication.instance().getString(R.string.zhi)}' else '${MyApplication.instance().getString(R.string.pei)}' end ) send_type, " +
                    "ana55.DMS,ana55.befInvQuantity, " +
                    "(case when ana55.days<0 then 0 else ana55.days end ) days, " +
                    "plu.returnType,nvl(dlvdtl.dlvQuantity,0) dlv, " +
                    "unit.unitName,ana55.Sale_Date,case when ana55.Dlv_Date='700101' or ana55.Dlv_Date='000000' then '' else " +
                    "ana55.Dlv_Date end Dlv_Date, plu.UNIT_CLASS,plu.minimaOrderQuantity, " +
                    "(case when trim(plu.STOP_TH_CODE) is null then '' else plu.stop_th_code||'(${MyApplication.instance().getString(R.string.ting)})' end)|| " +
                    "(case when trim(plu.STOP_TH_CODE) is null then '' else (case when trim(plu.OUT_TH_CODE) is null then '' else '-' end) end)|| " +
                    "(case when trim(plu.OUT_TH_CODE) is null then '' else plu.OUT_TH_CODE||'(${MyApplication.instance().getString(R.string.tui)})' end) th_code, " +
                    "case when nvl(trim(plu.STOP_TH_CODE),0)>nvl(trim(plu.OUT_TH_CODE),0) then " +
                    "nvl(trim(plu.STOP_TH_CODE),0) else nvl(trim(plu.OUT_TH_CODE),0) end th_code1, " +
                    "(nvl(inv.befInvQuantity,0)+nvl(inv.accDlvQuantity,0)-nvl(inv.accRtnQuantity,0)-nvl(inv.accSaleQuantity,0) " +
                    "+nvl(inv.accSaleRtnQuantity,0)-nvl(inv.accMrkQuantity,0)+nvl(inv.accCshDlvQuantity,0)-nvl(inv.accCshRtnQuantity,0) " +
                    "+nvl(inv.accTrsQuantity,0)+nvl(inv.accLeibianQuantity,0)+nvl(inv.accAdjQuantity,0)+nvl(inv.accHqAdjQuantity,0)) as InvQuantity " +
                    ",case when (nvl(inv.befInvQuantity,0)+nvl(inv.accDlvQuantity,0)-nvl(inv.accRtnQuantity,0)-nvl(inv.accSaleQuantity,0)+ " +
                    "nvl(inv.accSaleRtnQuantity,0)-nvl(inv.accMrkQuantity,0)+nvl(inv.accCshDlvQuantity,0)-nvl(inv.accCshRtnQuantity,0)+ " +
                    "nvl(inv.accTrsQuantity,0)+nvl(inv.accLeibianQuantity,0)+nvl(inv.accAdjQuantity,0)+nvl(inv.accHqAdjQuantity,0))>0 " +
                    "then 1 else (case when nvl(dlvdtl.dlvQuantity,0)>0 then 2 else 3 end) end orderSeq, " +
                    "decode(trim(itemfaceqty.ITEMNUMBER),null,'N','Y') display_yn " +
                    "FROM ana55,cat,plu " +
                    "left join itemfaceqty on itemfaceqty.storeID=plu.storeID AND plu.itemNumber=itemfaceqty.ITEMNUMBER " +
                    "left join unit on unit.storeID=plu.storeID AND unit.unitID=plu.smallUnitID " +
                    "left join dlvdtl on dlvdtl.storeID=plu.storeID AND dlvdtl.itemNumber=plu.itemNumber and dlvdtl.dlvdate =to_date('${CStoreCalendar.getCurrentDate(0)}','yyyy-mm-dd')+1 " +
                    "left join inv on inv.storeID=plu.storeID AND inv.itemNumber=plu.itemNumber AND inv.shipNumber='0' " +
                    "AND inv.busiDate = to_date('${CStoreCalendar.getCurrentDate(0)}','yyyy-mm-dd') " +
                    "left join sfitem on sfitem.storeid=plu.storeID AND sfitem.item=plu.itemNumber " +
                    "WHERE ana55.storeID='${User.getUser().storeId}' " +
                    "AND ana55.busiDate= to_date('${CStoreCalendar.getCurrentDate(0)}','yyyy-mm-dd') " +
                    "AND ana55.EXCEPTION_type='5' " +
                    "AND plu.storeID=ana55.storeID " +
                    "AND plu.itemNumber=ana55.itemNumber " +
                    "AND plu.shipNumber='0' " +
                    "AND trim(cat.midCategoryNumber) is null " +
                    "AND plu.categoryNumber=cat.categoryNumber " +
                    "order by plu.categoryNumber,plu.itemNumber,th_code1 desc\u0004"
        }

    /**
     * 需要移除商品卡商品
     */
    val needRemove: String
        get() {
            return "select '7' flag, ana55.itemNumber,plu.pluName,plu.categoryNumber,cat.categoryName, " +
                    "plu.status,plu.item_level layclass,plu.storeUnitPrice,ana55.DMS " +
                    "FROM ana55,plu,cat " +
                    "WHERE ana55.storeID='${User.getUser().storeId}' " +
                    "AND ana55.busiDate=to_date('${CStoreCalendar.getCurrentDate(0)}','yyyy-mm-dd') " +
                    "AND ana55.EXCEPTION_type='7' " +
                    "AND ana55.storeID=plu.storeID " +
                    "AND ana55.itemNumber=plu.itemNumber " +
                    "AND trim(cat.midCategoryNumber) is null " +
                    "AND plu.categoryNumber=cat.categoryNumber " +
                    "order by plu.categoryNumber,plu.itemNumber"
        }

    /**
     * L1缺货商品
     */
    val outL1: String
        get() {
            return "select  '2' flag," +
                    "plu.itemNumber,plu.pluName,plu.categoryNumber," +
                    "cat.categoryName,plu.orderMode,ana55.Class_type,120 NOSALEDAY," +
                    "nvl(sfitem.safe_qty,plu.face_qty) sfqty,plu.status,plu.market_date," +
                    "plu.item_level layclass,plu.storeUnitPrice, " +
                    "(case when plu.vendorID=plu.supplierID then '${MyApplication.instance().getString(R.string.zhi)}' else '${MyApplication.instance().getString(R.string.pei)}' end ) send_type, " +
                    "ana55.DMS,ana55.befInvQuantity, " +
                    "(case when ana55.days<0 then 0 else ana55.days end ) days, " +
                    "plu.returnType,nvl(dlvdtl.dlvQuantity,0) dlv, " +
                    "unit.unitName,ana55.Sale_Date,case when ana55.Dlv_Date='700101' or ana55.Dlv_Date='000000' then '' else " +
                    "ana55.Dlv_Date end Dlv_Date, plu.UNIT_CLASS,plu.minimaOrderQuantity, " +
                    "(case when trim(plu.STOP_TH_CODE) is null then '' else plu.stop_th_code||'(${MyApplication.instance().getString(R.string.ting)})' end)|| " +
                    "(case when trim(plu.STOP_TH_CODE) is null then '' else (case when trim(plu.OUT_TH_CODE) is null then '' else '-' end) end)|| " +
                    "(case when trim(plu.OUT_TH_CODE) is null then '' else plu.OUT_TH_CODE||'(${MyApplication.instance().getString(R.string.tui)})' end) th_code, " +
                    "case when nvl(trim(plu.STOP_TH_CODE),0)>nvl(trim(plu.OUT_TH_CODE),0) then " +
                    "nvl(trim(plu.STOP_TH_CODE),0) else nvl(trim(plu.OUT_TH_CODE),0) end th_code1, " +
                    "(nvl(inv.befInvQuantity,0)+nvl(inv.accDlvQuantity,0)-nvl(inv.accRtnQuantity,0)-nvl(inv.accSaleQuantity,0) " +
                    "+nvl(inv.accSaleRtnQuantity,0)-nvl(inv.accMrkQuantity,0)+nvl(inv.accCshDlvQuantity,0)-nvl(inv.accCshRtnQuantity,0) " +
                    "+nvl(inv.accTrsQuantity,0)+nvl(inv.accLeibianQuantity,0)+nvl(inv.accAdjQuantity,0)+nvl(inv.accHqAdjQuantity,0)) as InvQuantity " +
                    ",case when (nvl(inv.befInvQuantity,0)+nvl(inv.accDlvQuantity,0)-nvl(inv.accRtnQuantity,0)-nvl(inv.accSaleQuantity,0)+ " +
                    "nvl(inv.accSaleRtnQuantity,0)-nvl(inv.accMrkQuantity,0)+nvl(inv.accCshDlvQuantity,0)-nvl(inv.accCshRtnQuantity,0)+ " +
                    "nvl(inv.accTrsQuantity,0)+nvl(inv.accLeibianQuantity,0)+nvl(inv.accAdjQuantity,0)+nvl(inv.accHqAdjQuantity,0))>0 " +
                    "then 1 else (case when nvl(dlvdtl.dlvQuantity,0)>0 then 2 else 3 end) end orderSeq, " +
                    "decode(trim(itemfaceqty.ITEMNUMBER),null,'N','Y') display_yn " +
                    "FROM ana55,cat,plu " +
                    "left join itemfaceqty on itemfaceqty.storeID=plu.storeID AND plu.itemNumber=itemfaceqty.ITEMNUMBER " +
                    "left join unit on unit.storeID=plu.storeID AND unit.unitID=plu.smallUnitID " +
                    "left join dlvdtl on dlvdtl.storeID=plu.storeID AND dlvdtl.itemNumber=plu.itemNumber and dlvdtl.dlvdate =to_date('${CStoreCalendar.getCurrentDate(0)}','yyyy-mm-dd')+1 " +
                    "left join inv on inv.storeID=plu.storeID AND inv.itemNumber=plu.itemNumber AND inv.shipNumber='0' " +
                    "AND inv.busiDate = to_date('${CStoreCalendar.getCurrentDate(0)}','yyyy-mm-dd') " +
                    "left join sfitem on sfitem.storeid=plu.storeID AND sfitem.item=plu.itemNumber " +
                    "WHERE ana55.storeID='${User.getUser().storeId}' " +
                    "AND ana55.busiDate= to_date('${CStoreCalendar.getCurrentDate(0)}','yyyy-mm-dd') " +
                    "AND ana55.EXCEPTION_type='2' " +
                    "AND plu.storeID=ana55.storeID " +
                    "AND plu.itemNumber=ana55.itemNumber " +
                    "AND plu.shipNumber='0' " +
                    "AND trim(cat.midCategoryNumber) is null " +
                    "AND plu.categoryNumber=cat.categoryNumber " +
                    "order by plu.categoryNumber,plu.itemNumber,(case when (nvl(inv.befInvQuantity,0)+nvl(inv.accDlvQuantity,0)-nvl(inv.accRtnQuantity,0)-nvl(inv.accSaleQuantity,0)+ " +
                    "nvl(inv.accSaleRtnQuantity,0)-nvl(inv.accMrkQuantity,0)+nvl(inv.accCshDlvQuantity,0)-nvl(inv.accCshRtnQuantity,0)+ " +
                    "nvl(inv.accTrsQuantity,0)+nvl(inv.accLeibianQuantity,0)+nvl(inv.accAdjQuantity,0)+nvl(inv.accHqAdjQuantity,0))>0 " +
                    "then 1 else (case when nvl(dlvdtl.dlvQuantity,0)>0 then 2 else 3 end) end) desc\u0004"
        }

    /**
     * 高库存商品
     */
    val highInv: String
        get() {
            return "select '5' flag," +
                    "plu.itemNumber,plu.pluName,plu.categoryNumber," +
                    "cat.categoryName,plu.orderMode,ana55.Class_type,120 NOSALEDAY," +
                    "nvl(sfitem.safe_qty,plu.face_qty) sfqty,plu.status,plu.market_date," +
                    "plu.item_level layclass,plu.storeUnitPrice, " +
                    "(case when plu.vendorID=plu.supplierID then '${MyApplication.instance().getString(R.string.zhi)}' else '${MyApplication.instance().getString(R.string.pei)}' end ) send_type, " +
                    "ana55.DMS,ana55.befInvQuantity, " +
                    "(case when ana55.days<0 then 0 else ana55.days end ) days, " +
                    "plu.returnType,nvl(dlvdtl.dlvQuantity,0) dlv, " +
                    "unit.unitName,ana55.Sale_Date,case when ana55.Dlv_Date='700101' or ana55.Dlv_Date='000000' then '' else " +
                    "ana55.Dlv_Date end Dlv_Date, plu.UNIT_CLASS,plu.minimaOrderQuantity, " +
                    "(case when trim(plu.STOP_TH_CODE) is null then '' else plu.stop_th_code||'(${MyApplication.instance().getString(R.string.ting)})' end)|| " +
                    "(case when trim(plu.STOP_TH_CODE) is null then '' else (case when trim(plu.OUT_TH_CODE) is null then '' else '-' end) end)|| " +
                    "(case when trim(plu.OUT_TH_CODE) is null then '' else plu.OUT_TH_CODE||'(${MyApplication.instance().getString(R.string.tui)})' end) th_code, " +
                    "case when nvl(trim(plu.STOP_TH_CODE),0)>nvl(trim(plu.OUT_TH_CODE),0) then " +
                    "nvl(trim(plu.STOP_TH_CODE),0) else nvl(trim(plu.OUT_TH_CODE),0) end th_code1, " +
                    "(nvl(inv.befInvQuantity,0)+nvl(inv.accDlvQuantity,0)-nvl(inv.accRtnQuantity,0)-nvl(inv.accSaleQuantity,0) " +
                    "+nvl(inv.accSaleRtnQuantity,0)-nvl(inv.accMrkQuantity,0)+nvl(inv.accCshDlvQuantity,0)-nvl(inv.accCshRtnQuantity,0) " +
                    "+nvl(inv.accTrsQuantity,0)+nvl(inv.accLeibianQuantity,0)+nvl(inv.accAdjQuantity,0)+nvl(inv.accHqAdjQuantity,0)) as InvQuantity " +
                    ",case when (nvl(inv.befInvQuantity,0)+nvl(inv.accDlvQuantity,0)-nvl(inv.accRtnQuantity,0)-nvl(inv.accSaleQuantity,0)+ " +
                    "nvl(inv.accSaleRtnQuantity,0)-nvl(inv.accMrkQuantity,0)+nvl(inv.accCshDlvQuantity,0)-nvl(inv.accCshRtnQuantity,0)+ " +
                    "nvl(inv.accTrsQuantity,0)+nvl(inv.accLeibianQuantity,0)+nvl(inv.accAdjQuantity,0)+nvl(inv.accHqAdjQuantity,0))>0 " +
                    "then 1 else (case when nvl(dlvdtl.dlvQuantity,0)>0 then 2 else 3 end) end orderSeq, " +
                    "decode(trim(itemfaceqty.ITEMNUMBER),null,'N','Y') display_yn " +
                    "FROM ana55,cat,plu " +
                    "left join itemfaceqty on itemfaceqty.storeID=plu.storeID AND plu.itemNumber=itemfaceqty.ITEMNUMBER " +
                    "left join unit on unit.storeID=plu.storeID AND unit.unitID=plu.smallUnitID " +
                    "left join dlvdtl on dlvdtl.storeID=plu.storeID AND dlvdtl.itemNumber=plu.itemNumber and dlvdtl.dlvdate =to_date('${CStoreCalendar.getCurrentDate(0)}','yyyy-mm-dd')+1 " +
                    "left join inv on inv.storeID=plu.storeID AND inv.itemNumber=plu.itemNumber AND inv.shipNumber='0' " +
                    "AND inv.busiDate = to_date('${CStoreCalendar.getCurrentDate(0)}','yyyy-mm-dd') " +
                    "left join sfitem on sfitem.storeid=plu.storeID AND sfitem.item=plu.itemNumber " +
                    "WHERE ana55.storeID='${User.getUser().storeId}' " +
                    "AND ana55.busiDate= to_date('${CStoreCalendar.getCurrentDate(0)}','yyyy-mm-dd') " +
                    "AND ana55.EXCEPTION_type='4' " +
                    "AND plu.storeID=ana55.storeID " +
                    "AND plu.itemNumber=ana55.itemNumber " +
                    "AND plu.shipNumber='0' " +
                    "AND trim(cat.midCategoryNumber) is null " +
                    "AND plu.categoryNumber=cat.categoryNumber " +
                    "order by plu.categoryNumber,plu.itemNumber\u0004"
        }

    /**
     * 一般缺货
     */
    val genOut: String
        get() {
            return "SELECT '3' flag, " +
                    "plu.itemNumber,plu.pluName,plu.categoryNumber,  " +
                    "cat.categoryName,plu.orderMode,ana55.Class_type,120 nosaleday, " +
                    "nvl(sfitem.safe_qty,plu.face_qty) sfqty,plu.status,plu.market_date, " +
                    "plu.item_level layclass,plu.storeUnitPrice, " +
                    "(case when plu.vendorID=plu.supplierID then '${MyApplication.instance().getString(R.string.zhi)}' else '${MyApplication.instance().getString(R.string.pei)}' end ) send_type, " +
                    "ana55.DMS,ana55.befInvQuantity, " +
                    "(case when ana55.days<0 then 0 else ana55.days end ) days, " +
                    "plu.returnType,nvl(dlv_temp.dlvQuantity,0) dlv, " +
                    "unit.unitName,ana55.Sale_Date,ana55.Dlv_Date,plu.UNIT_CLASS,plu.minimaOrderQuantity, " +
                    "(nvl(inv.befInvQuantity,0)+nvl(inv.accDlvQuantity,0)-nvl(inv.accRtnQuantity,0)-nvl(inv.accSaleQuantity,0) " +
                    "+nvl(inv.accSaleRtnQuantity,0)-nvl(inv.accMrkQuantity,0)+nvl(inv.accCshDlvQuantity,0)-nvl(inv.accCshRtnQuantity,0) " +
                    "+nvl(inv.accTrsQuantity,0)+nvl(inv.accLeibianQuantity,0)+nvl(inv.accAdjQuantity,0)+nvl(inv.accHqAdjQuantity,0)) as InvQuantity,  " +
                    "(case when (nvl(inv.befInvQuantity,0)+nvl(inv.accDlvQuantity,0)-nvl(inv.accRtnQuantity,0)-   " +
                    "nvl(inv.accSaleQuantity,0)+nvl(inv.accSaleRtnQuantity,0)-nvl(inv.accMrkQuantity,0)+  " +
                    "nvl(inv.accCshDlvQuantity,0)-nvl(inv.accCshRtnQuantity,0)+nvl(inv.accTrsQuantity,0)+  " +
                    "nvl(inv.accLeibianQuantity,0)+nvl(inv.accAdjQuantity,0)+nvl(inv.accHqAdjQuantity,0)) >0  " +
                    "then 1 else ( case when  ( nvl(dlv_temp.dlvQuantity,0) )>0 then 2 else 3  end )  " +
                    "end  )order_seq    " +
                    "FROM ana55,cat, " +
                    "plu left join unit on unit.storeID=plu.storeID AND unit.unitID=plu.smallUnitID " +
                    "left join dlv_temp on dlv_temp.storeID=plu.storeID AND dlv_temp.itemNumber=plu.itemNumber " +
                    "left join inv on inv.storeID=plu.storeID AND inv.itemNumber=plu.itemNumber AND inv.shipNumber='0' " +
                    "AND inv.busiDate = to_date('${CStoreCalendar.getCurrentDate(0)}','yyyy-mm-dd') " +
                    "left join sfitem on sfitem.storeid=plu.storeID AND sfitem.item=plu.itemNumber  " +
                    "WHERE ana55.storeID= '${User.getUser().storeId}' " +
                    "AND ana55.busiDate= to_date('${CStoreCalendar.getCurrentDate(0)}','yyyy-mm-dd') " +
                    "AND ana55.EXCEPTION_type='6' " +
                    "AND plu.storeID=ana55.storeID " +
                    "AND plu.itemNumber=ana55.itemNumber " +
                    "AND plu.shipNumber='0' " +
                    "AND trim(cat.midCategoryNumber) is null " +
                    "AND plu.categoryNumber=cat.categoryNumber " +
                    "and plu.item_level<>'L1' " +
                    "order by plu.categoryNumber,plu.itemNumber,order_seq desc\u0004"
        }

    /***********************************************排班*****************************************************/

    /**
     * 根据日期得到排班数据
     */
    fun getPaibanData(beginDate: String, endDate: String): String {
        return "select p.storeid, p.systemdate, p.employeeid, e.employeename, p.begindatetime, p.enddatetime, p.danren_hr, p.updatedatetime  " +
                "from paiban_simple p,employee e   " +
                "where p.employeeid=e.employeeid " +
                "and p.systemdate between to_date('$beginDate', 'yyyy-mm-dd') and to_date('$endDate', 'yyyy-mm-dd')\u0004"
    }

    /**
     * 创建排班数据
     */
    fun createPaiban(data: PaibanBean): String {
        return "insert into paiban_simple  " +
                "values('${data.storeId}',to_date('${data.systemDate}','yyyy-mm-dd'),'${data.employeeId}', " +
                "to_date('${data.beginDateTime}','yyyy-MM-dd HH24:mi:ss'), to_date('${data.endDateTime}','yyyy-MM-dd HH24:mi:ss'), " +
                "${data.danrenHr},sysdate)\u0004"
    }

    /**
     * 更新排班数据
     */
    fun updatePaiban(data: PaibanBean): String {
        return "update paiban_simple set begindatetime=to_date('${data.beginDateTime}','yyyy-MM-dd HH24:mi:ss'), " +
                "enddatetime=to_date('${data.endDateTime}','yyyy-MM-dd HH24:mi:ss'), " +
                "danren_hr=${data.danrenHr}, " +
                "updatedatetime=sysdate " +
                "where storeid='${data.storeId}'  " +
                "and systemdate=to_date('${data.systemDate}','yyyy-mm-dd') " +
                "and employeeid='${data.employeeId}'\u0004"
    }

    fun deletePaiban(data: PaibanBean): String {
        return "delete from paiban_simple  " +
                "where storeid='${data.storeId}'  " +
                "and systemdate=to_date('${data.systemDate}','yyyy-mm-dd') " +
                "and employeeid='${data.employeeId}'\u0004"
    }
}