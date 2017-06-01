package com.c_store.zhiyazhang.cstoremanagement.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.c_store.zhiyazhang.cstoremanagement.R;
import com.c_store.zhiyazhang.cstoremanagement.bean.ScrapContractBean;
import com.c_store.zhiyazhang.cstoremanagement.utils.Util;

import java.util.ArrayList;

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
 * on 2017/5/26 14:48.
 */

public class ScrapDao {
    private static final String TAG = "ScrapDao";

    //定义列
    private final String[] SCRAP_COLUMNS = {
            _ID,
            _NAME,
            CATEGORY_ID,
            UNIT_PRICE,
            UNIT_COST,
            SELL_COST,
            CITEM_YN,
            RECYCLE_YN,
            BARCODE_YN,
            MRK_DATE,
            SALE_DATE,
            DLV_DATE,
            MRK_COUNT,
            IS_NEW,
            IS_SCRAP,
            CREATE_DAY
    };
    private Context context;
    private ScrapDBHelper scrapDBHelper;

    public ScrapDao(Context context) {
        this.context = context;
        scrapDBHelper = new ScrapDBHelper(context);
    }

    /**
     * 判断表中是否有数据
     *
     * @return 是，否
     */
    public boolean isDataExist() {
        int count = 0;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = scrapDBHelper.getReadableDatabase();
            cursor = db.query(SCRAP_TABLE_NAME, new String[]{"COUNT(" + _ID + ")"}, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            if (count > 0)
                return true;
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    /**
     * 执行操作
     *
     * @param scbs 传入要修改的数据
     * @param type 操作类型（insert,update,delete）
     */
    public void editSQL(ArrayList<ScrapContractBean> scbs, String type) {
        if (scbs == null || scbs.size() == 0) {
            return;
        }

        try {
            switch (type) {
                case "insert":
                    insert(scbs);
                    break;
                case "update":
                    update(scbs);
                    break;
                case "delete":
                    delete(scbs);
                    break;
                default:
                    break;
            }
        } catch (SQLiteConstraintException e) {
            Toast.makeText(context, "主键重复", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, R.string.strErrorSql, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "", e);
        }
    }

    /**
     * 查询数据库中所有数据
     *
     * @return 数据库中所有Scrap数据
     */
    public ArrayList<ScrapContractBean> getAllDate() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = scrapDBHelper.getReadableDatabase();
            cursor = db.query(SCRAP_TABLE_NAME, SCRAP_COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                ArrayList<ScrapContractBean> result = new ArrayList<ScrapContractBean>(cursor.getCount());
                while (cursor.moveToNext()) {
                    result.add(parseScrap(cursor));
                }
                return result;
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return new ArrayList<ScrapContractBean>();
    }

    /**
     * insert操作
     *
     * @param scbs insert数据
     */
    private void insert(ArrayList<ScrapContractBean> scbs) {
        SQLiteDatabase db = scrapDBHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (ScrapContractBean scb :
                    scbs) {
                ContentValues values = new ContentValues();
                values.put(_ID, scb.getScrapId());
                values.put(_NAME, scb.getScrapName());
                values.put(CATEGORY_ID, scb.getCategoryId());
                values.put(UNIT_PRICE, scb.getUnitPrice());
                values.put(UNIT_COST, scb.getUnitCost());
                values.put(SELL_COST, scb.getSellCost());
                values.put(CITEM_YN, scb.getCitemYN());
                values.put(RECYCLE_YN, scb.getRecycleYN());
                values.put(BARCODE_YN, scb.getBarcodeYN());
                values.put(MRK_DATE, scb.getMrkDate());
                values.put(SALE_DATE, scb.getSaleDate());
                values.put(DLV_DATE, scb.getDlvDate());
                values.put(MRK_COUNT, scb.getNowMrkCount());
                values.put(IS_NEW, 2);
                values.put(IS_SCRAP, false);
                values.put(CREATE_DAY, Util.getTodayDay());
                db.insertOrThrow(SCRAP_TABLE_NAME, null, values);
            }
        } finally {
            if (db != null) {
                db.setTransactionSuccessful();
                db.endTransaction();
                db.close();
            }
        }
    }

    /**
     * update操作
     *
     * @param scbs update数据
     */
    private void update(ArrayList<ScrapContractBean> scbs) {
        SQLiteDatabase db = scrapDBHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (ScrapContractBean scb :
                    scbs) {
                ContentValues values = new ContentValues();
                values.put(MRK_COUNT, scb.getNowMrkCount());
                values.put(IS_SCRAP, scb.getIsScrap());
                db.update(SCRAP_TABLE_NAME, values, _ID + " = ?", new String[]{scb.getScrapId()});
            }
        } finally {
            if (db != null) {
                db.setTransactionSuccessful();
                db.endTransaction();
                db.close();
            }
        }
    }

    /**
     * delete操作
     *
     * @param scbs delete数据
     */
    private void delete(ArrayList<ScrapContractBean> scbs) {
        SQLiteDatabase db = scrapDBHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (ScrapContractBean scb :
                    scbs) {
                db.delete(SCRAP_TABLE_NAME, _ID + " = ?", new String[]{scb.getScrapId()});
            }
        } finally {
            if (db != null) {
                db.setTransactionSuccessful();
                db.endTransaction();
                db.close();
            }
        }
    }

    /**
     * 数据库查找到的数据转换为ScrapContractBean
     *
     * @param cursor 查找到的数据
     * @return ScrapContract对象
     */
    private ScrapContractBean parseScrap(Cursor cursor) {
        ScrapContractBean s = new ScrapContractBean();
        s.setScrapId(cursor.getString(cursor.getColumnIndex(_ID)));
        s.setScrapName(cursor.getString(cursor.getColumnIndex(_NAME)));
        s.setCategoryId(cursor.getString(cursor.getColumnIndex(CATEGORY_ID)));
        s.setUnitPrice(cursor.getDouble(cursor.getColumnIndex(UNIT_PRICE)));
        s.setUnitCost(cursor.getDouble(cursor.getColumnIndex(UNIT_COST)));
        s.setSellCost(cursor.getDouble(cursor.getColumnIndex(SELL_COST)));
        s.setCitemYN(cursor.getString(cursor.getColumnIndex(CITEM_YN)));
        s.setRecycleYN(cursor.getString(cursor.getColumnIndex(RECYCLE_YN)));
        s.setBarcodeYN(cursor.getString(cursor.getColumnIndex(BARCODE_YN)));
        s.setMrkDate(cursor.getString(cursor.getColumnIndex(MRK_DATE)));
        s.setSaleDate(cursor.getString(cursor.getColumnIndex(SALE_DATE)));
        s.setDlvDate(cursor.getString(cursor.getColumnIndex(DLV_DATE)));
        s.setNowMrkCount(cursor.getInt(cursor.getColumnIndex(MRK_COUNT)));
        s.setIsNew(cursor.getInt(cursor.getColumnIndex(IS_NEW)));
        s.setIsScrap(cursor.getInt(cursor.getColumnIndex(IS_SCRAP)));
        s.setCreateDay(cursor.getInt(cursor.getColumnIndex(CREATE_DAY)));
        return s;
    }
}
