package indi.nightfish.potato_ip_display.parser.provider

import com.google.gson.Gson
import com.google.gson.JsonObject
import indi.nightfish.potato_ip_display.PotatoIpDisplay.Instance.plugin
import indi.nightfish.potato_ip_display.parser.IpParse
import indi.nightfish.potato_ip_display.util.IpAttributeMap
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class PconlineParser(private val ip: String) : IpParse {
    private val get = getPconlineData()
    private val unknown: String = plugin.conf.options.customUnknownString
    override fun getRegion(): String =
        get["region"]?.asString ?: unknown

    override fun getCountry(): String {
        return when {
            !get["pro"]?.asString.isNullOrBlank() -> "中国"
            get["proCode"]?.asString == "999999" -> "海外"
            else -> unknown
        }
    }

    override fun getProvince(): String =
        get["pro"]?.asString
            ?.replace("省", "") ?: unknown

    override fun getCity(): String =
        get["city"]?.asString
            ?.replace("市", "") ?: unknown

    override fun getISP(): String =
        get["addr"]?.asString ?: unknown

    override fun getFallback(): String {
        val values = arrayOf(getProvince(), getCountry(), getCity())
        for (value in values) {
            if (value.isNotBlank() && value != "") return value
        }
        return unknown
    }

    private fun getPconlineData(): JsonObject {
        IpAttributeMap.pconlineRawDataMap[ip]?.let { return it }

        val client = HttpClient.newHttpClient()
        val url = URI.create("https://whois.pconline.com.cn/ipJson.jsp?ip=$ip&json=true")
        val builder = HttpRequest.newBuilder(url)
            .GET()
            .header("Accept", "application/json")
        val response = try {
            client.send(builder.build(), HttpResponse.BodyHandlers.ofString())
        } catch (e: Exception) {
            return Gson().fromJson("{}", JsonObject::class.java)
        }

        val json = Gson().fromJson(response.body(), JsonObject::class.java)
        if (response.statusCode() == 200) {
            IpAttributeMap.pconlineRawDataMap[ip] = json
        }
        return json
    }
}