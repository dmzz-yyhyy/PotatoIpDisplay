package indi.nightfish.potato_ip_display.parser

import indi.nightfish.potato_ip_display.PotatoIpDisplay.Instance.plugin
import indi.nightfish.potato_ip_display.parser.provider.Ip2regionParser
import indi.nightfish.potato_ip_display.parser.provider.IpApiParser
import indi.nightfish.potato_ip_display.parser.provider.PconlineParser
import indi.nightfish.potato_ip_display.parser.providerv6.ZxincParser
import indi.nightfish.potato_ip_display.util.IpAttributeMap
import indi.nightfish.potato_ip_display.util.IpCache
import indi.nightfish.potato_ip_display.util.IpData
import org.bukkit.entity.Player
import java.net.InetAddress


object IpParseFactory {

    fun parse(ip: String): IpData {
        if (ip.contains(":")) {
            return when (plugin.conf.options.modeV6) {
                "disabled" -> {
                    plugin.logger.severe("Resolution for IPv6 is disabled in config, returning unknown.")
                    unknownData()
                }
                "zxinc" -> ZxincParser(ip).toIpData()
                else -> throw IllegalArgumentException("Invalid IPv6 mode: ${plugin.conf.options.modeV6}")
            }
        }

        return IpCache.get(ip) {
            when (plugin.conf.options.mode) {
                "pconline" -> PconlineParser(ip).toIpData()
                "ip2region" -> Ip2regionParser(ip).toIpData()
                "ip-api" -> IpApiParser(ip).toIpData()
                else -> throw IllegalArgumentException("Invalid mode set in config.yml: ${plugin.conf.options.mode}")
            }
        }
    }

    private fun unknownData(): IpData {
        val unknown = plugin.conf.options.customUnknownString
        return IpData(unknown, unknown, unknown, unknown, unknown, unknown)
    }

    fun getPlayerIp(player: Player, fallback: String = "0.0.0.0"): String {
        val name = player.name
        val existing = IpAttributeMap.playerIpAddressMap[name]
        if (existing != null) return existing
        val original = player.address?.address?.hostAddress ?: fallback
        val prefix = "potatoipdisplay.override."
        val override = player.effectivePermissions.find { it.permission.startsWith(prefix) }
        return override?.let {
            val ipStr = it.permission.removePrefix(prefix).replace('-', '.')
            if (regexValidated(ipStr)) {
                IpAttributeMap.playerIpAddressMap[name] = ipStr
                ipStr
            } else original
        } ?: original
    }

    fun regexValidated(ip: String): Boolean {
        val basicPattern = Regex("""^[0-9a-fA-F:.]+$""")
        if (!basicPattern.matches(ip)) return false

        return try {
            val address = InetAddress.getByName(ip)
            !address.hostName.contains(".") || address.hostName == ip
        } catch (e: Exception) {
            false
        }
    }


}