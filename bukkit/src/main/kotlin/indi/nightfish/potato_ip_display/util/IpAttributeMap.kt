package indi.nightfish.potato_ip_display.util

import com.google.gson.JsonObject
import java.util.concurrent.ConcurrentHashMap

object IpAttributeMap {
    val ip2regionRawDataMap: MutableMap<String, String> = mutableMapOf()
    val pconlineRawDataMap: MutableMap<String, JsonObject> = mutableMapOf()
    val ipApiRawDataMap: MutableMap<String, JsonObject> = mutableMapOf()
    val playerIpAttributeMap: MutableMap<String, String> = mutableMapOf()
    val playerIpAddressMap: MutableMap<String, String> = mutableMapOf()
    val zxincRawDataMap: MutableMap<String, JsonObject> = ConcurrentHashMap()

}

data class IpData(
    val region: String,
    val country: String,
    val province: String,
    val city: String,
    val isp: String,
    val fallback: String
)

object IpCache {
    private val cache = ConcurrentHashMap<String, IpData>()

    fun get(ip: String, loader: () -> IpData): IpData {
        return cache.computeIfAbsent(ip) { loader() }
    }

    fun invalidate(ip: String) {
        cache.remove(ip)
    }
}