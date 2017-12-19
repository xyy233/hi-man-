package com.cstore.zhiyazhang.cstoremanagement

import com.cstore.zhiyazhang.cstoremanagement.utils.wechat.WXPayConfigImpl
import com.github.wxpay.sdk.WXPay
import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {

    @Test
    fun testWX() {
        val config = WXPayConfigImpl()
        val wxpay = WXPay(config)
        val out_trade_no = "201613091059590000003433-asd002"
        val data = HashMap<String, String>()
        data.put("body", "腾讯充值中心-QQ会员充值")
        data.put("out_trade_no", out_trade_no)
        data.put("device_info", "")
        data.put("fee_type", "CNY")
        data.put("total_fee", "1")
        data.put("spbill_create_ip", "123.12.12.123")
        data.put("auth_code", "134547555692208421")
        try {
            val r = wxpay.microPay(data)
            System.out.print(r)
        } catch (e: Exception) {
            System.out.print(e.message)
        }
    }

    private val numberChar = "0123456789"

    @Test
    fun getMyString() : String {
        val sb = StringBuffer()
        val random = Random()
        for (i in 0..9) {
            sb.append(numberChar[random.nextInt(numberChar.length)])
        }
        return sb.toString()
    }
}