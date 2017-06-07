package com.c_store.zhiyazhang.cstoremanagement.bean;

import android.content.Context;
import android.content.SharedPreferences;

import com.c_store.zhiyazhang.cstoremanagement.utils.MyApplication;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by zhiya.zhang
 * on 2017/5/8 13:50.
 */

public class UserBean implements Serializable {
    @SerializedName("storeid")
    private String storeId;
    @SerializedName("employeeid")
    private String uid;
    @SerializedName("emppassword")
    private String password;
    @SerializedName("employeename")
    private String uName;
    @SerializedName("emptelphone")
    private String telphone;
    @SerializedName("storechinesename")
    private String storeName;
    private String address;

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

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

    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }


    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "{\"storeid\":\""
                + storeId + "\",\"employeeid\":\""
                + uid + "\",\"emppassword\":\""
                + password + "\",\"employeename\":\""
                + uName + "\",\"emptelphone\":\""
                + telphone + "\",\"storechinesename\":\""
                + storeName + "\",\"address\":\""
                + address + "\"}";
    }

    public static UserBean getUser() {
        UserBean user = new UserBean();
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        user.setStoreId(preferences.getString("storeId", ""));
        user.setUid(preferences.getString("uid", ""));
        user.setuName(preferences.getString("uName", ""));
        user.setTelphone(preferences.getString("telphone", ""));
        user.setStoreName(preferences.getString("storeName", ""));
        user.setAddress(preferences.getString("address", ""));
        return user;
    }
}
