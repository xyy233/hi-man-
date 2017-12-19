package com.cstore.zhiyazhang.cstoremanagement.sql

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import com.cstore.zhiyazhang.cstoremanagement.bean.WXPaySqlBean
import com.cstore.zhiyazhang.cstoremanagement.sql.SQLData.COMMA_SEP
import com.cstore.zhiyazhang.cstoremanagement.sql.SQLData.DB_NAME
import com.cstore.zhiyazhang.cstoremanagement.sql.SQLData.DB_VERSION
import com.cstore.zhiyazhang.cstoremanagement.sql.SQLData.DEFAULT
import com.cstore.zhiyazhang.cstoremanagement.sql.SQLData.DOUBLE_TYPE
import com.cstore.zhiyazhang.cstoremanagement.sql.SQLData.INT_TYPE
import com.cstore.zhiyazhang.cstoremanagement.sql.SQLData.TEXT_TYPE
import com.cstore.zhiyazhang.cstoremanagement.sql.WXPayData.WXPayEntry.Companion.ASS_POS
import com.cstore.zhiyazhang.cstoremanagement.sql.WXPayData.WXPayEntry.Companion.COUPON_FEE
import com.cstore.zhiyazhang.cstoremanagement.sql.WXPayData.WXPayEntry.Companion.CREATE_TIME
import com.cstore.zhiyazhang.cstoremanagement.sql.WXPayData.WXPayEntry.Companion.ERROR_MESSAGE
import com.cstore.zhiyazhang.cstoremanagement.sql.WXPayData.WXPayEntry.Companion.IS_DONE
import com.cstore.zhiyazhang.cstoremanagement.sql.WXPayData.WXPayEntry.Companion.IS_UPLOAD
import com.cstore.zhiyazhang.cstoremanagement.sql.WXPayData.WXPayEntry.Companion.NEXT_TRANNO
import com.cstore.zhiyazhang.cstoremanagement.sql.WXPayData.WXPayEntry.Companion.OPENID
import com.cstore.zhiyazhang.cstoremanagement.sql.WXPayData.WXPayEntry.Companion.OUT_TRADE_NO
import com.cstore.zhiyazhang.cstoremanagement.sql.WXPayData.WXPayEntry.Companion.SEQ
import com.cstore.zhiyazhang.cstoremanagement.sql.WXPayData.WXPayEntry.Companion.STORE_ID
import com.cstore.zhiyazhang.cstoremanagement.sql.WXPayData.WXPayEntry.Companion.TEL_SEQ
import com.cstore.zhiyazhang.cstoremanagement.sql.WXPayData.WXPayEntry.Companion.THE_STEP
import com.cstore.zhiyazhang.cstoremanagement.sql.WXPayData.WXPayEntry.Companion.TOTAL_FEE
import com.cstore.zhiyazhang.cstoremanagement.sql.WXPayData.WXPayEntry.Companion.TRANSACTION_ID
import com.cstore.zhiyazhang.cstoremanagement.sql.WXPayData.WXPayEntry.Companion.UPLOAD_COUNT
import com.cstore.zhiyazhang.cstoremanagement.sql.WXPayData.WXPayEntry.Companion.WXPAY_TABLE_NAME
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast

/**
 * Created by zhiya.zhang
 * on 2017/12/8 14:13.
 *
 * 正常流程：
 * 微信收款完成执行存储过程->未成功记1
 *      成功
 * 单独保存在微信记录中->未成功记2
 *      成功
 * 结束
 *
 * 撤销订单流程：
 * 向微信提交撤销订单->未成功记3
 *      成功
 *
 * 修改app_pos表中的next_tranno+1 清空shopping_basket表中本机tel_seq所有数据
 * 修改app_pos和清空shopping_basket整合成存储过程 ->未成功记4
 *
 * 完成
 *
 * 处理完毕的不上传至总部，未处理完成并已尝试1次以上的上传至总部直至已上传至总部
 */
object WXPayData {
    class WXPayEntry : BaseColumns {
        companion object {
            val WXPAY_TABLE_NAME = "wxpay"
            val OUT_TRADE_NO = "out_trade_no"//商户订单号
            val TRANSACTION_ID = "transaction_id"//微信订单号
            val TEL_SEQ = "tel_seq"//本机序列号
            val TOTAL_FEE = "total_fee"//交易金额
            val STORE_ID = "store_id"//店号
            val ASS_POS = "ass_pos"//分配pos机号
            val NEXT_TRANNO = "next_tranno"//生成订单的东西
            val SEQ = "seq"//第几次提交
            val OPENID = "openid"//用户的id
            val COUPON_FEE = "coupon_fee"//代金卷金额
            val THE_STEP = "the_step"//当前步骤数
            val ERROR_MESSAGE = "error_message"//错误原因
            val IS_DONE = "is_done"//是否处理完毕
            val IS_UPLOAD = "is_upload"//是否已上传
            val UPLOAD_COUNT = "upload_count"//已尝试执行几次
            val CREATE_TIME = "create_time"//创建时间
        }
    }
}

class WXPayDBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
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
        private val SQLITE_CREATE = "create table if not exists " + WXPAY_TABLE_NAME + " (" +
                OUT_TRADE_NO + TEXT_TYPE + " PRIMARY KEY, " +
                TRANSACTION_ID + TEXT_TYPE + COMMA_SEP +
                TEL_SEQ + TEXT_TYPE + COMMA_SEP +
                TOTAL_FEE + DOUBLE_TYPE + COMMA_SEP +
                STORE_ID + TEXT_TYPE + COMMA_SEP +
                ASS_POS + INT_TYPE + COMMA_SEP +
                NEXT_TRANNO + INT_TYPE + COMMA_SEP +
                SEQ + TEXT_TYPE + COMMA_SEP +
                OPENID + TEXT_TYPE + COMMA_SEP +
                COUPON_FEE + DOUBLE_TYPE + COMMA_SEP +
                THE_STEP + INT_TYPE + COMMA_SEP +
                ERROR_MESSAGE + TEXT_TYPE + COMMA_SEP +
                IS_DONE + INT_TYPE + DEFAULT + COMMA_SEP +
                IS_UPLOAD + INT_TYPE + DEFAULT + COMMA_SEP +
                UPLOAD_COUNT + INT_TYPE + DEFAULT + COMMA_SEP +
                CREATE_TIME + " DATETIME DEFAULT (datetime(CURRENT_TIMESTAMP,'localtime')))"

        private val SQLITE_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + WXPAY_TABLE_NAME
    }
}

class WXPayDao(context: Context) {
    private val WXPAY_COLUMNS = arrayOf<String>(OUT_TRADE_NO, TRANSACTION_ID, TEL_SEQ, TOTAL_FEE, STORE_ID,
            ASS_POS, NEXT_TRANNO, SEQ, OPENID, COUPON_FEE, THE_STEP, ERROR_MESSAGE, IS_DONE, IS_UPLOAD, UPLOAD_COUNT, CREATE_TIME)

    private val payHelper = WXPayDBHelper(context)

    /**
     * 修改
     * @param bean WXPay的数据库javaBean对象
     * 必要数据有： outTradeNo.
     * 更新数据有： theStep, isDone, isUpload, uploadCount, errorMessage.
     */
    fun updateSQL(bean: WXPaySqlBean) {
        val db = payHelper.writableDatabase
        db!!.beginTransaction()
        try {
            val values = ContentValues()
            values.put(THE_STEP, bean.theStep)
            values.put(IS_DONE, bean.isDone)
            values.put(IS_UPLOAD, bean.isUpload)
            values.put(UPLOAD_COUNT, bean.uploadCount)
            values.put(ERROR_MESSAGE, bean.errorMessage)
            db.update(WXPAY_TABLE_NAME, values, OUT_TRADE_NO + "=?", arrayOf(bean.outTradeNo))
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
    fun testFun(){
        val db: SQLiteDatabase = payHelper.readableDatabase
        try {
            db.delete(WXPAY_TABLE_NAME, IS_UPLOAD+"==?", arrayOf("1"))
           /* val values=ContentValues()
            values.put(IS_UPLOAD,"0")
            db.update(WXPAY_TABLE_NAME,values, IS_UPLOAD+"==?", arrayOf("1"))*/
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
    fun insertSql(payBean: WXPaySqlBean) {
        val db = payHelper.writableDatabase
        db!!.beginTransaction()
        try {
            val values = ContentValues()
            values.put(OUT_TRADE_NO, payBean.outTradeNo)
            values.put(TRANSACTION_ID, payBean.transactionId)
            values.put(TEL_SEQ, payBean.telSeq)
            values.put(TOTAL_FEE, payBean.totalFee)
            values.put(STORE_ID, payBean.storeId)
            values.put(ASS_POS, payBean.assPos)
            values.put(NEXT_TRANNO, payBean.nextTranNo)
            values.put(SEQ, payBean.seq)
            values.put(OPENID, payBean.openId)
            values.put(COUPON_FEE, payBean.couponFee)
            values.put(THE_STEP, payBean.theStep)
            values.put(ERROR_MESSAGE, payBean.errorMessage)
            values.put(IS_DONE, payBean.isDone)
            values.put(IS_UPLOAD, payBean.isUpload)
            values.put(UPLOAD_COUNT, payBean.uploadCount)
            db.insertOrThrow(WXPAY_TABLE_NAME, null, values)
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
    fun getAllData(): ArrayList<WXPaySqlBean> {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = payHelper.readableDatabase
            val sql = "select * from $WXPAY_TABLE_NAME where $IS_DONE == ?"
            cursor = db.rawQuery(sql, arrayOf("0"))
            if (cursor!!.count > 0) {
                val result = ArrayList<WXPaySqlBean>(cursor.count)
                //循环添加进list中
                while (cursor.moveToNext()) {
                    result.add(parseWXPayBean(cursor))
                }
                return result
            }
        } catch (e: Exception) {
            MyToast.getShortToast("微信数据库异常，${e.message.toString()}")
        } finally {
            cursor?.close()
            db?.close()
        }
        return ArrayList()
    }

    private fun parseWXPayBean(cursor: Cursor): WXPaySqlBean {
        val outTradeNo = cursor.getString(cursor.getColumnIndex(OUT_TRADE_NO))
        val transactionId = cursor.getString(cursor.getColumnIndex(TRANSACTION_ID))
        val telSeq = cursor.getString(cursor.getColumnIndex(TEL_SEQ))
        val totalFee = cursor.getDouble(cursor.getColumnIndex(TOTAL_FEE))
        val storeId = cursor.getString(cursor.getColumnIndex(STORE_ID))
        val assPos = cursor.getInt(cursor.getColumnIndex(ASS_POS))
        val nextTranno = cursor.getInt(cursor.getColumnIndex(NEXT_TRANNO))
        val seq = cursor.getString(cursor.getColumnIndex(SEQ))
        val openId = cursor.getString(cursor.getColumnIndex(OPENID))
        val couponFee = cursor.getDouble(cursor.getColumnIndex(COUPON_FEE))
        val theStep = cursor.getInt(cursor.getColumnIndex(THE_STEP))
        val errorMessage = cursor.getString(cursor.getColumnIndex(ERROR_MESSAGE))
        val isDone = cursor.getInt(cursor.getColumnIndex(IS_DONE))
        val isUpload = cursor.getInt(cursor.getColumnIndex(IS_UPLOAD))
        val uploadCount = cursor.getInt(cursor.getColumnIndex(UPLOAD_COUNT))
        val createTime = cursor.getString(cursor.getColumnIndex(CREATE_TIME))
        return WXPaySqlBean(outTradeNo, transactionId, telSeq, totalFee, storeId, assPos, nextTranno, seq, openId,
                couponFee, theStep, errorMessage, isDone, isUpload, uploadCount, createTime)
    }

    companion object {
        private val TAG = "WXPayDao"
    }
}