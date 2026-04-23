package com.flight.db

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

const val MAX_AIRPORT_LENGTH = 100

object AirportTable : IntIdTable("airports") {
    val name = varchar("name", MAX_AIRPORT_LENGTH)
    val city = varchar("city", MAX_AIRPORT_LENGTH)
    val country = varchar("country", MAX_AIRPORT_LENGTH)
}
