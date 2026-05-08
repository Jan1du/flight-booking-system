// Used Copilot for help with configuring a modular structure

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.ktor) apply false
}

group = "com.flight"
version = "0.0.1"

allprojects {
    repositories {
        mavenCentral()
    }
}
