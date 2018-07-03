package com.cstore.zhiyazhang.cstorepay.util

import fr.arnaudguyon.xmltojsonlib.XmlToJson

/**
 * Created by zhiya.zhang
 * on 2018/6/26 17:32.
 */
object XMLToJson {
    fun xmlToJson(data: String): String {
        return try {
            val result = XmlToJson.Builder(data.replace("\n", "")).build()
            result.toJson().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}