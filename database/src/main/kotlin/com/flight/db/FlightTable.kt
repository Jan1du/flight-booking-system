package com.flight.db

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

const val MAX_DATE_LENGTH = 10 // yyyy-mm-dd
const val MAX_TIME_LENGTH = 20
const val MAX_STATUS_LENGTH = 20
const val PRICE_PRECISION = 10
const val PRICE_SCALE = 2

object FlightTable : IntIdTable("flights") {
    // Foreign keys
    val airline = reference("airline_id", AirlineTable, ReferenceOption.CASCADE)
    val departureAirport = reference("departure_airport_id", AirportTable, ReferenceOption.CASCADE)
    val arrivalAirport = reference("arrival_airport_id", AirportTable, ReferenceOption.CASCADE)

    val date = varchar("date", MAX_DATE_LENGTH)
    val departureTime = varchar("departure_time", MAX_TIME_LENGTH)
    val arrivalTime = varchar("arrival_time", MAX_TIME_LENGTH)
    val defaultPrice = decimal("default_price", PRICE_PRECISION, PRICE_SCALE)
    val status = varchar("status", MAX_STATUS_LENGTH)
}
