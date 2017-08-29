package com.cstore.zhiyazhang.cstoremanagement.sql

import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapContractBean
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil

/**
 * Created by zhiya.zhang
 * on 2017/5/24 11:40.
 * 得到sql语句然后用socket发送sql语句给服务器查询,活久见,很强很无敌
 */

object MySql {

    /**
     * 事务头，用于执行批量sql语句
     */
    val affairHeader = "begin "

    /**
     * 事务脚，用于执行批量sql语句
     */
    val affairFoot = " commit;exception when others then rollback;end;"

    /**
     * 传入帐号得到新生成的sql语句

     * @param uid 输入框内的帐号
     * *
     * @return 新的sql语句
     */
    fun SignIn(uid: String): String {
        return "select storeid,employeeid,employeename,emppassword,emptelphone,storechinesename,address,(SELECT COUNT(*) CNT FROM CONT_ITEM X,PLU Y WHERE X.STOREID = Y.STOREID AND X.ITEMNO = Y.ITEMNUMBER AND TO_CHAR(SYSDATE-1,'YYYYMMDD') BETWEEN X.TRAN_DATE_ST AND X.TRAN_DATE_ED) cnt from (select A.storeid,A.Employeeid,A.Employeename,A.Emppassword,A.Emptelphone,B.Storechinesename,B.Address from employee A,store B) where employeeid='$uid'"
    }

    /**
     * 获得大类信息的存储过程
     */
    fun ordT2(): String =
            "call scb02_new_p01('${MyTimeUtil.tomorrowDate}','${MyTimeUtil.tomorrowDate}','0')\u000c"

    /**
     * 检测是否存在正确存储过程
     */
    val judgmentOrdt2 = "select distinct to_char(orderdate,'yyyy-mm-dd') orderdate from ord_t2"

    /**
     * 获得大类信息
     */
    fun getAllCategory(): String =
            "select plu.categoryNumber,cat.categoryName,count(*) tot_sku," +
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
                    "AND plu.itemNumber=ord.itemNumber " +
                    "AND ord.orderDate=to_date('${MyTimeUtil.tomorrowDate}','YYYY-MM-DD') " +
                    "AND plu.storeID=cat1.storeID " +
                    "AND plu.categoryNumber=cat1.categoryNumber " +
                    "AND plu.midCategoryNumber=cat1.midCategoryNumber " +
                    "AND trim(cat1.midCategoryNumber) is not null " +
                    "AND trim(cat1.microCategoryNumber) is null " +
                    "AND cat1.GROUP_YN='N' AND cat.categoryNumber<>'99' " +
                    "GROUP BY ord.orderDate,plu.categoryNumber,cat.categoryName " +
                    "order by PLU.CATEGORYNUMBER"

    /**
     * 通过大类id获得商品
     */
    fun getItemByCategoryId(categoryId: String, sort: String): String =
            "Select to_char(x.sell_cost, '999,999,990.000000') sell_cost," +
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
                    "AND Fresh_YN='N' $sort"

    /**
     * 得到货架分类
     */
    val getAllShelf =
            "Select gondra.GondraNumber,gondra.GondraNumber || ' - ' || gondra.GondraName as gondraname,count(*) tot_sku,sum(decode(sign(ord.ordActualQuantity+ord.ordActualQuantity1), 1,1,0)) ord_sku,sum((ord.ordActualQuantity+ord.ordActualQuantity1)*ord.storeUnitPrice) amt " +
                    "from itemgondra,ord,gondra " +
                    "where itemgondra.StoreID= '${User.getUser().storeId}' " +
                    "and ord.StoreID=itemgondra.StoreID " +
                    "and ord.itemNumber = itemgondra.itemNumber " +
                    "and ord.orderdate=to_date('${MyTimeUtil.tomorrowDate}','yyyy-MM-dd') " +
                    "and gondra.Storeid=itemgondra.StoreID " +
                    "and gondra.GondraNumber=itemgondra.GondraNumber " +
                    "group by gondra.GondraNumber,  gondra.GondraName " +
                    "order by gondra.GondraNumber"

    /**
     * 通过货架id获得商品
     */
    fun getItemByShelfId(shelfId: String, sort: String): String =
            "Select to_char(x.sell_cost, '999,999,990.000000') sell_cost," +
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
                    "and trim(y.item_no) is not null $sort"

    /**
     * 更新单品语句配合事务头和脚使用
     */
    fun updateOrdItem(itemId: String, value: Int) = "update ord set updateuserid='${User.getUser().uId}', updatedate=sysdate, ordstatus='1', ordactualquantity=$value where storeid='${User.getUser().storeId}' and orderdate=to_date('${MyTimeUtil.tomorrowDate}','yyyy-MM-dd') and itemnumber='$itemId'; update ord_t2 set ordactualquantity=$value,status='1' where orderdate=to_date('${MyTimeUtil.tomorrowDate}','yyyy-MM-dd') and itemnumber='$itemId';"


    /**
     * 单品订货去搜索
     */
    fun unitOrder(value: String) =
            "select * from " +
                    "(Select distinct x.itemnumber， to_char(x.sell_cost, '999,999,990.000000') sell_cost," +
                    "x.pluname,x.quantity,x.invquantity,x.ordactualquantity,x.dlv_qty,x.d1_dfs,x.INCREASEORDERQUANTITY,x.minimaorderquantity,x.maximaorderquantity,x.ordertype,x.pro_yn, " +
                    "substr(p.signType,12,1) s_returntype, " +
                    "round(p.storeunitprice,2) storeunitprice " +
                    "From ord_t2 x,plu p, itemplu i " +
                    "where x.itemnumber = p.itemnumber " +
                    "and i.itemnumber=x.itemnumber " +
                    "and (x.itemnumber like '%$value%' or x.pluname like'%$value%' or i.plunumber like '%$value%')) " +
                    "where rownum<100" +
                    "order by itemnumber"

    /**
     * 获得自用品分类
     */
    val getSelf = "select c.midcategorynumber,c.categoryname, " +
            "count(*) tot_sku, " +
            "sum(decode(sign(a.ordActualQuantity+a.ordActualQuantity1), 1,1,0)) ord_sku, " +
            "to_char(sum((a.ordActualQuantity+a.ordActualQuantity1)* a.sell_cost),'999,999,990.00') amt " +
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
            "order by 1"

    /**
     * 通过自用品id获得商品
     */
    fun getSelfBySelfId(selfId: String, orderBy: String) = "select p.itemnumber,p.pluname,x.ordactualquantity,p.midcategorynumber,p.minimaorderquantity,p.maximaorderquantity,p.increaseorderquantity, to_char(p.sell_cost,'999,999,990.00') storeunitprice,x.inv_qty,x.dlv_qty,p.ordertype " +
            "from ord x, plu p " +
            "where x.itemnumber=p.itemnumber " +
            "and x.orderdate=to_date('${MyTimeUtil.tomorrowDate}','yyyy-MM-dd') " +
            "and p.categorynumber =99 " +
            "and p.midcategorynumber=$selfId $orderBy"

    /**
     * 获得促销品和新品统计
     */
    val getNewItemId = "select * from (select distinct in_th_code,'第'|| in_th_code ||'期' as title, sum(tot_sku) tot_sku, sum(ord_sku) ord_sku, sum(amt) amt " +
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
            "from (Select to_char(x.sell_cost, '999,999,990.000000') sell_cost, " +
            "x.itemnumber,x.pluname,x.quantity,x.invquantity,x.ordactualquantity,x.ordactualquantity1,x.dlv_qty,x.d1_dfs,x.INCREASEORDERQUANTITY,x.minimaorderquantity,x.maximaorderquantity, " +
            "substr(p.signType,12,1) s_returntype, " +
            "round(p.storeunitprice,2) storeunitprice " +
            "From ord_t2 x,plu p " +
            "where x.itemnumber = p.itemnumber(+) " +
            "and x.pro_yn ='Y') " +
            "order by 1 desc"

    /**
     * 根据档期获得商品
     */
    fun getNewItemById(id: String, orderBy: String) =
            "Select to_char(x.sell_cost, '999,999,990.000000') sell_cost,x.itemnumber,x.pluname,x.quantity,x.invquantity,x.ordactualquantity,x.dlv_qty,x.d1_dfs,x.INCREASEORDERQUANTITY,x.minimaorderquantity,x.maximaorderquantity,x.dms,x.ordertype,x.pro_yn, substr(p.signType,12,1) s_returntype, round(p.storeunitprice,2) storeunitprice " +
                    "From ord_t2 x,plu p " +
                    "where x.itemnumber = p.itemnumber(+) " +
                    "AND nvl(x.market_date,TO_DATE('1970-01-01','YYYY-MM-DD'))>=(sysdate-60 ) " +
                    "AND x.item_level='L0' " +
                    "AND x.IN_TH_CODE like'%$id%' $orderBy"

    /**
     * 获得促销品
     */
    fun getPromotion(orderBy: String) = "Select to_char(x.sell_cost, '999,999,990.000000') sell_cost, x.itemnumber,x.pluname,x.quantity,x.invquantity,x.ordactualquantity,x.dlv_qty,x.d1_dfs,x.INCREASEORDERQUANTITY,x.minimaorderquantity,x.maximaorderquantity,x.dms,x.ordertype,x.pro_yn,substr(p.signType,12,1) s_returntype,round(p.storeunitprice,2) storeunitprice From ord_t2 x,plu p where x.itemnumber = p.itemnumber(+)and pro_yn ='Y' $orderBy"

    /**
     * 即期鲜食订货
     */
    val getFreshGroup1 =
            "SELECT distinct cat.categoryNumber,cat.midCategoryNumber," +
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
                    "ORDER BY cat.categoryNumber ||'-'|| cat.midCategoryNumber"

    /**
     * 其他鲜食订货
     */
    val getFreshGroup2 =
            "SELECT distinct cat.categoryNumber, cat.midCategoryNumber," +
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
                    "ORDER BY cat.categoryNumber ||'-'|| cat.midCategoryNumber"

    /**
     * 根据大类、中类获得鲜食
     */
    fun getFreashItem(categoryId: String, midId: String, orderBy: String) =
            "Select to_char(x.sell_cost, '999,999,990.000000') sell_cost," +
                    "x.itemnumber,x.pluname,x.quantity,x.invquantity,x.ordactualquantity,x.dlv_qty,x.d1_dfs,x.INCREASEORDERQUANTITY,x.minimaorderquantity,x.maximaorderquantity, x.dms,x.ordertype,x.pro_yn," +
                    "substr(p.signType,12,1) s_returntype," +
                    "round(p.storeunitprice,2) storeunitprice " +
                    "From ord_t2 x,plu p " +
                    "where x.itemnumber = p.itemnumber " +
                    "and x.categorynumber = '$categoryId' " +
                    "and x.midCategoryNumber = '$midId' $orderBy"

    /**
     * 报废商品
     * @param user 操作者
     * *
     * @param scbs 所有当前报废商品
     * *
     * @return 新的sql语句
     */
    fun getScrapSql(data: String) = "{\"sql\":\"insert into mrk (storeid, busidate, recordnumber, itemnumber, shipnumber, storeunitprice, unitcost, mrkquantity, mrkreasonnumber, updateuserid, updatedatetime, citem_yn, sell_cost, recycle_yn) values (?storeid, trunc(sysdate), ?recordnumber, ?itemnumber, '0', ?storeunitprice, ?unitcost, ?mrkquantity, '00', ?updateuserid, sysdate, ?citem_yn, ?sell_cost, ?recycle_yn)\",\"params\":$data}"

    /**
     * 创建报废,配合事务头脚使用
     */
    fun insertScrap(data:ScrapContractBean)="insert into mrk (storeid, busidate, recordnumber, itemnumber, shipnumber, storeunitprice, unitcost, mrkquantity, mrkreasonnumber, updateuserid, updatedatetime, citem_yn, sell_cost, recycle_yn) values (${User.getUser().storeId}, trunc(sysdate), ${data.recordNumber}, ${data.scrapId}, '0', ${data.unitPrice}, ${data.unitCost}, ${data.mrkCount}, '00', ${User.getUser().uId}, sysdate, '${data.citemYN}', ${data.sellCost}, '${data.recycleYN}');"

    /**
     * 更新报废，配合事务头脚使用
     */
    fun updateScrap(data:ScrapContractBean)="update mrk set mrkquantity=${data.mrkCount}, updateuserid='${User.getUser().uId}',updatedatetime=sysdate where itemnumber='${data.scrapId}' and busidate=to_date('${MyTimeUtil.nowDate}','yyyy-mm-dd');"

    /**
     * 删除报废，配合事务头脚使用
     */
    fun deleteScrap(data:ScrapContractBean)="delete from mrk where itemnumber='${data.scrapId}' and busidate=to_date('${MyTimeUtil.nowDate}','yyyy-mm-dd');"

    /**
     * 得到指定某天的所有报废信息
     */
    fun AllScrap(date:String)=" select a.itemnumber,to_char(m.busidate,'yyyy-mm-dd') busidate,a.pluname,a.STOREUNITPRICE,a.UNITCOST,a.SELL_COST,a.CITEM_YN,a.recycle_yn,a.barcode_yn, m.mrkquantity,m.recordnumber from app_mrkitem_view a, mrk m where a.itemnumber=m.itemnumber and busidate=to_date('$date','yyyy-mm-dd') order by itemnumber"

    /**
     * 通过扫码得到的数据加到sql查询语句的字符串中然后返回新的sql语句
     *
     * @param data 扫码得到的数据
     */
    fun getScrapByMessage(data: String): String {
        return "select distinct itemnumber, pluname, categorynumber, storeunitprice, unitcost, sell_cost, citem_yn, recycle_yn, barcode_yn, mrk_date, sale_date, dlv_date from app_mrkitem_view where (itemnumber in (select distinct itemnumber from ITEMPLU where plunumber like '%$data%') or itemnumber like '%$data%' or pluname like '%$data%') and rownum<50"
    }
}
