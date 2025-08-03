package indi.nightfish.potato_ip_display.command

import indi.nightfish.potato_ip_display.PotatoIpDisplay
import indi.nightfish.potato_ip_display.parser.IpParseFactory
import indi.nightfish.potato_ip_display.util.ConfigManager
import indi.nightfish.potato_ip_display.util.IpAttributeMap
import indi.nightfish.potato_ip_display.util.IpData
import indi.nightfish.potato_ip_display.util.UpdateUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.event.HandlerList

/**
 * The /potatoipdisplay command.
 */
class PotatoIpDisplayCommand : TabExecutor {
    private val plugin = PotatoIpDisplay.plugin

    override fun onCommand(
        sender: CommandSender,
        cmd: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (!sender.hasPermission("potatoipdisplay.command")) {
            sendNoPerms(sender)
            return true
        }

        when (args.getOrNull(0)?.lowercase()) {
            null -> showStatus(sender)
            "about" -> showAbout(sender)
            "reload" -> reloadPlugin(sender)
            "lookup" -> lookupCommand(sender, label, args)
            "clear" -> clearCommand(sender, label, args)
            else -> sender.sendMessage("§c未知子命令 (about; reload; lookup; clear)")
        }
        return true
    }

    private fun showStatus(sender: CommandSender) {
        val options = plugin.conf.options
        val modeDisplay = if (options.mode == "ip2region") "ip2region [${options.xdbBuffer}]" else options.mode
        sender.sendMessage("§7[§6PotatoIPDisplay§7] §fIPv4 工作模式: §b$modeDisplay")
        sender.sendMessage("§7[§6PotatoIPDisplay§7] §fIPv6 工作模式: §b${options.modeV6}")
        sender.sendMessage("§7[§6PotatoIPDisplay§7] §7尝试检查更新……")
        UpdateUtil.checkForUpdatesAsync { result ->
            sender.sendMessage("§7[§6PotatoIPDisplay§7] $result")
        }
    }

    private fun showAbout(sender: CommandSender) {
        val desc = plugin.description
        sender.sendMessage("§7[§6PotatoIPDisplay§7] §f版本 §b${desc.version} §fby §b${desc.authors.joinToString(", ")}")
        sender.sendMessage("§7[§6PotatoIPDisplay§7] §6开源项目地址: §bhttps://github.com/dmzz-yyhyy/PotatoIpDisplay")
        sender.sendMessage("§7[§6PotatoIPDisplay§7] §6文档: §bhttps://upt.curiousers.org/docs/PotatoIpDisplay/intro")
    }

    private fun reloadPlugin(sender: CommandSender) {
        if (!sender.hasPermission("potatoipdisplay.reload")) {
            sender.sendMessage("§c您没有执行重载的权限")
            return
        }
        plugin.logger.info("正在重载 v${plugin.description.version}...")
        kotlin.runCatching {
            plugin.conf = ConfigManager.load(plugin)
            plugin.initResources()
            HandlerList.unregisterAll(plugin)
            plugin.initPlugin()
        }.onSuccess {
            sender.sendMessage("§7[§6PotatoIPDisplay§7] §a重载成功！")
        }.onFailure { ex ->
            sender.sendMessage("§7[§6PotatoIPDisplay§7] §c重载失败，请查看控制台错误信息。")
            plugin.logger.severe("重载时出现异常:")
            ex.printStackTrace()
        }
    }

    private fun lookupCommand(sender: CommandSender, label: String, args: Array<String>) {
        if (!sender.hasPermission("potatoipdisplay.lookup")) {
            sendNoPerms(sender)
            return
        }
        val target = args.getOrNull(1) ?: run {
            sender.sendMessage("§e用法: /$label lookup [玩家名|IPv4]")
            return
        }
        val address = Bukkit.getPlayerExact(target)?.address?.address?.hostAddress
        val ip = address ?: target
        if (address == null && !IpParseFactory. regexValidated(ip)) {
            sender.sendMessage("§c玩家离线，或 IP 无效")
            return
        }
        sender.sendMessage("§7[§6PotatoIPDisplay§7] §f查询: §b$ip")
        plugin.pluginScope.launch(Dispatchers.IO) {
            val data = IpParseFactory.parse(ip)
            sender.sendMessage(formatLookup(ip, data))
        }
    }

    private fun clearCommand(sender: CommandSender, label: String, args: Array<String>) {
        val option = args.getOrNull(1)?.lowercase() ?: run {
            val itemCount = IpAttributeMap.playerIpAttributeMap.size
            val cacheCount = IpAttributeMap.ip2regionRawDataMap.size + IpAttributeMap.pconlineRawDataMap.size + IpAttributeMap.ipApiRawDataMap.size
            sender.sendMessage("§7[§6PotatoIPDisplay§7] §e用法: /$label clear <player|cache>")
            sender.sendMessage("§7[§6PotatoIPDisplay§7] §f缓存: 玩家 §b$itemCount§f 项, 查询 §b$cacheCount§f 项")
            return
        }
        val cleared = when (option) {
            "player" -> IpAttributeMap.playerIpAttributeMap.also { it.clear() }.size
            "cache" -> {
                val map = IpAttributeMap
                val total = map.ip2regionRawDataMap.size + map.pconlineRawDataMap.size + map.ipApiRawDataMap.size
                map.ip2regionRawDataMap.clear(); map.pconlineRawDataMap.clear(); map.ipApiRawDataMap.clear()
                total
            }
            else -> {
                sender.sendMessage("§c无效选项: $option")
                return
            }
        }
        sender.sendMessage("§7[§6PotatoIPDisplay§7] §f已清除 §b$cleared§f 项")
    }

    private fun formatLookup(ip: String, data: IpData): String {
        return """§7[§6PotatoIPDisplay§7] §f结果: §b$ip
国家: ${data.country}
省份: ${data.province}
城市: ${data.city}
区域: ${data.region}
ISP: ${data.isp}
回退: ${data.fallback}
    """.trimIndent()
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<String>
    ): List<String> {
        if (!sender.hasPermission("potatoipdisplay.command")) return emptyList()
        return when (args.size) {
            1 -> listOf("about", "reload", "lookup", "clear").filter { it.startsWith(args[0], true) }
            2 -> when (args[0].lowercase()) {
                "lookup" -> Bukkit.getOnlinePlayers().map { it.name }.plus("127.0.0.1").filter { it.startsWith(args[1], true) }
                "clear" -> listOf("player", "cache").filter { it.startsWith(args[1], true) }
                else -> emptyList()
            }
            else -> emptyList()
        }
    }

    private fun sendNoPerms(sender: CommandSender) {
        sender.sendMessage("§c您没有使用此命令的权限")
    }

}