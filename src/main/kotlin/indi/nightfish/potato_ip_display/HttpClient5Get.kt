package com.wdbyte.httpclient

import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.ParseException
import org.apache.hc.core5.http.io.entity.EntityUtils
import java.io.IOException

object HttpClient5Get {
    fun get(url: String?): String? {
        var resultContent: String? = null
        val httpGet = HttpGet(url)
        try {
            HttpClients.createDefault().use { httpclient ->
                httpclient.execute(httpGet).use { response ->
                    val entity = response.entity
                    // 获取响应信息
                    resultContent = EntityUtils.toString(entity)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return resultContent
    }
}
