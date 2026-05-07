package com.flight.db

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

class Airline(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<Airline>(AirlineTable)

    var name by AirlineTable.name
    val flights by Flight referrersOn FlightTable.airline
}
