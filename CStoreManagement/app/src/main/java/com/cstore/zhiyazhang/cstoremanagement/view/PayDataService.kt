package com.cstore.zhiyazhang.cstoremanagement.view

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.cstore.zhiyazhang.cstoremanagement.presenter.pay.PayDataPresenter
import com.cstore.zhiyazhang.cstoremanagement.sql.ALIPayDao
import com.cstore.zhiyazhang.cstoremanagement.sql.CashPayDao
import com.cstore.zhiyazhang.cstoremanagement.sql.WXPayDao

/**
 * Created by zhiya.zhang
 * on 2017/12/18 11:59.
 * IntentService不用考虑线程，随便玩
 * 每个方向只处理三次。三次后继续重来
 */
class PayDataService(value: String = "PayDataService") : IntentService(value) {
    private val mTag = "PayDataService"
    private val presenter = PayDataPresenter()
    private val data = ArrayList<Any>()
    // 1=微信 2=支付宝 3=现金 0=非指定方式，需要全部查询
    private var isWhere = 0
    private var isDone = true
    private lateinit var wxDao: WXPayDao
    private lateinit var cashDao: CashPayDao
    private lateinit var aliDao: ALIPayDao

    override fun onHandleIntent(intent: Intent?) {
        try {
            if (intent != null) {
                isWhere = intent.getIntExtra("isWhere", 0)
                val incData = intent.getSerializableExtra("sqlBean")
                if (incData != null) {
                    data.addAll(incData as ArrayList<*>)
                }

                cashDao = CashPayDao(this)
                wxDao = WXPayDao(this)
                aliDao = ALIPayDao(this)

                goTry(isWhere)
            }
        } catch (e: Exception) {
            Log.e(mTag, e.message)
        }
    }

    /**
     * 开始处理，没处理完就不停重复
     */
    private fun goTry(where: Int) {
        when (isWhere) {
            0 -> {
                //非指定支付，查询全部数据后处理
                judgmentWX()
                judgmentAli()
                judgmentCash()
            }
            1 -> {
                //微信
                judgmentWX()
            }
            2 -> {
                //支付宝
                judgmentAli()
            }
            3 -> {
                //现金
                judgmentCash()
            }
        }
        if (!isDone) {
            isDone = true
            Thread.sleep(1000 * 10)
            goTry(where)
        }
    }

    private var wxCount = 0
    /**
     * 判断微信的数据库
     */
    private fun judgmentWX() {
        if (wxCount++ >= 4) {
            wxCount = 0
            return
        }
        val wxData = wxDao.getAllData()
        if (wxData.size == 0) return
        //找出所有未处理的,循环处理
        wxData.filter { it.isDone == 0 }.forEach {
            presenter.goWXData(it, wxDao)
        }
        //处理完后重新获得数据库内容判断是否结束
        if (wxDao.getAllData().any { it.isDone == 0 }) {
            isDone = false
            judgmentWX()
        }
    }

    private var aliCount = 0
    /**
     * 查询支付宝的数据库
     */
    private fun judgmentAli() {
        if (aliCount++ >= 4) {
            aliCount = 0
            return
        }
        val aliData = aliDao.getAllData()
        if (aliData.size == 0) return
        aliData.filter { it.isDone == 0 }.forEach {
            presenter.goAliData(it, aliDao)
        }
        if (aliDao.getAllData().any { it.isDone == 0 }) {
            isDone = false
            judgmentAli()
        }
    }

    private var cashCount = 0
    /**
     * 查询现金的数据库
     */
    private fun judgmentCash() {
        if (cashCount++ >= 4) {
            cashCount = 0
            return
        }
        val cashData = cashDao.getAllData()
        if (cashData.size == 0) return
        cashData.filter { it.isDone == 0 }.forEach {
            presenter.goCashData(it, cashDao)
        }
        if (cashDao.getAllData().any { it.isDone == 0 }) {
            isDone = false
            judgmentCash()
        }
    }

}