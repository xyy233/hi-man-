package com.cstore.zhiyazhang.cstoremanagement.sql

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import android.util.Log
import com.cstore.zhiyazhang.cstoremanagement.bean.CashPaySqlBean
import com.cstore.zhiyazhang.cstoremanagement.sql.CashPayData.CashPayEntry.Companion.ASS_POS
import com.cstore.zhiyazhang.cstoremanagement.sql.CashPayData.CashPayEntry.Companion.CASHPAY_TABLE_NAME
import com.cstore.zhiyazhang.cstoremanagement.sql.CashPayData.CashPayEntry.Companion.CREATE_TIME
import com.cstore.zhiyazhang.cstoremanagement.sql.CashPayData.CashPayEntry.Companion.ERROR_MESSAGE
import com.cstore.zhiyazhang.cstoremanagement.sql.CashPayData.CashPayEntry.Companion.IS_DONE
import com.cstore.zhiyazhang.cstoremanagement.sql.CashPayData.CashPayEntry.Companion.IS_UPLOAD
import com.cstore.zhiyazhang.cstoremanagement.sql.CashPayData.CashPayEntry.Companion.NEXT_TRANNO
import com.cstore.zhiyazhang.cstoremanagement.sql.CashPayData.CashPayEntry.Companion.OUT_TRADE_NO
import com.cstore.zhiyazhang.cstoremanagement.sql.CashPayData.CashPayEntry.Companion.SEQ
import com.cstore.zhiyazhang.cstoremanagement.sql.CashPayData.CashPayEntry.Companion.STORE_ID
import com.cstore.zhiyazhang.cstoremanagement.sql.CashPayData.CashPayEntry.Companion.TEL_SEQ
import com.cstore.zhiyazhang.cstoremanagement.sql.CashPayData.CashPayEntry.Companion.THE_STEP
import com.cstore.zhiyazhang.cstoremanagement.sql.CashPayData.CashPayEntry.Companion.TOTAL_FEE
import com.cstore.zhiyazhang.cstoremanagement.sql.CashPayData.CashPayEntry.Companion.UPLOAD_COUNT
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast

/**
 * Created by zhiya.zhang
 * on 2017/12/20 12:19.
 *
 * 正常流程：
 * 现金收款完成执行存储过程->未成功记1
 *      成功
 * 结束
 *
 * 退款流程：
 * 提交退款sql->未成功记2
 *      成功
 * 完成
 *
 * 处理完毕的不上传至总部，未处理完成并已尝试1次以上的上传至总部直至已上传至总部
 */
object CashPayData {
    class CashPayEntry : BaseColumns {
        companion object {
            val CASHPAY_TABLE_NAME = "cashpay"
            val OUT_TRADE_NO = "cash_trade_no"//唯一交易号
            val TEL_SEQ = "tel_seq"//本机序列号
            val TOTAL_FEE = "total_fee"//交易金额
            val STORE_ID = "store_id"//店号
            val ASS_POS = "ass_pos"//分配pos机号
            val NEXT_TRANNO = "next_tranno"//生成订单的东西
            val SEQ = "seq"//第几次提交
            val THE_STEP = "the_step"//当前步骤数
            val ERROR_MESSAGE = "error_message"//错误原因
            val IS_DONE = "is_done"//是否处理完毕
            val IS_UPLOAD = "is_upload"//是否已上传
            val UPLOAD_COUNT = "upload_count"//已尝试执行几次
            val CREATE_TIME = "create_time"//创建时间

            val SQLITE_CREATE = "create table if not exists " + CashPayData.CashPayEntry.CASHPAY_TABLE_NAME + " (" +
                    CashPayData.CashPayEntry.OUT_TRADE_NO + SQLData.TEXT_TYPE + " PRIMARY KEY, " +
                    CashPayData.CashPayEntry.TEL_SEQ + SQLData.TEXT_TYPE + SQLData.COMMA_SEP +
                    CashPayData.CashPayEntry.TOTAL_FEE + SQLData.DOUBLE_TYPE + SQLData.COMMA_SEP +
                    CashPayData.CashPayEntry.STORE_ID + SQLData.TEXT_TYPE + SQLData.COMMA_SEP +
                    CashPayData.CashPayEntry.ASS_POS + SQLData.INT_TYPE + SQLData.COMMA_SEP +
                    CashPayData.CashPayEntry.NEXT_TRANNO + SQLData.INT_TYPE + SQLData.COMMA_SEP +
                    CashPayData.CashPayEntry.SEQ + SQLData.TEXT_TYPE + SQLData.COMMA_SEP +
                    CashPayData.CashPayEntry.THE_STEP + SQLData.INT_TYPE + SQLData.COMMA_SEP +
                    CashPayData.CashPayEntry.ERROR_MESSAGE + SQLData.TEXT_TYPE + SQLData.COMMA_SEP +
                    CashPayData.CashPayEntry.IS_DONE + SQLData.INT_TYPE + SQLData.DEFAULT + SQLData.COMMA_SEP +
                    CashPayData.CashPayEntry.IS_UPLOAD + SQLData.INT_TYPE + SQLData.DEFAULT + SQLData.COMMA_SEP +
                    CashPayData.CashPayEntry.UPLOAD_COUNT + SQLData.INT_TYPE + SQLData.DEFAULT + SQLData.COMMA_SEP +
                    CashPayData.CashPayEntry.CREATE_TIME + " DATETIME DEFAULT (datetime(CURRENT_TIMESTAMP,'localtime')))"

            val SQLITE_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + CashPayData.CashPayEntry.CASHPAY_TABLE_NAME
        }
    }
}

class CashPayDao(context: Context) {

    private val payHelper = SQLDBHelper(context)

    /**
     * 修改
     * @param bean CashPay的数据库javaBean对象
     * 必要数据有： outTradeNo.
     * 更新数据有： theStep, isDone, isUpload, uploadCount, errorMessage.
     */
    fun updateSQL(bean: CashPaySqlBean) {
        val db = payHelper.writableDatabase
        db!!.beginTransaction()
        try {
            val values = ContentValues()
            values.put(THE_STEP, bean.theStep)
            values.put(IS_DONE, bean.isDone)
            values.put(IS_UPLOAD, bean.isUpload)
            values.put(UPLOAD_COUNT, bean.uploadCount)
            values.put(ERROR_MESSAGE, bean.errorMessage)
            db.update(CASHPAY_TABLE_NAME, values, OUT_TRADE_NO + "=?", arrayOf(bean.outTradeNo))
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
        db!!.beginTransaction()
        try {
            db.delete(CASHPAY_TABLE_NAME, OUT_TRADE_NO + "!=?", arrayOf("0"))
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        } finally {
            db.setTransactionSuccessful()
            db.endTransaction()
            db.close()
        }
    }

    /**
     * 测试用方法
     */
    fun testFun() {
        val db: SQLiteDatabase = payHelper.readableDatabase
        try {
            db.delete(CASHPAY_TABLE_NAME, IS_UPLOAD + "==?", arrayOf("1"))
        } catch (e: Exception) {
            MyToast.getShortToast("微信数据库异常，${e.message.toString()}")
        } finally {
            db.setTransactionSuccessful()
            db.endTransaction()
            db.close()
        }
    }

    /**
     * 创建支付异常
     */
    fun insertSql(payBean: CashPaySqlBean) {
        val db = payHelper.writableDatabase
        db!!.beginTransaction()
        try {
            val values = ContentValues()
            values.put(OUT_TRADE_NO, payBean.outTradeNo)
            values.put(TEL_SEQ, payBean.telSeq)
            values.put(TOTAL_FEE, payBean.totalFee)
            values.put(STORE_ID, payBean.storeId)
            values.put(ASS_POS, payBean.assPos)
            values.put(NEXT_TRANNO, payBean.nextTranNo)
            values.put(SEQ, payBean.seq)
            values.put(THE_STEP, payBean.theStep)
            values.put(ERROR_MESSAGE, payBean.errorMessage)
            values.put(IS_DONE, payBean.isDone)
            values.put(IS_UPLOAD, payBean.isUpload)
            values.put(UPLOAD_COUNT, payBean.uploadCount)
            db.insertOrThrow(CASHPAY_TABLE_NAME, null, values)
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        } finally {
            db.setTransactionSuccessful()
            db.endTransaction()
            db.close()
        }
    }

    /**
     * 获得全部的数据，没必要写单独获得的
     */
    fun getAllData(): ArrayList<CashPaySqlBean> {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = payHelper.readableDatabase
            val sql = "select * from $CASHPAY_TABLE_NAME where $IS_DONE = ?"
            cursor = db.rawQuery(sql, arrayOf("0"))
            if (cursor!!.count > 0) {
                val result = ArrayList<CashPaySqlBean>(cursor.count)
                //循环添加进list中
                while (cursor.moveToNext()) {
                    result.add(parseCashPayBean(cursor))
                }
                return result
            }
        } catch (e: Exception) {
            MyToast.getShortToast("现金数据库异常，${e.message.toString()}")
        } finally {
            cursor?.close()
            db?.close()
        }
        return ArrayList()
    }

    private fun parseCashPayBean(cursor: Cursor): CashPaySqlBean {
        val outTradeNo = cursor.getString(cursor.getColumnIndex(OUT_TRADE_NO))
        val telSeq = cursor.getString(cursor.getColumnIndex(TEL_SEQ))
        val totalFee = cursor.getDouble(cursor.getColumnIndex(TOTAL_FEE))
        val storeId = cursor.getString(cursor.getColumnIndex(STORE_ID))
        val assPos = cursor.getInt(cursor.getColumnIndex(ASS_POS))
        val nextTranno = cursor.getInt(cursor.getColumnIndex(NEXT_TRANNO))
        val seq = cursor.getString(cursor.getColumnIndex(SEQ))
        val theStep = cursor.getInt(cursor.getColumnIndex(THE_STEP))
        val errorMessage = cursor.getString(cursor.getColumnIndex(ERROR_MESSAGE))
        val isDone = cursor.getInt(cursor.getColumnIndex(IS_DONE))
        val isUpload = cursor.getInt(cursor.getColumnIndex(IS_UPLOAD))
        val uploadCount = cursor.getInt(cursor.getColumnIndex(UPLOAD_COUNT))
        val createTime = cursor.getString(cursor.getColumnIndex(CREATE_TIME))
        return CashPaySqlBean(outTradeNo, telSeq, totalFee, storeId, assPos, nextTranno, seq,
                theStep, errorMessage, isDone, isUpload, uploadCount, createTime)
    }

    companion object {
        private val TAG = "CashPayDao"
    }
}