package indi.nightfish.potato_ip_display.util

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

object ConfigManager {

    fun load(plugin: JavaPlugin): Config {
        val configFile = File(plugin.dataFolder, "config.yml")
        val latestVersion = 2

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false)
            return buildConfig(YamlConfiguration.loadConfiguration(configFile))
        }

        val oldCfg = YamlConfiguration.loadConfiguration(configFile)
        val oldVersion = oldCfg.getInt("config-version", 1)

        if (oldVersion < latestVersion) {
            plugin.logger.info("Updating config file from v$oldVersion -> v$latestVersion")
            plugin.saveResource("config.yml", true)
            val newCfg = YamlConfiguration.loadConfiguration(File(plugin.dataFolder, "config.yml"))

            oldCfg.getKeys(true).forEach { path ->
                if (path != "config-version") {
                    newCfg.set(path, oldCfg.get(path))
                }
            }

            migrateConfig(oldVersion, newCfg)
            newCfg.set("config-version", latestVersion)

            newCfg.save(configFile)
            plugin.logger.info("Config file updated to: v$latestVersion")
            plugin.logger.info("To view changes and updates, visit GitHub Releases:")
            plugin.logger.info("要查看变更和更新内容，请前往本插件 GitHub Releases:")
            plugin.logger.info(">> https://github.com/dmzz-yyhyy/PotatoIpDisplay/releases")

            return buildConfig(newCfg)
        }

        return buildConfig(oldCfg)
    }

    private fun migrateConfig(oldVersion: Int, newCfg: YamlConfiguration) {
        when (oldVersion) {
            1 -> migrateV1toV2(newCfg)
        }
    }

    private fun migrateV1toV2(cfg: YamlConfiguration) {
        listOf(
            "messages.player-chat.string",
            "messages.player-login.string"
        ).forEach { path ->
            cfg.getString(path)?.let { value ->
                if ("%ipAttr%" in value) {
                    cfg.set(path, value.replace("%ipAttr%", "%ipFallback%"))
                }
            }
        }
        if (!cfg.contains("options.custom-unknown-string")) {
            cfg.set("options.custom-unknown-string", "未知")
        }
        if (!cfg.contains("options.mode-ipv6")) {
            cfg.set("options.mode-ipv6", "ping0")
        }
    }

    private fun buildConfig(fc: FileConfiguration): Config {
        return Config(
            configVersion = fc.getInt("config-version", 1),
            pluginConfigVersion = 1,
            options = Config.Options(
                mode = fc.getString("options.mode") ?: "ip2region",
                xdbBuffer = fc.getString("options.xdb-buffer") ?: "vindex",
                allowbStats = fc.getBoolean("options.allow-bstats"),
                modeV6 = fc.getString("options.modev6") ?: "ipdb",
                customUnknownString = fc.getString("options.custom-unknown-string") ?: "未知"
            ),
            message = Config.Message(
                playerChat = Config.Message.PlayerChat(
                    enabled = fc.getBoolean("messages.player-chat.enabled"),
                    string = fc.getString("messages.player-chat.string")
                        ?: "§7[§b%ipFallback%§7] §f%playerName% §7>> §f%msg%"
                ),
                playerLogin = Config.Message.PlayerLogin(
                    enabled = fc.getBoolean("messages.player-login.enabled"),
                    string = fc.getString("messages.player-login.string")
                        ?: "§7[§6PotatoIpDisplay§7] §e您当前IP归属地 §7[§b%ipFallback%§7]"
                )
            ),
            papi = Config.PAPISupport(
                enabled = fc.getBoolean("papi.enabled"),
            )
        )
    }
}