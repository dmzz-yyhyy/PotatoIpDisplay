plugins {
    kotlin("jvm") version "1.9.22"
    id("idea")
    id ("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "indi.nightfish.potato_ip_display"
version = "1.0-bukkit"

repositories {
    mavenCentral()
    maven ("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.22")
    testImplementation(kotlin("test"))
    implementation("org.apache.httpcomponents.client5:httpclient5:5.3.1")
    implementation("com.google.code.gson:gson:2.10.1")
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}

