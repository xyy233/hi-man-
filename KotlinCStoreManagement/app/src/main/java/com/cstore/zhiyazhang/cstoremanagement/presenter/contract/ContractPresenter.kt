package com.csto

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
import com.cstore.zhiyazhang.cstoremanagement.presenter.contract.ContractAdapter
import com.cstore.zhiyazhang.cstoremanagement.utils.ConnectionDetector
import com.cstore.zhiyazhang.cstoremanagement.utils.onclick.RecyclerOnTouch
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.ContractView
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView

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
            override fun contractSuccess() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun contractSuccess(`object`: Any) {
                if ((`object` as ContractResult).total == 0) {
                    cView.showNoMessage()
                    gView.hideLoading()
                    return
                }
                mHandler.post(object : ContractRunnable(`object`, adapter, cView.isJustLook) {})
            }

            override fun contractFailed(errorMessage: String) {
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
            override fun contractSuccess() {}

            override fun contractSuccess(`object`: Any) {
                if ((`object` as ContractResult).total == 0) {
                    cView.showNoMessage()
                    gView.hideLoading()
                    return
                }
                mHandler.post(object : ContractRunnable(`object`, adapter, true) {})
            }

            override fun contractFailed(errorMessage: String) {
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
            override fun contractSuccess() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun contractSuccess(`object`: Any) {
                if ((`object` as ContractResult).total == 0) {
                    cView.showNoMessage()
                    gView.hideLoading()
                    return
                }
                mHandler.post(object : ContractRunnable(`object`, adapter, false) {})
            }

            override fun contractFailed(errorMessage: String) {
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
            override fun contractSuccess() {
                cView.updateDone()
            }

            override fun contractSuccess(`object`: Any) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun contractFailed(errorMessage: String) {
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
                    override fun onClickImage(cb: ContractBean, position: Int) {
                        cView.clickImage(cb, position)
                    }

                    override fun onTouchAddListener(cb: ContractBean, event: MotionEvent, position: Int) {
                        cView.touchAdd(cb, event, position)
                    }

                    override fun onTouchLessListener(cb: ContractBean, event: MotionEvent, position: Int) {
                        cView.touchLess(cb, event, position)
                    }
                }, isJustLook)
                gView.showView(adapter)
                gView.hideLoading()
                cView.setPage(cr.page)
            } else {
                adapter!!.addItem(cr.detail)
                adapter!!.changeMoreStatus(adapter!!.PULLUP_LOAD_MORE)
                gView.hideLoading()
                cView.setPage(cr.page)
            }
        }

    }
}