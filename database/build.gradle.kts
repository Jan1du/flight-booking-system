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
    testImplementation(libs.kotlin.test.junit)
}

tasks.register<JavaExec>("syncUsersCsv") {
    group = "database"
    description = "Dump all User rows from DB to csv/users.csv"

    classpath = sourceSets["main"].runtimeClasspath
    workingDir = rootProject.projectDir
    mainClass.set("com.flight.db.SyncUsersCsvKt")
}

