package com.flight.db

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

class Booking(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<Booking>(BookingTable)

    var user by User referencedOn BookingTable.user
    var bookingDate by BookingTable.bookingDate
    var paxCount by BookingTable.paxCount
    var status by BookingTable.status
    val tickets by Ticket referrersOn TicketTable.booking

    // Assumes all tickets in a booking share the same flight and cabin class
    val flight get() = tickets.first().flight
    val cabinClass get() = tickets.first().cabinClass
    val passengerCount get() = tickets.count()
}
