package com.flight.db

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

const val MAX_AIRLINE_LENGTH = 100

object AirlineTable : IntIdTable("airlines") {
    val name = varchar("name", MAX_AIRLINE_LENGTH)
}
