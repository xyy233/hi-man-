package com.cstore.zhiyazhang.cstoremanagement.view.printer

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.RemoteException
import android.support.v7.app.AlertDialog
import android.view.ContextThemeWrapper
import android.view.View
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import com.cstore.zhiyazhang.cstoremanagement.utils.printer.BluetoothDeviceList
import com.cstore.zhiyazhang.cstoremanagement.utils.printer.PrinterServiceConnection
import com.gprinter.command.GpCom
import com.gprinter.io.GpDevice
import com.gprinter.io.PortParameters
import com.gprinter.save.PortParamDataBase
import com.gprinter.service.GpPrintService
import kotlinx.android.synthetic.main.activity_printer.*

/**
 * Created by zhiya.zhang
 * on 2018/5/30 10:55.
 */
class PrinterActivity : Activity() {
    private var conn: PrinterServiceConnection? = null

    companion object {
        val CONNECT_STATUS = "connect.status"
        val REQUEST_ENABLE_BT = 2
        val REQUEST_CONNECT_DEVICE = 3
        val REQUEST_USB_DEVICE = 4
        @JvmField
        val EXTRA_DEVICE_ADDRESS = "device_address"
    }

    private lateinit var mPortParam: PortParameters
    private val database = PortParamDataBase(this)
    private var isFinish = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(5)
        setContentView(R.layout.activity_printer)
        isFinish = intent.getBooleanExtra("isFinish", false)
        connection()
        initView()
        initClick()
    }

    private fun initView() {
        initPortParam()
        startMyService()
        registerBroadcast()
    }

    private fun registerBroadcast() {
        val filter = IntentFilter()
        filter.addAction(GpCom.ACTION_CONNECT_STATUS)
        this.registerReceiver(printerStatusBroadcastReceiver, filter)
    }

    private fun initPortParam() {
        mPortParam = database.queryPortParamDataBase("0")
        mPortParam.portOpenState = intent.getBooleanExtra("status", false)
        btConnect.text = if (mPortParam.portOpenState) {
            getString(R.string.cut)
        } else {
            getString(R.string.connect)
        }
        val info = "接口：蓝牙   地址：${
        if (mPortParam.portType == PortParameters.BLUETOOTH && mPortParam.bluetoothAddr != "") {
            mPortParam.bluetoothAddr
        } else {
            "未匹配"
        }
        }"
        tvInfo.text = info
    }

    private fun connection() {
        conn = PrinterServiceConnection(null)
        val i = Intent(this, GpPrintService::class.java)
        bindService(i, conn, Context.BIND_AUTO_CREATE)
    }

    private fun startMyService() {
        val i = Intent(this@PrinterActivity, GpPrintService::class.java)
        startService(i)
    }

    private fun initClick() {
        btConnect.setOnClickListener {
            //短按链接
            mPortParam.portType = PortParameters.BLUETOOTH
            var rel = 0
            if (getBluetoothDevice()) {
                if (!mPortParam.portOpenState) {
                    if (checkPortParamters()) {
                        try {
                            conn!!.mGpService!!.closePort(0)
                        } catch (e: RemoteException) {
                            e.printStackTrace()
                        }
                        if (mPortParam.portType == PortParameters.BLUETOOTH) {
                            try {
                                rel = conn!!.mGpService!!.openPort(0, mPortParam.portType, mPortParam.bluetoothAddr, 0)
                            } catch (e: RemoteException) {
                                e.printStackTrace()
                            }
                        }
                        val r = GpCom.ERROR_CODE.values()[rel]
                        if (r != GpCom.ERROR_CODE.SUCCESS) {
                            if (r == GpCom.ERROR_CODE.DEVICE_ALREADY_OPEN) {
                                mPortParam.portOpenState = conn!!.getConnectState()
                                btConnect.text = getString(R.string.cut)
                            } else {
                                showPrompt(GpCom.getErrorText(r))
                            }
                        }
                    } else {
                        showPrompt(getString(R.string.port_parameters_wrong))
                    }
                } else {
                    showLoading()
                    btConnect.text = getString(R.string.cutting)
                    try {
                        conn!!.mGpService!!.closePort(0)
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        btConnect.setOnLongClickListener {
            //长按删除清空
            AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                    .setTitle("提示")
                    .setMessage("请确认清除已配对打印机？")
                    .setPositiveButton("清除") { _, _ ->
                        //这里清除打印机记录
                        database.deleteDataBase("0")
                        mPortParam.bluetoothAddr = ""
                        setPortParamToView()
                    }
                    .setNegativeButton("取消", null)
                    .show()
            true
        }
    }

    private fun getBluetoothDevice(): Boolean {
        // Get local Bluetooth adapter
        val bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter()
        // If the adapter is null, then Bluetooth is not supported
        if (bluetoothAdapter == null) {
            showPrompt("Bluetooth is not supported by the device")
            return false
        } else {
            // If BT is not on, request that it be enabled.
            // setupChat() will then be called during onActivityResult
            return if (!bluetoothAdapter.isEnabled) {
                val enableIntent = Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableIntent,
                        REQUEST_ENABLE_BT)
                false
            } else {
                //已有蓝牙权限
                //已适配有蓝牙地址
                if (mPortParam.bluetoothAddr != "") {
                    true
                } else {
                    //未适配，无蓝牙地址
                    val intent = Intent(this@PrinterActivity,
                            BluetoothDeviceList::class.java)
                    startActivityForResult(intent,
                            REQUEST_CONNECT_DEVICE)
                    false
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                var bundle: Bundle? = Bundle()
                bundle = data!!.extras
                var param = bundle!!.getInt(GpPrintService.PORT_TYPE)
                mPortParam.portType = param
                var str = bundle.getString(GpPrintService.IP_ADDR)
                mPortParam.ipAddr = str
                param = bundle.getInt(GpPrintService.PORT_NUMBER)
                mPortParam.portNumber = param
                str = bundle.getString(GpPrintService.BLUETOOT_ADDR)
                mPortParam.bluetoothAddr = str
                str = bundle.getString(GpPrintService.USB_DEVICE_NAME)
                mPortParam.usbDeviceName = str
                setPortParamToView()
                if (checkPortParamters()) {
                    database.deleteDataBase("0")
                    database.insertPortParam(0, mPortParam)
                } else {
                    showPrompt(getString(R.string.port_parameters_wrong))
                }
            } else {
                showPrompt(getString(R.string.port_parameters_is_not_save))
            }
        } else if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                // Get the device MAC address
                val address = data!!.extras!!.getString(
                        EXTRA_DEVICE_ADDRESS)
                // fill in some parameters
                mPortParam.bluetoothAddr = address
                setPortParamToView()
                mPortParam.ipAddr = "192.168.123.100"
                mPortParam.portNumber = 9100
                database.deleteDataBase("0")
                database.insertPortParam(0, mPortParam)
            }
        }
    }

    private fun checkPortParamters(): Boolean {
        val type = mPortParam.portType
        if (type == PortParameters.BLUETOOTH) {
            if (mPortParam.bluetoothAddr != "") {
                return true
            }
        }
        return false
    }

    private fun setPortParamToView() {
        val info = "接口：蓝牙   地址：${
        if (mPortParam.portType == PortParameters.BLUETOOTH && mPortParam.bluetoothAddr != "") {
            mPortParam.bluetoothAddr
        } else {
            "未匹配"
        }
        }"
        tvInfo.text = info
    }


    private fun hideLoading() {
        bluetooth_pro.visibility = View.GONE
    }

    private fun showLoading() {
        bluetooth_pro.visibility = View.VISIBLE
    }

    private val printerStatusBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (GpCom.ACTION_CONNECT_STATUS == intent.action) {
                val type = intent.getIntExtra(GpPrintService.CONNECT_STATUS, 0)
                val id = intent.getIntExtra(GpPrintService.PRINTER_ID, 0)
                when (type) {
                    GpDevice.STATE_CONNECTING -> {
                        showLoading()
                        btConnect.isEnabled = false
                        mPortParam.portOpenState = false
                        btConnect.text = getString(R.string.connecting)
                    }
                    GpDevice.STATE_NONE -> {
                        hideLoading()
                        btConnect.isEnabled = true
                        mPortParam.portOpenState = false
                        btConnect.text = getString(R.string.connect)
                    }
                    GpDevice.STATE_VALID_PRINTER -> {
                        hideLoading()
                        btConnect.isEnabled = true
                        mPortParam.portOpenState = conn!!.getConnectState()
                        btConnect.text = getString(R.string.cut)
                        if (isFinish) {
                            setResult(28, Intent())
                            finish()
                        }
                    }
                    GpDevice.STATE_INVALID_PRINTER -> {
                        hideLoading()
                        btConnect.isEnabled = true
                        showPrompt("请使用公司派发打印机！")
                    }
                }
            }
        }
    }

    private fun showPrompt(msg: String) {
        MyToast.getLongToast(msg)
    }

    override fun onDestroy() {
        database.close()
        unregisterReceiver(printerStatusBroadcastReceiver)
        if (conn != null) {
            unbindService(conn)
        }
        super.onDestroy()
    }
}