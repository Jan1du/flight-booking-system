package com.flight.db

import org.apache.commons.csv.CSVFormat
import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.io.FileReader

const val USER_DATA = "csv/users.csv"

fun main(args: Array<String>) {
    val logging = args.isNotEmpty() && args[0].lowercase() == "--sql"

    transaction(DatabaseFactory.db) {
        if (logging) {
            addLogger(StdOutSqlLogger)
        }

        SchemaUtils.drop(UserTable)
        SchemaUtils.create(UserTable)

        addUsers(USER_DATA)
    }
}

private fun addUsers(filename: String) {
    FileReader(filename).use { reader ->
        val records = CSVFormat.DEFAULT.parse(reader).drop(1)
        for (record in records) {
            UserTable.insert {
                it[email] = record[0]
                it[firstName] = record[1].ifEmpty { null }
                it[lastName] = record[2].ifEmpty { null }
                it[phoneNo] = record[3].ifEmpty { null }
                it[passwordHash] = record[4]
            }
        }
    }
}