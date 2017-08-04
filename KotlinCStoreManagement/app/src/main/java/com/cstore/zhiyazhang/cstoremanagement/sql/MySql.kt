package com.cstore.zhiyazhang.cstoremanagement.sql

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
        return "select storeid,employeeid,employeename,emppassword,emptelphone,storechinesename,address,(SELECT COUNT(*) CNT FROM CONT_ITEM X,PLU Y WHERE X.STOREID = Y.STOREID AND X.ITEMNO = Y.ITEMNUMBER AND TO_CHAR(SYSDATE-1,'YYYYMMDD') BETWEEN X.TRAN_DATE_ST AND X.TRAN_DATE_ED) cnt from app_user_view where employeeid='$uid'"
    }

    /**
     * 通过扫码得到的数据加到sql查询语句的字符串中然后返回新的sql语句

     * @param data 扫码得到的数据
     * *
     * @return 新的sql语句
     */
    fun getScrapByBarcode(data: String): String {
        return "select * from app_mrkitem_view where itemnumber = (select itemnumber from ITEMPLU where plunumber='$data')"
    }

    /**
     * 获得报废的类名

     * @return 新的sql语句
     */
    val scrapCategory: String
        get() = "select distinct categorynumber, categoryname from cat where midcategorynumber=' ' and microcategorynumber=' ' and categorynumber in (select distinct categorynumber from app_mrkitem_view where barcode_yn = 'N')order by categorynumber"

    /**
     * 从类id获得所有报废品

     * @param categoryId 类的id
     * *
     * @return 新的sql语句
     */
    fun getAllScrapByCategory(categoryId: String): String {
        return "select * from app_mrkitem_view where barcode_yn='N' and categorynumber='$categoryId' order by mrk_date desc"
    }

    /**
     * 获得大类信息的存储过程
     */
    fun ordT2(): String =
            "call scb02_new_p01('${MyTimeUtil.nowDate}','${MyTimeUtil.nowDate}','0')\u000c"

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
                    "AND ord.orderDate=to_date('${MyTimeUtil.nowDate}','YYYY-MM-DD') " +
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
                    " x.itemnumber,x.pluname,x.quantity,x.invquantity,x.ordactualquantity,x.dlv_qty,x.d1_dfs,x.INCREASEORDERQUANTITY,x.minimaorderquantity,x.maximaorderquantity, " +
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
    fun getAllShelf(): String =
            "Select gondra.GondraNumber,gondra.GondraName " +
                    "from itemgondra,ord,gondra " +
                    "where itemgondra.StoreID= '${User.getUser().storeId}' " +
                    "and ord.StoreID=itemgondra.StoreID " +
                    "and ord.itemNumber = itemgondra.itemNumber " +
                    "and ord.orderdate=to_date('${MyTimeUtil.nowDate}','YYYY/MM/DD') " +
                    "and gondra.Storeid=itemgondra.StoreID " +
                    "and gondra.GondraNumber=itemgondra.GondraNumber " +
                    "group by gondra.GondraNumber,  gondra.GondraName " +
                    "order by gondra.GondraNumber"

    /**
     * 通过货架id获得商品
     */
    fun getItemByShelfId(shelfId: String, sort: String): String =
            "Select to_char(x.sell_cost, '999,999,990.000000') sell_cost," +
                    " x.itemnumber,x.pluname,x.quantity,x.invquantity,x.ordactualquantity,x.dlv_qty,x.d1_dfs,x.INCREASEORDERQUANTITY,x.minimaorderquantity,x.maximaorderquantity, " +
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
    fun updateOrdItem(itemId: String, value: Int) = "update ord set updateuserid='${User.getUser().uId}', updatedate=sysdate, ordstatus='1', ordactualquantity=$value where storeid='${User.getUser().storeId}' and orderdate=to_date('${MyTimeUtil.nowDate}','yyyy-MM-dd') and itemnumber='$itemId'; update ord_t2 set ordactualquantity=$value,status='1' where orderdate=to_date('${MyTimeUtil.nowDate}','yyyy-MM-dd') and itemnumber='$itemId';"


    /**
     * 单品订货去搜索
     */
    fun unitOrder(value: String) =
            "select * from " +
                    "(Select to_char(x.sell_cost, '999,999,990.000000') sell_cost," +
                    "x.itemnumber,x.pluname,x.quantity,x.invquantity,x.ordactualquantity,x.dlv_qty,x.d1_dfs,x.INCREASEORDERQUANTITY,x.minimaorderquantity,x.maximaorderquantity, " +
                    "substr(p.signType,12,1) s_returntype, " +
                    "round(p.storeunitprice,2) storeunitprice " +
                    "From ord_t2 x,plu p " +
                    "where x.itemnumber = p.itemnumber(+) " +
                    "and (x.itemnumber like '%$value%' or x.pluname like'%$value%')) " +
                    "where rownum<100" +
                    "order by itemnumber"

    /**
     * 获得自用品分类
     */
    val getSelf = "select * from cat where categorynumber='99' and midcategorynumber !=' ' and microcategorynumber !=' '"

    /**
     * 通过自用品id获得商品
     */
    fun getSelfBySelfId(selfId: String, orderBy: String) = "Select to_char(x.sell_cost, '999,999,990.000000') sell_cost," +
            "x.itemnumber,x.pluname,x.quantity,x.invquantity,x.ordactualquantity,x.dlv_qty,x.d1_dfs,x.INCREASEORDERQUANTITY,x.minimaorderquantity,x.maximaorderquantity, " +
            "substr(p.signType,12,1) s_returntype, " +
            "round(p.storeunitprice,2) storeunitprice " +
            "From ord_t2 x,plu p, " +
            "(select itemnumber item_no from itemgondra where storeid='111112' and gondranumber like '%' " +
            "group by itemnumber) y " +
            "where x.itemnumber= y.item_no(+) " +
            "and x.itemnumber = p.itemnumber(+) " +
            "and x.categorynumber like '99' " +
            "and x.midCategoryNumber like '$selfId' " +
            "AND Fresh_YN='N' $orderBy"

    /**
     * 获得新品档期id
     */
    val getNewItemId = "select p.in_th_code,'第'|| p.in_th_code ||'期' as title from (select in_th_code from plu where in_th_code is not null group by in_th_code order by in_th_code desc) p where rownum<5"

    /**
     * 根据档期获得商品
     */
    fun getNewItemById(id: String, orderBy: String) =
            "Select to_char(x.sell_cost, '999,999,990.000000') sell_cost,x.itemnumber,x.pluname,x.quantity,x.invquantity,x.ordactualquantity,x.dlv_qty,x.d1_dfs,x.INCREASEORDERQUANTITY,x.minimaorderquantity,x.maximaorderquantity, substr(p.signType,12,1) s_returntype, round(p.storeunitprice,2) storeunitprice From ord_t2 x,plu p where x.itemnumber = p.itemnumber(+) and x.categorynumber like '%' and x.midCategoryNumber like '%' AND nvl(x.market_date,TO_DATE('1970-01-01','YYYY-MM-DD'))>=(sysdate-60 ) AND x.item_level='L0' AND x.IN_TH_CODE like'$id' $orderBy"

    /**
     * 获得促销品
     */
    fun getPromotion(orderBy: String) = "Select to_char(x.sell_cost, '999,999,990.000000') sell_cost, x.itemnumber,x.pluname,x.quantity,x.invquantity,x.ordactualquantity,x.dlv_qty,x.d1_dfs,x.INCREASEORDERQUANTITY,x.minimaorderquantity,x.maximaorderquantity,substr(p.signType,12,1) s_returntype,round(p.storeunitprice,2) storeunitprice From ord_t2 x,plu p where x.itemnumber = p.itemnumber(+) and x.categorynumber like '%' and x.midCategoryNumber like '%' and pro_yn ='Y' $orderBy"

    /**
     * 报废商品

     * @param user 操作者
     * *
     * @param scbs 所有当前报废商品
     * *
     * @return 新的sql语句
     */
    /*
    @NonNull
    public static String getScrapSql(User user, ArrayList<ScrapContractBean> scbs) {
        ScrapSQLBean ssqlb = new ScrapSQLBean();
        ArrayList<MRKBean> mrkbs = new ArrayList<MRKBean>();
        int i = 0;
        for (ScrapContractBean scb :
                scbs) {
            i++;
            if (scb.getIsScrap() == 0) {
                mrkbs.add(new MRKBean(user.getStoreId(), i, scb.getScrapId(), scb.getUnitPrice(), scb.getUnitCost(), scb.getNowMrkCount(), user.getUid(), scb.getCitemYN(), scb.getSellCost(), scb.getRecycleYN()));
            }
        }
        *//*后台需要sql在前params在后，因此不用gson，手动写好
        ssqlb.setParams(mrkbs);
        ssqlb.setSql("insert into mrk (storeid, busidate, recordnumber, itemnumber, shipnumber, storeunitprice, unitcost, mrkquantity, mrkreasonnumber, updateuserid, updatedatetime, citem_yn, sell_cost, recycle_yn) values (?*//**//*storeid*//**//*, trunc(sysdate), ?*//**//*recordnumber*//**//*, ?*//**//*itemnumber*//**//*, ?*//**//*shipnumber*//**//*, ?*//**//*storeunitprice*//**//*, ?*//**//*unitcost*//**//*, ?*//**//*mrkquantity*//**//*, '00', ?*//**//*updateuserid*//**//*, sysdate, ?*//**//*citem_yn*//**//*, ?*//**//*sell_cost*//**//*, ?*//**//*recycle_yn*//**//*)");*//*
        return "{\"sql\":\"insert into mrk (storeid, busidate, recordnumber, itemnumber, shipnumber, storeunitprice, unitcost, mrkquantity, mrkreasonnumber, updateuserid, updatedatetime, citem_yn, sell_cost, recycle_yn) values (?*//*storeid*//*, trunc(sysdate), ?*//*recordnumber*//*, ?*//*itemnumber*//*, '0', ?*//*storeunitprice*//*, ?*//*unitcost*//*, ?*//*mrkquantity*//*, '00', ?*//*updateuserid*//*, sysdate, ?*//*citem_yn*//*, ?*//*sell_cost*//*, ?*//*recycle_yn*//*)\",\"params\":"+new Gson().toJson(mrkbs)+"}";
    }*/
}
