val ktorVersion: String by project
val mockkVersion: String by project
val kotestVersion: String by project
val typesafeConfigVersion: String by project
val bouncyCastleVersion: String by project
val logbackVersion: String by project
val junitVersion: String by project
val exposedVersion: String by project
val h2DatabaseVersion: String by project

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    // Common: Authentication
    api(project(":common:authentication"))

    // Logging
    api("ch.qos.logback:logback-classic:$logbackVersion")

    // Configuration
    api("com.typesafe:config:$typesafeConfigVersion")

    // Server: Engine - using CIO as it supports JVM, Native and GraalVM but doesn't support HTTP/2
    api("io.ktor:ktor-server-cio:$ktorVersion")

    // Serialisation
    api("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // Server: Common
    api("io.ktor:ktor-server-core:$ktorVersion")
    api("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-hsts:$ktorVersion")
    implementation("io.ktor:ktor-server-compression:$ktorVersion")
    implementation("io.ktor:ktor-server-double-receive:$ktorVersion")
    implementation("io.ktor:ktor-server-caching-headers:$ktorVersion")
    implementation("io.ktor:ktor-server-data-conversion:$ktorVersion")
    implementation("io.ktor:ktor-server-auto-head-response:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")

    // Crypto for safe password checking
    implementation("org.bouncycastle:bcprov-jdk15on:$bouncyCastleVersion")

    // Database
    api("com.h2database:h2:$h2DatabaseVersion")

    // JUnit 5 for tests definitions and running
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")

    // Asserting stuff
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")

    // Mocking
    testImplementation("io.mockk:mockk:$mockkVersion")

    // Test data generation
    testImplementation("org.apache.commons:commons-lang3:3.12.0")
}