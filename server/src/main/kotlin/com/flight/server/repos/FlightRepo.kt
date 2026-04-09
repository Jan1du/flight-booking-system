package com.flight.server.repos

import com.flight.db.FlightTable
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun findFlights(
    origin: String,
    destination: String,
    departDate: String,
    returnDate: String,
    cabinClass: String,
): List<Map<String, String>> =
    transaction {
        FlightTable
            .selectAll()
            .map { row ->
                mapOf(
                    "airline" to row[FlightTable.airline],
                    "flightCode" to row[FlightTable.flightCode],
                    "origin" to row[FlightTable.origin],
                    "destination" to row[FlightTable.destination],
                    "departDate" to row[FlightTable.departDate],
                    "returnDate" to (row[FlightTable.returnDate] ?: ""),
                    "departTime" to row[FlightTable.departTime],
                    "arrivalTime" to row[FlightTable.arrivalTime],
                    "cabinClass" to row[FlightTable.cabinClass],
                    "price" to row[FlightTable.price].toString(),
                    "seatsAvailable" to row[FlightTable.seatsAvailable].toString(),
                )
            }
            .filter { flight ->
                val matchesOrigin =
                    origin.isBlank() || flight["origin"]!!.contains(origin, ignoreCase = true)
                val matchesDestination =
                    destination.isBlank() || flight["destination"]!!.contains(destination, ignoreCase = true)
                val matchesDepartDate =
                    departDate.isBlank() || flight["departDate"] == departDate
                val matchesReturnDate =
                    returnDate.isBlank() || flight["returnDate"] == returnDate
                val matchesCabinClass =
                    cabinClass.isBlank() || flight["cabinClass"]!!.equals(cabinClass, ignoreCase = true)

                matchesOrigin &&
                    matchesDestination &&
                    matchesDepartDate &&
                    matchesReturnDate &&
                    matchesCabinClass
            }
    }
