import org.gradle.api.file.DuplicatesStrategy
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.2.0"
    id("idea")
    id("com.gradleup.shadow") version "9.6.1"
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

sourceSets {
    main {
        java.srcDir("../external/ipdb/src/main/java")
    }
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    implementation("org.lionsoul:ip2region:2.7.0")
    implementation("org.bstats:bstats-bukkit:3.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("com.alibaba:fastjson:1.2.83")
}

val pluginVersion = project.version

val launcher = javaToolchains.launcherFor {
    languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    shadowJar {
        archiveFileName.set("PotatoIPDisplay-Bukkit-${pluginVersion}.jar")
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        relocate("org.bstats", "indi.nightfish.potato_ip_display.bstats")
    }

    build {
        dependsOn(shadowJar)
    }

    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.21.11")
    }
}

tasks.withType<ProcessResources>().configureEach {
    inputs.property("pluginVersion", pluginVersion)
    filesMatching("plugin.yml") {
        expand("pluginVersion" to pluginVersion)
    }
}
