package indi.nightfish.potato_ip_display.listener

import indi.nightfish.potato_ip_display.PotatoIpDisplay
import indi.nightfish.potato_ip_display.parser.IpParseFactory
import indi.nightfish.potato_ip_display.util.IpAttributeMap
import indi.nightfish.potato_ip_display.util.pluginAsMainDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent


class PlayerJoinListener: Listener {
    val plugin = PotatoIpDisplay.plugin
    private val conf = plugin.conf

    @EventHandler
    fun onPlayerLogin(event: PlayerLoginEvent) {
        val player = event.player
        plugin.pluginScope.launch {
            val ip = IpParseFactory.getPlayerIp(player)
            val data = IpParseFactory.parse(ip)

            IpAttributeMap.playerIpAttributeMap[player.name] = data.fallback
            withContext(pluginAsMainDispatcher()) {
                plugin.log("Player named ${player.name} connected from ${data.province}${data.city} ${data.isp}")
            }
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val fallback = IpAttributeMap.playerIpAttributeMap[event.player.name] ?: conf.options.customUnknownString
        if (PotatoIpDisplay.plugin.conf.message.playerLogin.enabled) {
            event.player.sendMessage(
                PotatoIpDisplay.plugin.conf.message.playerLogin.string
                    .replace("%ipFallback%", fallback)
            )
        }
    }
}

