package indi.nightfish.potato_ip_display.integration

import indi.nightfish.potato_ip_display.PotatoIpDisplay
import indi.nightfish.potato_ip_display.parser.IpParseFactory
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player


class PlaceholderIntegration : PlaceholderExpansion() {
    private val plugin = PotatoIpDisplay.plugin

    override fun getAuthor(): String {
        return "[NightFish, yukonisen]"
    }

    override fun getIdentifier(): String {
        return "potatoipdisplay"
    }

    override fun getVersion(): String {
        return plugin.description.version
    }

    override fun onPlaceholderRequest(
        player: Player?,
        params: String
    ): String? {
        if (player == null) { return null}
        val ip: String = IpParseFactory.getPlayerIp(player)
        val ipParse = IpParseFactory.parse(ip)
        return when (params) {
            "ip" -> ip
            "country" -> ipParse.country
            "province" -> ipParse.province
            "city" -> ipParse.country
            "region" -> ipParse.region
            "isp" -> ipParse.isp
            "fallback" -> ipParse.fallback
            else -> null
        }
    }

}