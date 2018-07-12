package com.cstore.zhiyazhang.cstoremanagement.sql

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import android.util.Log
import com.cstore.zhiyazhang.cstoremanagement.bean.ReasonBean
import com.cstore.zhiyazhang.cstoremanagement.sql.ReasonData.Companion.REASONNAME
import com.cstore.zhiyazhang.cstoremanagement.sql.ReasonData.Companion.REASONNUMBER
import com.cstore.zhiyazhang.cstoremanagement.sql.ReasonData.Companion.REASON_TABLE_NAME
import com.cstore.zhiyazhang.cstoremanagement.sql.SQLData.TEXT_TYPE

/**
 * Created by zhiya.zhang
 * on 2017/12/22 17:16.
 * 退货原因表
 */
class ReasonData : BaseColumns {
    companion object {
        val REASON_TABLE_NAME = "reason"
        val REASONNUMBER = "reason_number"
        val REASONNAME = "reason_name"

        val SQLITE_CREATE = "create table if not exists " + REASON_TABLE_NAME + " (" +
                REASONNUMBER + TEXT_TYPE + " PRIMARY KEY, " +
                REASONNAME + TEXT_TYPE + ")"

        val SQLITE_DELETE_ENTRIES = "DROP TABLE IF EXISTS" + REASON_TABLE_NAME
    }
}

class ReasonDao(context: Context) {
    private val TAG = "ReasonDao"
    private val dbh = SQLDBHelper(context)
    fun insert(data: ArrayList<ReasonBean>) {
        val db = dbh.writableDatabase
        db.beginTransaction()
        try {
            data.forEach {
                val values = ContentValues()
                values.put(REASONNUMBER, it.reasonId)
                values.put(REASONNAME, it.reasonName)
                db.insertOrThrow(REASON_TABLE_NAME, null, values)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        } finally {
            db.setTransactionSuccessful()
            db.endTransaction()
            db.close()
        }
    }

    fun delete() {
        val db = dbh.writableDatabase
        db.beginTransaction()
        try {
            db.delete(REASON_TABLE_NAME, REASONNUMBER + " != ?", arrayOf("0"))
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        } finally {
            db.setTransactionSuccessful()
            db.endTransaction()
            db.close()
        }
    }

    fun get(reasonNumber: String): ReasonBean {
        val db = dbh.readableDatabase
        try {
            val sql = "select * from $REASON_TABLE_NAME where $REASONNUMBER = ?"
            val cursor = db.rawQuery(sql, arrayOf(reasonNumber))
            if (cursor.count > 0) {
                return parseReasonBean(cursor)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        } finally {
            db.close()
        }
        return ReasonBean("0", "0")
    }

    private fun parseReasonBean(cursor: Cursor): ReasonBean {
        val number = cursor.getString(cursor.getColumnIndex(REASONNUMBER))
        val name = cursor.getString(cursor.getColumnIndex(REASONNAME))
        return ReasonBean(number, name)
    }
}