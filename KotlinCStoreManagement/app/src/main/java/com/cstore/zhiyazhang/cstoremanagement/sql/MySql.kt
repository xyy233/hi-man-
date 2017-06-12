package com.cstore.zhiyazhang.cstoremanagement.sql

/**
 * Created by zhiya.zhang
 * on 2017/5/24 11:40.
 * 得到sql语句然后用socket发送sql语句给服务器查询,活久见,很强很无敌
 */

object MySql {

    /**
     * 传入帐号得到新生成的sql语句

     * @param uid 输入框内的帐号
     * *
     * @return 新的sql语句
     */
    fun SignIn(uid: String): String {
        return "select * from app_user_view where employeeid='$uid'"
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

    /*    */
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
