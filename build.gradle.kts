plugins {
    kotlin("jvm") version "1.9.22"
    id("idea")
    id ("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "indi.nightfish.potato_ip_display"
version = "1.0"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.22")
    testImplementation(kotlin("test"))
    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.3.1")
    implementation("com.google.code.gson:gson:2.10.1")
}

tasks.test {
    useJUnitPlatform()
}

