// Creates an initial database file and adds any existing data
// Run using ./gradlew :create:run before running the main application for the first time
package com.flight.db

import org.apache.commons.csv.CSVFormat
import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.io.FileReader

const val USER_DATA = "csv/users.csv"

fun main(args: Array<String>) {
    val logging = args.isNotEmpty() && args[0].lowercase() == "--sql"

    transaction(DatabaseFactory.db) {
        if (logging) {
            addLogger(StdOutSqlLogger)
        }

        SchemaUtils.drop(FlightTable, UserTable)
        SchemaUtils.create(UserTable, FlightTable)

        addUsers()
        addFlights()
    }
}

private fun addUsers() {
    FileReader(USER_DATA).use { reader ->
        val records = CSVFormat.DEFAULT.parse(reader).drop(1)
        for (record in records) {
            UserTable.insert {
                it[email] = record[0]
                it[firstName] = record[1].ifEmpty { null }
                it[lastName] = record[2].ifEmpty { null }
                it[phoneNo] = record[3].ifEmpty { null }
                it[passwordHash] = record[4]
            }
        }
    }
}

private fun addFlights() {
    FlightTable.insert {
        it[airline] = "SkyJet"
        it[flightCode] = "SJ102"
        it[origin] = "London"
        it[destination] = "Paris"
        it[departDate] = "2026-05-03"
        it[returnDate] = "2026-05-10"
        it[departTime] = "08:30"
        it[arrivalTime] = "10:50"
        it[cabinClass] = "economy"
        it[price] = 120
        it[seatsAvailable] = 8
    }

    FlightTable.insert {
        it[airline] = "EuroAir"
        it[flightCode] = "EA215"
        it[origin] = "London"
        it[destination] = "Rome"
        it[departDate] = "2026-03-24"
        it[returnDate] = "2026-03-31"
        it[departTime] = "13:15"
        it[arrivalTime] = "16:25"
        it[cabinClass] = "economy"
        it[price] = 185
        it[seatsAvailable] = 5
    }

    FlightTable.insert {
        it[airline] = "BlueWings"
        it[flightCode] = "BW410"
        it[origin] = "London"
        it[destination] = "Madrid"
        it[departDate] = "2026-05-15"
        it[returnDate] = "2026-05-22"
        it[departTime] = "09:40"
        it[arrivalTime] = "13:05"
        it[cabinClass] = "business"
        it[price] = 320
        it[seatsAvailable] = 4
    }
}
