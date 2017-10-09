package com.cstore.zhiyazhang.cstoremanagement.sql

import com.cstore.zhiyazhang.cstoremanagement.bean.*
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil

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
    val cstoreCalendar="select storeid,datetype,to_char(currentdate,'yyyy-MM-dd') currentdate,changetime,sceodresult,typename from calendar order by datetype\u0004"

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
    fun SignIn(uid: String): String {
        return "select storeid,employeeid,employeename,emppassword,emptelphone,storechinesename,address,store_attr,(SELECT COUNT(*) CNT FROM CONT_ITEM X,PLU Y WHERE X.STOREID = Y.STOREID AND X.ITEMNO = Y.ITEMNUMBER AND TO_CHAR(SYSDATE-1,'YYYYMMDD') BETWEEN X.TRAN_DATE_ST AND X.TRAN_DATE_ED) cnt from (select A.storeid,A.Employeeid,A.Employeename,A.Emppassword,A.Emptelphone,B.Storechinesename,B.Address,B.Store_Attr from employee A,store B) where employeeid='$uid'\u0004"
    }


    /**********************************************订货************************************************************/
    /**
     * 获得大类信息的存储过程
     */
    fun ordT2(): String {
        return "c0c"
    }

    /**
     * 检测是否存在正确存储过程
     */
    val judgmentOrdt2: String
        get() {
            return "select distinct to_char(orderdate,'yyyy-mm-dd') orderdate from ord_t2\u0004"
        }


    /**
     * 获得大类信息
     */
    fun getAllCategory(): String {
        /*return "select plu.categoryNumber,cat.categoryName,count(*) tot_sku," +
                "sum(decode(sign(ord.ordActualQuantity+ord.ordActualQuantity1), 1,1,0)) ord_sku," +
                "sum((ord.ordActualQuantity+ord.ordActualQuantity1)*ord.storeUnitPrice) amt,ord.orderDate " +
                "from cat,plu,ord,cat cat1 " +
                "WHERE cat.storeid='${User.getUser().storeId}' " +
                "AND trim(cat.categoryNumber) is not null " +
                "AND trim(cat.midcategoryNumber) is null " +
                "AND trim(cat.microcategoryNumber) is null " +
                "AND plu.storeID=cat.storeID " +
                "AND plu.categoryNumber=cat.categoryNumber " +
                "AND plu.storeID=ord.storeID " +
                "AND plu.itemNumber=ord.itemNumber " +all scb02_new_p01('${MyTimeUtil.tomorrowDate}','${MyTimeUtil.tomorrowDate}','0')\u00
                "AND ord.orderDate=to_date('${MyTimeUtil.tomorrowDate}','YYYY-MM-DD') " +
                "AND plu.storeID=cat1.storeID " +
                "AND plu.categoryNumber=cat1.categoryNumber " +
                "AND plu.midCategoryNumber=cat1.midCategoryNumber " +
                "AND trim(cat1.midCategoryNumber) is not null " +
                "AND trim(cat1.microCategoryNumber) is null " +
                "AND cat1.GROUP_YN='N' AND cat.categoryNumber<>'99' " +
                "GROUP BY ord.orderDate,plu.categoryNumber,cat.categoryName " +
                "order by PLU.CATEGORYNUMBER\u0004"*/
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
                "select '-1' categorynumber, '一览统计' categoryname, sum(tot_sku) tot_sku, sum(ord_sku) ord_sku, sum(amt) amt, to_date('${CStoreCalendar.getCurrentDate(2)}','YYYY-MM-DD')  orderdate from(select plu.categoryNumber,cat.categoryName, " +
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
                "order by CATEGORYNUMBER"
    }

    /**
     * 通过大类id获得商品
     */
    fun getItemByCategoryId(categoryId: String, sort: String): String {
        return "Select to_char(x.sell_cost, '999999990.00') sell_cost," +
                " x.itemnumber,x.pluname,x.quantity,x.invquantity,x.ordactualquantity,x.dlv_qty,x.d1_dfs,x.INCREASEORDERQUANTITY,x.minimaorderquantity,x.maximaorderquantity,x.dms,x.ordertype,x.pro_yn, " +
                "substr(p.signType,12,1) s_returntype," +
                "round(p.storeunitprice,2) storeunitprice " +
                "From ord_t2 x,plu p," +
                "(select itemnumber item_no from itemgondra where storeid='${User.getUser().storeId}' and gondranumber like '%' " +
                "group by itemnumber) y " +
                "where x.itemnumber= y.item_no(+) " +
                "and x.itemnumber = p.itemnumber(+) " +
                "and x.categorynumber like '$categoryId' " +
                "and x.midCategoryNumber like '%' " +
                "AND Fresh_YN='N' $sort\u0004"
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
                "From ord_t2 x,plu p," +
                "(select itemnumber item_no from itemgondra where storeid='${User.getUser().storeId}' and gondranumber like '$shelfId' " +
                "group by itemnumber) y " +
                "where x.itemnumber= y.item_no(+) " +
                "and x.itemnumber = p.itemnumber(+) " +
                "and x.categorynumber like '%' " +
                "and x.midCategoryNumber like '%' " +
                "and trim(y.item_no) is not null $sort\u0004"
    }

    /**
     * 更新单品语句配合事务头和脚使用
     */
    fun updateOrdItem(itemId: String, value: Int): String {
        return "update ord set updateuserid='${User.getUser().uId}', updatedate=sysdate, ordstatus='1', ordactualquantity=$value where storeid='${User.getUser().storeId}' and orderdate=to_date('${MyTimeUtil.tomorrowDate}','yyyy-MM-dd') and itemnumber='$itemId'; update ord_t2 set ordactualquantity=$value,status='1' where orderdate=to_date('${MyTimeUtil.tomorrowDate}','yyyy-MM-dd') and itemnumber='$itemId';"
    }

    /**
     * 单品订货去搜索
     */
    fun unitOrder(value: String): String {
        return "select * from " +
                "(Select distinct x.itemnumber， to_char(x.sell_cost, '999999990.00') sell_cost," +
                "x.pluname,x.quantity,x.invquantity,x.ordactualquantity,x.dlv_qty,x.d1_dfs,x.INCREASEORDERQUANTITY,x.minimaorderquantity,x.maximaorderquantity,x.ordertype,x.pro_yn, " +
                "substr(p.signType,12,1) s_returntype, " +
                "round(p.storeunitprice,2) storeunitprice " +
                "From ord_t2 x,plu p, itemplu i " +
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
        return "select p.itemnumber,p.pluname,x.ordactualquantity,p.midcategorynumber,p.minimaorderquantity,p.maximaorderquantity,p.increaseorderquantity, to_char(p.sell_cost,'999999990.00') storeunitprice,x.inv_qty,x.dlv_qty,p.ordertype " +
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
            return "select * from (select distinct in_th_code,'第'|| in_th_code ||'期' as title, sum(tot_sku) tot_sku, sum(ord_sku) ord_sku, sum(amt) amt " +
            "from (select substr(p.in_th_code,1,3) in_th_code, " +
                    "count(*) tot_sku, " +
                    "sum(decode(sign(p.ordactualquantity+p.ordactualquantity1),1,1,0)) ord_sku, " +
                    "sum((p.ordactualquantity+p.ordactualquantity1)*p.storeunitprice) amt " +
                    "from (select a.in_th_code,a.itemnumber,b.ordActualQuantity,b.ordActualQuantity1,b.storeUnitPrice " +
                    "from plu a,ord b,ord_t2 c " +
                    "where a.itemnumber = b.itemnumber " +
                    "and c.itemnumber=b.itemnumber " +
                    "and c.orderdate=b.orderdate " +
                    "and b.orderdate=to_date('${MyTimeUtil.tomorrowDate}','yyyy-MM-dd') " +
                    "and a.in_th_code is not null " +
                    "AND nvl(c.market_date,TO_DATE('1970-01-01','YYYY-MM-DD'))>=(sysdate-60 )AND c.item_level='L0') p " +
                    "group by p.in_th_code) " +
                    "group by in_th_code " +
                    "order by in_th_code desc) where rownum<5 " +
                    "union " +
                    "select '0' in_th_code,'促销品' title, count(*) tot_sku, " +
                    "sum(decode(sign(ordActualQuantity+ordActualQuantity1), 1,1,0)) ord_sku, " +
                    "sum((ordActualQuantity+ordActualQuantity1)*storeunitprice) amt " +
                    "from (Select to_char(x.sell_cost, '999999990.00') sell_cost, " +
                    "x.itemnumber,x.pluname,x.quantity,x.invquantity,x.ordactualquantity,x.ordactualquantity1,x.dlv_qty,x.d1_dfs,x.INCREASEORDERQUANTITY,x.minimaorderquantity,x.maximaorderquantity, " +
                    "substr(p.signType,12,1) s_returntype, " +
                    "round(p.storeunitprice,2) storeunitprice " +
                    "From ord_t2 x,plu p " +
                    "where x.itemnumber = p.itemnumber(+) " +
                    "and x.pro_yn ='Y') " +
                    "order by 1 desc\u0004"
        }

    /**
     * 根据档期获得商品
     */
    fun getNewItemById(id: String, orderBy: String): String {
        return "Select to_char(x.sell_cost, '999999990.00') sell_cost,x.itemnumber,x.pluname,x.quantity,x.invquantity,x.ordactualquantity,x.dlv_qty,x.d1_dfs,x.INCREASEORDERQUANTITY,x.minimaorderquantity,x.maximaorderquantity,x.dms,x.ordertype,x.pro_yn, substr(p.signType,12,1) s_returntype, round(p.storeunitprice,2) storeunitprice " +
                "From ord_t2 x,plu p " +
                "where x.itemnumber = p.itemnumber(+) " +
                "AND nvl(x.market_date,TO_DATE('1970-01-01','YYYY-MM-DD'))>=(sysdate-60 ) " +
                "AND x.item_level='L0' " +
                "AND x.IN_TH_CODE like'%$id%' $orderBy\u0004"
    }

    /**
     * 获得促销品
     */
    fun getPromotion(orderBy: String): String {
        return "Select to_char(x.sell_cost, '999999990.00') sell_cost, x.itemnumber,x.pluname,x.quantity,x.invquantity,x.ordactualquantity,x.dlv_qty,x.d1_dfs,x.INCREASEORDERQUANTITY,x.minimaorderquantity,x.maximaorderquantity,x.dms,x.ordertype,x.pro_yn,substr(p.signType,12,1) s_returntype,round(p.storeunitprice,2) storeunitprice From ord_t2 x,plu p where x.itemnumber = p.itemnumber(+)and pro_yn ='Y' $orderBy\u0004"
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
                "From ord_t2 x,plu p " +
                "where x.itemnumber = p.itemnumber " +
                "and x.categorynumber = '$categoryId' " +
                "and x.midCategoryNumber = '$midId' $orderBy\u0004"
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
     * 创建报废,配合事务头脚使用
     */
    fun insertScrap(data: ScrapContractBean): String {
        return "insert into mrk (storeid, busidate, recordnumber, itemnumber, shipnumber, storeunitprice, unitcost, mrkquantity, mrkreasonnumber, updateuserid, updatedatetime, citem_yn, sell_cost, recycle_yn) values (${User.getUser().storeId}, trunc(sysdate), ${data.recordNumber}, ${data.scrapId}, '0', ${data.unitPrice}, ${data.unitCost}, ${data.mrkCount}, '00', ${User.getUser().uId}, sysdate, '${data.citemYN}', ${data.sellCost}, '${data.recycleYN}');"
    }

    /**
     * 更新报废，配合事务头脚使用
     */
    fun updateScrap(data: ScrapContractBean): String {
        return "update mrk set mrkquantity=${data.mrkCount}, updateuserid='${User.getUser().uId}',updatedatetime=sysdate where itemnumber='${data.scrapId}' and busidate=to_date('${MyTimeUtil.nowDate}','yyyy-mm-dd');"
    }

    /**
     * 删除报废，配合事务头脚使用
     */
    fun deleteScrap(data: ScrapContractBean): String {
        return "delete from mrk where itemnumber='${data.scrapId}' and busidate=to_date('${MyTimeUtil.nowDate}','yyyy-mm-dd');"
    }

    /**
     * 得到指定某天的所有报废信息
     */
    fun AllScrap(date: String): String {
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
            return "select midcategorynumber,categoryname from cat where categorynumber='80' and midcategorynumber!=' ' and microcategorynumber=' ' order by midcategorynumber\u0004"
        }

    /**
     * 报废80大类里的中类的item
     */
    fun getScrap80Item(midId: String): String {
        val time=MyTimeUtil.nowDate
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
     * 得到配送列表
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
     * 得到配送表下的商品
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
     * 更新配送单,配合事务使用
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
     * 更新配送单的商品,配合事务使用
     */
    fun updateAcceptanceItem(aib:AcceptanceItemBean):String{
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
     * 得到配送商
     */
    val getVendor:String
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
     * 创建配送单,配合事务使用
     */
    fun createAcceptance(ab:AcceptanceBean, date:String):String{
        val time="to_date('$date','yyyy-MM-dd')"
        return "insert into dlvhead " +
                "(storeID, dlvDate, requestNumber, vendorID, shipNumber,ordDate, plnDlvDate, " +
                "actDlvTime, dlvStatus, ordItemQty, dlvItemQty,retailTotal, costTotal, busiDate, updateUserID, updateDate) " +
                "values " +
                "('${User.getUser().storeId}',$time,'${ab.distributionId}','${ab.vendorId}','0',$time,$time,$time,'2',${ab.ordItemQTY},${ab.dlvItemQTY},${ab.retailTotal},${ab.costTotal},$time,'${User.getUser().uId}',sysdate);"
    }

    /**
     * 创建配送单下的商品，配合事务使用
     */
    fun createAcceptanceItem(aib:AcceptanceItemBean, date:String):String{
        val time="to_date('$date','yyyy-MM-dd')"
        return "insert into dlvdtl " +
                "(storeid,dlvdate,vendorid,itemnumber,shipnumber,dlvquantity,varquantity,storeunitprice, " +
                "unitcost,requestnumber,pmcode,supplierid,updateuserid,updatedate,varreason,sell_cost,trsquantity,hqquantity,dctrsquantity,ordquantity) " +
                "values" +
                "('${User.getUser().storeId}',$time,'${aib.vendorId}','${aib.itemId}','${aib.shipNumber}',${aib.dlvQuantity},${aib.varQuantity},${aib.storeUnitPrice}," +
                "${aib.unitCost},'${aib.distributionId}','-','${aib.supplierId}','${User.getUser().uId}',sysdate,null,${aib.sellCost},${aib.trsQuantity},${aib.hqQuantity},${aib.dctrsQuantity},${aib.ordQuantity});"
    }

    /**
     * 得到今天最大的配送单
     */
    fun getMaxAcceptanceId(date:String, num:String, type:Int):String{
        return if (type==1){
            "select max(requestNumber) value from dlvHead where DlvDate = to_date('$date','yyyy-MM-dd') and RequestNumber< 'A00000000' and RequestNumber like'$num%'\u0004"
        }else{
            "select max(requestNumber) value from rtnHead where rtnDate = to_date('$date','yyyy-MM-dd') and RequestNumber< 'A00000000' and RequestNumber like'$num%'\u0004"
        }
    }

    /**
     * 得到配送单号的前缀
     */
    fun getNowNum(date:String): String {
        var month=""
        val x=date.substring(5,7).toInt()
        when(date.substring(5,7).toInt()){
            1->month="A"
            2->month="B"
            3->month="C"
            4->month="D"
            5->month="E"
            6->month="F"
            7->month="G"
            8->month="H"
            9->month="I"
            10->month="J"
            11->month="K"
            12->month="L"
        }
        val year=date.substring(2,4)
        val day= date.substring(8)
        return year+month+day
    }

    /**
     * 得到创建验收输入的商品
     */
    fun getAcceptanceCommodity(ab:AcceptanceBean?, vendorId:String):String{
        var sql=""
        if (ab!=null){
            sql+="select * from (select plu.itemNumber,plu.stocktype,plu.order_item,plu.pluName,plu.storeUnitPrice,plu.VendorID,plu.SupplierID, " +
                    "plu.basic_cost unitCost,plu.sell_cost sell_cost,unit.UnitName,plu.shipNumber,orderBeginDate,orderEndDate, " +
                    "round(plu.sell_cost*(1+nvl(taxtype.taxRate,0)),6) tax_sell_cost, plu.pcs, plu.sale_item " +
                    "from plu left join unit on  unit.storeID=plu.storeid and unit.unitID=plu.SmallUnitID " +
                    "left join taxtype on taxtype.storeID=plu.storeID AND taxtype.taxID=plu.taxID " +
                    "where plu.StoreID='${User.getUser().storeId}' " +
                    "and vendorid like '%$vendorId%' " +
                    "and trim(SupplierID) is not null and trim(VendorID) is not null " +
                    "and itemnumber not in ("
            ab.allItems.forEach { sql+= "${it.itemId},"}//得到not in的品号
            sql=sql.substring(0,sql.length-1)//去掉逗号
            sql+=") ) where rownum<100\u0004"
        }else{
            sql="select * from (select plu.itemNumber,plu.stocktype,plu.order_item,plu.pluName,plu.storeUnitPrice,plu.VendorID,plu.SupplierID, " +
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
    fun getReturnAcceptanceCommodity(rab:ReturnAcceptanceBean?, vendorId:String):String{
        var sql=""
        if (rab!=null){
            sql+="select * from (select plu.itemNumber,plu.stocktype,plu.order_item,plu.pluName,plu.storeUnitPrice,plu.VendorID,plu.SupplierID, " +
                    "plu.basic_cost unitCost,plu.sell_cost sell_cost,unit.UnitName,plu.shipNumber,orderBeginDate,orderEndDate, " +
                    "round(plu.sell_cost*(1+nvl(taxtype.taxRate,0)),6) tax_sell_cost, plu.pcs, plu.sale_item " +
                    "from plu left join unit on  unit.storeID=plu.storeid and unit.unitID=plu.SmallUnitID " +
                    "left join taxtype on taxtype.storeID=plu.storeID AND taxtype.taxID=plu.taxID " +
                    "where plu.StoreID='${User.getUser().storeId}' " +
                    "and vendorid like '%$vendorId%' " +
                    "and trim(SupplierID) is not null and trim(VendorID) is not null " +
                    "and itemnumber not in ("
            rab.allItems.forEach { sql+= "${it.itemId},"}//得到not in的品号
            sql=sql.substring(0,sql.length-1)//去掉逗号
            sql+=") ) where rownum<100\u0004"
        }else{
            sql="select * from (select plu.itemNumber,plu.stocktype,plu.order_item,plu.pluName,plu.storeUnitPrice,plu.VendorID,plu.SupplierID, " +
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
    fun getJudgmentCommodity(aib: ArrayList<AcceptanceItemBean>, date: String):String{
        var sql="select * from dlvdtl where dlvdate=to_date('$date','yyyy-MM-dd') and itemnumber in("
        aib.forEach { sql+="'${it.itemId}'," }
        sql = sql.substring(0,sql.length-1)//去掉逗号
        sql+=")\u0004"
        return sql
    }

    /**
     * 检测输入的退货商品是否重复
     */
    fun getJudgmentReturnCommodity(raib:ArrayList<ReturnAcceptanceItemBean>, date:String):String{
        var sql="select * from rtndtl where rtndate=to_date('$date','yyyy-MM-dd') and itemnumber in ("
        raib.forEach { sql+="'${it.itemId}'," }
        sql = sql.substring(0,sql.length-1)//去掉逗号
        sql+=")\u0004"
        return sql
    }

    /**********************************************退货验收************************************************************/

    /**
     * 得到退货验收单
     */
    fun getReturnAcceptanceList(date: String):String{
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
     * 得到退货单下的商品
     */
    fun getReturnAcceptanceItemList(rab:ReturnAcceptanceBean,date:String):String{
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
     * 更新退货验收单，配合事务使用
     */
    fun updateReturnAcceptance(rab: ReturnAcceptanceBean):String{
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
     * 更新验收单下的商品，配合事务使用
     */
    fun updateReturnAcceptanceItem(raib:ReturnAcceptanceItemBean):String{
        return "update rtndtl set " +
                "rtnQuantity=${raib.rtnQuantity},updateUserId='${User.getUser().uId}',updateDate=sysdate " +
                "where storeId='${User.getUser().storeId}' " +
                "and itemNumber='${raib.itemId}' " +
                "and rtnDate=to_date('${raib.rtnDate}','yyyy-MM-dd') " +
                "and requestNumber='${raib.distributionId}';"
    }

    /**
     * 创建退货验收单，配合事务使用
     */
    fun createReturnAcceptance(rab:ReturnAcceptanceBean):String{
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
     * 穿件退货验收单下的商品，配合事务使用
     */
    fun createReturnAcceptanceItem(raib: ReturnAcceptanceItemBean):String{
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
    fun getAdjustmentList(date:String):String{
        return "select adj.recordnumber, adj.itemnumber, plu.pluname, (adj.actStockQuantity+adj.adjQuantity) currStockQuantity, adj.actstockquantity,adj.adjquantity, plu.shipnumber, adjreasonnumber " +
                "from adj left join plu on plu.storeid=adj.storeid and plu.itemnumber=adj.itemnumber " +
                "where adj.storeid='${User.getUser().storeId}' " +
                "and adj.busidate=to_date('$date','yyyy-MM-dd') " +
                "order by adj.recordnumber,adj.itemnumber"
    }

    /**
     * 库调的单品搜索
     */
    fun searchAdjustment(date:String, msg:String):String{
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
    fun getAdjustmentMaxId(date:String):String{
        return "select MAX(recordnumber) value from adj where busidate = to_date('$date','yyyy-MM-dd')"
    }

    /**
     * 创建库调,必须配合事务使用!
     */
    fun createAdjustment(ab:AdjustmentBean,date: String, recordNumber:Int):String{
        //创建库调
        var result=
                "Insert into adj " +
                "(StoreID,BusiDate,RecordNumber,Itemnumber,Shipnumber,StoreUnitPrice, " +
                "UnitCost,AdjQuantity,adj.ActStockQuantity,adjReasonNumber,UpdateUserID,UpdateDateTime) " +
                "Values " +
                "('${User.getUser().storeId}',to_date('$date','yyyy-MM-dd'),$recordNumber,'${ab.itemId}',${ab.shipNumber},${ab.storeUnitPrice}, " +
                "${ab.unitCost}, ${ab.adjQTY}, ${ab.actStockQTY}, ${ab.adjReasonNumber}, ${User.getUser().uId}, sysdate);"
        //更新库存
        result+=
                "update inv set accAdjQuantity=accAdjQuantity + ${ab.adjQTY} " +
                "where storeID='${User.getUser().storeId}' " +
                "and busiDate=to_date('$date','yyyy-MM-dd') " +
                "and ItemNumber='${ab.itemId}' " +
                "and shipNumber='${ab.shipNumber}';"
        return result
    }
}
