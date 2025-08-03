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

class IpApiParser(private val ip: String) : IpParse {
    private val get = getIpApiData()
    private val unknown: String = plugin.conf.options.customUnknownString
    private val isReservedRange: Boolean =
        (get["status"]?.asString == "fail") && (get["message"]?.asString == "reserved range")

    override fun getRegion(): String =
        get["regionName"]?.asString ?: unknown

    override fun getCountry(): String =
        get["country"]?.asString
            ?.replace("省", "") ?: unknown

    override fun getProvince(): String =
        get["regionName"]?.asString
            ?.replace("省", "")
            ?.replace("市", "") ?: unknown

    override fun getCity(): String =
        get["city"]?.asString
            ?.replace("市", "") ?: unknown

    override fun getISP(): String {
        if (isReservedRange) return "保留地址"
        return get["isp"]?.asString ?: unknown
    }

    override fun getFallback(): String {
        if (isReservedRange) return "保留地址"
        val values = arrayOf(getProvince(), getCountry(), getCity())
        for (value in values) {
            if (value.isNotBlank() && value != "") return value
        }
        return unknown
    }

    private fun getIpApiData(): JsonObject {
        IpAttributeMap.ipApiRawDataMap[ip]?.let { return it }

        val client = HttpClient.newHttpClient()
        val url = URI.create("http://ip-api.com/json/$ip?lang=zh-CN")
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
            IpAttributeMap.ipApiRawDataMap[ip] = json
        }
        return json
    }
}