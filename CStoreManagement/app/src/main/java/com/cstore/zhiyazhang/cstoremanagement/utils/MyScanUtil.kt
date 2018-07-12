package com.cstore.zhiyazhang.cstoremanagement.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.cstore.zhiyazhang.cstoremanagement.bean.UtilBean
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener

/**
 * Created by zhiya.zhang
 * on 2018/5/23 9:17.
 */
object MyScanUtil {

    /**
     * 开启扫描注册view与监听
     */
    fun openScan(view: EditText, listener: MyListener) {
        val isGun = MyApplication.usbGunJudgment()
        if (isGun) {
            view.isEnabled = true
            view.visibility = View.VISIBLE
            view.isFocusable = true
            view.isFocusableInTouchMode = true
            view.requestFocus()
            view.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    val msg = view.text.toString().replace(" ", "")
                    if (msg == "") false
                    else {
                        val imm = MyApplication.instance().applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                        val datas = msg.split("|")
                        val data = if (datas.size > 1) {
                            datas[datas.size - 1]
                        } else {
                            msg
                        }
                        val barDate = if (datas.size > 1) {
                            try {
                                datas[1] + " " + datas[2]
                            } catch (e: Exception) {
                                ""
                            }
                        } else {
                            ""
                        }
                        val result = UtilBean(data, barDate, "")
                        listener.listenerSuccess(result)
                        getFocusable(view)
                        true
                    }
                } else {
                    false
                }
            }
        }

    }

    /**
     * 获得焦点
     */
    fun getFocusable(v: EditText) {
        val isGun = MyApplication.usbGunJudgment()
        if (isGun){
            val x = ArrayList<String>()
            x.isNotEmpty()
            v.isFocusable = true
            v.isFocusableInTouchMode = true
            v.requestFocus()
        }
    }
}