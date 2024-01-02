plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.allopen)
    id("io.gatling.gradle") version "3.10.3"
}

dependencies {
    // Security patching
    gatlingImplementation(enforcedPlatform("io.netty:netty-bom:4.1.101.Final")) {
        because("io.gatling:gatling-http brings in 4.1.92.Final")
    }
}