package indi.nightfish.potato_ip_display

import com.google.gson.Gson
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent


class PlayerJoinListener : Listener {

    @EventHandler
    fun onPlayerLoin(event: PlayerLoginEvent) {
        val playerAddress = event.realAddress.hostAddress
        val player = event.player
        val playerName = player.name
        val resJson = HttpClient5Get.get("https://whois.pconline.com.cn/ipJson.jsp?ip=$playerAddress&json=true")
        val gson = Gson().fromJson(resJson, IpData::class.java)
        val addr = gson.addr
        val pro = if (gson.pro.replace("省", "") == "") {
            Regex(pattern = """[\u4e00-\u9fa5]+""")
                .find(addr)?.value ?: ""
        } else {
            gson.pro.replace("省", "")
        }
        IpATTRMap.playerIpATTRMap[playerName] = pro
        Bukkit.getServer().logger.info("Player named $playerName connect to proxy from $addr")
    }

    @EventHandler
    fun onPlayerLogin(event: PlayerJoinEvent) {
        event.player.sendMessage("${ChatColor.DARK_GRAY}[${ChatColor.GOLD}PotatoIpDisplay${ChatColor.DARK_GRAY}] ${ChatColor.YELLOW}您当前ip归属地 ${ChatColor.GRAY}[${ChatColor.AQUA}${IpATTRMap.playerIpATTRMap[event.player.name]}${ChatColor.GRAY}]${ChatColor.RESET}")
    }
}

