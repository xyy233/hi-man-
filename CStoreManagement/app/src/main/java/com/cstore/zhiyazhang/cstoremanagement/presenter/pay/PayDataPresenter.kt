package com.cstore.zhiyazhang.cstoremanagement.presenter.pay

import com.cstore.zhiyazhang.cstoremanagement.bean.ALIPaySqlBean
import com.cstore.zhiyazhang.cstoremanagement.bean.CashPaySqlBean
import com.cstore.zhiyazhang.cstoremanagement.bean.WXPaySqlBean
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.pay.PayDataModel
import com.cstore.zhiyazhang.cstoremanagement.sql.ALIPayDao
import com.cstore.zhiyazhang.cstoremanagement.sql.CashPayDao
import com.cstore.zhiyazhang.cstoremanagement.sql.WXPayDao
import com.cstore.zhiyazhang.cstoremanagement.url.AppUrl
import com.cstore.zhiyazhang.cstoremanagement.utils.ConnectionDetector
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil
import com.google.gson.Gson
import com.zhy.http.okhttp.OkHttpUtils

/**
 * Created by zhiya.zhang
 * on 2017/12/18 15:40.
 */
class PayDataPresenter {
    private val model = PayDataModel()

    /**
     * 处理微信数据
     */
    fun goWXData(wxPaySqlBean: WXPaySqlBean, dao: WXPayDao) {
        model.goWXData(dao, wxPaySqlBean, object : MyListener {
            override fun listenerSuccess(data: Any) {
                //成功不进行任何操作直接结束
                return
            }

            override fun listenerOther(data: Any) {
                //替代出错
                data as WXPaySqlBean
                //没上传过才上传
                if (data.isUpload == 0) {
                    goWXDataHttp(data, dao)
                }
            }

        })
    }

    /**
     * 处理微信数据
     */
    fun goAliData(aliPaySqlBean: ALIPaySqlBean, dao: ALIPayDao) {
        model.goAliData(dao, aliPaySqlBean, object : MyListener {
            override fun listenerSuccess(data: Any) {
                //成功不进行任何操作直接结束
                return
            }

            override fun listenerOther(data: Any) {
                //替代出错
                data as ALIPaySqlBean
                //没上传过才上传
                if (data.isUpload == 0) {
                    goAliDataHttp(data, dao)
                }
            }

        })
    }

    fun goCashData(bean: CashPaySqlBean, dao: CashPayDao) {
        model.goCashData(dao, bean, object : MyListener {
            override fun listenerSuccess(data: Any) {
                return
            }

            override fun listenerOther(data: Any) {
                data as CashPaySqlBean
                if (data.isUpload == 0) {
                    goCashDataHttp(data, dao)
                }
            }
        })
    }

    private fun goCashDataHttp(bean: CashPaySqlBean, dao: CashPayDao) {
        if (!ConnectionDetector.getConnectionDetector().isOnline) return
        val myListener = object : MyListener {
            override fun listenerSuccess(data: Any) {
                //成功
                bean.isUpload = 1
                dao.updateSQL(bean)
            }
        }
        val response = OkHttpUtils
                .postString()
                .url(AppUrl.UPLOAD_ERROR)
                .content("${Gson().toJson(bean)} \r\n 时间：${MyTimeUtil.nowTimeString}\r\n\r\n\r\n\r\n\r\n")
                .addHeader("fileName", "${bean.storeId}/payError${MyTimeUtil.nowDate}.txt")
                .build()
                .execute()

        if (response.isSuccessful) {
            myListener.listenerSuccess("")
        }
    }

    /**
     * HTTP发送数据到总部
     */
    fun goWXDataHttp(bean: WXPaySqlBean, dao: WXPayDao) {
        if (!ConnectionDetector.getConnectionDetector().isOnline) return
        val myListener = object : MyListener {
            override fun listenerSuccess(data: Any) {
                //成功
                bean.isUpload = 1
                dao.updateSQL(bean)
            }
        }
        val response = OkHttpUtils
                .postString()
                .url(AppUrl.UPLOAD_ERROR)
                .content("${Gson().toJson(bean)} \r\n 时间：${MyTimeUtil.nowTimeString}\r\n\r\n\r\n\r\n\r\n")
                .addHeader("fileName", "${bean.storeId}/payError${MyTimeUtil.nowDate}.txt")
                .build()
                .execute()

        if (response.isSuccessful) {
            myListener.listenerSuccess("")
        }
    }

    fun goAliDataHttp(bean: ALIPaySqlBean, dao: ALIPayDao) {
        if (!ConnectionDetector.getConnectionDetector().isOnline) return
        val myListener = object : MyListener {
            override fun listenerSuccess(data: Any) {
                //成功
                bean.isUpload = 1
                dao.updateSQL(bean)
            }
        }
        val response = OkHttpUtils
                .postString()
                .url(AppUrl.UPLOAD_ERROR)
                .content("${Gson().toJson(bean)} \r\n 时间：${MyTimeUtil.nowTimeString}\r\n\r\n\r\n\r\n\r\n")
                .addHeader("fileName", "${bean.storeId}/payError${MyTimeUtil.nowDate}.txt")
                .build()
                .execute()

        if (response.isSuccessful) {
            myListener.listenerSuccess("")
        }
    }
}