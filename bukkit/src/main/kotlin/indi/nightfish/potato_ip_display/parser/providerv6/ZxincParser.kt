package indi.nightfish.potato_ip_display.parser.providerv6

import com.google.gson.Gson
import com.google.gson.JsonObject
import indi.nightfish.potato_ip_display.PotatoIpDisplay.Instance.plugin
import indi.nightfish.potato_ip_display.parser.IpParse
import indi.nightfish.potato_ip_display.util.IpAttributeMap
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class ZxincParser(private val ip: String) : IpParse {
    private val get = getZxincData()
    private val unknown: String = plugin.conf.options.customUnknownString
    private val countryInfo = splitInfo(get["country"]?.asString)
    private val localInfo = splitInfo(get["local"]?.asString)

    override fun getCountry(): String =
        countryInfo.firstOrNull() ?: unknown

    override fun getProvince(): String =
        countryInfo.getOrNull(1)?.cleanRegionName() ?: unknown

    override fun getCity(): String =
        countryInfo.drop(2).lastOrNull()?.cleanRegionName() ?: unknown

    override fun getRegion(): String {
        val region = countryInfo.drop(1).joinToString(" ")
        return region.ifBlank { getCountry() }
    }

    override fun getISP(): String =
        localInfo.firstOrNull() ?: unknown

    override fun getFallback(): String {
        val values = arrayOf(getCity(), getProvince(), getCountry())
        for (value in values) {
            if (value.isNotBlank() && value != unknown) return value
        }
        return unknown
    }

    private fun getZxincData(): JsonObject {
        IpAttributeMap.zxincRawDataMap[ip]?.let { return it }

        val client = HttpClient.newHttpClient()
        val url = URI.create("https://ip.zxinc.org/api.php?type=json&ip=$ip")
        val request = HttpRequest.newBuilder(url)
            .GET()
            .header("Accept", "application/json")
            .header("User-Agent", "PotatoIpDisplay/${plugin.description.version}")
            .build()
        val response = try {
            client.send(request, HttpResponse.BodyHandlers.ofString())
        } catch (e: Exception) {
            return Gson().fromJson("{}", JsonObject::class.java)
        }

        val json = Gson().fromJson(response.body(), JsonObject::class.java)
        val data = json["data"]?.asJsonObject ?: JsonObject()
        if (response.statusCode() == 200 && json["code"]?.asInt == 0) {
            IpAttributeMap.zxincRawDataMap[ip] = data
        }
        return data
    }

    private fun splitInfo(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return value
            .replace('–', '\t')
            .replace('—', '\t')
            .split('\t')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    private fun String.cleanRegionName(): String =
        removeSuffix("特别行政区")
            .removeSuffix("自治区")
            .removeSuffix("省")
            .removeSuffix("市")
            .removeSuffix("区")
}
