package com.c_store.zhiyazhang.cstoremanagement.bean;

import java.util.ArrayList;

/**
 * Created by zhiya.zhang
 * on 2017/5/31 12:03.
 */

public class ScrapSQLBean {
    private ArrayList<MRKBean> params;
    private String sql;

    public ArrayList<MRKBean> getParams() {
        return params;
    }

    public void setParams(ArrayList<MRKBean> params) {
        this.params = params;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
