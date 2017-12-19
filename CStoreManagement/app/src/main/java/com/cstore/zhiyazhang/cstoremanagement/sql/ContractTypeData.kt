package com.cstore.zhiyazhang.cstoremanagement.sql

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ContractTypeBean
import com.cstore.zhiyazhang.cstoremanagement.sql.ContractTypeData.ContractTypeEntry.Companion.CONTRACT_TYPE_TABLE_NAME
import com.cstore.zhiyazhang.cstoremanagement.sql.ContractTypeData.ContractTypeEntry.Companion.CREATE_DAY
import com.cstore.zhiyazhang.cstoremanagement.sql.ContractTypeData.ContractTypeEntry.Companion.TYPE_ID
import com.cstore.zhiyazhang.cstoremanagement.sql.SQLData.DB_NAME
import com.cstore.zhiyazhang.cstoremanagement.sql.SQLData.DB_VERSION
import com.cstore.zhiyazhang.cstoremanagement.sql.SQLData.INT_TYPE
import com.cstore.zhiyazhang.cstoremanagement.sql.SQLData.TEXT_TYPE
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast

/**
 * Created by zhiya.zhang
 * on 2017/6/23 10:27.
 */
object ContractTypeData {
    class ContractTypeEntry : BaseColumns {
        companion object {
            val CONTRACT_TYPE_TABLE_NAME = "contractType"
            val TYPE_ID = "typeId"//类id
            val CREATE_DAY = "createDay"//创建时间
        }
    }
}

class ContractTypeDBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
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
        private val SQLITE_CREATE = "create table if not exists " + CONTRACT_TYPE_TABLE_NAME + " ( " +
                TYPE_ID + TEXT_TYPE + " PRIMARY KEY, " +
                CREATE_DAY + INT_TYPE + ")"
        private val SQLITE_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + CONTRACT_TYPE_TABLE_NAME
    }
}

class ContractTypeDao(context: Context) {
    private val CONTRACTTYPECOLUMNS = arrayOf<String>(TYPE_ID, CREATE_DAY)
    private val ctdbh: ContractTypeDBHelper = ContractTypeDBHelper(context)

    fun editSQL(ctb: ContractTypeBean?, type: String) {
        try {
            when (type) {
                "insert" -> insert(ctb!!)
                "delete" -> delete(ctb!!)
                "deleteTable"->deleteTable()
                else -> {
                }
            }
        } catch (e: SQLiteConstraintException) {
            Log.e(TAG, "正常错误，主键重复，故意的不用管")
            MyToast.getShortToast("Key repeat!")
        } catch (e: Exception) {
            MyToast.getShortToast(MyApplication.instance().getString(R.string.strErrorSql))
        }
    }

    private fun insert(ctb: ContractTypeBean) {
        val db = ctdbh.writableDatabase
        db!!.beginTransaction()
        try {
            val values = ContentValues()
            values.put(TYPE_ID, ctb.typeId)
            values.put(CREATE_DAY, MyTimeUtil.todayDay)
            db.insertOrThrow(CONTRACT_TYPE_TABLE_NAME, null, values)
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        } finally {
            db.setTransactionSuccessful()
            db.endTransaction()
            db.close()
        }
    }

    private fun delete(ctb: ContractTypeBean) {
        val db = ctdbh.writableDatabase
        db!!.beginTransaction()
        try {
            db.delete(CONTRACT_TYPE_TABLE_NAME, TYPE_ID + "=?", arrayOf(ctb.typeId))
        } finally {
            db.setTransactionSuccessful()
            db.endTransaction()
            db.close()
        }
    }

    private fun delete(day: Int) {
        val db = ctdbh.writableDatabase
        db!!.beginTransaction()
        try {
            db.delete(CONTRACT_TYPE_TABLE_NAME, CREATE_DAY + "!=?", arrayOf(day.toString()))
        } finally {
            db.setTransactionSuccessful()
            db.endTransaction()
            db.close()
        }
    }

    private fun deleteTable(){
        val db=ctdbh.writableDatabase
        db!!.beginTransaction()
        try {
            ctdbh.onUpgrade(db,DB_VERSION,DB_VERSION)
        }finally {
            db.setTransactionSuccessful()
            db.endTransaction()
            db.close()
        }
    }

    val allData: ArrayList<ContractTypeBean>
        get() {
            var db: SQLiteDatabase? = null
            var cursor: Cursor? = null
            try {
                db = ctdbh.readableDatabase
                cursor = db!!.query(CONTRACT_TYPE_TABLE_NAME, CONTRACTTYPECOLUMNS, null, null, null, null, null)
                if (cursor!!.count > 0) {
                    val result = ArrayList<ContractTypeBean>(cursor.count)
                    while (cursor.moveToNext()) {
                        result.add(parseContractType(cursor))
                    }
                    if (result.size > 0 && result.any { it.createDay != MyTimeUtil.todayDay }) {
                        delete(MyTimeUtil.todayDay)
                        result.removeAll(result.filter { it.createDay != MyTimeUtil.todayDay })
                    }
                    return result
                }
            } catch (e: Exception) {
                MyToast.getShortToast(e.message.toString())
            } finally {
                cursor?.close()
                db?.close()
            }
            return ArrayList()
        }

    private fun parseContractType(cursor: Cursor): ContractTypeBean {
        return ContractTypeBean(
                cursor.getString(cursor.getColumnIndex(TYPE_ID)),
                "",
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, true,
                cursor.getInt(cursor.getColumnIndex(CREATE_DAY))
        )
    }

    companion object {
        private val TAG = "ContractTypeDao"
    }
}