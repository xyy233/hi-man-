package com.cstore.zhiyazhang.cstoremanagement.presenter.paiban

import com.cstore.zhiyazhang.cstoremanagement.bean.PaibanBean
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.paiban.PaibanModel
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView

/**
 * Created by zhiya.zhang
 * on 2018/2/8 13:51.
 */
class PaibanPresenter(private val view: GenericView) {
    val model = PaibanModel()

    /**
     * 得到排班数据，data1为activity， data2为需要得到数据的日期
     */
    fun getDataByDate() {
        if (!PresenterUtil.judgmentInternet(view)) return
        val handler = MyHandler().writeActivity(view.getData1() as MyActivity)
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                view.showView(data)
                view.hideLoading()
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                view.showPrompt(errorMessage)
                view.hideLoading()
                handler.cleanAll()
            }
        })
        model.getDataByDate(view.getData2() as String, view.getData4() as String, handler)
    }

    /**
     * 修改数据
     * @param type 1=创建 2=更新 3=删除
     */
    fun editData(type: Int) {
        if (!PresenterUtil.judgmentInternet(view)) return
        val handler = MyHandler().writeActivity(view.getData1() as MyActivity)
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                view.updateDone(data)
                view.hideLoading()
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                view.errorDealWith()
                view.showPrompt(errorMessage)
                view.hideLoading()
                handler.cleanAll()
            }
        })
        when (type) {
            1 -> {
                model.createPaiban(view.getData3() as PaibanBean, handler)
            }
            2 -> {
                model.updatePaiban(view.getData3() as PaibanBean, handler)
            }
            3 -> {
                model.deletePaiban(view.getData3() as PaibanBean, handler)
            }
        }
    }
}