/*
package com.cstore.zhiyazhang.cstoremanagement.sql

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import android.widget.Toast
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ScrapContractBean
import com.cstore.zhiyazhang.cstoremanagement.sql.ScrapDBHelper.Companion.DB_VERSION
import com.cstore.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.Companion.BARCODE_YN
import com.cstore.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.Companion.CATEGORY_ID
import com.cstore.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.Companion.CITEM_YN
import com.cstore.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.Companion.CREATE_DAY
import com.cstore.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.Companion.DLV_DATE
import com.cstore.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.Companion.IS_NEW
import com.cstore.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.Companion.IS_SCRAP
import com.cstore.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.Companion.MRK_COUNT
import com.cstore.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.Companion.MRK_DATE
import com.cstore.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.Companion.RECYCLE_YN
import com.cstore.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.Companion.SALE_DATE
import com.cstore.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.Companion.SCRAP_TABLE_NAME
import com.cstore.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.Companion.SELL_COST
import com.cstore.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.Companion.UNIT_COST
import com.cstore.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.Companion.UNIT_PRICE
import com.cstore.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.Companion._ID
import com.cstore.zhiyazhang.cstoremanagement.sql.ScrapData.ScrapEntry.Companion._NAME
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import java.util.*

*/
/**
 * Created by zhiya.zhang
 * on 2017/7/10 11:35.
 *//*

object ScrapData {
    class ScrapEntry : BaseColumns {
        companion object {
            val SCRAP_TABLE_NAME = "scrap"
            val _ID = "scrapId"//品号
            val _NAME = "scrapName"//品名
            val CATEGORY_ID = "categoryId"//类号
            val UNIT_PRICE = "unitPrice"//商品单价
            val UNIT_COST = "unitCost" //成本
            val SELL_COST = "sellCost"//卖价
            val CITEM_YN = "citemYN"//是否承包
            val RECYCLE_YN = "recycleYN"//是否回收
            val BARCODE_YN = "barcodeYN" //是否有条形码
            val MRK_DATE = "mrkDate" //报废时间
            val SALE_DATE = "saleDate" //销售时间
            val DLV_DATE = "dlvDate" //验收时间
            val MRK_COUNT = "mrkCount" //报废数量
            val IS_NEW = "isNew" //是否是新的
            val IS_SCRAP = "isScrap" //是否报废
            val CREATE_DAY = "createDay" //创建时间
        }
    }
}

class ScrapDBHelper internal constructor(context: Context) : SQLiteOpenHelper(context, ScrapDBHelper.DB_NAME, null, ScrapDBHelper.DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQLITE_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQLITE_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        val DB_VERSION = 1
        private val DB_NAME = "app_cstore.db"
        private val TEXT_TYPE = " TEXT"
        private val COMMA_SEP = ","
        private val DOUBLE_TYPE = " REAL"
        private val INT_TYPE = " INTEGER"
        private val SQLITE_CREATE =
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
                        CREATE_DAY + INT_TYPE + ")"
        val SQLITE_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + SCRAP_TABLE_NAME
    }
}

class ScrapDao(private val context: Context) {

    //定义列
    private val SCRAP_COLUMNS = arrayOf(_ID, _NAME, CATEGORY_ID, UNIT_PRICE, UNIT_COST, SELL_COST, CITEM_YN, RECYCLE_YN, BARCODE_YN, MRK_DATE, SALE_DATE, DLV_DATE, MRK_COUNT, IS_NEW, IS_SCRAP, CREATE_DAY)
    private val scrapDBHelper: ScrapDBHelper = ScrapDBHelper(context)

    */
/**
     * 判断表中是否有数据

     * @return 是，否
     *//*

    val isDataExist: Boolean
        get() {
            var count = 0
            var db: SQLiteDatabase? = null
            var cursor: Cursor? = null
            try {
                db = scrapDBHelper.readableDatabase
                cursor = db!!.query(SCRAP_TABLE_NAME, arrayOf("COUNT($_ID)"), null, null, null, null, null)
                if (cursor!!.moveToFirst()) {
                    count = cursor.getInt(0)
                }
                if (count > 0)
                    return true
            } catch (e: Exception) {
                Log.e(TAG, "", e)
            } finally {
                if (cursor != null) {
                    cursor.close()
                }
                if (db != null) {
                    db.close()
                }
            }
            return false
        }

    */
/**
     * 执行操作

     * @param scbs 传入要修改的数据
     * *
     * @param type 操作类型（insert,update,delete）
     *//*

    fun editSQL(scbs: ArrayList<ScrapContractBean>?, type: String) {
        if (type!="deleteTable"){
            if (scbs == null || scbs.size == 0) {
                return
            }
        }
        try {
            when (type) {
                "insert" -> insert(scbs!!)
                "update" -> update(scbs!!)
                "delete" -> delete(scbs!!)
                "deleteTable"->deleteTable()
                else -> {
                }
            }
        } catch (e: SQLiteConstraintException) {
            Toast.makeText(context, "主键重复", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, R.string.strErrorSql, Toast.LENGTH_SHORT).show()
            Log.e(TAG, "", e)
        }

    }

    */
/**
     * 查询数据库中所有数据

     * @return 数据库中所有Scrap数据
     *//*

    val allDate: ArrayList<ScrapContractBean>
        get() {
            var db: SQLiteDatabase? = null
            var cursor: Cursor? = null
            try {
                db = scrapDBHelper.readableDatabase
                cursor = db!!.query(SCRAP_TABLE_NAME, SCRAP_COLUMNS, null, null, null, null, null)
                if (cursor!!.count > 0) {
                    val result = ArrayList<ScrapContractBean>(cursor.count)
                    while (cursor.moveToNext()) {
                        result.add(parseScrap(cursor))
                    }
                    return result
                }
            } catch (e: Exception) {
                Log.e(TAG, "", e)
            } finally {
                if (cursor != null) {
                    cursor.close()
                }
                if (db != null) {
                    db.close()
                }
            }
            return ArrayList()
        }

    */
/**
     * insert操作

     * @param scbs insert数据
     *//*

    private fun insert(scbs: ArrayList<ScrapContractBean>) {
        val db = scrapDBHelper.writableDatabase
        db!!.beginTransaction()
        try {
            for (scb in scbs) {
                val values = ContentValues()
                values.put(_ID, scb.scrapId)
                values.put(_NAME, scb.scrapName)
                values.put(CATEGORY_ID, scb.categoryId)
                values.put(UNIT_PRICE, scb.unitPrice)
                values.put(UNIT_COST, scb.unitCost)
                values.put(SELL_COST, scb.sellCost)
                values.put(CITEM_YN, scb.citemYN)
                values.put(RECYCLE_YN, scb.recycleYN)
                values.put(BARCODE_YN, scb.barcodeYN)
                values.put(MRK_DATE, scb.mrkDate)
                values.put(SALE_DATE, scb.saleDate)
                values.put(DLV_DATE, scb.dlvDate)
                values.put(MRK_COUNT, scb.mrkCount)
                values.put(IS_NEW, 2)
                values.put(IS_SCRAP, false)
                values.put(CREATE_DAY, MyTimeUtil.todayDay)
                db.insertOrThrow(SCRAP_TABLE_NAME, null, values)
            }
        } finally {
            db.setTransactionSuccessful()
            db.endTransaction()
            db.close()
        }
    }

    */
/**
     * update操作

     * @param scbs update数据
     *//*

    private fun update(scbs: ArrayList<ScrapContractBean>) {
        val db = scrapDBHelper.writableDatabase
        db!!.beginTransaction()
        try {
            for (scb in scbs) {
                val values = ContentValues()
                values.put(MRK_COUNT, scb.mrkCount)
                values.put(IS_SCRAP, scb.isScrap)
                db.update(SCRAP_TABLE_NAME, values, _ID + " = ?", arrayOf(scb.scrapId))
            }
        } finally {
            db.setTransactionSuccessful()
            db.endTransaction()
            db.close()
        }
    }

    */
/**
     * delete操作

     * @param scbs delete数据
     *//*

    private fun delete(scbs: ArrayList<ScrapContractBean>) {
        val db = scrapDBHelper.writableDatabase
        db!!.beginTransaction()
        try {
            for ((scrapId) in scbs) {
                db.delete(SCRAP_TABLE_NAME, _ID + " = ?", arrayOf(scrapId))
            }
        } finally {
            db.setTransactionSuccessful()
            db.endTransaction()
            db.close()
        }
    }

    private fun deleteTable() {
        val db = scrapDBHelper.writableDatabase
        db!!.beginTransaction()
        try {
            scrapDBHelper.onUpgrade(db,DB_VERSION,DB_VERSION)
        } finally {
            db.setTransactionSuccessful()
            db.endTransaction()
            db.close()
        }
    }

    */
/**
     * 数据库查找到的数据转换为ScrapContractBean

     * @param cursor 查找到的数据
     * *
     * @return ScrapContract对象
     *//*

    private fun parseScrap(cursor: Cursor): ScrapContractBean {
        val s = ScrapContractBean(
                cursor.getString(cursor.getColumnIndex(_ID)),
                cursor.getString(cursor.getColumnIndex(_NAME)),
                cursor.getString(cursor.getColumnIndex(CATEGORY_ID)),
                cursor.getDouble(cursor.getColumnIndex(UNIT_PRICE)),
                cursor.getDouble(cursor.getColumnIndex(UNIT_COST)),
                cursor.getDouble(cursor.getColumnIndex(SELL_COST)),
                cursor.getString(cursor.getColumnIndex(CITEM_YN)),
                cursor.getString(cursor.getColumnIndex(RECYCLE_YN)),
                cursor.getString(cursor.getColumnIndex(BARCODE_YN)),
                cursor.getString(cursor.getColumnIndex(MRK_DATE)),
                cursor.getString(cursor.getColumnIndex(SALE_DATE)),
                cursor.getString(cursor.getColumnIndex(DLV_DATE)),
                cursor.getInt(cursor.getColumnIndex(MRK_COUNT)),
                cursor.getInt(cursor.getColumnIndex(CREATE_DAY)),
                cursor.getInt(cursor.getColumnIndex(IS_NEW)),
                cursor.getInt(cursor.getColumnIndex(IS_SCRAP))
        )
        return s
    }

    companion object {
        private val TAG = "ScrapDao"
    }
}

*/
