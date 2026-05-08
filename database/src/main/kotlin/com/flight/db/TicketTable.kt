package com.flight.db

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

const val MAX_SEAT_NUMBER_LENGTH = 5 // e.g. "32A"
const val MAX_CABIN_CLASS_LENGTH = 20 // e.g. "Business"

object TicketTable : IntIdTable("tickets") {
    // Foreign keys
    val booking = reference("booking_id", BookingTable, ReferenceOption.CASCADE)
    val flight = reference("flight_id", FlightTable, ReferenceOption.CASCADE)

    val passengerFirstName = varchar("passenger_first_name", MAX_USERNAME_LENGTH).nullable()
    val passengerLastName = varchar("passenger_last_name", MAX_USERNAME_LENGTH).nullable()
    val seatNumber = varchar("seat_number", MAX_SEAT_NUMBER_LENGTH).nullable()
    val cabinClass = varchar("cabin_class", MAX_CABIN_CLASS_LENGTH)
    val ticketPrice = decimal("ticket_price", PRICE_PRECISION, PRICE_SCALE)
}
