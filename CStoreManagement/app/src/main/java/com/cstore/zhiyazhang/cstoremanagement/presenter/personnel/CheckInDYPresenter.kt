package com.cstore.zhiyazhang.cstoremanagement.presenter.personnel

import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.personnel.CheckInDYInterface
import com.cstore.zhiyazhang.cstoremanagement.model.personnel.CheckInDYModel
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView

/**
 * Created by zhiya.zhang
 * on 2018/3/29 11:41.
 */
class CheckInDYPresenter(private val view: GenericView) {
    private val model: CheckInDYInterface = CheckInDYModel()

    fun getPhotoByDate() {
        if (!PresenterUtil.judgmentInternet(view)) return
        val handler = MyHandler()
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
        model.getPhotoByDate(view.getData2() as String, handler)
    }
}