package com.cstore.zhiyazhang.cstoremanagement.sql

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by zhiya.zhang
 * on 2017/12/21 10:59.
 */
class SQLDBHelper(context: Context) : SQLiteOpenHelper(context, SQLData.DB_NAME, null, SQLData.DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(WXPayData.WXPayEntry.SQLITE_CREATE)
        db.execSQL(ALIPayData.ALIPayEntry.SQLITE_CREATE)
        db.execSQL(CashPayData.CashPayEntry.SQLITE_CREATE)
        db.execSQL(ContractTypeData.ContractTypeEntry.SQLITE_CREATE)
        db.execSQL(TranData.TranEntry.SQLITE_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(WXPayData.WXPayEntry.SQLITE_DELETE_ENTRIES)
        db.execSQL(ALIPayData.ALIPayEntry.SQLITE_DELETE_ENTRIES)
        db.execSQL(CashPayData.CashPayEntry.SQLITE_DELETE_ENTRIES)
        db.execSQL(ContractTypeData.ContractTypeEntry.SQLITE_DELETE_ENTRIES)
        db.execSQL(TranData.TranEntry.SQLITE_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
}

object SQLData {
    val DB_NAME = "app_cstore.db"
    val DB_VERSION = 12
    val TEXT_TYPE = " TEXT"
    val COMMA_SEP = ","
    val DOUBLE_TYPE = " REAL"
    val INT_TYPE = " INTEGER"
    val DEFAULT = " DEFAULT 0"
}