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
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil
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
        if (!PresenterUtil.judgmentInternet(gView)) return
        anInterface.getAllContract(cView.getPage(), cView.sort, User.getUser(), cView.contractType, cView.isJustLook, object : MyListener {

            override fun listenerSuccess(data: Any) {
                Thread(Runnable {
                    val contracts= Gson().fromJson(data as String, ContractResult::class.java)
                    if (contracts.total == 0) {
                        mHandler.post {
                            cView.showNoMessage()
                            gView.hideLoading()
                        }
                        return@Runnable
                    }
                    //检查数据是否正常
                    val maxDate = contracts.detail.filter { it.todayCount > it.maxQty }
                    val minDate = contracts.detail.filter { it.todayCount < it.minQty }
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
                    initAdapter(contracts,adapter,cView.isJustLook)
                }).start()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }
        })
    }

    fun getAllEditContract(adapter: ContractAdapter?) {
        if (!PresenterUtil.judgmentInternet(gView)) return
        anInterface.getEditAllContract(cView.getPage(), User.getUser(), object : MyListener {
            override fun listenerSuccess(data: Any) {
                Thread(Runnable {
                    val contracts=Gson().fromJson(data as String, ContractResult::class.java)
                    if (contracts.total == 0) {
                        mHandler.post {
                            cView.showNoMessage()
                            gView.hideLoading()
                        }
                        return@Runnable
                    }
                    initAdapter(contracts,adapter,true)
                }).start()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }
        })
    }

    fun searchAllContract(adapter: ContractAdapter?) {
        if (!PresenterUtil.judgmentInternet(gView)) return
        anInterface.searchContract(cView.getPage(), cView.sort, cView.searchMsg, User.getUser(), object : MyListener {
            override fun listenerSuccess(data: Any) {
                Thread(Runnable {
                    val contracts=Gson().fromJson(data as String, ContractResult::class.java)
                    if (contracts.total == 0) {
                        mHandler.post {
                            cView.showNoMessage()
                            gView.hideLoading()
                        }
                        return@Runnable
                    }
                    initAdapter(contracts,adapter,false)
                }).start()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }
        })
    }

    fun updateAllContract() {
        if (!PresenterUtil.judgmentInternet(gView)) return
        anInterface.updateAllContract(cView.contractList, User.getUser(), object : MyListener {

            override fun listenerSuccess(data:Any) {
                cView.updateDone()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }

        })
    }

    private fun initAdapter(cr: ContractResult,adapter: ContractAdapter?,isJustLook: Boolean){
        if (adapter==null){
            val result =  ContractAdapter(cr, context, object : RecyclerOnTouch {
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
            mHandler.post { gView.showView(result) }
        }else{
            adapter.addItem(cr.detail)
            mHandler.post { adapter.changeMoreStatus(adapter.PULLUP_LOAD_MORE) }
        }
        mHandler.post {
            cView.setPage(cr.page)
            gView.hideLoading()
        }
    }
}