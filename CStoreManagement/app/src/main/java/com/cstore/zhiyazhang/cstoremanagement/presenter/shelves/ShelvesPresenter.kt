package com.cstore.zhiyazhang.cstoremanagement.presenter.shelves

import com.cstore.zhiyazhang.cstoremanagement.bean.ShelvesItemBean
import com.cstore.zhiyazhang.cstoremanagement.bean.ShelvesItemResult
import com.cstore.zhiyazhang.cstoremanagement.bean.ShelvesResult
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.shelves.ShelvesInterface
import com.cstore.zhiyazhang.cstoremanagement.model.shelves.ShelvesModel
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.google.gson.Gson

/**
 * Created by zhiya.zhang
 * on 2018/5/20 10:54.
 */
class ShelvesPresenter(private val view: GenericView) {
    val model: ShelvesInterface = ShelvesModel()
    fun getShelves() {
        if (!PresenterUtil.judgmentInternet(view)) return
        val listener = object : MyListener {
            override fun listenerSuccess(data: Any) {
                try {
                    val sb = Gson().fromJson(data as String, ShelvesResult::class.java)
                    sb.rows.sortBy { it.shelvesId }
                    view.showView(sb)
                    view.hideLoading()
                } catch (e: Exception) {
                    view.showPrompt(e.message.toString())
                    view.errorDealWith()
                }
            }

            override fun listenerFailed(errorMessage: String) {
                view.showPrompt(errorMessage)
                view.errorDealWith()
            }
        }
        model.getShelves(listener)
    }

    fun getShelvesItem() {
        if (!PresenterUtil.judgmentInternet(view)) return
        val listener = object : MyListener {
            override fun listenerSuccess(data: Any) {
                try {
                    val sib = Gson().fromJson(data as String, ShelvesItemResult::class.java)
                    val result = ShelvesItemResult(sib.code, sib.disTime, ArrayList())
                    var bigId = ""
                    sib.rows.forEach {
                        //如果是新大类就要添加上新的标题栏
                        if (bigId != it.bigId) {
                            bigId = it.bigId
                            result.rows.add(ShelvesItemBean(it.bigId, it.big_name, "", "", 0, 0, true))
                        }
                        //直接改不是标题并添加
                        it.isTitle = false
                        result.rows.add(it)
                    }
                    view.showView(result)
                    view.hideLoading()
                } catch (e: Exception) {
                    view.showPrompt(e.message.toString())
                    view.errorDealWith()
                }
            }

            override fun listenerFailed(errorMessage: String) {
                view.showPrompt(errorMessage)
                view.errorDealWith()
            }
        }
        model.getShelvesItem(view.getData1() as String, listener)
    }
}