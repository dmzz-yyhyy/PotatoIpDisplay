package indi.nightfish.potato_ip_display

import com.google.inject.Inject
import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.PlayerChatEvent
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import java.util.function.Consumer


class MessageListener(private var proxy: ProxyServer){



        @Subscribe(order = PostOrder.LAST)
        fun messageEvent(event: PlayerChatEvent) {
                val player = event.player
                val playerName = player.username
                val msg = event.message
                proxy.allPlayers.forEach(Consumer { p: Player ->
                        p.sendMessage(
                                Component.text("[").color(TextColor.color(170, 170, 170)).append(
                                Component.text("${IpATTRMap.playerIpATTRMap[event.player.username]}").color(TextColor.color(85, 255, 255))).append(
                                Component.text("]").color(TextColor.color(170, 170, 170))).append(
                                        Component.text(" $playerName >> $msg").color(TextColor.color(255, 255, 255))
                                )
                        )
                })
                event.result = PlayerChatEvent.ChatResult.denied()
        }
}