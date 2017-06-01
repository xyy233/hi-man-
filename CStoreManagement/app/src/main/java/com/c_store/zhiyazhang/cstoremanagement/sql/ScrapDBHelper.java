package com.c_store.zhiyazhang.cstoremanagement.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.c_store.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.BARCODE_YN;
import static com.c_store.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.CATEGORY_ID;
import static com.c_store.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.CITEM_YN;
import static com.c_store.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.CREATE_DAY;
import static com.c_store.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.DLV_DATE;
import static com.c_store.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.IS_NEW;
import static com.c_store.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.IS_SCRAP;
import static com.c_store.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.MRK_COUNT;
import static com.c_store.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.MRK_DATE;
import static com.c_store.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.RECYCLE_YN;
import static com.c_store.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.SALE_DATE;
import static com.c_store.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.SCRAP_TABLE_NAME;
import static com.c_store.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.SELL_COST;
import static com.c_store.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.UNIT_COST;
import static com.c_store.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.UNIT_PRICE;
import static com.c_store.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry._ID;
import static com.c_store.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry._NAME;

/**
 * Created by zhiya.zhang
 * on 2017/5/26 13:41.
 */

public class ScrapDBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "app_cstore.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String DOUBLE_TYPE = " REAL";
    private static final String INT_TYPE = " INTEGER";
    private static final String SQLITE_CREATE =
            "create table if not exists " + SCRAP_TABLE_NAME + " (" +
                    _ID + TEXT_TYPE + " PRIMARY KEY, " +
                    _NAME + TEXT_TYPE + COMMA_SEP +
                    CATEGORY_ID + TEXT_TYPE + COMMA_SEP +
                    UNIT_PRICE + DOUBLE_TYPE + COMMA_SEP +
                    UNIT_COST + DOUBLE_TYPE + COMMA_SEP +
                    SELL_COST + DOUBLE_TYPE + COMMA_SEP +
                    CITEM_YN + TEXT_TYPE + COMMA_SEP +
                    RECYCLE_YN + TEXT_TYPE + COMMA_SEP +
                    BARCODE_YN + TEXT_TYPE + COMMA_SEP +
                    MRK_DATE + TEXT_TYPE + COMMA_SEP +
                    SALE_DATE + TEXT_TYPE + COMMA_SEP +
                    DLV_DATE + TEXT_TYPE + COMMA_SEP +
                    MRK_COUNT + INT_TYPE + COMMA_SEP +
                    IS_NEW + INT_TYPE + COMMA_SEP +
                    IS_SCRAP + INT_TYPE + COMMA_SEP +
                    CREATE_DAY + INT_TYPE + ")";
    private static final String SQLITE_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SCRAP_TABLE_NAME;

    public ScrapDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLITE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQLITE_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
