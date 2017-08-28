package com.cstore.zhiyazhang.cstoremanagement.presenter.contract

import android.content.Context
import android.os.Handler
import android.view.MotionEvent
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ContractBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ContractResult
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.contract.ContractInterface
import com.cstore.zhiyazhang.cstoremanagement.model.contract.ContractModel
import com.cstore.zhiyazhang.cstoremanagement.utils.ConnectionDetector
import com.cstore.zhiyazhang.cstoremanagement.utils.ReportListener
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.RecyclerOnTouch
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.ContractView
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.google.gson.Gson
import com.zhiyazhang.mykotlinapplication.utils.MyApplication

/**
 * Created by zhiya.zhang
 * on 2017/6/14 16:24.
 */
class ContractPresenter(private val cView: ContractView, private val gView: GenericView, private val context: Context) {
    val anInterface: ContractInterface = ContractModel()
    val mHandler = Handler()

    fun getAllContract(adapter: ContractAdapter?) {
        gView.showLoading()
        if (!ConnectionDetector.getConnectionDetector().isOnline) {
            gView.showPrompt(context.getString(R.string.noInternet))
            gView.hideLoading()
            return
        }
        anInterface.getAllContract(cView.getPage(), cView.sort, User.getUser(), cView.contractType, cView.isJustLook, object : MyListener {
            override fun listenerSuccess() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun listenerSuccess(`object`: Any) {
                if ((`object` as ContractResult).total == 0) {
                    cView.showNoMessage()
                    gView.hideLoading()
                    return
                }
                //检查数据是否正常
                val maxDate = `object`.detail.filter { it.todayCount > it.maxQty }
                val minDate = `object`.detail.filter { it.todayCount < it.minQty }
                when {
                    maxDate.isNotEmpty() -> {
                        maxDate.forEach { it.isCanChange = false }
                    }
                    minDate.isNotEmpty() -> {
                        maxDate.forEach { it.isCanChange = false }
                    }
                }
                //数据异常就传递数据到服务器
                if (maxDate.isNotEmpty() || minDate.isNotEmpty()) {
                    ReportListener.report(User.getUser().storeId, MyApplication.getVersion()!!, context.getString(R.string.maxOrMinError), "${Gson().toJson(maxDate)}\r\n${Gson().toJson(minDate)}")
                }
                mHandler.post(object : ContractRunnable(`object`, adapter, cView.isJustLook) {})
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }
        })
    }

    fun getAllEditContract(adapter: ContractAdapter?) {
        gView.showLoading()
        if (!ConnectionDetector.getConnectionDetector().isOnline) {
            gView.showPrompt(context.getString(R.string.noInternet))
            gView.hideLoading()
            return
        }
        anInterface.getEditAllContract(cView.getPage(), User.getUser(), object : MyListener {
            override fun listenerSuccess() {}

            override fun listenerSuccess(`object`: Any) {
                if ((`object` as ContractResult).total == 0) {
                    cView.showNoMessage()
                    gView.hideLoading()
                    return
                }
                mHandler.post(object : ContractRunnable(`object`, adapter, true) {})
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }
        })
    }

    fun searchAllContract(adapter: ContractAdapter?) {
        gView.showLoading()
        if (!ConnectionDetector.getConnectionDetector().isOnline) {
            gView.showPrompt(context.getString(R.string.noInternet))
            gView.hideLoading()
            return
        }
        anInterface.searchContract(cView.getPage(), cView.sort, cView.searchMsg, User.getUser(), object : MyListener {
            override fun listenerSuccess() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun listenerSuccess(`object`: Any) {
                if ((`object` as ContractResult).total == 0) {
                    cView.showNoMessage()
                    gView.hideLoading()
                    return
                }

                mHandler.post(object : ContractRunnable(`object`, adapter, false) {})
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }
        })
    }

    fun updateAllContract() {
        gView.showLoading()
        if (!ConnectionDetector.getConnectionDetector().isOnline) {
            gView.showPrompt(context.getString(R.string.noInternet))
            gView.hideLoading()
            return
        }
        anInterface.updateAllContract(cView.contractList, User.getUser(), object : MyListener {
            override fun listenerSuccess() {
                cView.updateDone()
            }

            override fun listenerSuccess(`object`: Any) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }

        })
    }

    private open inner class ContractRunnable internal constructor(internal val cr: ContractResult, internal var adapter: ContractAdapter?, internal val isJustLook: Boolean) : Runnable {
        override fun run() {
            if (adapter == null) {
                adapter = ContractAdapter(cr, context, object : RecyclerOnTouch {
                    override fun <T> onClickImage(objects: T, position: Int) {
                        when (objects) {
                            is ContractBean -> cView.clickImage(objects, position)
                        }
                    }

                    override fun <T> onTouchAddListener(objects: T, event: MotionEvent, position: Int) {
                        when (objects) {
                            is ContractBean -> cView.touchAdd(objects, event, position)
                        }
                    }

                    override fun <T> onTouchLessListener(objects: T, event: MotionEvent, position: Int) {
                        when (objects) {
                            is ContractBean -> cView.touchLess(objects, event, position)
                        }
                    }
                }, isJustLook)
                gView.showView(adapter)
                cView.setPage(cr.page)
                gView.hideLoading()
            } else {
                adapter!!.addItem(cr.detail)
                adapter!!.changeMoreStatus(adapter!!.PULLUP_LOAD_MORE)
                cView.setPage(cr.page)
                gView.hideLoading()
            }
        }

    }
}