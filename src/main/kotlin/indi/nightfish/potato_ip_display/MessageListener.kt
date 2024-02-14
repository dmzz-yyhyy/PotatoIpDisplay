package indi.nightfish.potato_ip_display

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent


class MessageListener: Listener{
        @EventHandler(priority = EventPriority.LOWEST)
        fun onPlayerChat(event: AsyncPlayerChatEvent) {
                val player = event.player
                val playerName = player.name
                val msg = event.message
                Bukkit.getServer().broadcastMessage("${ChatColor.GRAY}[${ChatColor.AQUA}${IpATTRMap.playerIpATTRMap[playerName]}${ChatColor.GRAY}] ${ChatColor.RESET}$playerName ${ChatColor.GRAY}>> ${ChatColor.RESET}$msg")
                event.isCancelled = true
        }
}