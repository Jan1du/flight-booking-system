package com.flight.db

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

class Ticket(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<Ticket>(TicketTable)

    var booking by Booking referencedOn TicketTable.booking
    var flight by Flight referencedOn TicketTable.flight
    var passengerFirstName by TicketTable.passengerFirstName
    var passengerLastName by TicketTable.passengerLastName
    var seatNumber by TicketTable.seatNumber
    var cabinClass by TicketTable.cabinClass
    var ticketPrice by TicketTable.ticketPrice
}
