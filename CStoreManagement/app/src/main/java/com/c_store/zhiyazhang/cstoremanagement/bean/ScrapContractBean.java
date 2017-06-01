package com.c_store.zhiyazhang.cstoremanagement.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by zhiya.zhang
 * on 2017/5/24 11:06.
 */
public class ScrapContractBean implements Serializable {
    @SerializedName("itemnumber")
    private String scrapId; //品号
    @SerializedName("pluname")
    private String scrapName; //品名
    @SerializedName("categorynumber")
    private String categoryId; //类号
    @SerializedName("storeunitprice")
    private double unitPrice; //商品单价
    @SerializedName("unitcost")
    private double unitCost; //成本
    @SerializedName("sell_cost")
    private double sellCost; //卖价
    @SerializedName("citem_yn")
    private String citemYN; //是否承包
    @SerializedName("recycle_yn")
    private String recycleYN; //是否回收
    @SerializedName("barcode_yn")
    private String barcodeYN; //是否有条形码
    @SerializedName("mrk_date")
    private String mrkDate; //报废时间
    @SerializedName("sale_date")
    private String saleDate; //销售时间
    @SerializedName("dlv_date")
    private String dlvDate; //验收时间

    private int nowMrkCount;//现在的数量
    private int createDay;//数据库中的创建日期
    private int isNew;//是否是新添加的 2=老的
    private int isScrap;//是否已报废 0=未报废

    public String getScrapId() {
        return scrapId;
    }

    public void setScrapId(String scrapId) {
        this.scrapId = scrapId;
    }

    public String getScrapName() {
        return scrapName;
    }

    public void setScrapName(String scrapName) {
        this.scrapName = scrapName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }

    public double getSellCost() {
        return sellCost;
    }

    public void setSellCost(double sellCost) {
        this.sellCost = sellCost;
    }

    public String getCitemYN() {
        return citemYN;
    }

    public void setCitemYN(String citemYN) {
        this.citemYN = citemYN;
    }

    public String getRecycleYN() {
        return recycleYN;
    }

    public void setRecycleYN(String recycleYN) {
        this.recycleYN = recycleYN;
    }

    public String getBarcodeYN() {
        return barcodeYN;
    }

    public void setBarcodeYN(String barcodeYN) {
        this.barcodeYN = barcodeYN;
    }

    public String getMrkDate() {
        return mrkDate;
    }

    public void setMrkDate(String mrkDate) {
        this.mrkDate = mrkDate;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }

    public String getDlvDate() {
        return dlvDate;
    }

    public void setDlvDate(String dlvDate) {
        this.dlvDate = dlvDate;
    }

    public int getNowMrkCount() {
        return nowMrkCount;
    }

    public void setNowMrkCount(int nowMrkCount) {
        this.nowMrkCount = nowMrkCount;
    }

    public int getCreateDay() {
        return createDay;
    }

    public void setCreateDay(int createDay) {
        this.createDay = createDay;
    }

    public int getIsNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }

    public int getIsScrap() {
        return isScrap;
    }

    public void setIsScrap(int isScrap) {
        this.isScrap = isScrap;
    }
}
