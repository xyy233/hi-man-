package com.cstore.zhiyazhang.cstoremanagement.sql

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import android.util.Log
import com.cstore.zhiyazhang.cstoremanagement.bean.ALIPaySqlBean
import com.cstore.zhiyazhang.cstoremanagement.sql.ALIPayData.ALIPayEntry.Companion.ALIPAY_TABLE_NAME
import com.cstore.zhiyazhang.cstoremanagement.sql.ALIPayData.ALIPayEntry.Companion.ASS_POS
import com.cstore.zhiyazhang.cstoremanagement.sql.ALIPayData.ALIPayEntry.Companion.BUYER_LOGON_ID
import com.cstore.zhiyazhang.cstoremanagement.sql.ALIPayData.ALIPayEntry.Companion.CREATE_TIME
import com.cstore.zhiyazhang.cstoremanagement.sql.ALIPayData.ALIPayEntry.Companion.ERROR_MESSAGE
import com.cstore.zhiyazhang.cstoremanagement.sql.ALIPayData.ALIPayEntry.Companion.IS_DONE
import com.cstore.zhiyazhang.cstoremanagement.sql.ALIPayData.ALIPayEntry.Companion.IS_UPLOAD
import com.cstore.zhiyazhang.cstoremanagement.sql.ALIPayData.ALIPayEntry.Companion.NEXT_TRANNO
import com.cstore.zhiyazhang.cstoremanagement.sql.ALIPayData.ALIPayEntry.Companion.OUT_TRADE_NO
import com.cstore.zhiyazhang.cstoremanagement.sql.ALIPayData.ALIPayEntry.Companion.SEQ
import com.cstore.zhiyazhang.cstoremanagement.sql.ALIPayData.ALIPayEntry.Companion.STORE_ID
import com.cstore.zhiyazhang.cstoremanagement.sql.ALIPayData.ALIPayEntry.Companion.TEL_SEQ
import com.cstore.zhiyazhang.cstoremanagement.sql.ALIPayData.ALIPayEntry.Companion.THE_STEP
import com.cstore.zhiyazhang.cstoremanagement.sql.ALIPayData.ALIPayEntry.Companion.TOTAL_FEE
import com.cstore.zhiyazhang.cstoremanagement.sql.ALIPayData.ALIPayEntry.Companion.TRADE_NO
import com.cstore.zhiyazhang.cstoremanagement.sql.ALIPayData.ALIPayEntry.Companion.UPLOAD_COUNT
import com.cstore.zhiyazhang.cstoremanagement.sql.SQLData.COMMA_SEP
import com.cstore.zhiyazhang.cstoremanagement.sql.SQLData.DEFAULT
import com.cstore.zhiyazhang.cstoremanagement.sql.SQLData.DOUBLE_TYPE
import com.cstore.zhiyazhang.cstoremanagement.sql.SQLData.INT_TYPE
import com.cstore.zhiyazhang.cstoremanagement.sql.SQLData.TEXT_TYPE
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast

/**
 * Created by zhiya.zhang
 * on 2018/1/11 14:12.
 */
object ALIPayData {
    class ALIPayEntry : BaseColumns {
        companion object {
            val ALIPAY_TABLE_NAME = "alipay"
            val OUT_TRADE_NO = "out_trade_no"//商户订单号
            val TRADE_NO = "trade_no"//支付宝订单号
            val TEL_SEQ = "tel_seq"//本机序列号
            val TOTAL_FEE = "total_fee"//交易金额
            val STORE_ID = "store_id"//店号
            val ASS_POS = "ass_pos"//分配pos机号
            val NEXT_TRANNO = "next_tranno"//生成订单的东西
            val SEQ = "seq"//第几次提交
            val BUYER_LOGON_ID = "buyer_logon_id"//用户id
            val THE_STEP = "the_step"//当前步骤数
            val ERROR_MESSAGE = "error_message"//错误原因
            val IS_DONE = "is_done"//是否处理完毕
            val IS_UPLOAD = "is_upload"//是否已上传
            val UPLOAD_COUNT = "upload_count"//已尝试执行几次
            val CREATE_TIME = "create_time"//创建时间

            val SQLITE_CREATE = "create table if not exists " + ALIPAY_TABLE_NAME + " (" +
                    OUT_TRADE_NO + TEXT_TYPE + " PRIMARY KEY, " +
                    TRADE_NO + TEXT_TYPE + COMMA_SEP +
                    TEL_SEQ + TEXT_TYPE + COMMA_SEP +
                    TOTAL_FEE + DOUBLE_TYPE + COMMA_SEP +
                    STORE_ID + TEXT_TYPE + COMMA_SEP +
                    ASS_POS + INT_TYPE + COMMA_SEP +
                    NEXT_TRANNO + INT_TYPE + COMMA_SEP +
                    SEQ + TEXT_TYPE + COMMA_SEP +
                    BUYER_LOGON_ID + TEXT_TYPE + COMMA_SEP +
                    THE_STEP + INT_TYPE + COMMA_SEP +
                    ERROR_MESSAGE + TEXT_TYPE + COMMA_SEP +
                    IS_DONE + INT_TYPE + DEFAULT + COMMA_SEP +
                    IS_UPLOAD + INT_TYPE + DEFAULT + COMMA_SEP +
                    UPLOAD_COUNT + INT_TYPE + DEFAULT + COMMA_SEP +
                    CREATE_TIME + " DATETIME DEFAULT (datetime(CURRENT_TIMESTAMP,'localtime')))"

            val SQLITE_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + ALIPAY_TABLE_NAME
        }
    }
}

class ALIPayDao(context: Context) {
    private val payHelper = SQLDBHelper(context)

    fun updateSQL(bean: ALIPaySqlBean) {
        val db = payHelper.writableDatabase
        db!!.beginTransaction()
        try {
            val values = ContentValues()
            values.put(THE_STEP, bean.theStep)
            values.put(IS_DONE, bean.isDone)
            values.put(IS_UPLOAD, bean.isUpload)
            values.put(UPLOAD_COUNT, bean.uploadCount)
            values.put(ERROR_MESSAGE, bean.errorMessage)
            db.update(ALIPAY_TABLE_NAME, values, OUT_TRADE_NO + "=?", arrayOf(bean.outTradeNo))
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        } finally {
            db.setTransactionSuccessful()
            db.endTransaction()
            db.close()
        }
    }

    fun deleteAll() {
        val db = payHelper.writableDatabase
        db.beginTransaction()
        try {
            db.delete(ALIPAY_TABLE_NAME, OUT_TRADE_NO + "!=?", arrayOf("0"))
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        } finally {
            db.setTransactionSuccessful()
            db.endTransaction()
            db.close()
        }
    }

    fun insertSql(bean: ALIPaySqlBean) {
        val db = payHelper.writableDatabase
        db!!.beginTransaction()
        try {
            val values = ContentValues()
            values.put(OUT_TRADE_NO, bean.outTradeNo)
            values.put(TRADE_NO, bean.tradeNo)
            values.put(TEL_SEQ, bean.telSeq)
            values.put(TOTAL_FEE, bean.totalFee)
            values.put(STORE_ID, bean.storeId)
            values.put(ASS_POS, bean.assPos)
            values.put(NEXT_TRANNO, bean.nextTranNo)
            values.put(SEQ, bean.seq)
            values.put(BUYER_LOGON_ID, bean.buyerLogonId)
            values.put(THE_STEP, bean.theStep)
            values.put(ERROR_MESSAGE, bean.errorMessage)
            values.put(IS_DONE, bean.isDone)
            values.put(IS_UPLOAD, bean.isUpload)
            values.put(UPLOAD_COUNT, bean.uploadCount)
            db.insertOrThrow(ALIPAY_TABLE_NAME, null, values)
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        } finally {
            db.setTransactionSuccessful()
            db.endTransaction()
            db.close()
        }
    }

    fun getAllData(): ArrayList<ALIPaySqlBean> {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = payHelper.readableDatabase
            val sql = "select * from $ALIPAY_TABLE_NAME where $IS_DONE = ?"
            cursor = db.rawQuery(sql, arrayOf("0"))
            if (cursor!!.count > 0) {
                val result = ArrayList<ALIPaySqlBean>(cursor.count)
                //循环添加进list中
                while (cursor.moveToNext()) {
                    result.add(parseALIPayBean(cursor))
                }
                return result
            }
        } catch (e: Exception) {
            MyToast.getShortToast("支付宝数据库异常，${e.message.toString()}")
        } finally {
            cursor?.close()
            db?.close()
        }
        return ArrayList()
    }

    private fun parseALIPayBean(cursor: Cursor): ALIPaySqlBean {
        val outTradeNo = cursor.getString(cursor.getColumnIndex(OUT_TRADE_NO))
        val tradeNo = cursor.getString(cursor.getColumnIndex(TRADE_NO))
        val telSeq = cursor.getString(cursor.getColumnIndex(TEL_SEQ))
        val totalFee = cursor.getDouble(cursor.getColumnIndex(TOTAL_FEE))
        val storeId = cursor.getString(cursor.getColumnIndex(STORE_ID))
        val assPos = cursor.getInt(cursor.getColumnIndex(ASS_POS))
        val nextTranno = cursor.getInt(cursor.getColumnIndex(NEXT_TRANNO))
        val seq = cursor.getString(cursor.getColumnIndex(SEQ))
        val buyerLogonId = cursor.getString(cursor.getColumnIndex(BUYER_LOGON_ID))
        val theStep = cursor.getInt(cursor.getColumnIndex(THE_STEP))
        val errorMessage = cursor.getString(cursor.getColumnIndex(ERROR_MESSAGE))
        val isDone = cursor.getInt(cursor.getColumnIndex(IS_DONE))
        val isUpload = cursor.getInt(cursor.getColumnIndex(IS_UPLOAD))
        val uploadCount = cursor.getInt(cursor.getColumnIndex(UPLOAD_COUNT))
        val createTime = cursor.getString(cursor.getColumnIndex(CREATE_TIME))
        return ALIPaySqlBean(outTradeNo, tradeNo, telSeq, totalFee, storeId, assPos, nextTranno, seq, buyerLogonId, theStep, errorMessage, isDone, isUpload, uploadCount, createTime)
    }

    companion object {
        private val TAG = "ALIPayDao"
    }
}