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

    const val V4PREFIX = "potatoipdisplay.override.v4."
    const val V6PREFIX = "potatoipdisplay.override.v6."


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

    fun getPlayerIp(
        player: Player,
        fallback: String = player.address?.address?.hostAddress ?: "0.0.0.0"
    ): String {
        val name = player.name
        IpAttributeMap.playerIpAddressMap[name]?.let { return it }

        val permissions = player.effectivePermissions
            .filter { it.value }
            .map { it.permission }

        val overrideV4 = permissions.find { it.startsWith(V4PREFIX) }
        val overrideV6 = permissions.find { it.startsWith(V6PREFIX) }
        val overrideIp = parseOverrideIp(name, overrideV4, V4PREFIX, '.')
            ?: parseOverrideIp(name, overrideV6, V6PREFIX, ':')

        val ip = overrideIp ?: fallback
        IpAttributeMap.playerIpAddressMap[name] = ip
        return ip
    }

    private fun parseOverrideIp(
        playerName: String,
        permission: String?,
        prefix: String,
        separator: Char
    ): String? {
        if (permission == null) return null

        val ip = permission.removePrefix(prefix).replace('-', separator)
        if (isValidIp(ip)) {
            plugin.logger.info("$playerName has permission-specified IP $ip")
            return ip
        }

        plugin.logger.warning("$playerName has an unparseable permission node $permission")
        return null
    }

    fun isValidIp(ip: String): Boolean {
        if (':' !in ip) {
            if (ip.length !in 7..15) return false
            val parts = ip.split('.')
            return parts.size == 4 && parts.all { part ->
                part.isNotEmpty() &&
                    part.length <= 3 &&
                    part.all { it in '0'..'9' } &&
                    part.toInt() in 0..255
            }
        }

        if (ip.length !in 2..45) return false
        if (!ip.all {
                it in '0'..'9' ||
                    it in 'a'..'f' ||
                    it in 'A'..'F' ||
                    it == ':' ||
                    it == '.'
            }) return false

        return try {
            InetAddress.getByName(ip)
            true
        } catch (_: Exception) {
            false
        }
    }


}