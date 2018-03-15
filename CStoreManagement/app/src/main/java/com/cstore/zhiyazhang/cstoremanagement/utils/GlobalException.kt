package com.cstore.zhiyazhang.cstoremanagement.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by zhiya.zhang
 * on 2018/3/13 16:17.
 * 全局错误信息收集
 */
class GlobalException private constructor() : Thread.UncaughtExceptionHandler {
    private val mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    companion object {
        @JvmStatic
        public val crashFileName = "CStoreManagementErrorMessage.txt"
        @get:Synchronized
        val instance = GlobalException()

        /**
         * 获取崩溃文件
         */
        fun getCrashFile(): File {
            val cashFileName = MyApplication.instance().getSharedPreferences("crash", Context.MODE_PRIVATE).getString("CRASH_FILE_NAME", "")
            return File(cashFileName)
        }

    }

    override fun uncaughtException(t: Thread?, ex: Throwable?) {
        try {
            MyToast.getLongToast("很抱歉,程序出现异常,即将退出。")
            if (handleException(ex) && mDefaultHandler != null) {
                //如果用户没有处理则让系统默认的异常处理器来处理
                mDefaultHandler.uncaughtException(t, ex)
            } else {
                try {
                    Thread.sleep(3000)
                } catch (e: InterruptedException) {
                    Log.e("GlobalException", "error : ", e)
                }

                //退出程序
                android.os.Process.killProcess(android.os.Process.myPid())
                System.exit(1)
            }
        } catch (e: Exception) {
            if (mDefaultHandler != null) {
                mDefaultHandler.uncaughtException(t, ex)
            } else {
                android.os.Process.killProcess(android.os.Process.myPid())
            }
        }
    }

    private fun handleException(ex: Throwable?): Boolean {
        if (ex == null) return false

        //写入文件
        saveErrorInfoFile(ex)
/*        //写入文件
        val crashFileName = saveCrashInfoFile(ex)
        // 3. 缓存崩溃日志文件
        cacheCrashFile(crashFileName)*/
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

    /**
     * 记录错误信息
     */
    @SuppressLint("SimpleDateFormat")
    private fun saveCrashInfoFile(ex: Throwable): String {
        var fileName = ""
        val writer = StringWriter()
        val printWriter = PrintWriter(writer)
        ex.printStackTrace(printWriter)
        printWriter.close()
        val result = writer.toString()
        try {
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                val dir = File(MyApplication.instance().applicationContext.filesDir.toString() + File.separator + "crash"
                        + File.separator)
                //存在就删除
                if (dir.exists()) {
                    dir.deleteRecursively()
                }
                //重新创建
                if (!dir.exists()) {
                    dir.mkdir()
                }
                val formatter = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
                val time = formatter.format(Date())
                fileName = "$dir${File.separator}$time.txt"
                val fos = FileOutputStream(fileName)
                fos.write(result.toByteArray())
                fos.flush()
                fos.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return fileName
    }

    /**
     * 缓存崩溃日志文件
     *
     * @param fileName
     */
    private fun cacheCrashFile(fileName: String?) {
        val sp = MyApplication.instance().applicationContext.getSharedPreferences("crash", Context.MODE_PRIVATE)
        sp.edit().putString("CRASH_FILE_NAME", fileName).apply()
    }
}