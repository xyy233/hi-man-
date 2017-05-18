package com.c_store.zhiyazhang.cstoremanagement.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by zhiya.zhang
 * on 2017/5/9 16:33.
 */

public class ContractTypeBean implements Serializable {
    //类型id
    @SerializedName("type_id")
    private String typeId;
    //类型名
    @SerializedName("type_name")
    private String typeName;
    //库存
    @SerializedName("inv_qty")
    private int inventory;
    //今晚到量
    @SerializedName("on_qty")
    private int tonightCount;
    //总部订量
    @SerializedName("hord_qty")
    private int todayGh;
    //门店订量
    @SerializedName("sord_qty")
    private int todayStore;
    //实际订量
    @SerializedName("act_ord_qty")
    private int todayCount;
    //上一周销量
    @SerializedName("wk1_sqty")
    private int wk1Sqty;
    //上两周销量
    @SerializedName("wk2_sqty")
    private int wk2Sqty;
    //上三周销量
    @SerializedName("wk3_sqty")
    private int wk3Sqty;
    //预测销量
    @SerializedName("exp_qty")
    private int expQty;
    //建议订量
    @SerializedName("sug_qty")
    private int sugQty;
    //最小订量
    @SerializedName("min_qty")
    private int minQty;
    //最大订量
    @SerializedName("max_qty")
    private int maxQty;


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
    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
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

    public int getWk1Sqty() {
        return wk1Sqty;
    }

    public void setWk1Sqty(int wk1Sqty) {
        this.wk1Sqty = wk1Sqty;
    }

    public int getWk2Sqty() {
        return wk2Sqty;
    }

    public void setWk2Sqty(int wk2Sqty) {
        this.wk2Sqty = wk2Sqty;
    }

    public int getWk3Sqty() {
        return wk3Sqty;
    }

    public void setWk3Sqty(int wk3Sqty) {
        this.wk3Sqty = wk3Sqty;
    }

    public int getExpQty() {
        return expQty;
    }

    public void setExpQty(int expQty) {
        this.expQty = expQty;
    }

    public int getSugQty() {
        return sugQty;
    }

    public void setSugQty(int sugQty) {
        this.sugQty = sugQty;
    }
}
