package indi.nightfish.potato_ip_display

import org.bukkit.plugin.java.JavaPlugin


@Suppress("unused")
class PotatoIpDisplay : JavaPlugin() {
    override fun onLoad() {
        super.onLoad()
        logger.info("PotatoIpDisplay loading")
    }

    override fun onEnable() {
        super.onEnable()
        logger.info("Registering event -> Listener")
        server.pluginManager.registerEvents(PlayerJoinListener(), this)
        server.pluginManager.registerEvents(MessageListener(), this)
    }

    override fun onDisable() {
        super.onDisable()
        logger.info("Disabling PotatoIpDisplay")
    }

}