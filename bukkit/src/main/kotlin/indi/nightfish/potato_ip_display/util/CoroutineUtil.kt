package indi.nightfish.potato_ip_display.util

import kotlinx.coroutines.CoroutineDispatcher
import org.bukkit.Bukkit
import indi.nightfish.potato_ip_display.PotatoIpDisplay
import kotlin.coroutines.CoroutineContext

fun pluginAsMainDispatcher(): CoroutineDispatcher = object : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        Bukkit.getScheduler().runTask(PotatoIpDisplay.plugin, block)
    }
}
