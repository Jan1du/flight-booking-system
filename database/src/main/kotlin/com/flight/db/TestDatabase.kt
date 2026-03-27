package com.flight.db

import com.password4j.Password
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object TestDatabase {
    const val URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;"
    const val DRIVER = "org.h2.Driver"

    val db by lazy {
        Database.Companion.connect(URL, DRIVER)
    }

    fun create() {
        transaction(db) {
            SchemaUtils.drop(UserTable)
            SchemaUtils.create(UserTable)

            val passwordJohn = Password.hash("Password123").addRandomSalt(16).withScrypt()
            val passwordPeter = Password.hash("SpiderMan_13").addRandomSalt(16).withScrypt()

            // User with completed details
            UserTable.insert {
                it[email] = "johndoe@gmail.com"
                it[firstName] = "John"
                it[lastName] = "Doe"
                it[phoneNo] = "+447890123456"
                it[passwordHash] = passwordJohn.result
            }

            // User with incomplete details
            UserTable.insert {
                it[email] = "peter1973@gmail.com"
                it[passwordHash] = passwordPeter.result
            }
        }
    }
}
