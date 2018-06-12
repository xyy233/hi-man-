package com.cstore.zhiyazhang.cstoremanagement.utils

/**
 * Created by zhiya.zhang
 * on 2018/6/8 10:31.
 */
object QRcodeResolve {
    /**
     * 解析扫描到的二维码
     */
    fun qrCodeResolve(msg: String): List<String> {
        var data = msg
        data = msg.replace("http://wx2.rt-store.com/api/wechat/disc-show.jsp?code=", "CSTORE-BARCODE")
        data = data.replace("$", "|")
        val datas = data.split("|")
        val result = ArrayList<String>()
        val code = if (datas.size > 1) {
            datas[datas.size - 1]
        } else {
            msg
        }
        result.add(code)
        result.add(if (datas.size > 1) {
            try {
                datas[1] + " " + datas[2]
            } catch (e: Exception) {
                ""
            }
        } else {
            ""
        })
        return result
    }
}