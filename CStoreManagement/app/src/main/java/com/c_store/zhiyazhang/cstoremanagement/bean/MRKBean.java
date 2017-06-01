package com.c_store.zhiyazhang.cstoremanagement.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by zhiya.zhang
 * on 2017/5/31 12:11.
 */

public class MRKBean implements Serializable {
    @SerializedName("storeid")
    private String storeId;
    @SerializedName("busidate")
    private String busiDate;
    @SerializedName("recordnumber")
    private int recordNumber;
    @SerializedName("itemnumber")
    private String itemNumber;
    @SerializedName("shipnumber")
    private String shipNumber;
    @SerializedName("storeunitprice")
    private double storeUnitPrice;
    @SerializedName("unitcost")
    private double unitCost;
    @SerializedName("mrkquantity")
    private int mrkQuantity;
    @SerializedName("mrkreasonnumber")
    private String mrkReasonNumber;
    @SerializedName("updateuserid")
    private String updateUserId;
    @SerializedName("updatedatetime")
    private String updateDateTime;
    @SerializedName("citem_yn")
    private String citemYN;
    @SerializedName("sell_cost")
    private double sellCost;
    @SerializedName("recycle_yn")
    private String recycleYN;

    /**
     *
     * @param storeId 商店id
     * @param recordNumber 今天的第几个
     * @param itemNumber 品号
     * @param storeUnitPrice 单价
     * @param unitCost 成本
     * @param mrkQuantity 报废数
     * @param updateUserId 更新操作人
     * @param citemYN 是否是承包
     * @param sellCost 卖的成本
     * @param recycleYN 是否回收
     */
    public MRKBean(String storeId, int recordNumber, String itemNumber, double storeUnitPrice, double unitCost, int mrkQuantity, String updateUserId, String citemYN, double sellCost, String recycleYN) {
        this.storeId = storeId;
        this.recordNumber = recordNumber;
        this.itemNumber = itemNumber;
        this.storeUnitPrice = storeUnitPrice;
        this.unitCost = unitCost;
        this.mrkQuantity = mrkQuantity;
        this.updateUserId = updateUserId;
        this.citemYN = citemYN;
        this.sellCost = sellCost;
        this.recycleYN = recycleYN;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getBusiDate() {
        return busiDate;
    }

    public void setBusiDate(String busiDate) {
        this.busiDate = busiDate;
    }

    public int getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(int recordNumber) {
        this.recordNumber = recordNumber;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getShipNumber() {
        return shipNumber;
    }

    public void setShipNumber(String shipNumber) {
        this.shipNumber = shipNumber;
    }

    public double getStoreUnitPrice() {
        return storeUnitPrice;
    }

    public void setStoreUnitPrice(double storeUnitPrice) {
        this.storeUnitPrice = storeUnitPrice;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }

    public int getMrkQuantity() {
        return mrkQuantity;
    }

    public void setMrkQuantity(int mrkQuantity) {
        this.mrkQuantity = mrkQuantity;
    }

    public String getMrkReasonNumber() {
        return mrkReasonNumber;
    }

    public void setMrkReasonNumber(String mrkReasonNumber) {
        this.mrkReasonNumber = mrkReasonNumber;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    public String getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(String updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public String getCitemYN() {
        return citemYN;
    }

    public void setCitemYN(String citemYN) {
        this.citemYN = citemYN;
    }

    public double getSellCost() {
        return sellCost;
    }

    public void setSellCost(double sellCost) {
        this.sellCost = sellCost;
    }

    public String getRecycleYN() {
        return recycleYN;
    }

    public void setRecycleYN(String recycleYN) {
        this.recycleYN = recycleYN;
    }

    @Override
    public String toString() {
        return "MRKBean{" +
                "storeId='" + storeId + '\'' +
                ", busiDate='" + busiDate + '\'' +
                ", recordNumber=" + recordNumber +
                ", itemNumber='" + itemNumber + '\'' +
                ", shipNumber='" + shipNumber + '\'' +
                ", storeUnitPrice=" + storeUnitPrice +
                ", unitCost=" + unitCost +
                ", mrkQuantity=" + mrkQuantity +
                ", mrkReasonNumber='" + mrkReasonNumber + '\'' +
                ", updateUserId='" + updateUserId + '\'' +
                ", updateDateTime='" + updateDateTime + '\'' +
                ", citemYN='" + citemYN + '\'' +
                ", sellCost=" + sellCost +
                ", recycleYN='" + recycleYN + '\'' +
                '}';
    }
}
