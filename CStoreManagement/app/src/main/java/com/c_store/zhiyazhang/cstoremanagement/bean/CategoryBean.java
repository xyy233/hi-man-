package com.c_store.zhiyazhang.cstoremanagement.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by zhiya.zhang
 * on 2017/5/27 14:51.
 */

public class CategoryBean implements Serializable {

    @SerializedName("categorynumber")
    private int categorynumber;

    @SerializedName("categoryname")
    private String categoryname;


    public int getCategorynumber() {
        return categorynumber;
    }

    public void setCategorynumber(int categorynumber) {
        this.categorynumber = categorynumber;
    }

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

}
