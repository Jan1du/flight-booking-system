package com.flight.db

import org.jetbrains.exposed.v1.jdbc.Database

object DatabaseFactory {
    const val URL = "jdbc:h2:./flight_db"
    const val DRIVER = "org.h2.Driver"

    val db by lazy {
        Database.connect(URL, DRIVER)
    }
}
