package com.cstore.zhiyazhang.cstoremanagement.utils.printer

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.util.Base64
import com.cstore.zhiyazhang.cstoremanagement.bean.TransServiceBean
import com.cstore.zhiyazhang.cstoremanagement.bean.TrsBean
import com.cstore.zhiyazhang.cstoremanagement.bean.User
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import com.cstore.zhiyazhang.cstoremanagement.view.printer.PrinterActivity
import com.gprinter.aidl.GpService
import com.gprinter.command.EscCommand
import com.gprinter.command.GpCom
import com.gprinter.command.GpUtils
import com.gprinter.io.GpDevice

/**
 * Created by zhiya.zhang
 * on 2018/5/31 12:10.
 */
internal class PrinterServiceConnection(var mGpService: GpService?) : ServiceConnection {
    override fun onServiceDisconnected(name: ComponentName) {
        mGpService = null
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        mGpService = GpService.Stub.asInterface(service)
    }

    fun getConnectState(): Boolean {
        var result = false
        try {
            val status = mGpService!!.getPrinterConnectStatus(0)
            if (status == GpDevice.STATE_CONNECTED) {
                result = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    fun goActivity(context: Activity, isFinish: Boolean) {
        val i = Intent(context, PrinterActivity::class.java)
        val status = getConnectState()
        i.putExtra("status", status)
        i.putExtra("isFinish", isFinish)
        context.startActivityForResult(i, 0)
    }

    fun printZWTrs(data: TransServiceBean, title: String) {
        val esc = EscCommand()
        esc.addInitializePrinter()//初始化
        esc.addPrintAndFeedLines(3.toByte())//走纸3行
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER)//居中
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF)// 设置为倍高倍宽
        esc.addText("$title\n")
        esc.addPrintAndLineFeed()//打印并换行
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF)// 取消倍高倍宽
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT)// 设置打印左对齐
        esc.addText("单号：${data.requestNumber}")
        esc.addPrintAndLineFeed()//打印并换行
        esc.addText("调拨时间：${data.updTime}")
        esc.addPrintAndLineFeed()//打印并换行
        esc.addText("调出店：${data.storeName}(${data.storeId})")
        esc.addPrintAndLineFeed()//打印并换行
        esc.addText("调入店：${data.trsStoreName}(${data.trsStoreId})\n")
        esc.addPrintAndLineFeed()//打印并换行
        data.items.forEach {
            val str = "${it.itemNo} ${it.itemName}"
            esc.addText(str)
            esc.addSetHorAndVerMotionUnits(7.toByte(), 0.toByte())
            val i = " ${it.itemName}".length
            if (i in 14..16) {
                esc.addPrintAndLineFeed()
                esc.addText("  ")
                esc.addSetHorAndVerMotionUnits(7.toByte(), 0.toByte())
            }
            esc.addSetAbsolutePrintPosition(14.toShort())
            val trsQty = it.storeTrsQty ?: it.trsQty
            esc.addText(trsQty.toString())
            esc.addPrintAndLineFeed()//打印并换行
        }
        esc.addQueryPrinterStatus()// 加入查询打印机状态，打印完成后，此时会接收到GpCom.ACTION_DEVICE_STATUS广播
        esc.addPrintAndFeedLines(6.toByte())
        val datas = esc.command // 发送数据
        val bytes = GpUtils.ByteTo_byte(datas)
        val sss = Base64.encodeToString(bytes, Base64.DEFAULT)
        val rs: Int
        try {
            rs = mGpService!!.sendEscCommand(0, sss)
            val r = GpCom.ERROR_CODE.values()[rs]
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                MyToast.getLongToast(GpCom.getErrorText(r))
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun printTrs(data: TrsBean) {
        val esc = EscCommand()
        esc.addInitializePrinter()//初始化
        esc.addPrintAndFeedLines(3.toByte())//走纸3行
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER)//居中
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF)// 设置为倍高倍宽
        esc.addText("调拨单\n")
        esc.addPrintAndLineFeed()//打印并换行
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF)// 取消倍高倍宽
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT)// 设置打印左对齐
        esc.addText("单号：${data.trsNumber}")
        esc.addPrintAndLineFeed()//打印并换行
        esc.addText("调拨时间：${data.trsTime}")
        esc.addPrintAndLineFeed()//打印并换行
        esc.addText("调出店：${User.getUser().storeName}(${User.getUser().storeId})")
        esc.addPrintAndLineFeed()//打印并换行
        esc.addText("调入店：${data.oStoreName}(${data.trsStoreId})\n")
        esc.addPrintAndLineFeed()//打印并换行
        data.items.forEach {
            val str = "${it.pluId} ${it.pluName}"
            esc.addText(str)
            esc.addSetHorAndVerMotionUnits(7.toByte(), 0.toByte())
            esc.addSetAbsolutePrintPosition(14.toShort())
            val i = " ${it.pluName}".length
            if (i in 14..16) {
                esc.addPrintAndLineFeed()
                esc.addText("  ")
                esc.addSetHorAndVerMotionUnits(7.toByte(), 0.toByte())
            }
            val trsQty = it.trsQty
            esc.addText(trsQty.toString())
            esc.addPrintAndLineFeed()//打印并换行
        }
        esc.addQueryPrinterStatus()// 加入查询打印机状态，打印完成后，此时会接收到GpCom.ACTION_DEVICE_STATUS广播
        esc.addPrintAndFeedLines(6.toByte())
        val datas = esc.command // 发送数据
        val bytes = GpUtils.ByteTo_byte(datas)
        val sss = Base64.encodeToString(bytes, Base64.DEFAULT)
        val rs: Int
        try {
            rs = mGpService!!.sendEscCommand(0, sss)
            val r = GpCom.ERROR_CODE.values()[rs]
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                MyToast.getLongToast(GpCom.getErrorText(r))
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }
}