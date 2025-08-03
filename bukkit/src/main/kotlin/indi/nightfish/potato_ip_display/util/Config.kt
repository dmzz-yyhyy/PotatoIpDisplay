package indi.nightfish.potato_ip_display.util

data class Config(
    val configVersion: Int = 2,
    val pluginConfigVersion: Int,
    val options: Options,
    val message: Message,
    val papi: PAPISupport,
) {
    data class Options(
        val mode: String,
        val xdbBuffer: String,
        val allowbStats: Boolean,
        val modeV6: String,
        val customUnknownString: String
    )

    data class Message(
        val playerChat: PlayerChat,
        val playerLogin: PlayerLogin
    ) {
        data class PlayerChat(
            val enabled: Boolean,
            val string: String
        )

        data class PlayerLogin(
            val enabled: Boolean,
            val string: String
        )
    }

    data class PAPISupport(
        val enabled: Boolean,
    )
}