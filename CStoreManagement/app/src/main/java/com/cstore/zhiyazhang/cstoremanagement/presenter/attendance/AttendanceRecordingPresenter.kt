package com.cstore.zhiyazhang.cstoremanagement.presenter.attendance

import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.attendance.AttendanceRecordingInterface
import com.cstore.zhiyazhang.cstoremanagement.model.attendance.AttendanceRecordingModel
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView

/**
 * Created by zhiya.zhang
 * on 2018/3/12 17:30.
 */
class AttendanceRecordingPresenter(private val view: GenericView) {
    private val model: AttendanceRecordingInterface = AttendanceRecordingModel()

    /**
     * 得到工时
     * get1=myActivity
     * get2=要得到的年月 yyyyMM格式
     */
    fun getWorkHours() {
        if (!PresenterUtil.judgmentInternet(view)) return
        val handler = MyHandler().writeActivity(view.getData1() as MyActivity)
        handler.writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                view.requestSuccess(data)
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
        model.getWorkHours(view.getData2().toString(), handler)
    }

    /**
     * 得到考勤数据
     * get1=myActivity
     * get3=beginDate
     * get4=ednDate
     * get5=workHours
     */
    fun getRecordingData() {
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
        model.getRecordingData(view.getData3().toString(), view.getData4().toString(), view.getData5().toString().toDouble(), handler)
    }
}