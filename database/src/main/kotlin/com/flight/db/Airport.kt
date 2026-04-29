package com.flight.db

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

class Airport(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<Airport>(AirportTable)

    var name by AirportTable.name
    var city by AirportTable.city
    var country by AirportTable.country
    val location: String get() = "$city, $country"

    override fun toString() = location
}
