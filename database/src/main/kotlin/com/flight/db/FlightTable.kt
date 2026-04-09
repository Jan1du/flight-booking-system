package com.flight.db

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

const val MAX_CITY_LENGTH = 100
const val MAX_AIRLINE_LENGTH = 100
const val MAX_FLIGHT_CODE_LENGTH = 20
const val MAX_CABIN_CLASS_LENGTH = 30
const val MAX_DATE_LENGTH = 20
const val MAX_TIME_LENGTH = 10

object FlightTable : IntIdTable("flights") {
    val airline = varchar("airline", MAX_AIRLINE_LENGTH)
    val flightCode = varchar("flight_code", MAX_FLIGHT_CODE_LENGTH)

    val origin = varchar("origin", MAX_CITY_LENGTH)
    val destination = varchar("destination", MAX_CITY_LENGTH)

    val departDate = varchar("depart_date", MAX_DATE_LENGTH)
    val returnDate = varchar("return_date", MAX_DATE_LENGTH).nullable()

    val departTime = varchar("depart_time", MAX_TIME_LENGTH)
    val arrivalTime = varchar("arrival_time", MAX_TIME_LENGTH)

    val cabinClass = varchar("cabin_class", MAX_CABIN_CLASS_LENGTH)
    val price = integer("price")
    val seatsAvailable = integer("seats_available")
}
