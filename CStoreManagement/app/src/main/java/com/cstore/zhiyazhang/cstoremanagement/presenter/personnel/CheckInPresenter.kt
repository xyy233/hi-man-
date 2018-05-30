package com.cstore.zhiyazhang.cstoremanagement.presenter.personnel

import android.graphics.Bitmap
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.personnel.CheckInInterface
import com.cstore.zhiyazhang.cstoremanagement.model.personnel.CheckInModel
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView

/**
 * Created by zhiya.zhang
 * on 2017/10/12 14:11.
 */
class CheckInPresenter(private val gView: GenericView, private val activity: MyActivity) {
    private val model: CheckInInterface = CheckInModel()

    fun checkInUser(uid: String, bmp: Bitmap) {
        if (!PresenterUtil.judgmentInternet(gView)) return
        val handler = MyHandler()
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                gView.requestSuccess(data)
                gView.hideLoading()
                handler.cleanAll()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
                handler.cleanAll()
            }
        })
        model.checkInUser(gView.getData1() as Int, uid, bmp, handler)
    }
}