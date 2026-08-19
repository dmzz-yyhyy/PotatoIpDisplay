package indi.nightfish.potato_ip_display

import indi.nightfish.potato_ip_display.command.PotatoIpDisplayCommand
import indi.nightfish.potato_ip_display.integration.PlaceholderIntegration
import indi.nightfish.potato_ip_display.listener.MessageListener
import indi.nightfish.potato_ip_display.listener.PlayerJoinListener
import indi.nightfish.potato_ip_display.parser.IpParseFactory
import indi.nightfish.potato_ip_display.util.Config
import indi.nightfish.potato_ip_display.util.ConfigManager
import indi.nightfish.potato_ip_display.util.IpAttributeMap
import indi.nightfish.potato_ip_display.util.IpCache
import indi.nightfish.potato_ip_display.util.IpData
import indi.nightfish.potato_ip_display.util.UpdateUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.logging.Level

class PotatoIpDisplay : JavaPlugin() {

    companion object Instance {
        lateinit var instance: PotatoIpDisplay
        val plugin by lazy { instance }
    }
    val pluginScope = CoroutineScope(Dispatchers.Default)

    lateinit var conf: Config

    override fun onLoad() {
        super.onLoad()
        reloadConfig()
    }

    override fun onEnable() {
        super.onEnable()
        instance = this
        initPlugin()
        initResources()
        testParsers()

        if (conf.options.allowbStats) Metrics(this, 21473)
        log("PotatoIpDisplay has been enabled. [mode: ${conf.options.mode}]")
    }

    override fun onDisable() {
        super.onDisable()
        pluginScope.cancel()
        log("Disabled")
    }

    fun initResources() {
        val configFile = File(dataFolder, "config.yml")
        val dbFile = File(dataFolder, "ip2region.xdb")
        val authors = this.description.authors

        if ("yukonisen" !in authors || "NightFish" !in authors) this.isEnabled = false
        if (!configFile.exists()) this.saveDefaultConfig()

        if (conf.options.mode == "ip2region" && !dbFile.exists()) {
            saveResource("ip2region.xdb", false)
            log("ip2region.xdb saved to plugin directory.")
        }

    }

    fun testParsers() {
        pluginScope.launch(Dispatchers.IO) {
            testParser("IPv4", conf.options.mode, "223.5.5.5")

            if (conf.options.modeV6 != "disabled") {
                testParser("IPv6", conf.options.modeV6, "2408:8000:c000::8888")
            }
        }
    }

    private fun testParser(
        name: String,
        mode: String,
        ip: String,
    ) {
        logger.info("Now testing $mode with $name [$ip]")
        IpCache.invalidate(ip)
        IpAttributeMap.ip2regionRawDataMap.remove(ip)
        IpAttributeMap.pconlineRawDataMap.remove(ip)
        IpAttributeMap.ipApiRawDataMap.remove(ip)
        IpAttributeMap.zxincRawDataMap.remove(ip)

        runCatching {
            val result = IpParseFactory.parse(ip)
            check(!result.isUnknown()) { "$name parser returned no usable data for $ip" }
            result
        }.onSuccess { r ->
            logger.info(
                "$name test success: country=${r.country}, province=${r.province}, city=${r.city}, " +
                    "region=${r.region}, ISP=${r.isp}, fallback=${r.fallback}"
            )
        }.onFailure { exception ->
            logger.log(
                Level.SEVERE,
                "$name test failed: mode=$mode, ip=$ip",
                exception
            )
        }
    }

    private fun IpData.isUnknown(): Boolean {
        val unknown = conf.options.customUnknownString
        return listOf(region, country, province, city, isp, fallback)
            .all { it.isBlank() || it == unknown }
    }

    fun initPlugin() {
        val pm = Bukkit.getPluginManager()
        conf = ConfigManager.load(this)

        if (pm.getPlugin("PlaceholderAPI") != null) {
            PlaceholderIntegration().unregister()
            if (conf.papi.enabled) {
                PlaceholderIntegration().register()
            } else throw RuntimeException("PlaceholderAPI enabled in config but NOT installed!")
        }

        /* Unregistering events */
        HandlerList.unregisterAll(this)

        /* Registering events */
        if (conf.message.playerChat.enabled)
            pm.registerEvents(MessageListener(), this)
        if (conf.message.playerLogin.enabled)
            pm.registerEvents(PlayerJoinListener(), this)

        /* Registering commands */
        getCommand("potatoipdisplay")!!.setExecutor(PotatoIpDisplayCommand())
        getCommand("pipd")!!.setExecutor(PotatoIpDisplayCommand())
    }

    fun log(message: String, level: Level = Level.INFO) =
        logger.log(level, message)
}
