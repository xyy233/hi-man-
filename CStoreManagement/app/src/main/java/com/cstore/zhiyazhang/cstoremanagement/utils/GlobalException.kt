package com.cstore.zhiyazhang.cstoremanagement.utils

import android.app.Activity
import android.os.Environment
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Created by zhiya.zhang
 * on 2018/3/13 16:17.
 * 全局错误信息收集
 */
class GlobalException private constructor() : Thread.UncaughtExceptionHandler {
    private val mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    companion object {
        @JvmStatic
        val crashFileName = "CStoreManagementErrorMessage.txt"
        @get:Synchronized
        val instance = GlobalException()

    }

    override fun uncaughtException(t: Thread?, ex: Throwable?) {
        try {

            if (handleException(ex) && mDefaultHandler != null) {
                //系统默认的异常处理器处理
                mDefaultHandler.uncaughtException(t, ex)
                System.exit(1)
            } else {
                //退出程序
                System.exit(1)
            }
        } catch (e: Exception) {
            if (mDefaultHandler != null) {
                mDefaultHandler.uncaughtException(t, ex)
            } else {
                System.exit(1)
            }
        }
    }

    private fun handleException(ex: Throwable?): Boolean {
        if (ex == null) return false
        //写入文件
        saveErrorInfoFile(ex)
        return true
    }

    /**
     * 记录错误信息
     */
    private fun saveErrorInfoFile(ex: Throwable) {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val writer = StringWriter()
            val printWriter = PrintWriter(writer)
            ex.printStackTrace(printWriter)
            printWriter.close()
            val result = writer.toString()
            val outputStream = MyApplication.instance().applicationContext.openFileOutput(crashFileName, Activity.MODE_APPEND)
            outputStream.write(result.toByteArray())
            outputStream.flush()
            outputStream.close()
        }
    }
}