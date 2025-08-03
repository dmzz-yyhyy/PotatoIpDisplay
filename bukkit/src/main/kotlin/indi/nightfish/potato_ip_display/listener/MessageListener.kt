package indi.nightfish.potato_ip_display.listener

import indi.nightfish.potato_ip_display.PotatoIpDisplay
import indi.nightfish.potato_ip_display.parser.IpParseFactory
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent


class MessageListener : Listener {
    private val plugin = PotatoIpDisplay.plugin

    @EventHandler(priority = EventPriority.LOWEST)

    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        val ip = IpParseFactory.getPlayerIp(event.player)
        val data = IpParseFactory.parse(ip)
        var msg = plugin.conf.message.playerChat.string
        msg = msg.replace("%playerName%", $$"%1$s")
            .replace("%msg%", $$"%2$s")
            .replace("%ipRegion%", data.region)
            .replace("%ipCountry%", data.country)
            .replace("%ipProvince%", data.province)
            .replace("%ipCity%", data.city)
            .replace("%ipISP%", data.isp)
            .replace("%ipFallback%", data.fallback)
        event.format = msg
    }

}