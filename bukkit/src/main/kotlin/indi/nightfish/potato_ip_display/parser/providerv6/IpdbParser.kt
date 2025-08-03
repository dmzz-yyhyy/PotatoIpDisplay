package indi.nightfish.potato_ip_display.parser.providerv6

import indi.nightfish.potato_ip_display.PotatoIpDisplay
import indi.nightfish.potato_ip_display.parser.IpParse
import indi.nightfish.potato_ip_display.util.IpAttributeMap
import net.ipip.ipdb.City
import net.ipip.ipdb.CityInfo
import net.ipip.ipdb.IPFormatException
import java.io.File


class IpdbParser(private val ip: String) : IpParse {
    private val plugin = PotatoIpDisplay.plugin
    private val dbPath: String = File(plugin.dataFolder, "qqwry.ipdb").absolutePath
    private val unknown: String = plugin.conf.options.customUnknownString
    private val cityDb: City by lazy {
        City(dbPath)
    }

    override fun getCountry(): String {
        val info = getIpdbData()
        return info?.countryName?.takeIf { it.isNotBlank() } ?: unknown
    }

    override fun getRegion(): String {
        val info = getIpdbData()
        return info?.line?.takeIf { it.isNotBlank() } ?: unknown
    }


    override fun getProvince(): String {
        val info = getIpdbData()
        return info?.regionName?.takeIf { it.isNotBlank() } ?: unknown
    }

    override fun getCity(): String {
        val info = getIpdbData()
        return info?.districtName?.takeIf { it.isNotBlank() }
            ?: info?.cityName?.takeIf { it.isNotBlank() }
            ?: unknown
    }

    override fun getISP(): String {
        val info = getIpdbData()
        return info?.ispDomain?.takeIf { it.isNotBlank() }
            ?: info?.ownerDomain?.takeIf { it.isNotBlank() }
            ?: unknown
    }

    override fun getFallback(): String {
        return listOf(getCity(), getProvince(), getCountry())
            .firstOrNull { it != unknown }
            ?: unknown
    }

    private fun getIpdbData(): CityInfo? {
        IpAttributeMap.ipdbRawDataMap[ip]?.let { return it }

        return try {
            val info = cityDb.findInfo(ip, "CN")
            IpAttributeMap.ipdbRawDataMap[ip] = info
            info
        } catch (e: IPFormatException) {
            plugin.logger.warning("IPdb 解析错误 for $ip: ${e.message}")
            null
        }
    }

}
