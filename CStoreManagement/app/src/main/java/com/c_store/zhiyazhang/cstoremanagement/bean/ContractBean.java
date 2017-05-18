package com.c_store.zhiyazhang.cstoremanagement.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by zhiya.zhang
 * on 2017/5/10 13:36.
 */

public class ContractBean implements Serializable {

    @SerializedName("item_id")
    private String cId;

    @SerializedName("item_name")
    private String cName;
    //库存
    @SerializedName("inv_qty")
    private int inventory;
    //今晚到量
    @SerializedName("on_qty")
    private int tonightCount;
    //总部订量
    @SerializedName("hord_qty")
    private int todayGh;
    //门店增减订量
    @SerializedName("sord_qty")
    private int todayStore;
    //实际订量
    @SerializedName("act_ord_qty")
    private int todayCount;
    //每次增量数
    @SerializedName("step_qty")
    private int stepQty;
    //最小量
    @SerializedName("min_qty")
    private int minQty;
    //最大量
    @SerializedName("max_qty")
    private int maxQty;
    //零售价
    @SerializedName("price")
    private double cPrice;
    //图片URL
    @SerializedName("image_url")
    private String img_url;
    //告诉后台动作
    @SerializedName("action")
    private String action;

    private boolean isChange;

    public double getcPrice() {
        return cPrice;
    }

    public void setcPrice(double cPrice) {
        this.cPrice = cPrice;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public int getInventory() {
        return inventory;
    }

    public void setInventory(int inventory) {
        this.inventory = inventory;
    }

    public int getTonightCount() {
        return tonightCount;
    }

    public void setTonightCount(int tonightCount) {
        this.tonightCount = tonightCount;
    }

    public int getTodayGh() {
        return todayGh;
    }

    public void setTodayGh(int todayGh) {
        this.todayGh = todayGh;
    }

    public int getTodayStore() {
        return todayStore;
    }

    public void setTodayStore(int todayStore) {
        this.todayStore = todayStore;
    }

    public int getTodayCount() {
        return todayCount;
    }

    public void setTodayCount(int todayCount) {
        this.todayCount = todayCount;
    }

    public int getStepQty() {
        return stepQty;
    }

    public void setStepQty(int stepQty) {
        this.stepQty = stepQty;
    }

    public int getMinQty() {
        return minQty;
    }

    public void setMinQty(int minQty) {
        this.minQty = minQty;
    }

    public int getMaxQty() {
        return maxQty;
    }

    public void setMaxQty(int maxQty) {
        this.maxQty = maxQty;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isChange() {
        return isChange;
    }

    public void setChange(boolean change) {
        isChange = change;
    }
}
