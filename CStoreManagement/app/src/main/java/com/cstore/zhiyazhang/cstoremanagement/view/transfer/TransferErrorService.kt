package com.cstore.zhiyazhang.cstoremanagement.view.transfer

import android.app.IntentService
import android.content.Intent
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.transfer.TransferServiceInterface
import com.cstore.zhiyazhang.cstoremanagement.model.transfer.TransferServiceModel
import com.cstore.zhiyazhang.cstoremanagement.sql.TranDao
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView

/**
 * Created by zhiya.zhang
 * on 2018/6/12 10:12.
 * 处理调拨异常专用服务
 */
class TransferErrorService(value: String = "TransferErrorService") : IntentService(value), GenericView {
    companion object {
        val TAG = "com.cstore.zhiyazhang.cstoremanagement.view.transfer.TransferErrorService"
    }

    private val db = TranDao.instance()
    private val model: TransferServiceInterface = TransferServiceModel()
    private val handler = MyHandler()
    private val listener = object : MyListener {
        override fun listenerSuccess(data: Any) {
            go()
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        handler.writeListener(listener)
        go()
    }

    private fun go() {
        //得到所有异常调拨单
        val data = db.getAberrantData()
        if (data.size > 0) {
            model.serviceDoneTrs(db, data, handler)
        }
    }

    override fun onDestroy() {
        handler.cleanAll()
        super.onDestroy()
    }
}
