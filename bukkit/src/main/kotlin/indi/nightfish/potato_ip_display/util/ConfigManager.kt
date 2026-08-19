package indi.nightfish.potato_ip_display.util

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

object ConfigManager {
    private const val LATEST_VERSION = 3

    fun load(plugin: JavaPlugin): Config {
        val configFile = File(plugin.dataFolder, "config.yml")

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false)
            return buildConfig(loadYaml(configFile))
        }

        val oldCfg = loadYaml(configFile)
        val oldVersion = oldCfg.getInt("config-version", 1)
        if (oldVersion >= LATEST_VERSION) return buildConfig(oldCfg)

        plugin.logger.info("Updating config file from v$oldVersion -> v$LATEST_VERSION")
        val backupFile = File(plugin.dataFolder, "config.yml.v$oldVersion.old")
        configFile.copyTo(backupFile, overwrite = true)
        plugin.saveResource("config.yml", true)

        val newCfg = loadYaml(configFile)
        oldCfg.getKeys(true)
            .filter { path ->
                path != "config-version" &&
                    !oldCfg.isConfigurationSection(path) &&
                    newCfg.contains(path)
            }
            .forEach { path -> newCfg.set(path, oldCfg.get(path)) }

        migrateConfig(oldVersion, oldCfg, newCfg)
        newCfg.set("config-version", LATEST_VERSION)
        newCfg.save(configFile)

        plugin.logger.info("Old config backed up to: ${backupFile.name}")
        plugin.logger.info("Config file updated to: v$LATEST_VERSION")
        plugin.logger.info("To view changes and updates, visit GitHub Releases:")
        plugin.logger.info("要查看变更和更新内容，请前往本插件 GitHub Releases:")
        plugin.logger.info(">> https://github.com/dmzz-yyhyy/PotatoIpDisplay/releases")
        return buildConfig(newCfg)
    }

    private fun loadYaml(file: File): YamlConfiguration =
        YamlConfiguration().apply {
            options().parseComments(true)
            load(file)
        }

    private fun migrateConfig(
        oldVersion: Int,
        oldCfg: YamlConfiguration,
        newCfg: YamlConfiguration
    ) {
        if (oldVersion < 2) migrateV1toV2(newCfg)
        if (oldVersion < 3) migrateV2toV3(oldCfg, newCfg)
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
    }

    private fun migrateV2toV3(oldCfg: YamlConfiguration, newCfg: YamlConfiguration) {
        val oldMode = oldCfg.getString("options.mode-ipv6")
            ?: oldCfg.getString("options.modev6")

        newCfg.set(
            "options.mode-ipv6",
            when (oldMode?.lowercase()) {
                "zxinc" -> "zxinc"
                "ping0", "ipdb" -> "zxinc"
                else -> "disabled"
            }
        )
    }

    private fun buildConfig(fc: FileConfiguration): Config {
        return Config(
            configVersion = fc.getInt("config-version", 1),
            pluginConfigVersion = 1,
            options = Config.Options(
                mode = fc.getString("options.mode")?.lowercase() ?: "ip2region",
                xdbBuffer = fc.getString("options.xdb-buffer") ?: "vindex",
                allowbStats = fc.getBoolean("options.allow-bstats"),
                modeV6 = fc.getString("options.mode-ipv6")?.lowercase() ?: "disabled",
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
