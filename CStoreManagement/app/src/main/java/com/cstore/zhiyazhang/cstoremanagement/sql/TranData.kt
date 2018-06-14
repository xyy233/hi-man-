package com.cstore.zhiyazhang.cstoremanagement.sql

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Message
import android.provider.BaseColumns
import android.util.Log
import com.cstore.zhiyazhang.cstoremanagement.bean.TransItem
import com.cstore.zhiyazhang.cstoremanagement.bean.TransServiceBean
import com.cstore.zhiyazhang.cstoremanagement.sql.SQLData.COMMA_SEP
import com.cstore.zhiyazhang.cstoremanagement.sql.SQLData.INT_TYPE
import com.cstore.zhiyazhang.cstoremanagement.sql.SQLData.TEXT_TYPE
import com.cstore.zhiyazhang.cstoremanagement.sql.TranData.TranEntry.Companion.DIS_TIME
import com.cstore.zhiyazhang.cstoremanagement.sql.TranData.TranEntry.Companion.IS_INT
import com.cstore.zhiyazhang.cstoremanagement.sql.TranData.TranEntry.Companion.IS_SC
import com.cstore.zhiyazhang.cstoremanagement.sql.TranData.TranEntry.Companion.ITEMS
import com.cstore.zhiyazhang.cstoremanagement.sql.TranData.TranEntry.Companion.REQ_NO
import com.cstore.zhiyazhang.cstoremanagement.sql.TranData.TranEntry.Companion.STORE_ID
import com.cstore.zhiyazhang.cstoremanagement.sql.TranData.TranEntry.Companion.STORE_NAME
import com.cstore.zhiyazhang.cstoremanagement.sql.TranData.TranEntry.Companion.TRAN_DATE
import com.cstore.zhiyazhang.cstoremanagement.sql.TranData.TranEntry.Companion.TRAN_ID
import com.cstore.zhiyazhang.cstoremanagement.sql.TranData.TranEntry.Companion.TRAN_TABLE_NAME
import com.cstore.zhiyazhang.cstoremanagement.sql.TranData.TranEntry.Companion.TRS_QTYS
import com.cstore.zhiyazhang.cstoremanagement.sql.TranData.TranEntry.Companion.TRS_STORE_ID
import com.cstore.zhiyazhang.cstoremanagement.sql.TranData.TranEntry.Companion.TRS_STORE_NAME
import com.cstore.zhiyazhang.cstoremanagement.sql.TranData.TranEntry.Companion.TRS_TYPE
import com.cstore.zhiyazhang.cstoremanagement.sql.TranData.TranEntry.Companion.UPD_TIME
import com.cstore.zhiyazhang.cstoremanagement.sql.TranData.TranEntry.Companion.ZX_WX
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.Companion.ERROR
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by zhiya.zhang
 * on 2018/6/11 11:09.
 */
object TranData {
    class TranEntry : BaseColumns {
        companion object {
            val TRAN_TABLE_NAME = "tran"
            val TRAN_ID = "tran_id"
            val TRAN_DATE = "tran_date"
            val STORE_ID = "store_id"
            val STORE_NAME = "store_name"
            val ZX_WX = "zx_wx"
            val TRS_STORE_ID = "trs_store_id"
            val TRS_STORE_NAME = "trs_store_name"
            val TRS_TYPE = "trs_type"
            val DIS_TIME = "dis_time"
            val TRS_QTYS = "trs_qtys"
            val UPD_TIME = "upd_time"
            val REQ_NO = "request_number"
            val ITEMS = "items"
            val IS_SC = "is_sc"//是否上传sc  1==完成  0==未完成
            val IS_INT = "is_int"//是否网络上传总部 1==完成  0==未完成
            val CREATE_TIME = "create_time"

            val SQLITE_CREATE = "create table if not exists $TRAN_TABLE_NAME (" +
                    "$TRAN_ID$INT_TYPE primary key autoincrement$COMMA_SEP" +
                    "$TRAN_DATE$TEXT_TYPE$COMMA_SEP" +
                    "$STORE_ID$TEXT_TYPE$COMMA_SEP" +
                    "$STORE_NAME$TEXT_TYPE$COMMA_SEP" +
                    "$ZX_WX$TEXT_TYPE$COMMA_SEP" +
                    "$TRS_STORE_ID$TEXT_TYPE$COMMA_SEP" +
                    "$TRS_STORE_NAME$TEXT_TYPE$COMMA_SEP" +
                    "$TRS_TYPE$INT_TYPE$COMMA_SEP" +
                    "$DIS_TIME$TEXT_TYPE$COMMA_SEP" +
                    "$TRS_QTYS$INT_TYPE$COMMA_SEP" +
                    "$UPD_TIME$TEXT_TYPE$COMMA_SEP" +
                    "$REQ_NO$TEXT_TYPE$COMMA_SEP" +
                    "$ITEMS$TEXT_TYPE$COMMA_SEP" +
                    "$IS_SC$INT_TYPE$COMMA_SEP" +
                    "$IS_INT$INT_TYPE$COMMA_SEP" +
                    "$CREATE_TIME DATETIME DEFAULT (datetime(CURRENT_TIMESTAMP,'localtime')))"

            val SQLITE_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TRAN_TABLE_NAME"
        }
    }
}

class TranDao(context: Context) {
    private val TAG = "TranDao"
    private val tranHelper = SQLDBHelper(context)

    companion object {
        private var tranDao: TranDao? = null
        fun instance(): TranDao {
            if (tranDao == null) {
                tranDao = TranDao(MyApplication.instance().applicationContext)
            }
            return tranDao!!
        }
    }

    /**
     * 更新调拨数据
     */
    fun updateSQL(bean: TransServiceBean, isSc: Int, isInt: Int) {
        val db = tranHelper.writableDatabase
        db!!.beginTransaction()
        try {
            val values = ContentValues()
            values.put(REQ_NO, bean.requestNumber)
            values.put(IS_SC, isSc)
            values.put(IS_INT, isInt)
            val items = Gson().toJson(bean.items)
            values.put(ITEMS, items)
            db.update(TRAN_TABLE_NAME, values, "$TRAN_DATE=? and $STORE_ID=? and $TRS_STORE_ID=? and $DIS_TIME=? and $TRS_TYPE=?",
                    arrayOf(MyTimeUtil.nowDate, bean.storeId, bean.trsStoreId, bean.disTime, bean.trsType.toString()))
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        } finally {
            close(db)
        }
    }

    /**
     * 删除非今天的调拨数据
     */
    fun deleteSQL() {
        val db = tranHelper.writableDatabase
        db!!.beginTransaction()
        try {
            db.delete(TRAN_TABLE_NAME, "$TRAN_DATE!=?", arrayOf(MyTimeUtil.nowDate))
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        } finally {
            close(db)
        }
    }

    /**
     * 清空所有数据
     */
    fun clearSQL() {
        val db = tranHelper.writableDatabase
        db!!.beginTransaction()
        try {
            db.delete(TRAN_TABLE_NAME, "$TRAN_ID!=?", arrayOf("0"))
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        } finally {
            close(db)
        }
    }

    private fun close(db: SQLiteDatabase) {
        try {
            db.setTransactionSuccessful()
        } catch (e: Exception) {
        }
        try {
            db.endTransaction()
        } catch (e: Exception) {
        }
        try {
            db.close()
        } catch (e: Exception) {
        }
    }

    /**
     * 新建
     */
    fun insertSQL(data: ArrayList<TransServiceBean>) {
        val db = tranHelper.writableDatabase
        db!!.beginTransaction()
        try {
            for (bean in data) {
                val values = ContentValues()
                values.put(TRAN_DATE, MyTimeUtil.nowDate)
                values.put(STORE_ID, bean.storeId)
                values.put(STORE_NAME, bean.storeName)
                values.put(ZX_WX, bean.type)
                values.put(TRS_STORE_ID, bean.trsStoreId)
                values.put(TRS_STORE_NAME, bean.trsStoreName)
                values.put(TRS_TYPE, bean.trsType)
                values.put(DIS_TIME, bean.disTime)
                values.put(TRS_QTYS, bean.trsQuantities)
                values.put(UPD_TIME, bean.updTime)
                values.put(REQ_NO, bean.requestNumber)
                val items = Gson().toJson(bean.items)
                values.put(ITEMS, items)
                if (bean.isSc == null) bean.isSc = 0
                if (bean.isInt == null) bean.isInt = 0
                values.put(IS_SC, bean.isSc)
                values.put(IS_INT, bean.isInt)
                db.insertOrThrow(TRAN_TABLE_NAME, null, values)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        } finally {
            close(db)
        }
    }


    /**
     * 得到今天全部的调拨数据
     */
    fun getAllData(): ArrayList<TransServiceBean> {
        val db = tranHelper.readableDatabase
        val sql = "select * from $TRAN_TABLE_NAME where $TRAN_DATE = ?"
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(sql, arrayOf(MyTimeUtil.nowDate))
            if (cursor.count > 0) {
                val result = ArrayList<TransServiceBean>()
                while (cursor.moveToNext()) {
                    result.add(parseTranData(cursor))
                }
                return result
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        } finally {
            cursor?.close()
            close(db)
        }
        return ArrayList()
    }

    /**
     * 得到所有异常调拨数据
     */
    fun getAberrantData(): ArrayList<TransServiceBean> {
        val db = tranHelper.readableDatabase
        val sql = "select * from $TRAN_TABLE_NAME where $REQ_NO is not null and $TRS_TYPE = ? and ($IS_SC = ? or $IS_INT = ?)"
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(sql, arrayOf("-1", "0", "0"))
            if (cursor.count > 0) {
                val result = ArrayList<TransServiceBean>()
                while (cursor.moveToNext()) {
                    result.add(parseTranData(cursor))
                }
                return result
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        } finally {
            cursor?.close()
            close(db)
        }
        return ArrayList()
    }

    /**
     * 得到最大单号，从本地sql
     */
    fun getSQLiteRequestNumber(trsNumber: String, msg: Message, handler: MyHandler): String {
        val db = tranHelper.readableDatabase
        return try {
            getResultNumber(trsNumber, db)
        } catch (e: Exception) {
            msg.what = ERROR
            msg.obj = e.message.toString()
            handler.sendMessage(msg)
            "ERROR"
        } finally {
            close(db)
        }
    }

    /**
     * 本地判断得到最大requestnumber
     */
    private fun getResultNumber(trsNumber: String, db: SQLiteDatabase): String {
        val sql = "select * from $TRAN_TABLE_NAME where $REQ_NO like '%$trsNumber%'"
        val cursor = db.rawQuery(sql, arrayOf())
        return try {
            if (cursor.count > 0) {
                val header = trsNumber.substring(0, 8)
                val foot = (trsNumber.substring(8).toInt() + 1).toString().padStart(4, '0')
                getResultNumber(header + foot, db)
            } else {
                trsNumber
            }
        } finally {
            cursor.close()
        }
    }

    private fun parseTranData(cursor: Cursor): TransServiceBean {
        val storeId = cursor.getString(cursor.getColumnIndex(STORE_ID))
        val storeName = cursor.getString(cursor.getColumnIndex(STORE_NAME))
        val type = cursor.getString(cursor.getColumnIndex(ZX_WX))
        val trsStoreId = cursor.getString(cursor.getColumnIndex(TRS_STORE_ID))
        val trsStoreName = cursor.getString(cursor.getColumnIndex(TRS_STORE_NAME))
        val trsType = cursor.getInt(cursor.getColumnIndex(TRS_TYPE))
        val disTime = cursor.getString(cursor.getColumnIndex(DIS_TIME))
        val trsQtys = cursor.getInt(cursor.getColumnIndex(TRS_QTYS))
        val updTime = cursor.getString(cursor.getColumnIndex(UPD_TIME))
        val reqNo = cursor.getString(cursor.getColumnIndex(REQ_NO))
        val itemsJson = cursor.getString(cursor.getColumnIndex(ITEMS))
        val items = Gson().fromJson<ArrayList<TransItem>>(itemsJson, object : TypeToken<ArrayList<TransItem>>() {}.type)
        val isSc = cursor.getInt(cursor.getColumnIndex(IS_SC))
        val isInt = cursor.getInt(cursor.getColumnIndex(IS_INT))
        return TransServiceBean(storeId, storeName, type, trsStoreId, trsStoreName, trsType, disTime, trsQtys, updTime, reqNo, null, items, isSc, isInt)
    }
}