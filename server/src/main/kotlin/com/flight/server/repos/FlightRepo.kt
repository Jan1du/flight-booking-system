package com.flight.server.repos

import com.flight.db.AirportTable
import com.flight.db.Flight
import com.flight.db.FlightTable
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.SizedIterable
import org.jetbrains.exposed.v1.jdbc.emptySized
import org.jetbrains.exposed.v1.jdbc.selectAll

fun findFlights(
    origin: String,
    destination: String,
    date: String,
): SizedIterable<Flight> {
    val originAirportId =
        AirportTable
            .selectAll()
            .where { AirportTable.name eq origin }
            .singleOrNull()
            ?.get(AirportTable.id)

    val destinationAirportId =
        AirportTable
            .selectAll()
            .where { AirportTable.name eq destination }
            .singleOrNull()
            ?.get(AirportTable.id)

    if (originAirportId == null || destinationAirportId == null) {
        return emptySized()
    }

    return Flight.find {
        (FlightTable.departureAirport eq originAirportId) and
            (FlightTable.arrivalAirport eq destinationAirportId) and
            (FlightTable.date eq date)
    }
}
