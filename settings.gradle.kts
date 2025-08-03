pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "PotatoIpDisplay"
include (
    "bukkit",
    /*"velocity",*/
)
include("ipdb")
project(":ipdb").projectDir = file("external/ipdb")