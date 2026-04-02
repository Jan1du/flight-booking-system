plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.dao)
    implementation(libs.h2)
    implementation(libs.commons.csv)
    implementation(libs.password4j)
}

tasks.register<JavaExec>("syncUsersCsv") {
    group = "database"
    description = "Insert all User rows from DB to csv/users.csv"

    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
    mainClass.set("com.flight.db.SyncUsersCsvKt")
}

