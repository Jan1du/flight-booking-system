rootProject.name = "flight-booking-system"

include("create", "database", "query", "server")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

