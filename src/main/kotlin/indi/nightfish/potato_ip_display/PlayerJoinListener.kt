package indi.nightfish.potato_ip_display

import com.google.gson.Gson
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.LoginEvent
import com.velocitypowered.api.event.player.ServerPostConnectEvent
import com.velocitypowered.api.proxy.ProxyServer
import com.wdbyte.httpclient.HttpClient5Get
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextColor
import org.slf4j.Logger


class PlayerJoinListener(private val logger: Logger){


    @Subscribe
    fun onPlayerLoin(event: LoginEvent) {
        val playerAddress = event.player.remoteAddress.hostName
        val player = event.player
        val playerName = player.username
        val resJson = HttpClient5Get.get("https://whois.pconline.com.cn/ipJson.jsp?ip=$playerAddress&json=true")
        val gson = Gson().fromJson(resJson, IpData::class.java)
        val pro = gson.pro.replace("省","")
        val addr = gson.addr
        IpATTRMap.playerIpATTRMap[playerName] = pro
        logger.info("Player named $playerName connect to proxy from $addr")
    }
    @Subscribe
    fun onPlayerLogin(event: ServerPostConnectEvent) {
        event.player.sendMessage(
                Component.text("[").color(TextColor.color(85, 85, 85)).append(
            Component.text("PotatoIpDisplay").color(TextColor.color(255, 170, 0))).append(
            Component.text("] ").color(TextColor.color(85, 85, 85))).append(
            Component.text("您当前ip归属地 ").color(TextColor.color(255, 255, 85))).append(
                Component.text("[").color(TextColor.color(170, 170, 170))).append(
                Component.text("${IpATTRMap.playerIpATTRMap[event.player.username]}").color(TextColor.color(85, 255, 255))).append(
            Component.text("]").color(TextColor.color(170, 170, 170))))
    }
}

