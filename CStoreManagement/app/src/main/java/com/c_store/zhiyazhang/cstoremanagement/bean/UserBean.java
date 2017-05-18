package com.c_store.zhiyazhang.cstoremanagement.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by zhiya.zhang
 * on 2017/5/8 13:50.
 */

public class UserBean implements Serializable {
    @SerializedName("user_id")
    private String uid;
    private String password;
    @SerializedName("user_name")
    private String uName;
    @SerializedName("dept_id")
    private String uEname;
    @SerializedName("dept_name")
    private String deptName;
    @SerializedName("start_date")
    private String startDate;
    @SerializedName("end_date")
    private String endDate;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getuEname() {
        return uEname;
    }

    public void setuEname(String uEname) {
        this.uEname = uEname;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getstartDate() {
        return startDate;
    }

    public void setstartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getendDate() {
        return endDate;
    }

    public void setendDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "{\"user_id\":\""
                + uid + "\",\"user_name\":\""
                + uName + "\",\"dept_id\":\""
                + uEname + "\",\"dept_name\":\""
                + deptName + "\",\"start_date\":\""
                + startDate + "\",\"end_date\":\""
                + endDate + "\"}";
    }
}
