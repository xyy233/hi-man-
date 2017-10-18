package com.cstore.zhiyazhang.template.sql;

/**
 * Created by zhiya.zhang
 * on 2017/10/17 17:13.
 *
 * 获得SQL语句的类
 */

public class MySql {

    /**
     * 登录用的SQL语句
     * @param user_id 用户id
     * @return 查询sql语句
     */
    public static String getsignIn(String user_id) {
        return "select storeid,employeeid,employeename,emppassword,emptelphone,storechinesename,address,store_attr,(SELECT COUNT(*) CNT FROM CONT_ITEM X,PLU Y WHERE X.STOREID = Y.STOREID AND X.ITEMNO = Y.ITEMNUMBER AND TO_CHAR(SYSDATE-1,'YYYYMMDD') BETWEEN X.TRAN_DATE_ST AND X.TRAN_DATE_ED) cnt " +
                "from (select A.storeid,A.Employeeid,A.Employeename,A.Emppassword,A.Emptelphone,B.Storechinesename,B.Address,B.Store_Attr from employee A,store B) " +
                "where employeeid='"+user_id+"'\u0004";
    }
}
