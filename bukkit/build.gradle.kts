plugins {
    kotlin("jvm") version "1.9.22"
    id("idea")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

repositories {
    mavenCentral()
    maven ("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven ("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    implementation("org.lionsoul:ip2region:2.7.0")
}
val pluginVersion = project.parent?.version

tasks {
    shadowJar {
        archiveFileName = "PotatoIPDisplay-Bukkit-${pluginVersion}.jar"
    }
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.20.4")
    }
}

tasks.withType<ProcessResources> {
    filesMatching("plugin.yml") {
        expand(mapOf("pluginVersion" to pluginVersion))
    }
}