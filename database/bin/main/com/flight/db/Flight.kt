package com.flight.db

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

class Flight(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<Flight>(FlightTable)

    var airline by Airline referencedOn FlightTable.airline
    var departureAirport by Airport referencedOn FlightTable.departureAirport
    var arrivalAirport by Airport referencedOn FlightTable.arrivalAirport
    var date by FlightTable.date
    var departureTime by FlightTable.departureTime
    var arrivalTime by FlightTable.arrivalTime
    var defaultPrice by FlightTable.defaultPrice
    var status by FlightTable.status
}
