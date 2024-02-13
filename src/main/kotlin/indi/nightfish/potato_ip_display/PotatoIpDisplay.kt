package indi.nightfish.potato_ip_display

import com.google.inject.Inject
import org.slf4j.Logger
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer


@Plugin(
    id = "potatoipdisplay",
    name = "PotatoIpDisplay",
    version = "1.0",
    authors = ["NightFish"]
)
class PotatoIpDisplay(

) {
    private var instance: PotatoIpDisplay? = null
    private var proxy: ProxyServer? = null
    private var logger: Logger? = null

    @Inject
    fun PotatoIpDisplay(server: ProxyServer, logger: Logger) {
        this.proxy = server
        this.logger = logger
        this.instance = this
    }


    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent?) {
        logger!!.info("PotatoIpDisplay loading")
        logger!!.info("Registering event -> Listener")
        proxy!!.eventManager.register(this, PlayerJoinListener(logger!!))
        proxy!!.eventManager.register(this, MessageListener(proxy!!))
        logger!!.info("PotatoIpDisplay ready")
    }

    @Subscribe
    fun onDisable(event: ProxyShutdownEvent) {
        logger!!.info("Disabling PotatoIpDisplay")
    }
}