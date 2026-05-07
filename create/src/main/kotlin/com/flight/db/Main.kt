// Creates an initial database file and adds any existing data
// Run using ./gradlew :create:run before running the main application for the first time
package com.flight.db

import org.apache.commons.csv.CSVFormat
import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.io.FileReader
import java.time.LocalDate
import java.time.LocalTime
import kotlin.random.Random

const val USER_DATA = "csv/users.csv"
const val AIRPORT_DATA = "csv/airports.csv"
const val AIRLINE_DATA = "csv/airlines.csv"
const val NUM_DAYS = 30 // Number of days from now for flights to be generated
const val NUM_FLIGHTS = 500 // Number of flights per day that needs to be generated
const val MAX_HOURS: Long = 23
const val MAX_MINUTES: Long = 59

fun main(args: Array<String>) {
    val logging = args.isNotEmpty() && args[0].lowercase() == "--sql"

    transaction(DatabaseFactory.db) {
        if (logging) {
            addLogger(StdOutSqlLogger)
        }

        SchemaUtils.drop(UserTable, AirportTable, AirlineTable, FlightTable)
        SchemaUtils.create(UserTable, AirportTable, AirlineTable, FlightTable)

        addUsers()
        addFlights()
        generateRandomFlights()
    }
}

// Adds any existing data from the csv file
private fun addUsers() {
    FileReader(USER_DATA).use { reader ->
        val records = CSVFormat.DEFAULT.parse(reader).drop(1)
        for (record in records) {
            User.new {
                email = record[0]
                firstName = record[1].ifEmpty { null }
                lastName = record[2].ifEmpty { null }
                phoneNo = record[3].ifEmpty { null }
                passwordHash = record[4]
            }
        }
    }
}

// Loads data from the airline and airport csv files to the relevant tables
private fun addFlights() {
    FileReader(AIRPORT_DATA).use { reader ->
        val records = CSVFormat.DEFAULT.parse(reader).drop(1)
        for (record in records) {
            Airport.new {
                name = record[0]
                city = record[1]
                country = record[2]
            }
        }
    }

    FileReader(AIRLINE_DATA).use { reader ->
        val records = CSVFormat.DEFAULT.parse(reader).drop(1)
        for (record in records) {
            Airline.new {
                name = record[0]
            }
        }
    }
}

// Generates random flight information from the airport and airline tables and adds it to the flight table
private fun generateRandomFlights() {
    val airlines = Airline.all().toList()
    val airports = Airport.all().toList()

    if (airlines.isEmpty() || airports.isEmpty()) {
        println("No airline or airport data found.")
        return
    }

    val currentDate = LocalDate.now()
    for (day in 0..NUM_DAYS) {
        repeat(NUM_FLIGHTS) {
            val randomAirline = airlines.random()
            val randomDepartureAirport = airports.random()
            var randomArrivalAirport = airports.random()
            while (randomDepartureAirport.id == randomArrivalAirport.id) {
                randomArrivalAirport = airports.random()
            }

            val flightDate =
                currentDate
                    .plusDays(day.toLong())
                    .toString()

            val hours = Random.nextLong(0, MAX_HOURS)
            val minutes = Random.nextLong(0, MAX_MINUTES)
            val randomDepartureTime =
                LocalTime.MIDNIGHT
                    .plusHours(hours)
                    .plusMinutes(minutes)

            val flightDuration = Random.nextLong(2, 12)
            val randomArrivalTime = randomDepartureTime.plusHours(flightDuration)

            val randomPriceInCents = Random.nextInt(10000, 100000)
            val formattedPrice =
                randomPriceInCents
                    .toBigDecimal()
                    .movePointLeft(2)

            // Random status with "Scheduled" weighted more
            val statuses =
                listOf(
                    "Scheduled",
                    "Scheduled",
                    "Scheduled",
                    "Scheduled",
                    "Delayed",
                    "Delayed",
                    "Cancelled",
                )

            Flight.new {
                airline = randomAirline
                departureAirport = randomDepartureAirport
                arrivalAirport = randomArrivalAirport
                date = flightDate
                departureTime = randomDepartureTime.toString()
                arrivalTime = randomArrivalTime.toString()
                defaultPrice = formattedPrice
                status = statuses.random()
            }
        }
    }
}
