package com.c_store.zhiyazhang.cstoremanagement.sql;

import android.provider.BaseColumns;

/**
 * Created by zhiya.zhang
 * on 2017/5/26 13:55.
 */

public final class ScrapData {
    private ScrapData() {
    }

    public static class ScrapEntry implements BaseColumns {
        public static final String SCRAP_TABLE_NAME = "scrap";
        public static final String _ID = "scrapId";//品号
        public static final String _NAME = "scrapName";//品名
        public static final String CATEGORY_ID = "categoryId";//类号
        public static final String UNIT_PRICE = "unitPrice";//商品单价
        public static final String UNIT_COST = "unitCost"; //成本
        public static final String SELL_COST = "sellCost";//卖价
        public static final String CITEM_YN = "citemYN";//是否承包
        public static final String RECYCLE_YN = "recycleYN";//是否回收
        public static final String BARCODE_YN = "barcodeYN"; //是否有条形码
        public static final String MRK_DATE = "mrkDate"; //报废时间
        public static final String SALE_DATE = "saleDate"; //销售时间
        public static final String DLV_DATE = "dlvDate"; //验收时间
        public static final String MRK_COUNT = "mrkCount"; //报废数量
        public static final String IS_NEW="isNew"; //是否是新的
        public static final String IS_SCRAP="isScrap"; //是否报废
        public static final String CREATE_DAY = "createDay"; //创建时间

    }
}
